package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.UatScriptReportDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.utility.Utility;
import com.centram.common.vo.TimeSheetReportVO;
import com.centram.core.repository.ProjectAllocationDetailRepository;
import com.centram.core.repository.ProjectRepository;
import com.centram.core.repository.TimeSheetRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final Utility utility = new Utility();
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private VendorService miscService;
    @Autowired
    private AssetOrderService assetOrderService;
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private ProjectUatService projectUatService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private UserService userService;
    @Autowired
    private TimeSheetRepository timeSheetRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectAllocationDetailRepository projectAllocationDetailRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;


    @Transactional(readOnly = true)
    public PaginatedList<Organisation> organisationReport(String name, Status status, LicenseType licenseType, Pageable pageable) {
        return organisationService.getOrganisations(name, status, licenseType, pageable);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentReport(String incidentType, String moduleId, String subModuleId, String priorityId, String agingFilter, String raisedUserId, String assignedUserId, String status, Boolean allOpen, Boolean allClosed, Boolean reOpened, LocalDateTime start, LocalDateTime end, Pageable pageable) {
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
        return incidentService.incidentReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, agingFilter, uId, aId, intStatus, allOpen, allClosed, reOpened, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentEscalationReport(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentEscalationReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, intStatus, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentReopenReport(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentReopenReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, intStatus, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentAgingReport(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        return incidentService.incidentReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, null, null, null, intStatus, false, false, false, start, end, pageable, false, null, null);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Vendor> vendorReport(String inHouse, VendorType vendorType, Pageable pageable) {
        return miscService.getVendors(inHouse, vendorType, pageable);
    }

    @Transactional(readOnly = true)
    public PaginatedList<AssetOrder> getOrderedAssetsForReport(String orderNo, String status, Pageable pageable) {
        return assetOrderService.getOrderedAssetsForReport(orderNo, status, pageable);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> assetTicketReport(String incidentType, Integer assigned, Integer deallocated, String serialNo, Integer approved, String incidentNo, String moduleId, String subModuleId, String priorityId, String assignedUserId, String raisedUserId, String title, String status, Pageable pageable) {
        return incidentService.assetTicketReport(incidentType, assigned, deallocated, serialNo, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId, title, status, pageable);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadOrganisationReport(String name, Status status, LicenseType licenseType) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<Organisation> page = this.organisationReport(name, status, licenseType, Pageable.unpaged());
        List<Organisation> organisations = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Company Name", "Location", "Addr1", "Addr2", "Key Person 1 Name", "Key Person 1 Email", "Key Person 1 Contact Number", "Key Person 2 Name", "Key Person 2 Email", "Key Person 2 Contact Number", "License Type", "License Start Date", "License End Date", "Status");
            csvPrinter.printRecord(data);
            for (Organisation organisation : organisations) {
                data = Arrays.asList(organisation.getName(), organisation.getCity(), organisation.getAdd1(), organisation.getAdd2(), organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getName() : "", organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getEmail() : "", organisation.getContactPersons().size() > 0 ? organisation.getContactPersons().get(0).getContactNo() : "", organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getName() : "", organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getEmail() : "", organisation.getContactPersons().size() > 1 ? organisation.getContactPersons().get(1).getContactNo() : "", organisation.getLicenseType().name(), organisation.getLicenseStart().format(DateTimeFormatter.ofPattern(dateFormat)), organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)), organisation.getStatus().name());
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadIncidentReport(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
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
        PaginatedList<Incident> page = incidentService.incidentReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, null, null, null, intStatus, false, false, false, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Incident Number", "Incident Title", "Incident Description", "Category", "Sub Category", "Incident Raised Date", "Priority", "Requested By", "Requester Name", "Assigned to", "Vendor Name", "SLA Overdue?", "Resolve by Date", "Current Status", "Hours Estimated", "Time Entry");
            csvPrinter.printRecord(data);
            //String actualTimeTaken = "";
            //Long seconds = null;
            //Long day = null;
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
                //seconds = Duration.between(incident.getCreatedDate(), incident.getModifiedDate()).toSeconds();
                //day = TimeUnit.SECONDS.toDays(seconds);
                //actualTimeTaken = "";
                //actualTimeTaken += ( day > 0) ? String.valueOf(day) + ":" : "00:";
                //actualTimeTaken += (TimeUnit.SECONDS.toHours(seconds) - (day * 24) > 0) ? String.valueOf(TimeUnit.SECONDS.toHours(seconds) - (day * 24)) + ":" : "00:";
                //actualTimeTaken += (TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60) > 0) ? String.valueOf(TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60))  : "00";
                data = Arrays.asList(incident.getIncidentNo(), incident.getTitle(), incident.getCommunications().iterator().next().getMessage().replaceAll("<[^>]*>", ""), moduleService.getModuleById(incident.getModuleId()).getName(), moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getPriority().getName(), incident.getRaisedUser().getEmployeeId(), incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(), (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "", (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "", incident.getSlaBreached() ? "YES" : "NO", incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getStatus().name(), incident.getExpectedTime() != null ? incident.getExpectedTime() + " HRS" : "", !CollectionUtils.isEmpty(incident.getTimeEntries()) ? this.getActualTimeSpentAccordingTimeEntries(incident.getTimeEntries()) : "");
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | ParseException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    private String getActualTimeSpentAccordingTimeEntries(List<TimeEntry> timeEntries) throws ParseException {
        String s = "";
        Integer minute = 0;
        for (int k = 0; k < timeEntries.size(); k++) {
            minute += (int) Float.parseFloat(timeEntries.get(k).getTime().replace(":", "."));
        }
        return utility.convertSecondsToStringDate(minute * 60);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream incidentEscalationReportDownload(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
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
        PaginatedList<Incident> page = incidentService.incidentEscalationReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, intStatus, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Incident Number", "Incident Title", "Incident Description", "Category", "Sub Category", "Incident Raised Date", "Priority", "Requested By", "Requester Name", "Assigned to", "Vendor Name", "SLA Overdue?", "Resolve by Date", "Current Status", "Escalation Type", "1st Level Escalated Date", "2nd Level Escalated Date");
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
                data = Arrays.asList(incident.getIncidentNo(), incident.getTitle(), incident.getCommunications().iterator().next().getMessage().replaceAll("<[^>]*>", ""), moduleService.getModuleById(incident.getModuleId()).getName(), moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getPriority().getName(), incident.getRaisedUser().getEmployeeId(), incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(), (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "", (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "", incident.getSlaBreached() ? "YES" : "NO", incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getStatus().name(), (incident.getEscalation2At() != null) ? "2nd Level" : "1st Level", (incident.getEscalation1At() != null) ? incident.getEscalation1At().format(DateTimeFormatter.ofPattern(dateFormat)) : "", (incident.getEscalation2At() != null) ? incident.getEscalation2At().format(DateTimeFormatter.ofPattern(dateFormat)) : "");
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream incidentReopenReportDownload(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
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
        PaginatedList<Incident> page = incidentService.incidentReopenReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, intStatus, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Incident Number", "Incident Title", "Incident Description", "Category", "Sub Category", "Incident Raised Date", "Priority", "Requested By", "Requester Name", "Assigned to", "Vendor Name", "SLA Overdue?", "Resolve by Date", "Current Status", "Reopened Date");
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
                data = Arrays.asList(incident.getIncidentNo(), incident.getTitle(), incident.getCommunications().iterator().next().getMessage().replaceAll("<[^>]*>", ""), moduleService.getModuleById(incident.getModuleId()).getName(), moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getPriority().getName(), incident.getRaisedUser().getEmployeeId(), incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(), (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "", (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "", incident.getSlaBreached() ? "YES" : "NO", incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getStatus().name(), (incident.getReopenedAt() != null) ? incident.getReopenedAt().format(DateTimeFormatter.ofPattern(dateFormat)) : "");
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream incidentAgingReportDownload(String incidentType, String moduleId, String subModuleId, String priorityId, String status, LocalDateTime start, LocalDateTime end) {
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
        PaginatedList<Incident> page = incidentService.incidentReport(LicenseType.valueOf(incidentType).ordinal(), mId, smId, pId, null, null, null, intStatus, false, false, false, start, end, Pageable.unpaged(), false, null, null);
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Incident Number", "Incident Title", "Incident Description", "Category", "Sub Category", "Incident Raised Date", "Priority", "Requested By", "Requester Name", "Assigned to", "Vendor Name", "SLA Overdue?", "Resolve by Date", "Current Status", "Age in Days");
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
                data = Arrays.asList(incident.getIncidentNo(), incident.getTitle(), incident.getCommunications().iterator().next().getMessage().replaceAll("<[^>]*>", ""), moduleService.getModuleById(incident.getModuleId()).getName(), moduleService.getModuleById(incident.getSubModuleId()).getName(), incident.getRaisedAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getPriority().getName(), incident.getRaisedUser().getEmployeeId(), incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(), (incident.getAssignedUser() != null) ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "", (incident.getAssignedUser() != null && incident.getAssignedUser().getVendor() != null) ? incident.getAssignedUser().getVendor().getName() : "", incident.getSlaBreached() ? "YES" : "NO", incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getStatus().name(), Duration.between(incident.getRaisedAt(), LocalDateTime.now()).toDays() + " Days");
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream vendorReportDownload(String inHouse, VendorType vendorType) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<Vendor> page = miscService.getVendors(inHouse, vendorType, Pageable.unpaged());
        List<Vendor> vendors = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Name", "Contact Name", "Contact Email", "Contact Number", "Contact Address", "Product Categories", "Product Sub Categories");
            csvPrinter.printRecord(data);
            List<BigInteger> moduleIds = new ArrayList<BigInteger>();
            List<BigInteger> subModuleIds = new ArrayList<BigInteger>();
            List<String> modules = new ArrayList<String>();
            List<String> subModules = new ArrayList<String>();
            for (Vendor vendor : vendors) {
                moduleIds = vendor.getVendorModules().stream().map(VendorModule::getModuleId).collect(Collectors.toList());
                subModuleIds = vendor.getVendorModules().stream().map(VendorModule::getSubModuleId).collect(Collectors.toList());
                modules = new ArrayList<String>();
                subModules = new ArrayList<String>();
                for (BigInteger m : moduleIds) {
                    modules.add(moduleService.getModuleById(m).getName());
                }
                for (BigInteger sm : subModuleIds) {
                    subModules.add(moduleService.getModuleById(sm).getName());
                }
                data = Arrays.asList(vendor.getName(), vendor.getContactName(), vendor.getContactEmail(), vendor.getContactNumber(), vendor.getContactAddress(), String.join(",", modules), String.join(",", subModules));
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream assetTicketReportDownload(String incidentType, Integer assigned, Integer deallocated, String serialNo, Integer approved, String incidentNo, String moduleId, String subModuleId, String priorityId, String assignedUserId, String raisedUserId, String title, String status) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<Incident> page = incidentService.assetTicketReport(incidentType, assigned, deallocated, serialNo, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId, title, status, Pageable.unpaged());
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Request No.", "Ticket Type", "Product Category", "Product Sub Category", "Priority", "Request Detail", "Current Status", "Requester ID", "Requester Name", "Requester Email", "Requester Department", "Requester Location", "Requester Organization", "Requester Manager", "Approved by Manager?", "Approver ID", "Approver Name", "Approver Email", "Asset Serial No", "Allocated On", "Deallocated On", "Agent Name", "Agent Email", "First Level Escalation?", "Second Level Escalation?", "Requested Date", "SLA Due Date", "Closed Date");
            csvPrinter.printRecord(data);
            User manager = null;
            for (Incident incident : incidents) {
                if (incident.getRaisedUser().getManagerId() != null)
                    manager = new User(userService.getUserById(incident.getRaisedUser().getManagerId()));
                data = Arrays.asList(incident.getIncidentNo(), incident.getTicketType(), incident.getModuleName(), incident.getSubModuleName(), incident.getPriority().getName(), incident.getTitle(), incident.getStatus().name(), incident.getRaisedUser().getEmployeeId(), incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(), incident.getRaisedUser().getEmail(), incident.getRaisedUser().getDepartment().getName(), incident.getRaisedUser().getLocation().getName(), incident.getRaisedUser().getLocation().getOfficeName(), manager != null ? manager.getFirstName() + " " + manager.getLastName() : "NA", incident.getApprovalRequired() ? incident.getAssetApproved() && incident.getFeedbackProvided() ? "YES" : "NO" : "NA", incident.getApprovalRequired() ? manager != null ? manager.getEmployeeId() : "NA" : "NA", incident.getApprovalRequired() ? manager != null ? manager.getFirstName() + " " + manager.getLastName() : "NA" : "NA", incident.getApprovalRequired() ? manager != null ? manager.getEmail() : "NA" : "NA", incident.getAsset() != null ? incident.getAsset().getSerialNo() : "NA", incident.getAllocationDateTime() != null ? incident.getAllocationDateTime().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA", incident.getDeallocationDateTime() != null ? incident.getDeallocationDateTime().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA", incident.getAssignedUser() != null ? incident.getAssignedUser().getFirstName() + " " + incident.getAssignedUser().getLastName() : "NA", incident.getAssignedUser() != null ? incident.getAssignedUser().getEmail() : "NA", incident.getEscalation1At() != null ? "YES" : "NO", incident.getEscalation2At() != null ? "YES" : "NO", incident.getCreatedDate().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateFormat)), incident.getStatus() == IncidentStatus.CLOSED ? incident.getModifiedDate().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA");
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream orderReportDownload(String orderNo, String status) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<AssetOrder> page = assetOrderService.getOrderedAssetsForReport(orderNo, status, Pageable.unpaged());
        List<AssetOrder> assetOrders = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Order No", "Department Name", "Organization Name", "Product Category", "Product Sub-Category", "Model", "Quantity", "Currency", "Total Cost", "Within Budget?", "Available Budget Amount", "Additional Amount", "Vendor Name", "Purchase Type", "Existing Agreement?", "Agreement Valid Till", "Duration", "Other details", "Requester ID", "Requester Name", "Approver1 ID", "Approver1 Name", "Approver1 Email", "Approver1 Status", "Approver1 Feedback", "Approver2 Feedback Date", "Approver2 ID", "Approver2 Name", "Approver2 Email", "Approver2 Status", "Approver2 Feedback", "Approver2 Feedback Date");
            csvPrinter.printRecord(data);
            for (AssetOrder assetOrder : assetOrders) {
                data = Arrays.asList(assetOrder.getOrderNo(), assetOrder.getDepartment() == null ? "NA" : assetOrder.getDepartment().getName(), assetOrder.getLocation() == null ? "NA" : assetOrder.getLocation().getOfficeName(), assetOrder.getModuleName(), assetOrder.getSubModuleName(), assetOrder.getModel(), String.valueOf(assetOrder.getQuantity()), assetOrder.getCurrency(), String.valueOf(assetOrder.getTotalAmount()), assetOrder.getWithinBudget() ? "YES" : "NO", !assetOrder.getWithinBudget() ? assetOrder.getLimitAmount().toString() : "NA", !assetOrder.getWithinBudget() ? assetOrder.getExtraAmount().toString() : "NA", assetOrder.getVendor() != null ? assetOrder.getVendor().getName() : "Others", assetOrder.getPurchaseType().name(), assetOrder.getExistingAgreement() ? "YES" : "NO", assetOrder.getVendor() != null ? assetOrder.getAgreementEndAt().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA", assetOrder.getPurchaseType() == PurchaseType.RENTED ? assetOrder.getRentDuration() != null && assetOrder.getRentDuration() != "" ? assetOrder.getRentDuration() : "NA" : "NA", assetOrder.getOtherDetails() != null && !assetOrder.getOtherDetails().equals("") ? assetOrder.getOtherDetails() : "NA", assetOrder.getRaisedUser().getEmployeeId(), assetOrder.getRaisedUser().getFirstName() + " " + assetOrder.getRaisedUser().getLastName(), assetOrder.getApproverUser1().getEmployeeId(), assetOrder.getApproverUser1().getFirstName() + " " + assetOrder.getApproverUser1().getLastName(), assetOrder.getApproverUser1().getEmail(), assetOrder.getApproverUser1FeedbackAt() != null ? (assetOrder.getApprovedUser1() ? "Approved" : "Rejected") : "Feedback Not Provided Yet", assetOrder.getApproverUser1FeedbackAt() != null ? assetOrder.getApproverUser1Comment() : "NA", assetOrder.getApproverUser1FeedbackAt() != null ? assetOrder.getApproverUser1FeedbackAt().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA", assetOrder.getApproverUser2().getEmployeeId(), assetOrder.getApproverUser2().getFirstName() + " " + assetOrder.getApproverUser2().getLastName(), assetOrder.getApproverUser2().getEmail(), assetOrder.getApproverUser2FeedbackAt() != null ? (assetOrder.getApprovedUser2() ? "Approved" : "Rejected") : "Feedback Not Provided Yet", assetOrder.getApproverUser2FeedbackAt() != null ? assetOrder.getApproverUser2Comment() : "NA", assetOrder.getApproverUser2FeedbackAt() != null ? assetOrder.getApproverUser2FeedbackAt().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA");
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream assetAssignmentReportDownload(String incidentType, Integer assigned, Integer deallocated, String serialNo, Integer approved, String incidentNo, String moduleId, String subModuleId, String priorityId, String assignedUserId, String raisedUserId, String title, String status) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<Incident> page = incidentService.assetTicketReport(incidentType, assigned, deallocated, serialNo, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId, title, status, Pageable.unpaged());
        List<Incident> incidents = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Asset Serial No.", "Allocated to: ID", "Allocated to: Name", "Allocated to: Email", "Department", "Location", "Organization", "Allocated On", "Deallocated On", "Status");
            csvPrinter.printRecord(data);
            User manager = null;
            for (Incident incident : incidents) {
                if (incident.getRaisedUser().getManagerId() != null)
                    manager = new User(userService.getUserById(incident.getRaisedUser().getManagerId()));
                data = Arrays.asList(incident.getAsset() != null ? incident.getAsset().getSerialNo() : "NA", incident.getRaisedUser().getEmployeeId(), incident.getRaisedUser().getFirstName() + " " + incident.getRaisedUser().getLastName(), incident.getRaisedUser().getEmail(), incident.getRaisedUser().getDepartment().getName(), incident.getRaisedUser().getLocation().getName(), incident.getRaisedUser().getLocation().getOfficeName(), incident.getAllocationDateTime() != null ? incident.getAllocationDateTime().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA", incident.getDeallocationDateTime() != null ? incident.getDeallocationDateTime().format(DateTimeFormatter.ofPattern(dateFormat)) : "NA", incident.getTicketType());
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    public PaginatedList<UatScriptReportDTO> uatScriptReport(LocalDateTime start, LocalDateTime end, Technology technology, String moduleId, String subModuleId, String projectId, String projectUatId, String projectUatScriptId, String uploadedByUserId, String status, Pageable pageable) {
        //int tech = technology.ordinal();
        BigInteger mId = (!moduleId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(moduleId)) : null;
        BigInteger smId = (!subModuleId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(subModuleId)) : null;
        BigInteger pId = (!projectId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(projectId)) : null;
        BigInteger pUatId = (!projectUatId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(projectUatId)) : null;
        BigInteger pUatScriptId = (!projectUatScriptId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(projectUatScriptId)) : null;
        BigInteger pUplId = (!uploadedByUserId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(uploadedByUserId)) : null;
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        Page<UatScriptReportDTO> page = projectUatService.uatScriptReport(start, end, technology, mId, smId, pId, pUatId, pUatScriptId, pUplId, pageable);
        page.getContent().forEach(i -> {
            Module module = moduleService.getModuleById(i.getModuleId());
            i.setModule(module.getCustomerModuleName());
            module = moduleService.getModuleById(i.getSubModuleId());
            i.setSubModule(module.getCustomerModuleName());
        });
        List<UatScriptReportDTO> uatScriptReportDTOS = new LinkedList<UatScriptReportDTO>();
        uatScriptReportDTOS = page.getContent();
        if (!status.isEmpty()) {
            if (status.equalsIgnoreCase("completed")) {
                uatScriptReportDTOS = uatScriptReportDTOS.stream().filter(i -> {
                    return i.getStatus().equalsIgnoreCase("Completed");
                }).collect(Collectors.toList());
            } else if (status.equalsIgnoreCase("notStarted")) {
                uatScriptReportDTOS = uatScriptReportDTOS.stream().filter(i -> {
                    return i.getStatus().equalsIgnoreCase("Not Started");
                }).collect(Collectors.toList());
            } else if (status.equalsIgnoreCase("inProgress")) {
                uatScriptReportDTOS = uatScriptReportDTOS.stream().filter(i -> {
                    return i.getStatus().equalsIgnoreCase("In Progress");
                }).collect(Collectors.toList());
            }
        }
        if (!pageable.isUnpaged()) {
            return new PaginatedList<UatScriptReportDTO>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), page.getPageable().getOffset(), page.getPageable().getPageNumber(), page.getPageable().getPageSize(), uatScriptReportDTOS);
        } else {
            return new PaginatedList<UatScriptReportDTO>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), 0, 1, 1, uatScriptReportDTOS);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream uatScriptReportDownload(LocalDateTime start, LocalDateTime end, Technology technology, String moduleId, String subModuleId, String projectId, String projectUatId, String projectUatScriptId, String uploadedByUserId, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        PaginatedList<UatScriptReportDTO> page = this.uatScriptReport(start, end, technology, moduleId, subModuleId, projectId, projectUatId, projectUatScriptId, uploadedByUserId, status, Pageable.unpaged());
        List<UatScriptReportDTO> uatScriptReportDTOS = page.getContent();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList("Sl.No", "Technology", "Module", "Sub Module", "Project Type", "Project Name", "Project Code", "Project Manager", "Consultant Responsible", "Test Case ID", "Test Case Description", "Script Detail", "Status", "Currently with", "Age (days)");
            csvPrinter.printRecord(data);
            User manager = null;
            int c = 0;
            for (UatScriptReportDTO uatScriptReportDTO : uatScriptReportDTOS) {
                data = Arrays.asList(String.valueOf(++c), uatScriptReportDTO.getTechnology(), uatScriptReportDTO.getModule(), uatScriptReportDTO.getSubModule(), uatScriptReportDTO.getProjectType(), uatScriptReportDTO.getProjectName(), uatScriptReportDTO.getProjectCode(), uatScriptReportDTO.getProjectManager(), uatScriptReportDTO.getConsultantResponsible(), uatScriptReportDTO.getTestCaseId(), uatScriptReportDTO.getTestCaseDescription(), uatScriptReportDTO.getCustomerUserEmail() + "-" + uatScriptReportDTO.getScriptName(), uatScriptReportDTO.getStatus(), uatScriptReportDTO.getCurrentlyWith(), String.valueOf(uatScriptReportDTO.getAge()));
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    public PaginatedList<ProjectUat> uatReport(LoggedInUser loggedInUser, LocalDateTime start, LocalDateTime end, Technology technology, String moduleId, String subModuleId, String projectId, String projectUatId, String uploadedByUserId, String status, Pageable pageable) {
        //int tech = technology.ordinal();
        BigInteger mId = (!moduleId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(moduleId)) : null;
        BigInteger smId = (!subModuleId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(subModuleId)) : null;
        BigInteger pId = (!projectId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(projectId)) : null;
        BigInteger pUatId = (!projectUatId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(projectUatId)) : null;
        BigInteger pUplId = (!uploadedByUserId.isEmpty()) ? BigInteger.valueOf(Long.parseLong(uploadedByUserId)) : null;
        if (start == null || end == null) {
            end = LocalDateTime.now();
            start = end.minusDays(90);
        }
        Page<ProjectUat> page = projectUatService.uatReport(loggedInUser, start, end, technology.ordinal(), mId, smId, pId, pUatId, pUplId, status, pageable);
        page.getContent().forEach(i -> {
            Module module = moduleService.getModuleById(i.getModuleId());
            i.setModuleName(module.getCustomerModuleName());
            module = moduleService.getModuleById(i.getSubModuleId());
            i.setSubModuleName(module.getCustomerModuleName());
            for (ProjectUatScript projectUatScript : i.getProjectUatScripts()) {
                i.getActionDetails().put(projectUatScript.getCustomerUser().getEmail(), projectUatScript.getTestScriptName().concat("/").concat(projectUatScript.getUatComplete() ? "Completed" : "Pending"));
            }
            if (i.getUatCycleComplete()) {
                i.setStatus("Completed");
            } else if (this.getUatCycleStatus(i) == -1) {
                i.setStatus("Not Started");
            } else if (this.getUatCycleStatus(i) == 0) {
                i.setStatus("In Progress");
            }
        });
        return new PaginatedList<ProjectUat>(page);
    }

    private Integer getUatCycleStatus(ProjectUat projectUat) {
        for (ProjectUatScript projectUatScript : projectUat.getProjectUatScripts()) {
            if (projectUatScript.getUatComplete()) {
                return 0;
            }
        }
        return -1;
    }

    /**
     * @param loggedInUser
     * @param start
     * @param end
     * @param approved
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<TimeSheetReportVO> timesheetReport(LoggedInUser loggedInUser, LocalDateTime start, LocalDateTime end, Boolean approved, List<BigInteger> suppliedUserIds, Boolean includeAll, Pageable pageable) {
        LocalDate startDate;
        LocalDate endDate;
        if (start == null || end == null) {
            endDate = LocalDate.now(ZoneId.systemDefault());
            startDate = endDate.minusDays(90);
        } else {
            startDate = start.toLocalDate();
            endDate = end.toLocalDate();
        }
        List<BigInteger> userIds = Collections.emptyList();
        if (CollectionUtils.isEmpty(suppliedUserIds)) {
            List<String> roleNames = roleService.getByIds(loggedInUser.getRoles());
            List<Project> projects;
            List<ProjectAllocationDetail> projectAllocationDetails;
            List<BigInteger> projectIds;
            String emailExp = Stream.of(loggedInUser.getEmail()).map(String::valueOf).collect(Collectors.joining("|"));
            List<User> users;
            if (roleNames.contains("ORG_ADMIN_PROJECT") || roleNames.contains("ORG_ADMIN")) {
                users = userRepository.getUsersByRoleNames(Collections.singleton("ORG_USER_PROJECT").stream().map(String::valueOf).collect(Collectors.joining("|")), loggedInUser.getOrganisationId());
                userIds = users.stream().map(User::getId).collect(Collectors.toList());
            } else if (roleNames.contains("ORG_PROJECT_TIMESHEET_APPROVER")) {
                projects = projectRepository.getProjectByProjectApproversEmail(emailExp, loggedInUser.getOrganisationId());
                projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());
                projectAllocationDetails = projectAllocationDetailRepository.getUsersProjects(projectIds);
                users = projectAllocationDetails.stream().map(ProjectAllocationDetail::getUser).collect(Collectors.toList());
                userIds = users.stream().map(User::getId).collect(Collectors.toList());
            } else if (roleNames.contains("ORG_USER_PROJECT")) {
                projectAllocationDetails = projectAllocationDetailRepository.getUserProjects(loggedInUser.getUserId());
                users = projectAllocationDetails.stream().map(ProjectAllocationDetail::getUser).collect(Collectors.toList());
                userIds = users.stream().map(User::getId).collect(Collectors.toList());
            }
        } else {
            userIds = suppliedUserIds;
        }
        Page<TimeSheetEntry> page = timeSheetRepository.getTimeSheetsByUserIdsAndDateRange(userIds, approved, includeAll, startDate, endDate, pageable);
        List<TimeSheetReportVO> timeSheetReportVOS = new LinkedList<TimeSheetReportVO>();
        page.getContent().forEach(i -> {
            timeSheetReportVOS.add(new TimeSheetReportVO(i));
        });
        if (!pageable.isUnpaged()) {
            return new PaginatedList<TimeSheetReportVO>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), page.getPageable().getOffset(), page.getPageable().getPageNumber(), page.getPageable().getPageSize(), timeSheetReportVOS);
        } else {
            return new PaginatedList<TimeSheetReportVO>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), 0, 1, 1, timeSheetReportVOS);
        }
    }
}
