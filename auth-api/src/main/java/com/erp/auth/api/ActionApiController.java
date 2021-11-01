package com.erp.auth.api;


import com.erp.auth.api.interfaces.IActionApi;
import com.erp.auth.service.ActionService;
import com.erp.domain.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")

@Controller
public class ActionApiController implements IActionApi {

    private static final Logger log = LoggerFactory.getLogger(ActionApiController.class);

    @Autowired
    private ActionService actionService;

    public ResponseEntity<Page<Action>> getActions(Pageable pageable) {
        return new ResponseEntity<Page<Action>>(actionService.getActions(pageable), HttpStatus.OK);
    }

}
