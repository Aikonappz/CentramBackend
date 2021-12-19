package com.centram.batch.router;

import com.centram.batch.aggregator.IncidentAggregator;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.core.service.IncidentNotificationService;
import com.centram.core.service.IncidentService;
import com.centram.core.service.MiscService;
import com.centram.domain.Incident;
import com.centram.domain.IncidentNotification;
import com.centram.domain.enumarator.IncidentNotificationType;
import com.centram.domain.enumarator.IncidentStatus;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncidentRouter extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(IncidentRouter.class);
    private final String interval1Minute = "0 0/1 * * * ?";
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;
    @Autowired
    private IncidentService incidentService;

    @Autowired
    private ProducerTemplate producerTemplate;
    //producerTemplate.sendBodyAndHeader("direct:partner-processBulkEvents", partnerToPartnerEvents, "origin", "kafka");

    @Autowired
    private IncidentNotificationService incidentNotificationService;

    @Autowired
    private MiscService miscService;

    @Override
    public void configure() throws Exception {
        /*from("quartzComponent://incidentSla/notifyIncidents?cron=".concat(interval1Minute).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("incident-sla")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(new ArrayList<IncidentStatus>() {{
                            add(IncidentStatus.OPEN);
                            add(IncidentStatus.ASSIGNED);
                            add(IncidentStatus.WORK_IN_PROGRESS);
                            add(IncidentStatus.SLA_ABOUT_TO_BREACH);
                            add(IncidentStatus.SLA_BREACHED);
                        }});
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "CURRENT_DATE_TIME -> ${header.CURRENT_DATE_TIME}")
                .to("direct:getNonBlockedIncidents");*/

        from("direct:getNonBlockedIncidents")
                .routeId("get-non-blocked-incidents")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //log.info("BODY {}", exchange.getIn().getBody());
                    }
                })
                .enrich("bean:incidentService?method=getNonBlockedIncidents(${body})", new IncidentAggregator())
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Incident> nonBlockedIncidents = (List<Incident>) exchange.getIn().getBody();
                        Incident incident = nonBlockedIncidents.get((int) exchange.getProperty("CamelLoopIndex"));

                        String targetRouter = null;
                        String timeZone = incident.getRaisedUser().getLocation().getTimezone();
                        ZonedDateTime currentDatetime = LocalDateTime.now().atZone(ZoneId.of(timeZone));
                        ZonedDateTime startDatetime = incident.getCreatedDate().atZone(ZoneId.of(timeZone));
                        ZonedDateTime endDatetime = incident.getSlaAt().atZone(ZoneId.of(timeZone));
                        Long difference = endDatetime.toEpochSecond() - startDatetime.toEpochSecond();
                        ZonedDateTime wip50PercentPassed = incident.getCreatedDate().atZone(ZoneId.of(timeZone));
                        wip50PercentPassed.plus((difference * 50 / 100), ChronoUnit.SECONDS);
                        ZonedDateTime wip75PercentPassed = incident.getCreatedDate().atZone(ZoneId.of(timeZone));
                        wip75PercentPassed.plus((difference * 75 / 100), ChronoUnit.SECONDS);
                        ZonedDateTime sla60MinutesPassed = incident.getSlaAt().atZone(ZoneId.of(timeZone));
                        sla60MinutesPassed.plusMinutes(60);

                        if (currentDatetime.isAfter(wip50PercentPassed) && currentDatetime.isBefore(wip75PercentPassed)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.WIP_50_PERCENT_TIME_PASSED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else if (currentDatetime.isAfter(wip75PercentPassed)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.WIP_75_PERCENT_TIME_PASSED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else if (currentDatetime.isBefore(sla60MinutesPassed) && currentDatetime.isAfter(endDatetime)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.SLA_JUST_BREACHED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else if (currentDatetime.isAfter(sla60MinutesPassed)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.SLA_BREACHED_60_MINUTES_PASSED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else {
                            producerTemplate.requestBodyAndHeader("skipIncident", null, "route", "skipIncident");
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end();

        from("direct:wip50PercentTimePassed")
                .routeId("wip-50-percent-time-passed")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    Optional<IncidentNotification> incidentNotification = Optional.ofNullable(incidentNotificationService.find(new IncidentNotification(incident.getId(), IncidentNotificationType.WIP_50_PERCENT_TIME_PASSED, LocalDateTime.now())));
                    return !incidentNotification.isPresent();
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("WIP_50_PERCENT_TIME_PASSED BODY {}", incident);
                        incidentNotificationService.save(new IncidentNotification(incident.getId(), IncidentNotificationType.WIP_50_PERCENT_TIME_PASSED, LocalDateTime.now()));
                        miscService.notifyWip50PercentTimePassed(new IncidentEmailVO(incident, dateTimeFormat));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("NOTIFICATION WIP_50_PERCENT_TIME_PASSED ALREADY TRIGGERED FOR {}!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end();

        from("direct:wip75PercentTimePassed")
                .routeId("wip-75-percent-time-passed")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    Optional<IncidentNotification> incidentNotification = Optional.ofNullable(incidentNotificationService.find(new IncidentNotification(incident.getId(), IncidentNotificationType.WIP_75_PERCENT_TIME_PASSED, LocalDateTime.now())));
                    return !incidentNotification.isPresent();
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("WIP_75_PERCENT_TIME_PASSED BODY {}", incident);
                        incidentNotificationService.save(new IncidentNotification(incident.getId(), IncidentNotificationType.WIP_75_PERCENT_TIME_PASSED, LocalDateTime.now()));
                        miscService.notifyWip75PercentTimePassed(new IncidentEmailVO(incident, dateTimeFormat));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("NOTIFICATION WIP_75_PERCENT_TIME_PASSED ALREADY TRIGGERED FOR {}!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end();

        from("direct:slaJustBreached")
                .routeId("sla-just-breached")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    Optional<IncidentNotification> incidentNotification = Optional.ofNullable(incidentNotificationService.find(new IncidentNotification(incident.getId(), IncidentNotificationType.SLA_JUST_BREACHED, LocalDateTime.now())));
                    return !incidentNotification.isPresent();
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("SLA_JUST_BREACHED BODY {}", incident);
                        incidentNotificationService.save(new IncidentNotification(incident.getId(), IncidentNotificationType.SLA_JUST_BREACHED, LocalDateTime.now()));
                        miscService.notifySlaBreached(new IncidentEmailVO(incident, dateTimeFormat));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("NOTIFICATION SLA_JUST_BREACHED ALREADY TRIGGERED FOR {}!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end();

        from("direct:slaBreached60MinutesPassed")
                .routeId("sla-breached-60-minutes-passed")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    Optional<IncidentNotification> incidentNotification = Optional.ofNullable(incidentNotificationService.find(new IncidentNotification(incident.getId(), IncidentNotificationType.SLA_BREACHED_60_MINUTES_PASSED, LocalDateTime.now())));
                    return !incidentNotification.isPresent();
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("SLA_BREACHED_60_MINUTES_PASSED BODY {}", incident);
                        incidentNotificationService.save(new IncidentNotification(incident.getId(), IncidentNotificationType.SLA_BREACHED_60_MINUTES_PASSED, LocalDateTime.now()));
                        miscService.notifySlaBreached60MinutesPassed(new IncidentEmailVO(incident, dateTimeFormat));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("NOTIFICATION ALREADY TRIGGERED FOR {}!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end();

        from("direct:skipIncident")
                .routeId("skip-incident")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.info("SKIPPED!");
                    }
                })
                .end();

    }
}
