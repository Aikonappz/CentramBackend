package com.centram.batch.router;

import com.centram.batch.aggregator.OrganisationAggregator;
import com.centram.common.vo.UserVO;
import com.centram.core.service.*;
import com.centram.domain.Incident;
import com.centram.domain.Organisation;
import com.centram.domain.Role;
import com.centram.domain.User;
import com.centram.domain.enumarator.IncidentStatus;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IncidentAssign extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(IncidentAssign.class);
    private final String interval = "0 0/58 * * * ?";
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private IncidentNotificationService incidentNotificationService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MiscService miscService;

    @Override
    public void configure() throws Exception {
        from("quartzComponent://incidentAssign/roundRobinAssignIncidents?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("incident-assign")
                .enrich("bean:organisationService?method=getRoundRobinOrganisations()", new OrganisationAggregator())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("APP_ROLES", roleService.getRoles(Pageable.unpaged()).getContent());
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "CURRENT_DATE_TIME -> ${header.CURRENT_DATE_TIME}")
                .to("direct:getOrganisationIncidents");

        from("direct:getOrganisationIncidents")
                .routeId("get-organisation-incidents")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //log.info("BODY {}", exchange.getIn().getBody());
                    }
                })
                .loop(simple("${body.size}"))
                .log("Incident Index => ${exchangeProperty.CamelLoopIndex}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Organisation> organisations = (List<Organisation>) exchange.getIn().getBody();
                        Organisation organisation = organisations.get((int) exchange.getProperty("CamelLoopIndex"));
                        log.info("organisation info {}", organisation);
                        List<Role> roleList = (List<Role>) exchange.getIn().getHeader("APP_ROLES");
                        List<String> agentRoles = roleList.stream()
                                .filter(
                                        i -> {
                                            return i.getName().contains("_AGENT_");
                                        }
                                )
                                .map(Role::getName)
                                .collect(Collectors.toList());
                        List<UserVO> users = userService.getUsersByRolesAndOrganisation(agentRoles, organisation.getId());
                        Collections.sort(users);
                        List<Incident> incidents = incidentService.getOpenIncidents(organisation.getId());
                        Integer max = users.size();
                        Integer counter = 0;
                        List<Incident> assignedIncidents = new ArrayList<Incident>();
                        for (Incident incident : incidents) {
                            if (counter < max) {
                                log.info("INCIDENT NO {} -- USER ID {} ", incident.getIncidentNo(), users.get(counter).getId());
                                incident.setAssignedUser(new User(users.get(counter)));
                                incident.setStatus(IncidentStatus.ASSIGNED);
                                assignedIncidents.add(incident);
                                counter++;
                            } else {
                                counter = 0;
                            }
                        }
                        assignedIncidents  = incidentService.saveAll(assignedIncidents);
                        incidentService.assignIncidentViaBatch(assignedIncidents);
                    }
                })
                .end()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.info("ROUND ROBIN ASSIGNMENT COMPLETE!");
                    }
                })
                .end();
    }
}
