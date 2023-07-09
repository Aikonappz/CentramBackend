package com.centram.batch.router;

import com.centram.batch.aggregator.OrganisationAggregator;
import com.centram.core.service.AssetService;
import com.centram.core.service.IncidentService;
import com.centram.core.service.MiscService;
import com.centram.core.service.OrganisationService;
import com.centram.domain.Asset;
import com.centram.domain.Incident;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.LicenseType;
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

public class AllocatedAssetValidityExpiry extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(LicenseExpiry.class);
    @Value("${app.org.allocated.asset.validity.expiry.notification.cron}")
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
    @Autowired
    private IncidentService incidentService;


    @Override
    public void configure() throws Exception {
        from("quartzComponent://organisation/allocatedAssetValidityExpiry?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .log(LoggingLevel.INFO, "=================== allocated-asset-validity-expiry job started ===================")
                .autoStartup(true)
                .routeId("allocated-asset-validity-expiry")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                //.log(LoggingLevel.INFO, "allocated-asset-validity-expiry -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processAllocatedAssetValidityExpiryNotifications");

        from("direct:processAllocatedAssetValidityExpiryNotifications")
                .log(LoggingLevel.INFO, "=================== process-allocated-asset-validity-expiry-notifications ===================")
                .routeId("process-allocated-asset-validity-expiry-notifications")
                .loop(simple("${body.size}"))
                //.log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        if (organisation.getLicenseType() == LicenseType.ALL || organisation.getLicenseType() == LicenseType.ASSET) {
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            List<Incident> incidents = incidentService.getAllocatedAssets(organisation.getId());
                            if (incidents.size() > 0) {
                                for (Incident incident : incidents) {
                                    if (currentDateTime.isBefore(incident.getAssetValidity()) && !incident.getValidityExpirationMessageSent()) {
                                        Long daysBetween = Duration.between(currentDateTime, incident.getAssetValidity()).abs().toDays();
                                        if (notifyOrganisation(daysBetween.intValue())) {
                                            incident.setValidityExpirationMessageSent(true);
                                            incident = incidentService.saveViaBatch(incident);
                                            miscService.assetValidityExpiration(incident);
                                        }
                                    } else {
                                        // TODO:  Asset validity Expired message
                                        incident.setValidityExpiredMessageSent(true);
                                        incident = incidentService.saveViaBatch(incident);
                                    }
                                }
                            }
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
                //.log(LoggingLevel.INFO, "allocated-asset-validity-expiry completed -> ${header.CURRENT_DATE_TIME}")
                .log(LoggingLevel.INFO, "=================== allocated-asset-validity-expiry job completed ===================")
                .end();
    }

    private Boolean notifyOrganisation(Integer remainingDays) {
        //List<Integer> ranges = Arrays.asList(60, 45, 30, 25, 20, 15, 10, 7);
        List<Integer> ranges = Arrays.asList(30);
        return ranges.contains(remainingDays);
    }
}