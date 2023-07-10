package com.centram.core.service;

import com.centram.common.dto.ProjectDeallocateDTO;
import com.centram.core.repository.ProjectAllocationDetailRepository;
import com.centram.domain.ProjectAllocationDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectAllocationDetailService {

    private static final Logger log = LoggerFactory.getLogger(ProjectAllocationDetailService.class);

    @Autowired
    private ProjectAllocationDetailRepository projectAllocationDetailRepository;

    @Autowired
    private MiscService miscService;

    /**
     * @param projectAllocationDetailList
     * @return
     */
    @Transactional(readOnly = false)
    public Map<BigInteger, String> allocation(List<ProjectAllocationDetail> projectAllocationDetailList) {
        //prepare project codes for user
        Map<BigInteger, String> allocateProjectDTOS = new HashMap<BigInteger, String>();
        String projects;
        for (ProjectAllocationDetail projectAllocationDetail : projectAllocationDetailList) {
            if (allocateProjectDTOS.containsKey(projectAllocationDetail.getUser().getId())) {
                projects = allocateProjectDTOS.get(projectAllocationDetail.getUser().getId());
                projects = projects.concat(",").concat(projectAllocationDetail.getProject().getCode());
                allocateProjectDTOS.put(projectAllocationDetail.getUser().getId(), projects);
            } else {
                allocateProjectDTOS.put(projectAllocationDetail.getUser().getId(), projectAllocationDetail.getProject().getCode());
            }
        }
        // save project allocation detail
        projectAllocationDetailList.stream().forEach(i -> {
            i.setDeallocated(false);
        });
        projectAllocationDetailRepository.saveAll(projectAllocationDetailList);
        return allocateProjectDTOS;
    }

    /**
     * @param projectDeallocateDTO
     * @return
     */
    @Transactional(readOnly = false)
    public Map<BigInteger, String> deallocation(ProjectDeallocateDTO projectDeallocateDTO) {
        List<ProjectAllocationDetail> projectAllocationDetails = projectAllocationDetailRepository.getDeallocationList(projectDeallocateDTO.getProjectId(), projectDeallocateDTO.getUserIds());
        Map<BigInteger, String> deallocateProjectDTOS = new HashMap<BigInteger, String>();
        for (ProjectAllocationDetail projectAllocationDetail : projectAllocationDetails) {
            deallocateProjectDTOS.put(projectAllocationDetail.getUser().getId(), projectAllocationDetail.getProject().getCode());
        }
        projectAllocationDetails.stream().forEach(i -> {
            i.setDeallocated(true);
        });
        projectAllocationDetailRepository.saveAll(projectAllocationDetails);
        return deallocateProjectDTOS;
    }

}