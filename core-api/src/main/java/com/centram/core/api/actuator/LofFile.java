package com.centram.core.api.actuator;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RestControllerEndpoint(id = "logtrack")
public class LofFile {
    @GetMapping("/")
    public @ResponseBody
    ResponseEntity customEndPoint() {
        return new ResponseEntity<>("Test rest end point", HttpStatus.OK);
    }

}