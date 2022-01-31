package com.centram.batch.router;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JunkCleaner extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(JunkCleaner.class);
    private final Integer cutOff = (7 * 24 * 60 * 60 * 1000);
    @Value("${app.cleaner.junk.cron}")
    private String interval;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.report.path}")
    private String appReportPath;

    @Override
    public void configure() throws Exception {
        from("quartzComponent://cleaner/junkCleaner?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("junk-cleaner")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "junk-cleaner started -> ${header.CURRENT_DATE_TIME}")
                .to("direct:processGeneratedReportJunk");

        from("direct:processGeneratedReportJunk")
                .routeId("process-generated-report-file-junk")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<File> files = Files.list(Paths.get(appReportPath))
                                .filter(Files::isRegularFile)
                                .map(Path::toFile)
                                .collect(Collectors.toList());
                        Long diff = 0L;
                        for (File fl : files) {
                            diff = new Date().getTime() - fl.lastModified();
                            if (diff > cutOff) {
                                fl.delete();
                            }
                        }
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "junk-cleaner/process-generated-report-file-junk completed -> ${header.CURRENT_DATE_TIME}")
                .end();

    }
}
