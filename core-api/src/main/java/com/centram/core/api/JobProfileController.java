package com.centram.core.api;

import com.centram.common.dto.*;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.JobProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
        jobProfileService.deleteJobFamilyById(id);
        return ResponseEntity.ok("Job Family deleted successfully");
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
        jobProfileService.deleteJobRoleById(id);
        return ResponseEntity.ok("Job Role deleted successfully");
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
        jobProfileService.deleteCompetencyById(id);
        return ResponseEntity.ok("Competency deleted successfully");
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
        jobProfileService.deleteJobProfileById(id);
        return ResponseEntity.ok("Job Profile deleted successfully");
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

    @GetMapping("/get-all/jobcodes")
    public ResponseEntity<JobCodeWrapperResponse> getAllUniqueJobCodes() {
        return new ResponseEntity<>(jobProfileService.getAllUniqueJobCodes(), HttpStatus.OK);
    }

    @GetMapping("/next-job-code")
    public String getNextJobCode() {
        return jobProfileService.generateNextJobCode();
    }

}
