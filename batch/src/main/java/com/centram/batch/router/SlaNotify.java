package com.centram.batch.router;

import com.centram.batch.aggregator.IncidentAggregator;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.core.service.IncidentService;
import com.centram.core.service.MiscService;
import com.centram.domain.Incident;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SlaNotify extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(SlaNotify.class);
    @Value("${app.incident.sla.notification.cron}")
    private String interval;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private MiscService miscService;
    @Value("${app.date.time.view.format}")
    private String appDateTimeViewFormat;

    @Override
    public void configure() throws Exception {
        from("quartzComponent://incident/slaNotify?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .log(LoggingLevel.INFO, "=================== sla-notify job started ===================")
                .autoStartup(true)
                .routeId("sla-notify")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                //.log(LoggingLevel.INFO, "sla-notify started -> ${header.CURRENT_DATE_TIME}")
                .to("direct:getNonBlockedIncidents");

        from("direct:getNonBlockedIncidents")
                .log(LoggingLevel.INFO, "=================== get-non-blocked-incidents ===================")
                .routeId("get-non-blocked-incidents")
                .enrich("bean:incidentService?method=getNonBlockedIncidents()", new IncidentAggregator())
                .loop(simple("${body.size}"))
                //.log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Incident> nonBlockedIncidents = (List<Incident>) exchange.getIn().getBody();
                        Incident incident = nonBlockedIncidents.get((int) exchange.getProperty("CamelLoopIndex"));
                        String targetRouter = null;
                        String timeZone = incident.getRaisedUser().getLocation().getTimezone();
                        ZonedDateTime currentDatetime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of(timeZone));
                        ZonedDateTime startDatetime = ZonedDateTime.of(
                                incident.getReOpened() ? incident.getReopenedAt() : incident.getRaisedAt(),
                                ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(timeZone)
                        );
                        ZonedDateTime endDatetime = ZonedDateTime.of(incident.getSlaAt(), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(timeZone));
                        Duration duration = Duration.between(startDatetime, endDatetime);
                        ZonedDateTime wip50PercentPassed = ZonedDateTime.of(incident.getCreatedDate(), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(timeZone));
                        wip50PercentPassed = wip50PercentPassed.plus(duration.toSeconds() * 50 / 100, ChronoUnit.SECONDS);
                        ZonedDateTime wip75PercentPassed = ZonedDateTime.of(incident.getCreatedDate(), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(timeZone));
                        wip75PercentPassed = wip75PercentPassed.plus(duration.toSeconds() * 75 / 100, ChronoUnit.SECONDS);
                        ZonedDateTime sla60MinutesPassed = ZonedDateTime.of(incident.getSlaAt(), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(timeZone));
                        sla60MinutesPassed = sla60MinutesPassed.plusMinutes(60);
                        if (currentDatetime.isAfter(wip50PercentPassed) && currentDatetime.isBefore(wip75PercentPassed)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.WIP_50_PERCENT_TIME_PASSED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else if (currentDatetime.isAfter(wip75PercentPassed) && currentDatetime.isBefore(endDatetime)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.WIP_75_PERCENT_TIME_PASSED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else if (currentDatetime.isAfter(endDatetime) && currentDatetime.isBefore(sla60MinutesPassed)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.SLA_JUST_BREACHED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else if (currentDatetime.isAfter(sla60MinutesPassed)) {
                            targetRouter = CaseUtils.toCamelCase(IncidentNotificationType.SLA_BREACHED_60_MINUTES_PASSED.name(), false, '_');
                            producerTemplate.requestBodyAndHeader("direct:".concat(targetRouter), incident, "route", targetRouter);
                        } else {
                            producerTemplate.requestBodyAndHeader("direct:skipIncident", incident, "route", "skipIncident");
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end();

        from("direct:wip50PercentTimePassed")
                .log(LoggingLevel.INFO, "=================== wip-50-percent-time-passed ===================")
                .routeId("wip-50-percent-time-passed")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    return incident.getAgentNotification1At() == null;
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("[{}] notification triggered for wip-50-percent-time-passed!", incident.getIncidentNo());
                        incident.setAgentNotification1At(LocalDateTime.now());
                        incident = incidentService.saveViaBatch(incident);
                        miscService.notifyWip50PercentTimePassed(new IncidentEmailVO(incident, appDateTimeViewFormat, incident.getIncidentNo().concat(" 50% time passed! Please complete within SLA.")));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        //log.info("[{}] notification already triggered for wip-50-percent-time-passed!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end()
                .to("direct:completeIncidentSlaNotification")
                .end();

        from("direct:wip75PercentTimePassed")
                .log(LoggingLevel.INFO, "=================== wip-75-percent-time-passed ===================")
                .routeId("wip-75-percent-time-passed")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    return incident.getAgentNotification2At() == null;
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("[{}] notification triggered for wip-75-percent-time-passed!", incident.getIncidentNo());
                        incident.setStatus(IncidentStatus.SLA_ABOUT_TO_BREACH);
                        incident.setAgentNotification2At(LocalDateTime.now());
                        incident = incidentService.saveViaBatch(incident);
                        miscService.notifyWip75PercentTimePassed(new IncidentEmailVO(incident, appDateTimeViewFormat, incident.getIncidentNo().concat(" 75% time passed! Please complete within SLA.")));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        //log.info("[{}] notification already triggered for wip-75-percent-time-passed!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end()
                .to("direct:completeIncidentSlaNotification")
                .end();

        from("direct:slaJustBreached")
                .log(LoggingLevel.INFO, "=================== sla-just-breached ===================")
                .routeId("sla-just-breached")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    return incident.getEscalation1At() == null;
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("[{}] notification triggered for sla-just-breached!", incident.getIncidentNo());
                        incident.setStatus(IncidentStatus.SLA_BREACHED);
                        incident.setSlaBreached(true);
                        incident.setEscalation1At(LocalDateTime.now());
                        incident = incidentService.saveViaBatch(incident);
                        miscService.notifySlaBreached(new IncidentEmailVO(incident, appDateTimeViewFormat, incident.getIncidentNo().concat(" exceeds SLA. Please respond immediately!")));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        //log.info("[{}] notification already triggered for sla-just-breached!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end()
                .to("direct:completeIncidentSlaNotification")
                .end();

        from("direct:slaBreached60MinutesPassed")
                .log(LoggingLevel.INFO, "=================== sla-breached-60-minutes-passed ===================")
                .routeId("sla-breached-60-minutes-passed")
                .choice()
                .when(exchange -> {
                    Incident incident = (Incident) exchange.getIn().getBody();
                    return incident.getEscalation2At() == null;
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        log.info("[{}] notification triggered for sla-breached-60-minutes-passed!", incident.getIncidentNo());
                        incident.setEscalation2At(LocalDateTime.now());
                        incident = incidentService.saveViaBatch(incident);
                        miscService.notifySlaBreached60MinutesPassed(new IncidentEmailVO(incident, appDateTimeViewFormat, incident.getIncidentNo().concat(" exceeds SLA. Please respond immediately!")));
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        //log.info("[{}] notification already triggered for sla-breached-60-minutes-passed!", incident.getIncidentNo());
                    }
                })
                .endChoice()
                .end()
                .to("direct:completeIncidentSlaNotification")
                .end();

        from("direct:skipIncident")
                .routeId("skip-incident")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = (Incident) exchange.getIn().getBody();
                        //log.info("[{}] skipped!", incident.getIncidentNo());
                    }
                })
                .to("direct:completeIncidentSlaNotification")
                .end();

        from("direct:completeIncidentSlaNotification")
                .routeId("complete-incident-sla-notification")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                //.log(LoggingLevel.INFO, "sla-notify completed -> ${header.CURRENT_DATE_TIME}")
                .log(LoggingLevel.INFO, "=================== sla-notify job completed ===================")
                .end();

    }
}
