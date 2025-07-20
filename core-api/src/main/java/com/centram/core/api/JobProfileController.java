package com.centram.core.api;

import com.centram.common.dto.*;
import com.centram.core.service.JobProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class JobProfileController {

    @Autowired
    JobProfileService jobProfileService;

    @PostMapping("/job-family/add")
    public ResponseEntity<JobFamilyDTO> saveJobFamily(@RequestBody JobFamilyDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveJobFamily(dto));
    }

    @GetMapping("/job-family/{id}")
    public ResponseEntity<JobFamilyDTO> getJobFamily(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getJobFamily(id));
    }

    @PostMapping("/job-role/add")
    public ResponseEntity<JobRoleDTO> saveJobRole(@RequestBody JobRoleDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveJobRole(dto));
    }

    @GetMapping("/job-role/{id}")
    public ResponseEntity<JobRoleDTO> getJobRole(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getJobRole(id));
    }

    @PostMapping("/competency/add")
    public ResponseEntity<CompetencyDTO> saveCompetency(@RequestBody CompetencyDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveCompetency(dto));
    }

    @GetMapping("/competency/{id}")
    public ResponseEntity<CompetencyDTO> getCompetency(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getCompetency(id));
    }

    @PostMapping("/job-profile/add")
    public ResponseEntity<JobProfileDTO> saveJobProfile(@RequestBody JobProfileDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveJobProfile(dto));
    }

    @GetMapping("/job-profile/{id}")
    public ResponseEntity<JobProfileDTO> getJobProfile(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getJobProfile(id));
    }

    @GetMapping("/job-code/{jobCodeId}")
    public ResponseEntity<List<JobProfileResponseDTO>> getJobProfilesByJobCode(@PathVariable BigInteger jobCodeId) {
        return ResponseEntity.ok(jobProfileService.getJobProfilesByJobCode(jobCodeId));
    }


}
