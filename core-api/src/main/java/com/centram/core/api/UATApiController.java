package com.centram.core.api;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.ProjectUATRequestDTO;
import com.centram.common.view.Views;
import com.centram.core.service.ProjectUatService;
import com.centram.domain.ProjectUat;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


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

}