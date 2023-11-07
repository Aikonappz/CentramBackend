package com.centram.batch.router;

import com.centram.core.service.*;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.LicenseType;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigInteger;
import java.util.*;

public class EmailDistributionListListener extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(EmailDistributionListListener.class);

    private final String interval = "0 0/3 * * * ?";
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.report.path}")
    private String appReportPath;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PriorityService priorityService;

    @Autowired
    private IncidentService incidentService;

    @Override
    public void configure() throws Exception {
        /*from("{{support.mail.protocol}}://{{support.mail.host}}:{{support.mail.port}}?username={{support.mail.username}}&password={{support.mail.password}}&unseen=true&delete=false&peek=false&closeFolder=false&disconnect=false&folderName=INBOX&searchTerm.subject=Issue Report")
                .log(LoggingLevel.INFO, "=================== organization-license-expiry job started ===================")
                .autoStartup(true)
                .routeId("test")
                *//*.process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody("Category ==> IT Support\n" +
                                "Sub Category ==> Mouse\n" +
                                "Priority ==> P4\n" +
                                "Watch List ==> abc@gmail.com,bcd@gmai.com\n" +
                                "Title ==> My Desktop Mouse Not Working.\n" +
                                "Description ==> My Desktop Mouse Not Working.");
                        log.info(exchange.getIn().getBody().toString());
                    }
                })*//*
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
                            List<String> keys = Arrays.asList("Category", "Sub Category", "Priority", "Watch List", "Title", "Description");
                            for (String s : keys) {
                                if (!dataAttributes.containsKey(s)) {
                                    log.error("Key ==> {} not exist!", s);
                                    return;
                                }
                            }
                            String email = exchange.getIn().getHeader("From").toString();
                            email = email.substring(email.indexOf("<") + 1);
                            email = email.substring(0, email.indexOf(">"));
                            List<String> watchListEmail = new ArrayList<String>();
                            User user = userService.findUserByEmail(email);
                            if (user == null) {
                                log.error("User {} not exist or active!", email);
                                return;
                            }
                            Module category = moduleService.getModuleByCustomerModuleName(LicenseType.INCIDENT, String.valueOf(dataAttributes.get("Category")));
                            if (category == null) {
                                log.error("Not a valid Category {}!", dataAttributes.get("Category"));
                                return;
                            }
                            Module subCategory = moduleService.getModuleByCustomerModuleName(LicenseType.INCIDENT, String.valueOf(dataAttributes.get("Sub Category")));
                            if (subCategory == null) {
                                log.error("Not a valid Sub Category {}!", dataAttributes.get("Sub Category"));
                                return;
                            }
                            List<Permission> permissions = permissionService.getPermissionByRoleIds(user.getRoles());
                            if (!hasPermissionToRaiseIncident(category.getId(), subCategory.getId(), permissions)) {
                                log.error("User don't have valid permission to raise incident!");
                                return;
                            }
                            Priority priority = priorityService.getPriorityByNameAndAccountIdAndOrganisationId(
                                    String.valueOf(dataAttributes.get("Priority")),
                                    user.getAccount().getId(),
                                    user.getOrganisation().getId()
                            );
                            if (priority == null) {
                                log.error("Not a valid Priority {}!", dataAttributes.get("Priority"));
                                return;
                            }
                            if (!String.valueOf(dataAttributes.get("Watch List")).trim().equalsIgnoreCase("")) {
                                String[] emails = String.valueOf(dataAttributes.get("Watch List")).split(",");
                                watchListEmail = Arrays.asList(emails);
                            }
                            Incident incident = new Incident();
                            incident.setCategory(category);
                            incident.setSubCategory(subCategory);
                            incident.setTitle(String.valueOf(dataAttributes.get("Title")));
                            incident.setPriority(priority);
                            incident.setRaisedUser(user);
                            incident.setModuleId(category.getId());
                            incident.setSubModuleId(subCategory.getId());
                            incident.setStatus(IncidentStatus.OPEN);
                            incident.setIncidentType(LicenseType.INCIDENT);
                            incident.setOrganisation(user.getOrganisation());
                            if (watchListEmail != null && watchListEmail.size() > 0) {
                                incident.setWatchList(watchListEmail);
                            }
                            IncidentCommunication incidentCommunication = new IncidentCommunication();
                            incidentCommunication.setIncident(incident);
                            incidentCommunication.setCommunicatedBy(user);
                            incidentCommunication.setMessage(String.valueOf(dataAttributes.get("Description")));
                            //incidentCommunication.setAttachments();
                            incident.setCommunications(
                                    new HashSet<IncidentCommunication>() {{
                                        add(incidentCommunication);
                                    }}
                            );
                            incidentService.createFromEmail(incident);
                        } else {
                            log.error("Category=> {},Sub Category=> {}", dataAttributes.get("Category"), dataAttributes.get("Sub Category"));
                        }
                    }
                })
                .log(LoggingLevel.INFO, "junk-cleaner started -> ${header.CURRENT_DATE_TIME}")
                .end();*/
    }

    private Boolean hasPermissionToRaiseIncident(BigInteger moduleId, BigInteger subModuleId, List<Permission> permissions) {
        return permissions.stream()
                .filter(i -> {
                    return (
                            i.getModule().getId().compareTo(moduleId) == 0 && i.getAction().getName().equals("RAISE INCIDENT")
                                    ||
                                    i.getModule().getId().compareTo(subModuleId) == 0 && i.getAction().getName().equals("RAISE INCIDENT") && i.getModule().getParentModuleId().compareTo(moduleId) == 0
                    );
                }).count() > 1;
    }

}
