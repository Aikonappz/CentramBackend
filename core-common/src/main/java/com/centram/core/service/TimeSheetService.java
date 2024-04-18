package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ProjectRepository;
import com.centram.core.repository.TimeSheetRepository;
import com.centram.domain.Project;
import com.centram.domain.TimeSheet;
import com.centram.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class TimeSheetService {

    private static final Logger log = LoggerFactory.getLogger(TimeSheetService.class);

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MiscService miscService;

    /**
     * @param timeSheet
     * @return
     */
    @Transactional(readOnly = false)
    public TimeSheet save(LoggedInUser loggedInUser, TimeSheet timeSheet) {
        Boolean newEntry = false;
        TimeSheet updatedTimesheet;
        try {
            if (timeSheet.getId() == null) {
                timeSheet.setUser(new User(userService.getUserById(loggedInUser.getUserId())));
                if (!CollectionUtils.isEmpty(timeSheet.getTimeSheetEntries())) {
                    timeSheet.getTimeSheetEntries().stream().forEach(i -> {
                        Project project = projectRepository.getById(i.getProject().getId());
                        List<String> approverEmails = project.getApprovers();
                        if (!CollectionUtils.isEmpty(approverEmails)) {
                            i.setApprover(userService.getUserByEmail(approverEmails.get(0)));
                        }
                        i.setTimeSheet(timeSheet);
                    });
                }
                timeSheet.setNewSubmission(true);
                newEntry = true;
            } else {
                if (!CollectionUtils.isEmpty(timeSheet.getTimeSheetEntries())) {
                    timeSheet.getTimeSheetEntries().stream().forEach(i -> {
                        i.setTimeSheet(timeSheet);
                    });
                }
                timeSheet.setNewSubmission(false);
                newEntry = false;
            }
            long approvedCount = timeSheet.getAllTimeSheetEntries().stream().filter(g -> {
                return g.getApproved();
            }).count();
            long totalTimeSheet = timeSheet.getAllTimeSheetEntries().size();
            if (totalTimeSheet == approvedCount) {
                timeSheet.setFreezed(true);
            }
            timeSheet.setModifiedBy(loggedInUser.getUserId());
            updatedTimesheet = proxyService.saveTimeSheet(timeSheet);
            timeSheet.setNewSubmission(newEntry);
            miscService.notifyTimeSheetUpdate(timeSheet);
            return updatedTimesheet;
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.DATA_EXIST, new HashMap<String, Object>() {{
                put("entity", "Time Sheet");
            }});
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public PaginatedList<TimeSheet> getTimeSheets(LoggedInUser loggedInUser, BigInteger projectId, LocalDate date, Pageable pageable) {
        Page<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByUser(date, projectId, loggedInUser.getUserId(), pageable);
        timeSheets.getContent().stream().forEach(i -> {
            i.setAllTimeSheetEntries(i.getTimeSheetEntries());
            i.getAllTimeSheetEntries().stream().forEach(k -> {
                i.setFreezed(false);
                long approvedCount = i.getAllTimeSheetEntries().stream().filter(g -> {
                    return g.getApproved();
                }).count();
                long totalTimeSheet = i.getAllTimeSheetEntries().size();
                if (totalTimeSheet == approvedCount) {
                    i.setFreezed(true);
                }
                if (i.getFreezed()) {
                    k.setUserCanEdit(false);
                    k.setApproverCanTakeAction(false);
                    i.setUserCanEdit(false);
                    i.setApproverCanTakeAction(false);
                } else {
                    if (i.getUser().getId().compareTo(loggedInUser.getUserId()) == 0) {
                        if (i.getModifiedBy().compareTo(loggedInUser.getUserId()) == 0) {
                            k.setUserCanEdit(false);
                            if (!k.getApproved()) k.setApproverCanTakeAction(true);
                            i.setUserCanEdit(false);
                            i.setApproverCanTakeAction(true);
                        } else {
                            if (!k.getApproved()) k.setUserCanEdit(true);
                            k.setApproverCanTakeAction(false);
                            i.setUserCanEdit(true);
                            i.setApproverCanTakeAction(false);
                        }
                    } else {
                        if (i.getModifiedBy().compareTo(loggedInUser.getUserId()) == 0) {
                            if (!k.getApproved()) k.setUserCanEdit(true);
                            k.setApproverCanTakeAction(false);
                            i.setUserCanEdit(true);
                            i.setApproverCanTakeAction(false);
                        } else {
                            k.setUserCanEdit(false);
                            if (!k.getApproved()) k.setApproverCanTakeAction(true);
                            i.setUserCanEdit(false);
                            i.setApproverCanTakeAction(true);
                        }
                    }
                }
            });
        });
        return new PaginatedList<TimeSheet>(timeSheets);
    }

    @Transactional(readOnly = true)
    public TimeSheet getTimeSheet(LoggedInUser loggedInUser, BigInteger timeSheetId) {
        Optional<TimeSheet> timeSheetOptional = timeSheetRepository.findById(timeSheetId);
        if (!timeSheetOptional.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        TimeSheet timeSheet = timeSheetOptional.get();
        timeSheet.setAllTimeSheetEntries(timeSheet.getTimeSheetEntries());
        timeSheet.getAllTimeSheetEntries().stream().forEach(k -> {
            timeSheet.setFreezed(false);
            long approvedCount = timeSheet.getAllTimeSheetEntries().stream().filter(g -> {
                return g.getApproved();
            }).count();
            long totalTimeSheet = timeSheet.getAllTimeSheetEntries().size();
            if (totalTimeSheet == approvedCount) {
                timeSheet.setFreezed(true);
            }
            if (timeSheet.getFreezed()) {
                k.setUserCanEdit(false);
                k.setApproverCanTakeAction(false);
                timeSheet.setUserCanEdit(false);
                timeSheet.setApproverCanTakeAction(false);
            } else {
                if (timeSheet.getUser().getId().compareTo(loggedInUser.getUserId()) == 0) {
                    if (timeSheet.getModifiedBy().compareTo(loggedInUser.getUserId()) == 0) {
                        k.setUserCanEdit(false);
                        if (!k.getApproved()) k.setApproverCanTakeAction(true);
                        timeSheet.setUserCanEdit(false);
                        timeSheet.setApproverCanTakeAction(true);
                    } else {
                        if (!k.getApproved()) k.setUserCanEdit(true);
                        k.setApproverCanTakeAction(false);
                        timeSheet.setUserCanEdit(true);
                        timeSheet.setApproverCanTakeAction(false);
                    }
                } else {
                    if (timeSheet.getModifiedBy().compareTo(loggedInUser.getUserId()) == 0) {
                        if (!k.getApproved()) k.setUserCanEdit(true);
                        k.setApproverCanTakeAction(false);
                        timeSheet.setUserCanEdit(true);
                        timeSheet.setApproverCanTakeAction(false);
                    } else {
                        k.setUserCanEdit(false);
                        if (!k.getApproved()) k.setApproverCanTakeAction(true);
                        timeSheet.setUserCanEdit(false);
                        timeSheet.setApproverCanTakeAction(true);
                    }
                }
            }
        });
        long approvedCount = timeSheet.getAllTimeSheetEntries().stream().filter(i -> {
            return i.getApproved();
        }).count();
        long totalTimeSheet = timeSheet.getAllTimeSheetEntries().size();
        if (totalTimeSheet == approvedCount) {
            timeSheet.setFreezed(true);
        }
        timeSheet.setNewSubmission(false);
        return timeSheet;
    }

    @Transactional(readOnly = true)
    public PaginatedList<TimeSheet> getPendingApprovalTimeSheets(Integer pendingApproval, BigInteger projectId, BigInteger userId, final LoggedInUser loggedInUser, LocalDate date, Pageable pageable) {
        Page<TimeSheet> timeSheets = timeSheetRepository.getPendingApprovalTimeSheets(pendingApproval, projectId, loggedInUser.getUserId(), date, userId, pageable);
        timeSheets.getContent().stream().forEach(i -> {
            i.setAllTimeSheetEntries(i.getTimeSheetEntries());
            i.getAllTimeSheetEntries().stream().forEach(k -> {
                i.setFreezed(false);
                long approvedCount = i.getAllTimeSheetEntries().stream().filter(g -> {
                    return g.getApproved();
                }).count();
                long totalTimeSheet = i.getAllTimeSheetEntries().size();
                if (totalTimeSheet == approvedCount) {
                    i.setFreezed(true);
                }
                if (i.getFreezed()) {
                    k.setUserCanEdit(false);
                    k.setApproverCanTakeAction(false);
                    i.setUserCanEdit(false);
                    i.setApproverCanTakeAction(false);
                } else {
                    if (i.getUser().getId().compareTo(loggedInUser.getUserId()) == 0) {
                        if (i.getModifiedBy().compareTo(loggedInUser.getUserId()) == 0) {
                            k.setUserCanEdit(false);
                            if (!k.getApproved()) k.setApproverCanTakeAction(true);
                            i.setUserCanEdit(false);
                            i.setApproverCanTakeAction(true);
                        } else {
                            if (!k.getApproved()) k.setUserCanEdit(true);
                            k.setApproverCanTakeAction(false);
                            i.setUserCanEdit(true);
                            i.setApproverCanTakeAction(false);
                        }
                    } else {
                        if (i.getModifiedBy().compareTo(loggedInUser.getUserId()) == 0) {
                            if (!k.getApproved()) k.setUserCanEdit(true);
                            k.setApproverCanTakeAction(false);
                            i.setUserCanEdit(true);
                            i.setApproverCanTakeAction(false);
                        } else {
                            k.setUserCanEdit(false);
                            if (!k.getApproved()) k.setApproverCanTakeAction(true);
                            i.setUserCanEdit(false);
                            i.setApproverCanTakeAction(true);
                        }
                    }
                }
            });
        });
        return new PaginatedList<TimeSheet>(timeSheets);
    }
}