package com.centram.batch.router;

import com.centram.batch.aggregator.OrganisationAggregator;
import com.centram.common.vo.CategoryLocationVO;
import com.centram.common.vo.UserVO;
import com.centram.core.service.*;
import com.centram.domain.Incident;
import com.centram.domain.Organisation;
import com.centram.domain.User;
import com.centram.domain.enumarator.IncidentStatus;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Assign extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(Assign.class);
    @Value("${app.round-robin.assign.ticket.cron}")
    private String interval;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UserService userService;
    @Autowired
    private MiscService miscService;
    @Autowired
    private ModuleService moduleService;

    @Override
    public void configure() throws Exception {
        from("quartzComponent://incident/assign?cron=".concat(interval).concat("&stateful=true&durableJob=true&recoverableJob=true"))
                .autoStartup(true)
                .routeId("assign")
                .enrich("bean:organisationService?method=getRoundRobinOrganisations()", new OrganisationAggregator())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("CURRENT_DATE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                })
                .log(LoggingLevel.INFO, "assign started -> ${header.CURRENT_DATE_TIME}")
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
                        List<BigInteger> mods = new LinkedList<>();
                        List<UserVO> users = new ArrayList<UserVO>();
                        List<Incident> incidents = new ArrayList<Incident>();
                        List<Incident> assignedIncidents = new ArrayList<Incident>();
                        Integer max = 0;
                        Integer counter = 0;
                        List<CategoryLocationVO> categoryLocationVOS = moduleService.getCategorySubCategoriesAndLocation(organisation.getId());
                        for (CategoryLocationVO categoryLocationVO : categoryLocationVOS) {
                            mods = new LinkedList<>() {{
                                add(categoryLocationVO.getCategoryId());
                                add(categoryLocationVO.getSubCategoryId());
                            }};
                            incidents = incidentService.getOpenIncidents(categoryLocationVO.getCategoryId(), categoryLocationVO.getSubCategoryId(), categoryLocationVO.getLocationId(), organisation.getId());
                            if (incidents.size() > 0) {
                                users = userService.getAgents(mods, "SOLVE", categoryLocationVO.getLocationId(), organisation.getId());
                                if (users.size() > 0) {
                                    Collections.sort(users);
                                    max = users.size();
                                    counter = 0;
                                    assignedIncidents = new ArrayList<Incident>();
                                    for (Incident incident : incidents) {
                                        if (counter < max) {
                                            log.info("{} assigned to {} ", incident.getIncidentNo(), users.get(counter).getFullName());
                                            incident.setAssignedUser(new User(users.get(counter)));
                                            incident.setStatus(IncidentStatus.ASSIGNED);
                                            assignedIncidents.add(incident);
                                            counter++;
                                        } else {
                                            counter = 0;
                                        }
                                    }
                                    assignedIncidents = incidentService.saveAll(assignedIncidents);
                                    incidentService.assignIncidentViaBatch(assignedIncidents);
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
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
                .log(LoggingLevel.INFO, "assign completed -> ${header.CURRENT_DATE_TIME}")
                .end();
    }
}
