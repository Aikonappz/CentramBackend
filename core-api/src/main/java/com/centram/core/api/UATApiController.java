package com.centram.core.api;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.ProjectUATRequestDTO;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.ProjectUatService;
import com.centram.domain.MediaFile;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequestMapping(value = "/api/v1/project-uat")
@Controller
public class UATApiController {

    private static final Logger log = LoggerFactory.getLogger(UATApiController.class);

    @Autowired
    private ProjectUatService projectUatService;

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/upload-script", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUat> uploadScript(@RequestPart(name = "file", required = true) MultipartFile multipartFile, @RequestPart("projectUATRequestDTO") ProjectUATRequestDTO projectUATRequestDTO) throws IOException {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUat>(projectUatService.uploadScripts(loggedInUser, multipartFile, projectUATRequestDTO), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/uploaded-scripts", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','READ|WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<ProjectUat>> uploadedScripts(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<PaginatedList<ProjectUat>>(projectUatService.uploadedScripts(loggedInUser.getUserId(), pageable), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/download-uploaded-scripts-file", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','READ|WRITE',authentication.principal)")
    public ResponseEntity<Resource> downloadUploadedScript(@RequestParam(value = "projectUatId", required = true) BigInteger projectUatId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final InputStreamResource resource = new InputStreamResource(projectUatService.downloadUploadedScript(loggedInUser,projectUatId));
        MediaFile mediaFile = projectUatService.getUatScriptFileName(projectUatId);
        //FileNameMap fileNameMap = URLConnection.getFileNameMap();
        //log.info("file name -> {}, type -> {}, mime type -> {}", mediaFile.getFileName(),mediaFile.getFileType(),MediaType.valueOf(fileNameMap.getContentTypeFor(mediaFile.getFileName())));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mediaFile.getFileName())
                .contentType(
                        MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                )
                .body(resource);
    }

    @RequestMapping(value = "/uat-cycles", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES,REPORT,UAT REPORT','WRITE,READ,READ',authentication.principal)")
    public ResponseEntity<List<ProjectUat>> getProjectUats(@NotNull @Valid @RequestParam(value = "projectId", required = true) BigInteger projectId, @NotNull @Valid @RequestParam(value = "moduleId", required = true) BigInteger moduleId, @NotNull @Valid @RequestParam(value = "subModuleId", required = false) BigInteger subModuleId) {
        return new ResponseEntity<List<ProjectUat>>(projectUatService.getProjectUats(projectId, moduleId, subModuleId), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat-scripts", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES,REPORT,UAT REPORT','WRITE,READ,READ',authentication.principal)")
    public ResponseEntity<Set<ProjectUatScript>> getProjectUatScripts(
            @RequestParam(value = "uatProjectId", required = true) BigInteger uatProjectId,
            @RequestParam(value = "customerId", required = false, defaultValue = "-1") BigInteger customerId
    ) {
        return new ResponseEntity<Set<ProjectUatScript>>(projectUatService.getProjectUatScriptsByUatProjectId(uatProjectId,customerId), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat-script-detail", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES,REPORT,UAT REPORT','WRITE,READ,READ',authentication.principal)")
    public ResponseEntity<PaginatedList<ProjectUatScriptDetail>> getProjectUatScriptDetails(@NotNull @Valid @RequestParam(value = "projectUATScriptId", required = false) BigInteger projectUATScriptId, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<ProjectUatScriptDetail>>(projectUatService.findByProjectUATScriptId(projectUATScriptId, pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/can-mark-script-complete", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES,REPORT,UAT REPORT','WRITE,READ,READ',authentication.principal)")
    public ResponseEntity<ProjectUatScript> canMarkScriptComplete(@NotNull @Valid @RequestParam(value = "projectUATScriptId", required = false) BigInteger projectUATScriptId) {
        return new ResponseEntity<ProjectUatScript>(projectUatService.canMarkScriptComplete(projectUATScriptId), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/update-project-uat-script-detail", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUatScriptDetail> updateProjectUatScriptDetail(@RequestBody(required = true) ProjectUatScriptDetail projectUatScriptDetail) throws JsonProcessingException, InterruptedException {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUatScriptDetail>(projectUatService.updateProjectUatScriptDetail(loggedInUser, projectUatScriptDetail), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/mark-project-uat-script-complete/{uatScriptId}", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUatScript> markProjectUatScriptComplete(@PathVariable("uatScriptId") BigInteger uatScriptId) throws JsonProcessingException, InterruptedException {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUatScript>(projectUatService.markProjectUatScriptComplete(loggedInUser, uatScriptId), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat-script", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<ProjectUatScript>> getProjectUatScripts(
            @NotNull @Valid @RequestParam(value = "projectUatId", required = false) BigInteger projectUatId,
            @NotNull @Valid @RequestParam(value = "isCustomer", required = false, defaultValue = "false") Boolean isCustomer,
            @NotNull @Valid @RequestParam(value = "isProjectManager", required = false, defaultValue = "false") Boolean isProjectManager,
            @NotNull @Valid @RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
            @NotNull @Valid @RequestParam(value = "isConsultant", required = false, defaultValue = "false") Boolean isConsultant,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<PaginatedList<ProjectUatScript>>(projectUatService.getProjectUatScripts(loggedInUser, projectUatId, isCustomer, isProjectManager, isAdmin, isConsultant,pageable), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/mark-project-uat-complete/{projectUatId}", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUat> markUATCycleComplete(@PathVariable("projectUatId") BigInteger projectUatId) throws JsonProcessingException, InterruptedException {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUat>(projectUatService.markProjectUatComplete(loggedInUser, projectUatId), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/uat-script-already-uploaded/{moduleId}/{subModuleId}/{projectId}", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUat> uatScriptAlreadyUploaded(
            @PathVariable("moduleId") BigInteger moduleId,
            @PathVariable("subModuleId") BigInteger subModuleId,
            @PathVariable("projectId") BigInteger projectId
    ) throws JsonProcessingException, InterruptedException {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUat>(projectUatService.uatScriptAlreadyUploaded(moduleId, subModuleId,projectId), HttpStatus.OK);
    }
}