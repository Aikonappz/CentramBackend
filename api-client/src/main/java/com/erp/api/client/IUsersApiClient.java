package com.erp.api.client;


import com.erp.api.client.config.FeignConfig;
import com.erp.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "erp-auth", configuration = FeignConfig.class)
@RequestMapping("/api/v1/user")
public interface IUsersApiClient {
    @PostMapping(path = "/")
    ResponseEntity<User> addUser(@RequestBody User body);
}