package com.centram.core.dao;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.MediaFileRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.MediaFile;
import com.centram.domain.User;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Transactional
@Component
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public void updateStatus(Status status, BigInteger userId) {
        userRepository.updateStatus(status, userId);
    }

    public void changePassword(String password, BigInteger userId) {
        userRepository.changePassword(password, userId);
    }

    public void updatePassword(String password, BigInteger userId) {
        userRepository.updatePassword(password, userId);
    }

    @Transactional(readOnly = true)
    public MediaFile getProfilePhoto(BigInteger userId) {
        return mediaFileRepository.getMediaFile(EntityType.USER, MediaType.USER_PROFILE_IMAGE, userId);
    }

    @Transactional(readOnly = true)
    public User getUserById(BigInteger userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Page<User> getUsers(BigInteger organisationId, Pageable pageable) {
        return userRepository.getUsers(organisationId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getAppUsers(Pageable pageable) {
        return userRepository.getAppUsers( pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserByIds(List<BigInteger> ids, Pageable pageable) {
        return userRepository.getUserByIds(ids, pageable);
    }

    @Transactional(readOnly = true)
    public User getUserByUserName(String userName) {
        User user = userRepository.getUserByUserName(userName);
        if (user == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return user;
    }

}
