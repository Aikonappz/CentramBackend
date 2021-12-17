package com.centram.batch.router;

import com.centram.core.service.IncidentService;
import com.centram.domain.enumarator.IncidentStatus;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IncidentRouter extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(IncidentRouter.class);
    private final String interval1Minute = "0 0/1 * * * ?";
    @Value("${date.time.format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String dateTimeFormat;
    @Value("${date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;
    @Autowired
    private IncidentService incidentService;

    @Override
    public void configure() throws Exception {

        /*from("quartzComponent://currentTimer?trigger.repeatInterval=1000&trigger.repeatCount=5")
                .setBody().simple("TechGeekNext Quartz Example 1")
                .to("stream:out");*/

        //from("quartzComponent://incident-sla/mark-incident?cron="+interval1Minute+"&stateful=true&durableJob=true&recoverableJob=true")
        from("quartz://incident-sla/trigger-to-get-non-blocked-incidents?cron=" + interval1Minute + "&stateful=true&durableJob=true&recoverableJob=true")
                .routeId("incident-sla")
                .setHeader("CURRENT_DATE_TIME", simple(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat))))
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(new ArrayList<IncidentStatus>(){{
                            add(IncidentStatus.OPEN);
                            add(IncidentStatus.ASSIGNED);
                            add(IncidentStatus.WORK_IN_PROGRESS);
                            add(IncidentStatus.SLA_ABOUT_TO_BREACH);
                            add(IncidentStatus.SLA_BREACHED);
                        }});
                        log.info(" CURRENT_DATE_TIME -> ${header.ROUTING_KEY} ");

                    }
                })
                .log(LoggingLevel.INFO, "CURRENT_DATE_TIME -> ${header.ROUTING_KEY}")
        .to("direct:getNonBlockedIncidents");

       /* from("direct:getNonBlockedIncidents")
                .routeId("get-non-blocked-incidents")
                .doTry()
                .process(new PreLogProcessor())
                .loop(simple("${body.events.size}"))
                .log("Event Index => ${exchangeProperty.CamelLoopIndex}")
                .enrich("bean:eventLogService?method=get(${header.event-ids[${exchangeProperty.CamelLoopIndex}]})", new EventLogAggregation())
                .choice()
                .when(exchange -> {
                    EventProcessDTO eventProcessDTO = (EventProcessDTO) exchange.getIn().getBody();
                    return CollectionUtils.isEmpty(eventProcessDTO.getEventLogs());
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        // logging fresh events
                        EventProcessDTO eventProcessDTO = (EventProcessDTO) exchange.getIn().getBody();
                        Event event = eventProcessDTO.getEvents().get(exchange.getProperty("CamelLoopIndex", Integer.class));
                        eventLog = new EventLog();
                        eventLog.setNoOfTries(1);
                        eventLog.setEvent(event);
                        eventLog.setConsumptionDateTime(LocalDateTime.now());
                        eventLog.setEventId(event.getId());
                        eventLog.setStatus(Status.IN_PROGRESS);
                        eventLog.setMaxTriesReached(Boolean.FALSE);
                        eventLog.setExceptionMessage(null);
                        eventLog = eventLogService.save(eventLog);
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        EventProcessDTO eventProcessDTO = (EventProcessDTO) exchange.getIn().getBody();
                        BigInteger currentEventId = (BigInteger) exchange.getIn().getHeader("event-ids", List.class).get(exchange.getProperty("CamelLoopIndex", Integer.class));
                        List<Event> events = eventProcessDTO.getEvents().stream()
                                .filter(ev -> ev.getId().compareTo(currentEventId) != 0)
                                .collect(Collectors.toList());
                        eventProcessDTO.setEvents(events);
                        exchange.getIn().setBody(eventProcessDTO);
                    }
                })
                .log("SAME EVENT CONSUMED FROM KAFKA! SO IGNORING THIS EVENT.")
                .endChoice()
                .end()
                .end()
                .process(new ManualKafkaCommitProcessor())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        EventProcessDTO eventProcessDTO = (EventProcessDTO) exchange.getIn().getBody();
                        // side line error events/payload which missed data for further processing
                        List<Event> notForFurtherProcessEvents = eventProcessDTO.getEvents()
                                .stream()
                                .filter(e -> e.getEventSource() == null || e.getEventSource().getOrganizationId() == null || e.getEventDestination() == null || e.getEventDestination().getOrganizationId() == null)
                                .collect(Collectors.toList());
                        List<BigInteger> errorIds = notForFurtherProcessEvents.stream().map(event -> event.getId()).collect(Collectors.toList());
                        List<EventLog> eventLogs = eventLogService.findByEventIds(errorIds);
                        eventLogs.forEach(el -> {
                            el.setStatus(Status.FAILED);
                            el.setExceptionMessage("SOURCE/DESTINATION NOT CONFIGURED PROPERLY IN EVENT PAYLOAD!");
                        });
                        eventLogService.saveAll(eventLogs);
                        // filter events for further processing
                        List<Event> filteredEvents = eventProcessDTO.getEvents().stream()
                                .filter(e -> e.getEventSource() != null && e.getEventSource().getOrganizationId() != null && e.getEventDestination() != null && e.getEventDestination().getOrganizationId() != null)
                                .collect(Collectors.toList());
                        exchange.getIn().setBody(filteredEvents);
                    }
                })
                .to("direct:distributeEvents")
                .endDoTry()
                .doCatch(Exception.class)
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                        LOG.error(cause.getMessage());
                        throw cause;
                    }
                })
                .end();*/

    }
}
