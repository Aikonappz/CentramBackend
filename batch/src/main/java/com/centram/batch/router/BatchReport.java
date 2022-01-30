package com.centram.batch.router;

import com.centram.core.service.MiscService;
import com.centram.core.service.ReportService;
import com.centram.core.service.UserService;
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
import java.time.format.DateTimeFormatter;

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
                /*.enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
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
                        List<UserVO> userVOS = userService.getUsersByRoleNames(
                                Arrays.asList(
                                        "ORG_ADMIN",
                                        "ORG_INCIDENT_AGENT_MANAGER",
                                        "ORG_INCIDENT_AGENT_LEAD",
                                        "_CATEGORY_ADMIN"
                                ),
                                organisation.getId()
                        );
                        ByteArrayInputStream resource = null;
                        File file = null;
                        Map<String, String> mailData = new HashMap<String, String>();
                        for (UserVO userVO : userVOS) {
                            resource = reportService.downloadIncidentReport("", "", "", "", null, null, true, userVO.getRoleNames(), organisation.getId());
                            mailData = new HashMap<String, String>();
                            mailData.put("start", start.format(DateTimeFormatter.ofPattern(dateFormat)));
                            mailData.put("end", end.format(DateTimeFormatter.ofPattern(dateFormat)));
                            mailData.put("email", userVO.getEmail());
                            mailData.put("file", appReportPath.concat("/incident-" + System.currentTimeMillis() + ".csv"));
                            file = new File(mailData.get("file"));
                            if (!file.getParentFile().exists()) {
                                file.getParentFile().mkdirs();
                            }
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            //IOUtils.copy(resource, new FileOutputStream(file, false));
                        }
                    }
                })
                //.toD("direct:${header.next-route}")
                .end()*/
                .log(LoggingLevel.INFO, "incident-report completed -> ${header.CURRENT_DATE_TIME}")
                .end();
    }
}
