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
import java.time.format.DateTimeFormatter;
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
    private PermissionService permissionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private HolidayCalenderService holidayCalenderService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private LocationService locationService;

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
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        if (incident.getId() == null) {
            incident.setRaisedUser(new User(userVO.getVersion(), userVO.getId()));
            incident.setRaisedAt(LocalDateTime.now());
            Setting setting = organisationService.getOrganisationSettings();
            String prefix = (setting != null && setting.getIncidentPrefix() != null) ? setting.getIncidentPrefix() : appDefaultIncidentPrefix;
            incident.setIncidentNo(incidentNo(prefix));


        }
        /**fetch current year holiday calender**/
        HolidayCalender currentYearHolidayCalender = holidayCalenderService.getByYear(Year.now().toString());
        /**fetch next year holiday calender**/
        HolidayCalender nextYearHolidayCalender = holidayCalenderService.getByYear(Year.now().plusYears(1).toString());
        /**prepare holiday List*/
        List<Holiday> holidays = currentYearHolidayCalender.getHolidays();
            /*if (nextYearHolidayCalender != null) {
                holidays.addAll(nextYearHolidayCalender.getHolidays());
            }*/
        /**fetch priority**/
        Priority priority = priorityService.getById(incident.getPriority().getId());
        /**fetch location**/
        Location location = locationService.getById(loggedInUser.getLocationId());

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(loggedInUser.getTimeZone()).getRules().getOffset(LocalDateTime.now()));
        incident.setSlaAt(this.getSlADateTime(
                localDateTime,
                priority.getSla(),
                location.getOpsStartTime(),
                location.getOpsEndTime(),
                holidays
        ));
        Set<IncidentCommunication> communicationSet = new HashSet<IncidentCommunication>();
        for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
            if (incidentCommunication.getId() == null) {
                incidentCommunication.setCommunicatedBy(new User(userVO.getVersion(), userVO.getId()));
            }
            incidentCommunication.setIncident(incident);
            communicationSet.add(incidentCommunication);
        }
        incident.setCommunications(communicationSet);
        return incidentRepository.save(incident);
    }

    /**
     * calculate sla
     *
     * @param startDateTime
     * @param hour
     * @param opsStartTime
     * @param opsEndTime
     * @param holidays
     * @return
     */
    private LocalDateTime getSlADateTime(
            LocalDateTime startDateTime,
            String hour,
            LocalTime opsStartTime,
            LocalTime opsEndTime,
            List<Holiday> holidays
    ) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer hours = Integer.valueOf(hour.split((":"))[0]);
        Integer minutes = Integer.valueOf(hour.split((":"))[1]);
        log.info("hours => {}, minutes => {}", hours, minutes);
        Integer minuteToAdd = (hours * 60) + minutes;
        log.info(" converted minutes => {}", minuteToAdd);
        List<WorkingDay> workingDays = this.getWorkingDays(
                startDateTime,
                holidays,
                opsStartTime,
                opsEndTime
        );
        Collections.sort(workingDays);
        LocalDateTime sla = null;
        if (this.isHoliday(startDateTime.toLocalDate(), holidays)) {
            sla = startDateTime.plusDays(1).toLocalDate().atTime(LocalTime.MIN);
            sla = sla.toLocalDate().atTime(opsStartTime).plusMinutes(minuteToAdd);
        } else {
            sla = startDateTime.plusMinutes(minuteToAdd);
        }
        if (!this.checkIsValidSLA(sla, workingDays)) {
            LocalDateTime todayEnd = startDateTime.with(opsEndTime);
            Duration duration = Duration.between(todayEnd, startDateTime);
            Long pendingSla = minuteToAdd - duration.toMinutes();
            sla = sla.plusDays(1).toLocalDate().atTime(LocalTime.MIN).plusMinutes(pendingSla);
        }
        log.info("offset {}", sla.atZone(ZoneId.systemDefault()).toLocalDateTime());
        log.info("offset {}", sla.atZone(ZoneId.of(loggedInUser.getTimeZone())).toLocalDateTime());
        sla = sla.atZone(ZoneId.systemDefault()).toLocalDateTime();


        return sla;
    }

    /**
     * check whether a datetime is working day and fall under shift time
     *
     * @param dateTime
     * @param workingDays
     * @return
     */
    private Boolean checkIsValidSLA(LocalDateTime dateTime, List<WorkingDay> workingDays) {
        LocalDate date = dateTime.toLocalDate();
        for (WorkingDay workingDay : workingDays) {
            if (workingDay.getDate().compareTo(date) == 0) {
                return (dateTime.compareTo(workingDay.getStartTime()) == 0 || dateTime.isAfter(workingDay.getStartTime()))
                        &&
                        (dateTime.compareTo(workingDay.getEndTime()) == 0 || dateTime.isBefore(workingDay.getEndTime()));
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
    private List<WorkingDay> getWorkingDays(
            LocalDateTime dateTime,
            List<Holiday> holidays,
            LocalTime opsStartTime,
            LocalTime opsEndTime
    ) {
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
}
