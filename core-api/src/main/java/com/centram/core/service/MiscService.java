package com.centram.core.service;

import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.vo.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class MiscService {

    private static final Logger log = LoggerFactory.getLogger(MiscService.class);

    @Autowired
    private AppEmailService appEmailService;

    /**
     * Onboard request mail
     *
     * @param requestDemoDTO
     * @return
     */
    public CommonResponse requestDemo(RequestDemoDTO requestDemoDTO) {
        CommonResponse commonResponse = null;
        appEmailService.sendOnboardRequestMail(requestDemoDTO, new HashMap<>());
        commonResponse = new CommonResponse(Boolean.TRUE, "Request send successfully");
        return commonResponse;
    }
}