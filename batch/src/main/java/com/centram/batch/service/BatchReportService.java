package com.centram.batch.service;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.IncidentService;
import com.centram.core.service.ModuleService;
import com.centram.domain.Incident;
import com.centram.domain.IncidentCommunication;
import com.centram.domain.enumarator.IncidentStatus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BatchReportService {
    private static final Logger log = LoggerFactory.getLogger(BatchReportService.class);

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private ModuleService moduleService;

    @Transactional(readOnly = true)
    public void generateIncidentReport(
            LocalDateTime start,
            LocalDateTime end,
            List<String> roleNames,
            BigInteger organisationId,
            String absoluteFilePath
    ) {
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentReport(null, null, null, null, null, IncidentStatus.ALL.ordinal(), start, end, Pageable.unpaged(), true, roleNames, organisationId);
        if (page.getNumberOfElements() == 0) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        List<String> data = new ArrayList<String>();
        List<Incident> incidents = page.getContent();
        try (
                CSVPrinter csvPrinter = CSVFormat.DEFAULT
                        .withQuoteMode(QuoteMode.MINIMAL)
                        .print(new File(absoluteFilePath), StandardCharsets.UTF_8)
        ) {
            data = Arrays.asList(
                    "Incident Number",
                    "Incident Title",
                    "Incident Description",
                    "Category",
                    "Sub Category",
                    "Incident Raised Date",
                    "Priority",
                    "Requested By",
                    "Requester Name",
                    "Assigned to",
                    "Vendor Name",
                    "SLA Overdue?",
                    "Resolve by Date",
                    "Current Status"
            );
            csvPrinter.printRecord(data);
            for (Incident incident : incidents) {
                TreeSet<IncidentCommunication> ascSortedCommunicationSet = new TreeSet<IncidentCommunication>(new Comparator<IncidentCommunication>() {
                    @Override
                    public int compare(IncidentCommunication ic1, IncidentCommunication ic2) {
                        return ic1.getId().compareTo(ic2.getId());
                    }
                });
                for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
                    ascSortedCommunicationSet.add(incidentCommunication);
                }
                incident.setCommunications(ascSortedCommunicationSet);
                data = Arrays.asList(
                        incident.getIncidentNo(),
                        incident.getTitle(),
                        incident.getCommunications().iterator().next().getMessage(),
                        moduleService.getModuleById(incident.getModuleId()).getName(),
                        moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getPriority().getName(),
                        incident.getRaisedUser().getEmployeeId(),
                        incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(),
                        (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "",
                        (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "",
                        incident.getSlaBreached() ? "YES" : "NO",
                        incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getStatus().name()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public void generateEscalatedIncidentReport(
            LocalDateTime start,
            LocalDateTime end,
            List<String> roleNames,
            BigInteger organisationId,
            String absoluteFilePath
    ) {
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentEscalationReport(null, null, null, IncidentStatus.ALL.ordinal(), start, end, Pageable.unpaged(), true, roleNames, organisationId);
        if (page.getNumberOfElements() == 0) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        List<String> data = new ArrayList<String>();
        List<Incident> incidents = page.getContent();
        try (
                CSVPrinter csvPrinter = CSVFormat.DEFAULT
                        .withQuoteMode(QuoteMode.MINIMAL)
                        .print(new File(absoluteFilePath), StandardCharsets.UTF_8)
        ) {
            data = Arrays.asList(
                    "Incident Number",
                    "Incident Title",
                    "Incident Description",
                    "Category",
                    "Sub Category",
                    "Incident Raised Date",
                    "Priority",
                    "Requested By",
                    "Requester Name",
                    "Assigned to",
                    "Vendor Name",
                    "SLA Overdue?",
                    "Resolve by Date",
                    "Current Status",
                    "Escalation Type",
                    "1st Level Escalated Date",
                    "2nd Level Escalated Date"
            );
            csvPrinter.printRecord(data);
            for (Incident incident : incidents) {
                TreeSet<IncidentCommunication> ascSortedCommunicationSet = new TreeSet<IncidentCommunication>(new Comparator<IncidentCommunication>() {
                    @Override
                    public int compare(IncidentCommunication ic1, IncidentCommunication ic2) {
                        return ic1.getId().compareTo(ic2.getId());
                    }
                });
                for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
                    ascSortedCommunicationSet.add(incidentCommunication);
                }
                incident.setCommunications(ascSortedCommunicationSet);
                data = Arrays.asList(
                        incident.getIncidentNo(),
                        incident.getTitle(),
                        incident.getCommunications().iterator().next().getMessage(),
                        moduleService.getModuleById(incident.getModuleId()).getName(),
                        moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getPriority().getName(),
                        incident.getRaisedUser().getEmployeeId(),
                        incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(),
                        (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "",
                        (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "",
                        incident.getSlaBreached() ? "YES" : "NO",
                        incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getStatus().name(),
                        (incident.getEscalation2At() != null) ? "2nd Level" : "1st Level",
                        (incident.getEscalation1At() != null) ? incident.getEscalation1At().format(DateTimeFormatter.ofPattern(dateFormat)) : "",
                        (incident.getEscalation2At() != null) ? incident.getEscalation2At().format(DateTimeFormatter.ofPattern(dateFormat)) : ""
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public void generateReopenedReport(
            LocalDateTime start,
            LocalDateTime end,
            List<String> roleNames,
            BigInteger organisationId,
            String absoluteFilePath
    ) {
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentReopenReport(null, null, null, IncidentStatus.ALL.ordinal(), start, end, Pageable.unpaged(), true, roleNames, organisationId);
        if (page.getNumberOfElements() == 0) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        List<String> data = new ArrayList<String>();
        List<Incident> incidents = page.getContent();
        try (
                CSVPrinter csvPrinter = CSVFormat.DEFAULT
                        .withQuoteMode(QuoteMode.MINIMAL)
                        .print(new File(absoluteFilePath), StandardCharsets.UTF_8)
        ) {
            data = Arrays.asList(
                    "Incident Number",
                    "Incident Title",
                    "Incident Description",
                    "Category",
                    "Sub Category",
                    "Incident Raised Date",
                    "Priority",
                    "Requested By",
                    "Requester Name",
                    "Assigned to",
                    "Vendor Name",
                    "SLA Overdue?",
                    "Resolve by Date",
                    "Current Status",
                    "Reopened Date"
            );
            csvPrinter.printRecord(data);
            for (Incident incident : incidents) {
                TreeSet<IncidentCommunication> ascSortedCommunicationSet = new TreeSet<IncidentCommunication>(new Comparator<IncidentCommunication>() {
                    @Override
                    public int compare(IncidentCommunication ic1, IncidentCommunication ic2) {
                        return ic1.getId().compareTo(ic2.getId());
                    }
                });
                for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
                    ascSortedCommunicationSet.add(incidentCommunication);
                }
                incident.setCommunications(ascSortedCommunicationSet);
                data = Arrays.asList(
                        incident.getIncidentNo(),
                        incident.getTitle(),
                        incident.getCommunications().iterator().next().getMessage(),
                        moduleService.getModuleById(incident.getModuleId()).getName(),
                        moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getPriority().getName(),
                        incident.getRaisedUser().getEmployeeId(),
                        incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(),
                        (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "",
                        (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "",
                        incident.getSlaBreached() ? "YES" : "NO",
                        incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getStatus().name(),
                        (incident.getReopenedAt() != null) ? incident.getReopenedAt().format(DateTimeFormatter.ofPattern(dateFormat)) : ""
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public void generateIncidentAgingReport(
            LocalDateTime start,
            LocalDateTime end,
            List<String> roleNames,
            BigInteger organisationId,
            String absoluteFilePath
    ) {
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentReport(null, null, null, null, null, IncidentStatus.ALL.ordinal(), start, end, Pageable.unpaged(), true, roleNames, organisationId);
        if (page.getNumberOfElements() == 0) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        List<String> data = new ArrayList<String>();
        List<Incident> incidents = page.getContent();
        try (
                CSVPrinter csvPrinter = CSVFormat.DEFAULT
                        .withQuoteMode(QuoteMode.MINIMAL)
                        .print(new File(absoluteFilePath), StandardCharsets.UTF_8)
        ) {
            data = Arrays.asList(
                    "Incident Number",
                    "Incident Title",
                    "Incident Description",
                    "Category",
                    "Sub Category",
                    "Incident Raised Date",
                    "Priority",
                    "Requested By",
                    "Requester Name",
                    "Assigned to",
                    "Vendor Name",
                    "SLA Overdue?",
                    "Resolve by Date",
                    "Current Status",
                    "Age in Days"
            );
            csvPrinter.printRecord(data);
            for (Incident incident : incidents) {
                TreeSet<IncidentCommunication> ascSortedCommunicationSet = new TreeSet<IncidentCommunication>(new Comparator<IncidentCommunication>() {
                    @Override
                    public int compare(IncidentCommunication ic1, IncidentCommunication ic2) {
                        return ic1.getId().compareTo(ic2.getId());
                    }
                });
                for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
                    ascSortedCommunicationSet.add(incidentCommunication);
                }
                incident.setCommunications(ascSortedCommunicationSet);
                data = Arrays.asList(
                        incident.getIncidentNo(),
                        incident.getTitle(),
                        incident.getCommunications().iterator().next().getMessage(),
                        moduleService.getModuleById(incident.getModuleId()).getName(),
                        moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getPriority().getName(),
                        incident.getRaisedUser().getEmployeeId(),
                        incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(),
                        (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "",
                        (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "",
                        incident.getSlaBreached() ? "YES" : "NO",
                        incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        incident.getStatus().name(),
                        Duration.between(incident.getRaisedAt(), LocalDateTime.now()).toDays() + " Days"
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }
}
