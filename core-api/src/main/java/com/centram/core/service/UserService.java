package com.centram.core.service;

import com.centram.common.dto.AuthRequestDTO;
import com.centram.common.dto.LoggedInUserDTO;
import com.centram.common.dto.OnboardRequestDTO;
import com.centram.common.dto.UserDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.JwtTokenUtil;
import com.centram.common.utility.Utility;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.UserVO;
import com.centram.core.dao.UserDao;
import com.centram.core.repository.PermissionRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.MediaFile;
import com.centram.domain.Permission;
import com.centram.domain.User;
import com.centram.domain.enumarator.ActivityType;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Duration;
import java.util.*;


@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;



    @Value("${jwt.token.prefix}")
    private String jwtTokenPrefix;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private OperationalEmailService operationalEmailService;

    /**
     * Sign In
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUserName(username);
        if (user != null) {
            UserVO userVO = new UserVO(userRepository.getUserByUserName(username));
            List<String> roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            List<Permission> permissions = permissionService.getPermissionByRoleIds(userVO.getRoles(), PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id"))).getContent();
            HashMap<String, String> modulePermissions = new HashMap<String, String>();
            String prm = null;
            for (Permission permission : permissions) {
                prm = null;
                if (modulePermissions.containsKey(permission.getModule().getName())) {
                    prm = modulePermissions.get(permission.getModule().getName());
                    prm = prm.concat(",").concat(permission.getAction().getName());
                } else {
                    prm = permission.getAction().getName();
                }
                modulePermissions.put(permission.getModule().getName(), prm);
            }
            LoggedInUserDTO loggedInUserDTO = new LoggedInUserDTO(userVO, modulePermissions);
            //save data in redis
            redisTemplate.opsForValue().set(username, loggedInUserDTO);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.SIGNIN));
            return loggedInUserDTO;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    /**
     * Sign Out
     * @return
     */
    public CommonResponse signOut() {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = jwtTokenUtil.getUsernameFromToken(loggedInUserDTO.getAuthToken().replaceAll("(?i)".concat(jwtTokenPrefix), ""));
        CommonResponse commonResponse = null;
        if (redisTemplate.delete(userName)) {
            commonResponse = new CommonResponse(Boolean.TRUE, "LOGGED_OUT_SUCCESS");
        } else {
            commonResponse = new CommonResponse(Boolean.FALSE, "LOGGED_OUT_FAILED");
        }
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.SIGNOUT));
        return commonResponse;
    }
























    public UserVO save(User user) {
        Boolean newOnboard = user.getId() == null;
        String password = null;
        if (newOnboard) {
            user.setUserName(Utility.getUniqueString(10));
            password = Utility.getUniqueString(8);
            user.setPassword(passwordEncoder.encode(password));
        }
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUserDTO.getOrganisationId() != null) {
            user.setOrganisation(organisationService.getOrganisationById(loggedInUserDTO.getOrganisationId()));
        }
        UserVO userVO = new UserVO(userDao.save(user));
        List<String> roleNames = new ArrayList<>();
        for (BigInteger roleId : userVO.getRoles()) {
            roleNames.add(roleService.getById(roleId).getName());
        }
        userVO.setRoleNames(roleNames);
        if (newOnboard) {
            Map<String, String> mailValues = new HashMap<>();
            mailValues.put("password", password);
            operationalEmailService.sendOnboardMail(userVO, mailValues);
        }
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, (newOnboard) ? ActivityType.ADD_USER : ActivityType.UPDATE_USER));
        return redisService.redisOperation(userVO.getId(), userVO);
    }

    public void updateStatus(Status status, BigInteger userId) {
        userDao.updateStatus(status, userId);
        UserVO userVo = this.getUserById(userId);
        userVo.setStatus(status);
        redisService.redisOperation(userId, userVo);
        activityLogService.save(new ActivityLog(userVo.getId(), (userVo.getOrganisationId() != null) ? userVo.getOrganisationId() : null, ActivityType.UPDATE_USER));
    }

    public UserVO getUserById(BigInteger userId) {
        UserVO userVO = redisService.getCachedUser(userId);
        if (userVO == null) {
            User user = userDao.getUserById(userId);
            userVO = new UserVO(user);
            List<String> roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            redisService.redisOperation(userVO.getId(), userVO);
        }
        return userVO;
    }

    public Page<UserVO> getUsers(Pageable pageable) {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> users = new ArrayList<>();
        if (loggedInUserDTO.getOrganisationId() == null) {
            users = userDao.getAppUsers(pageable).getContent();
        } else {
            users = userDao.getUsers(loggedInUserDTO.getOrganisationId(), pageable).getContent();
        }
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = null;
        List<String> roleNames = null;
        for (User user : users) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return new PageImpl<>(userVOS);
    }

    public Page<UserVO> getUserByIds(List<BigInteger> ids, Pageable pageable) {
        List<User> users = userDao.getUserByIds(ids, pageable).getContent();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        for (User user : users) {
            userVOS.add(new UserVO(user));
        }
        return new PageImpl<>(userVOS);
    }

    public UserVO getUserByUserName(String userName) {
        UserVO userVO = redisService.getCachedUser(userName);
        if (userVO == null) {
            User user = userDao.getUserByUserName(userName);
            userVO = new UserVO(user);
            redisService.redisOperation(userVO.getId(), userVO);
        }
        return userVO;
    }



    public CommonResponse forgotPassword(AuthRequestDTO authRequestDTO) {
        UserVO userVO = redisService.getCachedUser(authRequestDTO.getUsername());
        if (userVO == null) {
            User user = userDao.getUserByUserName(authRequestDTO.getUsername());
            userVO = new UserVO(user);
            redisService.redisOperation(userVO.getId(), userVO);
        }
        CommonResponse commonResponse = null;
        if (userVO != null) {
            String uuid = UUID.randomUUID().toString();
            Map<String, String> mailValues = new HashMap<>();
            mailValues.put("uuid", uuid);
            redisTemplate.opsForValue().set(uuid, userVO, Duration.ofHours(24));
            operationalEmailService.sendForgotPasswordMail(userVO, mailValues);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.FORGOT_PASSWORD));
            commonResponse = new CommonResponse(Boolean.TRUE, "RESET_PASSWORD_REQUEST_SUCCESS");
            //commonResponse = new CommonResponse(Boolean.TRUE, link);
        } else {
            commonResponse = new CommonResponse(Boolean.FALSE, "RESET_PASSWORD_REQUEST_FAILED");
        }
        return commonResponse;
    }

    public CommonResponse resetPassword(AuthRequestDTO authRequestDTO) {
        CommonResponse commonResponse = null;
        UserVO userVO = (UserVO) redisTemplate.opsForValue().get(authRequestDTO.getUsername());
        if (userVO != null) {
            redisTemplate.delete(authRequestDTO.getUsername());
            String encodedPassword = passwordEncoder.encode(authRequestDTO.getPassword());
            userDao.updatePassword(encodedPassword, userVO.getId());
            Map<String, String> mailValues = new HashMap<>();
            operationalEmailService.sendResetPasswordMail(userVO, mailValues);
            commonResponse = new CommonResponse(Boolean.TRUE, "RESET_PASSWORD_SUCCESS");
            //commonResponse = new CommonResponse(Boolean.TRUE, link);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.RESET_PASSWORD));
        } else {
            commonResponse = new CommonResponse(Boolean.FALSE, "RESET_PASSWORD_FAILED");
        }
        return commonResponse;
    }

    public CommonResponse onboardRequest(OnboardRequestDTO onboardRequestDTO) {
        CommonResponse commonResponse = null;
        operationalEmailService.sendOnboardRequestMail(onboardRequestDTO, new HashMap<>());
        commonResponse = new CommonResponse(Boolean.TRUE, "Request send successfully");
        return commonResponse;
    }

    public MediaFile getProfilePhoto(BigInteger userId) {
        return userDao.getProfilePhoto(userId);
    }

    public UserDTO getUserSettings() {
        UserDTO userDTO = new UserDTO();
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDTO.setMediaFile(userDao.getProfilePhoto(loggedInUserDTO.getUserId()));
        return userDTO;
    }

    public void changePassword(UserDTO userDTO) {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO userVO = this.getUserById(loggedInUserDTO.getUserId());
        userDao.changePassword(passwordEncoder.encode(userDTO.getNewPassword()), loggedInUserDTO.getUserId());
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.RESET_PASSWORD));
    }

    public UserDTO uploadUserProfile(HttpServletRequest request) {
        UserDTO userDTO = new UserDTO();
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        MediaFile mediaFile = userDao.getProfilePhoto(loggedInUserDTO.getUserId());
        mediaFile = (mediaFile == null) ? new MediaFile() : mediaFile;
        mediaFile.setEntityId(loggedInUserDTO.getUserId());
        mediaFile.setEntityType(EntityType.USER);
        mediaFile.setMediaType(MediaType.USER_PROFILE_IMAGE);
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator itemIterator = upload.getItemIterator(request);
            while (itemIterator.hasNext()) {
                FileItemStream item = itemIterator.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    String filename = item.getName();
                    mediaFile.setFileName(filename);
                    mediaFile.setFileType(new MimetypesFileTypeMap().getContentType(filename));
                    mediaFile.setContent(IOUtils.toByteArray(stream));
                    //OutputStream out = new FileOutputStream(filename);
                    //IOUtils.copy(stream, out);
                    stream.close();
                    //out.close();
                }
            }
        } catch (FileUploadException e) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
        }
        userDTO.setMediaFile(mediaService.save(mediaFile));
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.USER_PROFILE_PHOTO_UPLOAD));
        return userDTO;
    }

    public Page<ActivityLog> getActivityLogs(Pageable pageable) {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return activityLogService.getActivities(loggedInUserDTO.getUserId(), pageable);
    }
}
