package com.centram.batch.router;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class EmailDistributionListListener extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(EmailDistributionListListener.class);

    private String interval = "0 0/3 * * * ?";
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.report.path}")
    private String appReportPath;

    @Override
    public void configure() throws Exception {
        // from("quartzComponent://junk/cleaner?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
        from("{{support.mail.protocol}}://{{support.mail.host}}:{{support.mail.port}}?username={{support.mail.username}}&password={{support.mail.password}}&unseen=true&delete=false&peek=false&closeFolder=false&disconnect=false")
                .log(LoggingLevel.INFO, "=================== organization-license-expiry job started ===================")
                .autoStartup(true)
                .routeId("test")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Exchange ex = exchange;
                        String body = exchange.getIn().getBody(String.class);
                        log.info("exchange body {}", body);
                    }
                })
                .log(LoggingLevel.INFO, "junk-cleaner started -> ${header.CURRENT_DATE_TIME}")
                .end();
    }


}
