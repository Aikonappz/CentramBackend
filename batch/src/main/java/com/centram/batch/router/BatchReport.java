package com.centram.batch.router;

import com.centram.batch.aggregator.OrganisationAggregator;
import com.centram.batch.service.BatchReportService;
import com.centram.common.vo.UserVO;
import com.centram.core.service.MiscService;
import com.centram.core.service.ReportService;
import com.centram.core.service.UserService;
import com.centram.domain.Organisation;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchReport extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(BatchReport.class);
    @Value("${app.incident.report.ticket.cron}")
    private String interval;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.report.path}")
    private String appReportPath;
    @Autowired
    private UserService userService;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private MiscService miscService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private BatchReportService batchReportService;

    @Override
    public void configure() throws Exception {
        from("quartzComponent://report/incident?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("incident-report")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "incident-report started -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processIncidentReport")
                .end();

        from("direct:processIncidentReport")
                .routeId("process-incident-report")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        LocalDateTime start = currentDateTime.minusDays(7).toLocalDate().atStartOfDay();
                        LocalDateTime end = currentDateTime.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
                        log.info(" current date time {}, start {}, end {}", currentDateTime, start, end);
                        List<UserVO> userVOS = userService.getUsersByRoleNames(Arrays.asList("ORG_ADMIN", "ORG_INCIDENT_AGENT_MANAGER", "ORG_INCIDENT_AGENT_LEAD", "_CATEGORY_ADMIN"), organisation.getId());
                        Map<String, Object> mailData = new HashMap<String, Object>();
                        for (UserVO userVO : userVOS) {
                            try {
                                mailData = new HashMap<String, Object>();
                                mailData.put("start", start.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("end", end.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("file", appReportPath.concat("/incident-" + System.currentTimeMillis() + ".csv"));
                                mailData.put("recipient", userVO);
                                mailData.put("mailTemplate", "BATCH_REPORT");
                                mailData.put("mailSubject", "incidentReportSub");
                                mailData.put("mailBody", "incidentReportBody");
                                batchReportService.generateIncidentReport(start, end, userVO.getRoleNames(), organisation.getId(), mailData.get("file").toString());
                                miscService.sendBatchReport(mailData);
                            } catch (Exception e) {
                                log.info("exception occurred during file creation {} ", e.getMessage());
                                continue;
                            }
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end()
                .log(LoggingLevel.INFO, "incident-report completed -> ${header.CURRENT_DATE_TIME}")
                .end();

        from("quartzComponent://report/escalatedIncident?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("escalated-incident-report")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "escalated-incident-report started -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processEscalatedIncidentReport")
                .end();

        from("direct:processEscalatedIncidentReport")
                .routeId("process-escalated-incident-report")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        LocalDateTime start = currentDateTime.minusDays(7).toLocalDate().atStartOfDay();
                        LocalDateTime end = currentDateTime.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
                        log.info(" current date time {}, start {}, end {}", currentDateTime, start, end);
                        List<UserVO> userVOS = userService.getUsersByRoleNames(Arrays.asList("ORG_ADMIN", "ORG_INCIDENT_AGENT_MANAGER", "ORG_INCIDENT_AGENT_LEAD", "_CATEGORY_ADMIN"), organisation.getId());
                        Map<String, Object> mailData = new HashMap<String, Object>();
                        for (UserVO userVO : userVOS) {
                            try {
                                mailData = new HashMap<String, Object>();
                                mailData.put("start", start.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("end", end.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("file", appReportPath.concat("/escalated-incident-" + System.currentTimeMillis() + ".csv"));
                                mailData.put("recipient", userVO);
                                mailData.put("mailTemplate", "BATCH_REPORT");
                                mailData.put("mailSubject", "incidentReportSub");
                                mailData.put("mailBody", "incidentReportBody");
                                batchReportService.generateEscalatedIncidentReport(start, end, userVO.getRoleNames(), organisation.getId(), mailData.get("file").toString());
                                miscService.sendBatchReport(mailData);
                            } catch (Exception e) {
                                log.info("exception occurred during file creation {} ", e.getMessage());
                                continue;
                            }
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end()
                .log(LoggingLevel.INFO, "escalated-incident-report completed -> ${header.CURRENT_DATE_TIME}")
                .end();

        from("quartzComponent://report/reOpenedIncident?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("reopened-incident-report")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "reopened-incident-report started -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processReopenedIncidentReport")
                .end();

        from("direct:processReopenedIncidentReport")
                .routeId("process-reopened-incident-report")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        LocalDateTime start = currentDateTime.minusDays(7).toLocalDate().atStartOfDay();
                        LocalDateTime end = currentDateTime.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
                        log.info(" current date time {}, start {}, end {}", currentDateTime, start, end);
                        List<UserVO> userVOS = userService.getUsersByRoleNames(Arrays.asList("ORG_ADMIN", "ORG_INCIDENT_AGENT_MANAGER", "ORG_INCIDENT_AGENT_LEAD", "_CATEGORY_ADMIN"), organisation.getId());
                        Map<String, Object> mailData = new HashMap<String, Object>();
                        for (UserVO userVO : userVOS) {
                            try {
                                mailData = new HashMap<String, Object>();
                                mailData.put("start", start.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("end", end.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("file", appReportPath.concat("/escalated-incident-" + System.currentTimeMillis() + ".csv"));
                                mailData.put("recipient", userVO);
                                mailData.put("mailTemplate", "BATCH_REPORT");
                                mailData.put("mailSubject", "incidentReportSub");
                                mailData.put("mailBody", "incidentReportBody");
                                batchReportService.generateReopenedReport(start, end, userVO.getRoleNames(), organisation.getId(), mailData.get("file").toString());
                                miscService.sendBatchReport(mailData);
                            } catch (Exception e) {
                                log.info("exception occurred during file creation {} ", e.getMessage());
                                continue;
                            }
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end()
                .log(LoggingLevel.INFO, "reopened-incident-report completed -> ${header.CURRENT_DATE_TIME}")
                .end();

        from("quartzComponent://report/agingIncident?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("aging-incident-report")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "aging-incident-report started -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processAgingIncidentReport")
                .end();

        from("direct:processAgingIncidentReport")
                .routeId("process-aging-incident-report")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        LocalDateTime start = currentDateTime.minusDays(7).toLocalDate().atStartOfDay();
                        LocalDateTime end = currentDateTime.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
                        log.info(" current date time {}, start {}, end {}", currentDateTime, start, end);
                        List<UserVO> userVOS = userService.getUsersByRoleNames(Arrays.asList("ORG_ADMIN", "ORG_INCIDENT_AGENT_MANAGER", "ORG_INCIDENT_AGENT_LEAD", "_CATEGORY_ADMIN"), organisation.getId());
                        Map<String, Object> mailData = new HashMap<String, Object>();
                        for (UserVO userVO : userVOS) {
                            try {
                                mailData = new HashMap<String, Object>();
                                mailData.put("start", start.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("end", end.format(DateTimeFormatter.ofPattern(dateFormat)));
                                mailData.put("file", appReportPath.concat("/escalated-incident-" + System.currentTimeMillis() + ".csv"));
                                mailData.put("recipient", userVO);
                                mailData.put("mailTemplate", "BATCH_REPORT");
                                mailData.put("mailSubject", "incidentReportSub");
                                mailData.put("mailBody", "incidentReportBody");
                                batchReportService.generateIncidentAgingReport(start, end, userVO.getRoleNames(), organisation.getId(), mailData.get("file").toString());
                                miscService.sendBatchReport(mailData);
                            } catch (Exception e) {
                                log.info("exception occurred during file creation {} ", e.getMessage());
                                continue;
                            }
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end()
                .log(LoggingLevel.INFO, "reopened-incident-report completed -> ${header.CURRENT_DATE_TIME}")
                .end();
    }


}
