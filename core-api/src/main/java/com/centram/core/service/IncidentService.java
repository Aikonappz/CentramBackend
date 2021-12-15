package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.UserVO;
import com.centram.common.vo.WorkingDay;
import com.centram.core.repository.IncidentRepository;
import com.centram.domain.*;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.centram.common.utility.Utility.incidentNo;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
public class IncidentService {
    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);
    @Value("${app.default.incident.prefix}")
    public String appDefaultIncidentPrefix;
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private MiscService miscService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private HolidayCalenderService holidayCalenderService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private AppEmailService appEmailService;

    @Value("${date.time.format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String dateTimeFormat;

    @Value("${date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Transactional(readOnly = true)
    public PaginatedList<Incident> getIncomingIncidents(String incidentNo, String moduleId, String subModuleId, String priorityId, String assignedUserId, String title, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = loggedInUser.getAuthorities().stream()
                .map(i -> i.getAuthority())
                .collect(Collectors.toList());
        List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
        List<BigInteger> modSubModIds = permissions.stream()
                .filter(i -> !i.getModule().getAppModule())
                .map(i -> i.getModule().getId())
                .collect(Collectors.toList());
        BigInteger uId = (!assignedUserId.equals("")) ? BigInteger.valueOf(Long.valueOf(assignedUserId)) : null;
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncomingIncidents(
                incidentNo, mId, smId, pId, uId, modSubModIds, title, intStatus, pageable
        ));
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> getIncidents(String incidentNo, String title, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncidents(loggedInUser.getUserId(), incidentNo, title, intStatus, pageable));
    }

    @Transactional(readOnly = true)
    public Incident getIncidentById(BigInteger incidentId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Incident> incident = incidentRepository.findById(incidentId);
        if (!incident.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        Set<IncidentCommunication> communicationSet = new HashSet<IncidentCommunication>();
        for (IncidentCommunication incidentCommunication : incident.get().getCommunications()) {
            incidentCommunication.setAttachments(mediaService.getMediaFiles(incidentCommunication.getId(), EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION));
            communicationSet.add(incidentCommunication);
        }
        incident.get().setCommunications(communicationSet);
        return incident.get();
    }

    @Transactional(readOnly = false)
    public void assignIncidents(List<BigInteger> ids, BigInteger userId) {
        incidentRepository.assignIncidents(userId, LocalDateTime.now(), ids);
    }

    @Transactional(readOnly = false)
    public void changeStatus(String status, List<BigInteger> ids) {
        incidentRepository.changeStatus(IncidentStatus.valueOf(status), LocalDateTime.now(), ids);
    }

    @Transactional
    public Incident save(Incident incident) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Incident raisedIncident = null;
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        if (incident.getId() == null) {
            incident.setRaisedUser(new User(userVO.getVersion(), userVO.getId()));
            incident.setRaisedAt(LocalDateTime.now());
            Setting setting = organisationService.getOrganisationSettings();
            String prefix = (setting != null && setting.getIncidentPrefix() != null) ? setting.getIncidentPrefix() : appDefaultIncidentPrefix;
            incident.setIncidentNo(incidentNo(prefix));
            /*fetch location*/
            Location location = locationService.getById(loggedInUser.getLocationId());
            /*fetch priority*/
            Priority priority = priorityService.getById(incident.getPriority().getId());
            /*prepare holiday List*/
            ZonedDateTime raiseDateTime = ZonedDateTime.now();
            List<Holiday> holidays = new ArrayList<Holiday>();
            List<Holiday> currentYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().toString());
            List<Holiday> nextYearHolidays = new ArrayList<Holiday>();
            if (raiseDateTime.getMonth() == Month.DECEMBER) {
                nextYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().plusYears(1).toString());
            }
            holidays = this.mergeHolidays(currentYearHolidays, nextYearHolidays);
            /*prepare holiday List*/
            raiseDateTime = raiseDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone()));
            incident.setSlaAt(this.getSlADateTime(raiseDateTime, priority.getSla(), location.getOpsStartTime(), location.getOpsEndTime(), holidays));
        }
        Set<IncidentCommunication> communicationSet = new HashSet<IncidentCommunication>();
        for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
            if (incidentCommunication.getId() == null) {
                incidentCommunication.setCommunicatedBy(new User(userVO.getVersion(), userVO.getId()));
            }
            incidentCommunication.setIncident(incident);
            communicationSet.add(incidentCommunication);
        }
        incident.setCommunications(communicationSet);
        raisedIncident = incidentRepository.save(incident);
        //notify respected user
        miscService.notifyIncidentUpdate(new Incident());
        return raisedIncident;
    }

    /**
     * calculate sla
     *
     * @param raisedDateTime
     * @param hour
     * @param opsStartTime
     * @param opsEndTime
     * @param holidays
     * @return
     */
    private LocalDateTime getSlADateTime(ZonedDateTime raisedDateTime, String hour, LocalTime opsStartTime, LocalTime opsEndTime, List<Holiday> holidays) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long hours = Long.valueOf(hour.split((":"))[0]);
        Long minutes = Long.valueOf(hour.split((":"))[1]);
        log.info("hours => {}, minutes => {}", hours, minutes);
        Long minuteToAdd = (hours * 60) + minutes;
        log.info(" converted minutes => {}", minuteToAdd);
        List<WorkingDay> workingDays = this.getWorkingDays(raisedDateTime, holidays, opsStartTime, opsEndTime);
        Collections.sort(workingDays);
        ZonedDateTime slaDayDateTime = raisedDateTime;
        ZonedDateTime slaDayEndTime = null;
        Duration duration = null;
        if (this.isHoliday(raisedDateTime.toLocalDate(), holidays)) {
            Optional<WorkingDay> nextWorkingDayOptional = this.nextWorkingDay(raisedDateTime.toLocalDate(), workingDays);
            if (nextWorkingDayOptional.isPresent()) {
                slaDayDateTime = nextWorkingDayOptional.get()
                        .getDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
            } else {
                throw new AppException(GenericErrorCode.HOLIDAY_CALENDER_MASTER_DATA_MISSING);
            }
        }
        for (WorkingDay workingDay : workingDays) {
            if (slaDayDateTime.toLocalDate().compareTo(workingDay.getDate()) == 0) {
                slaDayDateTime = slaDayDateTime.plusMinutes(minuteToAdd).toLocalDateTime().atZone(ZoneId.of(loggedInUser.getTimeZone()));
                if (!this.checkIsValidSLA(slaDayDateTime, workingDays)) {
                    slaDayEndTime = slaDayDateTime.with(opsEndTime);
                    duration = Duration.between(slaDayEndTime, slaDayDateTime);
                    minuteToAdd = duration.toMinutes();
                    slaDayDateTime = slaDayDateTime.plusDays(1).toLocalDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
                }
            }
        }
        log.info("UTC {}", slaDayDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        log.info("LOCAL {}", slaDayDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone())).toLocalDateTime());
        return slaDayDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * check whether a datetime is working day and fall under shift time
     *
     * @param dateTime
     * @param workingDays
     * @return
     */
    private Boolean checkIsValidSLA(ZonedDateTime dateTime, List<WorkingDay> workingDays) {
        LocalDate date = dateTime.toLocalDate();
        for (WorkingDay workingDay : workingDays) {
            if (workingDay.getDate().compareTo(date) == 0) {
                return (dateTime.toLocalDateTime().compareTo(workingDay.getStartTime()) == 0 || dateTime.toLocalDateTime().isAfter(workingDay.getStartTime()))
                        &&
                        (dateTime.toLocalDateTime().compareTo(workingDay.getEndTime()) == 0 || dateTime.toLocalDateTime().isBefore(workingDay.getEndTime()));
            }
        }
        return false;
    }


    /**
     * Check a date is holiday or not
     *
     * @param date
     * @param holidays
     * @return
     */
    private Boolean isHoliday(LocalDate date, List<Holiday> holidays) {
        return holidays.stream()
                .filter(i -> {
                    return (i.getDate().compareTo(date) == 0);
                })
                .findAny().isPresent();
    }

    /**
     * Get list of working days
     *
     * @param dateTime
     * @param holidays
     * @param opsStartTime
     * @param opsEndTime
     * @return
     */
    private List<WorkingDay> getWorkingDays(ZonedDateTime dateTime, List<Holiday> holidays, LocalTime opsStartTime, LocalTime opsEndTime) {
        List<WorkingDay> workingDays = new ArrayList<WorkingDay>();
        LocalDate startDate = dateTime.toLocalDate().with(firstDayOfYear());
        LocalDate endDate = dateTime.toLocalDate().with(lastDayOfYear());
        while (!startDate.isAfter(endDate)) {
            if (!this.isHoliday(startDate, holidays)) {
                workingDays.add(new WorkingDay(startDate, opsStartTime, opsEndTime));
            }
            startDate = startDate.plusDays(1);
        }
        return workingDays;
    }

    private Optional<WorkingDay> nextWorkingDay(LocalDate date, List<WorkingDay> workingDays) {
        return workingDays
                .stream().filter(
                        i -> {
                            return i.getDate().isAfter(date);
                        }
                ).findFirst();
    }

    private List<Holiday> mergeHolidays(List<Holiday> currentYearHolidays, List<Holiday> nextYearHolidays) {
        List<Holiday> upcomingHolidays = new ArrayList<Holiday>();
        upcomingHolidays.addAll(currentYearHolidays);
        upcomingHolidays.addAll(nextYearHolidays);
        Collections.sort(upcomingHolidays);
        return upcomingHolidays;
    }
}
