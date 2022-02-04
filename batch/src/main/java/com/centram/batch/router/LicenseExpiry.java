package com.centram.batch.router;

import com.centram.batch.aggregator.OrganisationAggregator;
import com.centram.core.service.MiscService;
import com.centram.core.service.OrganisationService;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.Status;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class LicenseExpiry extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(LicenseExpiry.class);
    @Value("${app.org.notification.cron}")
    private String interval;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private MiscService miscService;

    @Override
    public void configure() throws Exception {
        from("quartzComponent://organisation/licenseExpiry?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("license-expiry")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "license-expiry -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processOrganisationNotifications");

        from("direct:processOrganisationNotifications")
                .routeId("process-organisation-notifications")
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        if (currentDateTime.isBefore(organisation.getLicenseEnd())) {
                            //Long daysBetween = Duration.between(currentDateTime, organisation.getLicenseEnd()).get(ChronoUnit.DAYS);
                            Long daysBetween = Duration.between(currentDateTime, organisation.getLicenseEnd()).abs().toDays();
                            if (notifyOrganisation(daysBetween.intValue())) {
                                miscService.organisationNotification(organisation, false);
                            }
                        } else {
                            organisation.setStatus(Status.INACTIVE);
                            organisation = organisationService.saveViaBatch(organisation);
                            miscService.organisationNotification(organisation, true);
                        }
                    }
                })
                .end()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "license-expiry completed -> ${header.CURRENT_DATE_TIME}")
                .end();
    }

    private Boolean notifyOrganisation(Integer remainingDays) {
        List<Integer> ranges = Arrays.asList(60, 45, 30, 25, 20, 15, 10, 7);
        return ranges.contains(remainingDays);
    }
}