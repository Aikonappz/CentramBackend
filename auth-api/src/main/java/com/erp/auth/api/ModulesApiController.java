package com.erp.auth.api;

import com.erp.auth.api.interfaces.IModulesApi;
import com.erp.auth.service.ModuleService;
import com.erp.domain.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")

@RestController
public class ModulesApiController implements IModulesApi {

    private static final Logger log = LoggerFactory.getLogger(ModulesApiController.class);

    @Autowired
    private ModuleService moduleService;

    public ResponseEntity<Page<Module>> getModules(Pageable pageable) {
        return new ResponseEntity<Page<Module>>(moduleService.getModules(pageable), HttpStatus.OK);
    }

    public ResponseEntity<Module> getModuleById(BigInteger moduleId) {
        return new ResponseEntity<Module>(moduleService.getModuleById(moduleId), HttpStatus.OK);
    }

}
