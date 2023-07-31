package com.centram.core.api;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.ProjectUATRequestDTO;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.ProjectUatService;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequestMapping(value = "/api/v1/project-uat")
@Controller
public class UATApiController {

    private static final Logger log = LoggerFactory.getLogger(UATApiController.class);

    @Autowired
    private ProjectUatService projectUatService;

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/upload-scripts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUat> uploadScripts(@RequestPart(name = "file", required = true) MultipartFile multipartFile, @RequestPart("projectUATRequestDTO") ProjectUATRequestDTO projectUATRequestDTO) throws IOException {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUat>(projectUatService.uploadScripts(loggedInUser.getUserId(), loggedInUser.getOrganisationId(), multipartFile, projectUATRequestDTO), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat-script", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<Set<ProjectUatScript>> getProjectUatScripts(@NotNull @Valid @RequestParam(value = "projectId", required = true) BigInteger projectId, @NotNull @Valid @RequestParam(value = "moduleId", required = true) BigInteger moduleId, @NotNull @Valid @RequestParam(value = "subModuleId", required = false) BigInteger subModuleId) {
        return new ResponseEntity<Set<ProjectUatScript>>(projectUatService.getProjectUatScripts(projectId, moduleId, subModuleId), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat-script-detail", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<ProjectUatScriptDetail>> getProjectUatScriptDetails(@NotNull @Valid @RequestParam(value = "projectId", required = false) BigInteger projectId, @NotNull @Valid @RequestParam(value = "moduleId", required = false) BigInteger moduleId, @NotNull @Valid @RequestParam(value = "subModuleId", required = false) BigInteger subModuleId, @NotNull @Valid @RequestParam(value = "projectUATScriptId", required = false) BigInteger projectUATScriptId, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<ProjectUatScriptDetail>>(projectUatService.getProjectUatScriptDetails(projectId, moduleId, subModuleId, projectUATScriptId, pageable), HttpStatus.OK);
    }

    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/update-project-uat-script-detail", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE} ,method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('UAT ACTIVITIES','WRITE',authentication.principal)")
    public ResponseEntity<ProjectUatScriptDetail> saveProjectUatScriptDetail(@RequestBody(required = true) ProjectUatScriptDetail projectUatScriptDetail) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<ProjectUatScriptDetail>(projectUatService.saveProjectUatScriptDetail(projectUatScriptDetail), HttpStatus.OK);
    }
}