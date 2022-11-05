package com.centram.core.service;


import com.centram.core.repository.UserAuthRepository;
import com.centram.domain.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class UserAuthService {
    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public UserAuth save(UserAuth userAuth) {
        return userAuthRepository.save(userAuth);
    }

    @Transactional(readOnly = true)
    public UserAuth getById(BigInteger appAuthId) {
        return userAuthRepository.findById(appAuthId).get();
    }

    @Transactional(readOnly = true)
    public int anyUserOnline(List<BigInteger> userIds) {
        return userAuthRepository.anyUserOnline(userIds);
    }

}
