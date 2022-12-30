package com.centram.core.api;


import com.centram.common.dto.ThirdPartyLoggedInUser;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.vo.DepartmentVO;
import com.centram.common.vo.LocationVO;
import com.centram.core.service.AssetService;
import com.centram.core.service.DepartmentService;
import com.centram.core.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(value = "/integration/api/v1")
@Controller
public class IntegrationApiController {

    private static final Logger log = LoggerFactory.getLogger(IntegrationApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@appSecurityUtilityService.hasThirdPartyUserAccess(authentication)")
    public ResponseEntity<List<LocationVO>> getLocation() {
        ThirdPartyLoggedInUser thirdPartyLoggedInUser = (ThirdPartyLoggedInUser) SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<List<LocationVO>>(locationService.getLocations(thirdPartyLoggedInUser.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/location", method = RequestMethod.POST, consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@appSecurityUtilityService.hasThirdPartyUserAccess(authentication)")
    public ResponseEntity saveLocation(@Valid @RequestBody List<LocationVO> body) {
        ThirdPartyLoggedInUser thirdPartyLoggedInUser = (ThirdPartyLoggedInUser) SecurityContextHolder.getContext().getAuthentication();
        locationService.saveAll(body, thirdPartyLoggedInUser.getId());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/department", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@appSecurityUtilityService.hasThirdPartyUserAccess(authentication)")
    public ResponseEntity<List<DepartmentVO>> getDepartment() {
        ThirdPartyLoggedInUser thirdPartyLoggedInUser = (ThirdPartyLoggedInUser) SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<List<DepartmentVO>>(departmentService.getDepartments(thirdPartyLoggedInUser.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/department", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@appSecurityUtilityService.hasThirdPartyUserAccess(authentication)")
    public ResponseEntity saveDepartment(@Valid @RequestBody List<DepartmentVO> body) {
        ThirdPartyLoggedInUser thirdPartyLoggedInUser = (ThirdPartyLoggedInUser) SecurityContextHolder.getContext().getAuthentication();
        departmentService.saveAll(body, thirdPartyLoggedInUser.getId());
        return new ResponseEntity(HttpStatus.OK);
    }

}