package com.centram.common.utility;

import com.centram.common.dto.LoggedInUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppSecurityUtilityService {

    private static final Logger LOG = LoggerFactory.getLogger(AppSecurityUtilityService.class);

    public Boolean hasAppAdminAccess(LoggedInUserDTO loggedInUserDTO) {
        return loggedInUserDTO.getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().equals("ADMIN"))
                .count() > 0;
    }
}
