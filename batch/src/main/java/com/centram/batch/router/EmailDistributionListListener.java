package com.centram.batch.router;

import com.centram.core.service.ModuleService;
import com.centram.domain.Module;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailDistributionListListener extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(EmailDistributionListListener.class);

    private final String interval = "0 0/3 * * * ?";
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.report.path}")
    private String appReportPath;

    @Autowired
    private ModuleService moduleService;

    @Override
    public void configure() throws Exception {
        from("{{support.mail.protocol}}://{{support.mail.host}}:{{support.mail.port}}?username={{support.mail.username}}&password={{support.mail.password}}&unseen=true&delete=false&peek=false&closeFolder=false&disconnect=false&folderName=INBOX&searchTerm.subject=Issue Report")
                .log(LoggingLevel.INFO, "=================== organization-license-expiry job started ===================")
                .autoStartup(true)
                .routeId("test")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Exchange ex = exchange;
                        String body = exchange.getIn().getBody(String.class);
                        Map<String, Object> dataAttributes = new HashMap<String, Object>();
                        if (body.trim().length() > 0) {
                            log.info("exchange body {}", body);
                            String[] lines = body.split(System.getProperty("line.separator"));
                            String[] line;
                            String desc;
                            for (int k = 0; k < lines.length; k++) {
                                if (lines[k].contains("==>")) {
                                    line = lines[k].split("==>");
                                    dataAttributes.put(line[0].trim(), line[1].trim());
                                } else continue;
                            }
                            List<String> keys = Arrays.asList("Category", "Sub Category", "Priority", "Title", "Description");
                            for (String s : keys) {
                                if (!dataAttributes.containsKey(s)) {
                                    log.error("Key ==> {} not exist!",s);
                                }
                            }

                            //Module category = moduleService.getModuleByCustomerModuleName(String.valueOf(dataAttributes.get("Category")));
                            //Module subCategory = moduleService.getModuleByCustomerModuleName(String.valueOf(dataAttributes.get("Sub Category")));
                        }else{
                            log.error("Body ==> {} not valid for incident creation!",body);
                        }
                    }
                })
                .log(LoggingLevel.INFO, "junk-cleaner started -> ${header.CURRENT_DATE_TIME}")
                .end();
    }


}
