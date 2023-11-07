package com.centram.batch.router;

import com.centram.batch.aggregator.OrganisationAggregator;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.AssetService;
import com.centram.core.service.MiscService;
import com.centram.core.service.OrganisationService;
import com.centram.domain.Asset;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AssetWarrantyExpiry extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(LicenseExpiry.class);
    @Value("${app.org.asset.warranty.expiry.notification.cron}")
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
    private AssetService assetService;


    @Override
    public void configure() throws Exception {
        /*from("quartzComponent://organisation/assetWarrantyExpiry?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .log(LoggingLevel.INFO, "=================== asset-warranty-expiry job started ===================")
                .autoStartup(true)
                .routeId("asset-warranty-expiry")
                .enrich("bean:organisationService?method=getActiveOrganisations()", new OrganisationAggregator())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                //.log(LoggingLevel.INFO, "asset-warranty-expiry -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processAssetWarrantyExpiryNotifications");

        from("direct:processAssetWarrantyExpiryNotifications")
                .log(LoggingLevel.INFO, "=================== process-asset-warranty-expiry-notifications ===================")
                .routeId("process-asset-warranty-expiry-notifications")
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        if (organisation.getLicenseType() == LicenseType.ALL || organisation.getLicenseType() == LicenseType.ASSET) {
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            List<Asset> assets = assetService.getAssets(organisation.getId());
                            if (assets.size() > 0) {
                                for (Asset asset : assets) {
                                    if (currentDateTime.isBefore(asset.getWarrantyExpiredAt()) && !asset.getWarrantyExpirationMessageSent()) {
                                        Long daysBetween = Duration.between(currentDateTime, asset.getWarrantyExpiredAt()).abs().toDays();
                                        if (notifyOrganisation(daysBetween.intValue())) {
                                            asset.setWarrantyExpirationMessageSent(true);
                                            asset = assetService.saveViaBatch(asset);
                                            miscService.assetWarrantyExpiration(asset);
                                        }
                                    } else {
                                        // TODO:  Asset warranty Expired message
                                        asset.setWarrantyExpiredMessageSent(true);
                                        asset = assetService.saveViaBatch(asset);
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
                //.log(LoggingLevel.INFO, "asset-warranty-expiry completed -> ${header.CURRENT_DATE_TIME}")
                .log(LoggingLevel.INFO, "=================== asset-warranty-expiry job completed ===================")
                .end();*/
    }

    private Boolean notifyOrganisation(Integer remainingDays) {
        //List<Integer> ranges = Arrays.asList(60, 45, 30, 25, 20, 15, 10, 7);
        List<Integer> ranges = Arrays.asList(30);
        return ranges.contains(remainingDays);
    }
}