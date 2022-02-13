package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.domain.Incident;
import com.centram.domain.IncidentCommunication;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private ModuleService moduleService;

    @Transactional(readOnly = true)
    public PaginatedList<Organisation> organisationReport(String name, Status status, LicenseType licenseType, Pageable pageable) {
        return organisationService.getOrganisations(name, status, licenseType, pageable);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentReport(String moduleId, String subModuleId, String priorityId, String agingFilter, String raisedUserId, String assignedUserId, String status, Boolean escalated1stLevel, Boolean escalated2ndLevel, Boolean reOpened, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        BigInteger uId = (!raisedUserId.equals("")) ? BigInteger.valueOf(Long.valueOf(raisedUserId)) : null;
        BigInteger aId = (!assignedUserId.equals("")) ? BigInteger.valueOf(Long.valueOf(assignedUserId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentReport(mId, smId, pId, agingFilter, uId, aId, intStatus, escalated1stLevel, escalated2ndLevel, reOpened, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentEscalationReport(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentEscalationReport(mId, smId, pId, intStatus, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentReopenReport(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentReopenReport(mId, smId, pId, intStatus, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentAgingReport(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentReport(mId, smId, pId, null, null, null, intStatus, null, null, null, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadOrganisationReport(String name, Status status, LicenseType licenseType) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<Organisation> page = this.organisationReport(name, status, licenseType, Pageable.unpaged());
        List<Organisation> organisations = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList(
                    "Company Name",
                    "Location",
                    "Addr1",
                    "Addr2",
                    "Key Person 1 Name",
                    "Key Person 1 Email",
                    "Key Person 1 Contact Number",
                    "Key Person 2 Name",
                    "Key Person 2 Email",
                    "Key Person 2 Contact Number",
                    "License Type",
                    "License Start Date",
                    "License End Date",
                    "Status"
            );
            csvPrinter.printRecord(data);
            for (Organisation organisation : organisations) {
                data = Arrays.asList(
                        organisation.getName(),
                        organisation.getCity(),
                        organisation.getAdd1(),
                        organisation.getAdd2(),
                        organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getName() : "",
                        organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getEmail() : "",
                        organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getContactNo() : "",
                        organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getName() : "",
                        organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getEmail() : "",
                        organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getContactNo() : "",
                        organisation.getLicenseType().name(),
                        organisation.getLicenseStart().format(DateTimeFormatter.ofPattern(dateFormat)),
                        organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)),
                        organisation.getStatus().name()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadIncidentReport(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentReport(mId, smId, pId, null, null, null, intStatus, null, null, null, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
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
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream incidentEscalationReportDownload(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentEscalationReport(mId, smId, pId, intStatus, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
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
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream incidentReopenReportDownload(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentReopenReport(mId, smId, pId, intStatus, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
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
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream incidentAgingReportDownload(String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        PaginatedList<Incident> page = incidentService.incidentReport(mId, smId, pId, null, null, null, intStatus, null, null, null, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
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
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }
}
