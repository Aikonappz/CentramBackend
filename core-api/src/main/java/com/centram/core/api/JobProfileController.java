package com.centram.core.api;

import com.centram.common.dto.*;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.JobProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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


    @DeleteMapping("/delete/job-family/{id}")
    public ResponseEntity<String> deleteJobFamilyById(@PathVariable BigInteger id){
        return ResponseEntity.ok(jobProfileService.deleteJobFamilyById(id));
    }

    @PostMapping("/job-role/add")
    public ResponseEntity<JobRoleDTO> saveJobRole(@RequestBody JobRoleDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveJobRole(dto));
    }

    @GetMapping("/job-role/{id}")
    public ResponseEntity<JobRoleDTO> getJobRole(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getJobRole(id));
    }

    @DeleteMapping("/delete/job-role/{id}")
    public ResponseEntity<String> deleteJobRoleById(@PathVariable BigInteger id){
        return ResponseEntity.ok(jobProfileService.deleteJobRoleById(id));
    }

    @PostMapping("/competency/add")
    public ResponseEntity<CompetencyDTO> saveCompetency(@RequestBody CompetencyDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveCompetency(dto));
    }

    @GetMapping("/competency/{id}")
    public ResponseEntity<CompetencyDTO> getCompetency(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getCompetency(id));
    }

    @DeleteMapping("/delete/competency/{id}")
    public ResponseEntity<String> deleteCompetencyById(@PathVariable BigInteger id){
        return ResponseEntity.ok(jobProfileService.deleteCompetencyById(id));
    }

    @PostMapping("/job-profile/add")
    public ResponseEntity<JobProfileDTO> saveJobProfile(@RequestBody JobProfileDTO dto) {
        return ResponseEntity.ok(jobProfileService.saveJobProfile(dto));
    }

    @GetMapping("/job-profile/{id}")
    public ResponseEntity<JobProfileDTO> getJobProfile(@PathVariable BigInteger id) {
        return ResponseEntity.ok(jobProfileService.getJobProfile(id));
    }

    @DeleteMapping("/delete/job-profile/{id}")
    public ResponseEntity<String> deleteJobProfileById(@PathVariable BigInteger id){
        return ResponseEntity.ok(jobProfileService.deleteJobProfileById(id));
    }

    @GetMapping("/job-code/{jobCodeId}")
    public ResponseEntity<List<JobProfileResponseDTO>> getJobProfilesByJobCode(@PathVariable String jobCodeId) {
        return ResponseEntity.ok(jobProfileService.getJobProfilesByJobCode(jobCodeId));
    }

    @GetMapping("/job-family/get-all")
    public ResponseEntity<PaginatedList<JobFamilyDTO>> getAllJobFamilies(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(jobProfileService.getAllJobFamilies(pageable));
    }

    @GetMapping("/job-role/get-all")
    public ResponseEntity<PaginatedList<JobRoleDTO>> getAllJobRoles(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(jobProfileService.getAllJobRoles(pageable));
    }

    @GetMapping("/competency/get-all")
    public ResponseEntity<PaginatedList<CompetencyDTO>> getAllCompetencies(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(jobProfileService.getAllCompetencies(pageable));
    }

    @GetMapping("/job-profile/get-all")
    public ResponseEntity<PaginatedList<JobProfileDTO>> getAllJobProfiles(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(jobProfileService.getAllJobProfiles(pageable));
    }


}
