package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.ProjectRepository;
import com.centram.core.repository.TimeSheetRepository;
import com.centram.domain.Project;
import com.centram.domain.TimeSheet;
import com.centram.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

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

    /**
     * @param timeSheet
     * @return
     */
    @Transactional(readOnly = false)
    public TimeSheet save(LoggedInUser loggedInUser, TimeSheet timeSheet) {
        timeSheet.setUser(new User(userService.getUserById(loggedInUser.getUserId())));
        try {
            if (timeSheet.getId() == null) {
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
            }
            return proxyService.saveTimeSheet(timeSheet);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.DATA_EXIST, new HashMap<String, Object>() {{
                put("entity", "Time Sheet");
            }});
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}