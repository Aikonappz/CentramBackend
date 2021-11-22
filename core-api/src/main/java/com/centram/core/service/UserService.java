package com.centram.core.service;

import com.centram.common.dto.AuthRequestDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.UserDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.JwtTokenUtil;
import com.centram.common.utility.PaginatedList;
import com.centram.common.utility.Utility;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.PermissionVO;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.UserRepository;
import com.centram.domain.*;
import com.centram.domain.enumarator.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
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
import java.io.*;
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

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private AppEmailService appEmailService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Value("${jwt.token.prefix}")
    private String jwtTokenPrefix;

    @Value("${app.firebase.topic}")
    private String appFirebaseTopic;

    /**
     * Sign In
     *
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            UserVO userVO = new UserVO(user);
            userVO.setRoleNames(roleService.getByIds(userVO.getRoles()));
            List<Permission> permissions = permissionService.getPermissionByRoleIds(userVO.getRoles(), PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id"))).getContent();
            List<PermissionVO> modulePermissions = new ArrayList<PermissionVO>();
            for (Permission permission : permissions) {
                Boolean alreadyExist = modulePermissions.stream()
                        .filter(o -> o.getModuleId().equals(permission.getModule().getId()))
                        .findFirst().isPresent();
                if (alreadyExist) {
                    modulePermissions.stream()
                            .filter(o -> o.getModuleId().equals(permission.getModule().getId()))
                            .findFirst()
                            .ifPresent(i -> {
                                String actionNames = i.getActionNames().concat(",").concat(permission.getAction().getName());
                                i.setActionNames(actionNames);
                            });
                } else {
                    modulePermissions.add(new PermissionVO(permission));
                }
            }
            /*HashMap<String, String> modulePermissions = new HashMap<String, String>();
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
            }*/
            LoggedInUser loggedInUser = new LoggedInUser(userVO, modulePermissions);
            //save data in redis
            redisTemplate.opsForValue().set(email, loggedInUser);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.SIGNIN));
            return loggedInUser;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
    }

    /**
     * Sign Out
     *
     * @return
     */
    public CommonResponse signOut() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = jwtTokenUtil.getUsernameFromToken(loggedInUser.getAuthToken().replaceAll("(?i)".concat(jwtTokenPrefix), ""));
        CommonResponse commonResponse = null;
        if (redisTemplate.delete(userName)) {
            commonResponse = new CommonResponse(Boolean.TRUE, "LOGGED_OUT_SUCCESS");
        } else {
            commonResponse = new CommonResponse(Boolean.FALSE, "LOGGED_OUT_FAILED");
        }
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.SIGNOUT));
        return commonResponse;
    }

    /**
     * Forgot Password
     *
     * @param authRequestDTO
     * @return
     */
    @Transactional
    public CommonResponse forgotPassword(AuthRequestDTO authRequestDTO) {
        UserVO userVO = redisService.getCachedUser(authRequestDTO.getUsername());
        if (userVO == null) {
            User user = userRepository.getUserByEmail(authRequestDTO.getUsername());
            if (user != null) {
                userVO = new UserVO(user);
                redisService.redisOperation(userVO.getId(), userVO);
            }
        }
        CommonResponse commonResponse = null;
        if (userVO != null) {
            String uuid = UUID.randomUUID().toString();
            Map<String, String> mailValues = new HashMap<>();
            mailValues.put("uuid", uuid);
            log.info("uuid => {}", uuid);
            redisTemplate.opsForValue().set(uuid, userVO, Duration.ofHours(24));
            appEmailService.sendForgotPasswordMail(userVO, mailValues);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.FORGOT_PASSWORD));
            commonResponse = new CommonResponse(Boolean.TRUE, "RESET_PASSWORD_REQUEST_SUCCESS");
            //commonResponse = new CommonResponse(Boolean.TRUE, link);
        } else {
            //commonResponse = new CommonResponse(Boolean.FALSE, "RESET_PASSWORD_REQUEST_FAILED");
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return commonResponse;
    }

    /**
     * Reset Password
     *
     * @param authRequestDTO
     * @return
     */
    @Transactional
    public CommonResponse resetPassword(AuthRequestDTO authRequestDTO) {
        CommonResponse commonResponse = null;
        UserVO userVO = (UserVO) redisTemplate.opsForValue().get(authRequestDTO.getUsername());
        if (userVO != null) {
            redisTemplate.delete(authRequestDTO.getUsername());
            String encodedPassword = passwordEncoder.encode(authRequestDTO.getPassword());
            userRepository.updatePassword(encodedPassword, userVO.getId());
            Map<String, String> mailValues = new HashMap<>();
            appEmailService.sendResetPasswordMail(userVO, mailValues);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.RESET_PASSWORD));
            commonResponse = new CommonResponse(Boolean.TRUE, "RESET_PASSWORD_SUCCESS");
            //commonResponse = new CommonResponse(Boolean.TRUE, link);
        } else {
            //commonResponse = new CommonResponse(Boolean.FALSE, "RESET_PASSWORD_FAILED");
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return commonResponse;
    }

    /**
     * Create or update user
     *
     * @param user
     * @return
     */
    @Transactional
    public UserVO save(User user) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean newOnboard = user.getId() == null;
        String password = null;
        if (newOnboard) {
            password = Utility.getUniqueString(8);
            user.setPassword(passwordEncoder.encode(password));
        }
        if (loggedInUser.getOrganisationId() != null) {
            user.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        } else {
            if (user.getOrganisation() != null && user.getOrganisation().getId() != null) {
                user.setOrganisation(new Organisation(user.getOrganisation().getId(), user.getOrganisation().getVersion()));
            }
        }
        /*if (user.getDepartment().getId() != null) {
            user.setDepartment(departmentService.getById(user.getDepartment().getId()));
        }
        if (user.getLocation().getId() != null) {
            user.setLocation(locationService.getById(user.getLocation().getId()));
        }*/
        UserVO userVO = new UserVO(userRepository.save(user));
        List<String> roleNames = new ArrayList<>();
        for (BigInteger roleId : userVO.getRoles()) {
            roleNames.add(roleService.getById(roleId).getName());
        }
        userVO.setRoleNames(roleNames);
        if (newOnboard) {
            Map<String, String> mailValues = new HashMap<>();
            mailValues.put("password", password);
            appEmailService.sendOnboardMail(userVO, mailValues);
            /* fetch onboard related notification config */
            List<AppConfiguration> appConfigs = appConfigService.getAppConfigurations(Arrays.asList("ONBOARD_NOTIFICATION"));
            /* save notification to db*/
            Notification notification = new Notification();
            notification.setStatus(Status.PUSHED);
            notification.setNotificationTitle(appConfigs.get(0).getConfigurationProperties().get("title").toString());
            notification.setNotificationBody(appConfigs.get(0).getConfigurationProperties().get("body").toString());
            notification.setNotificationType(NotificationType.INFO);
            notification.setUser(new User(userVO.getVersion(), userVO.getId()));
            notification = notificationService.save(notification);
            /* push firebase notification */
            /*try {
                firebaseMessagingService.sendNotification(notification, appFirebaseTopic);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                throw new AppException(GenericErrorCode.FIREBASE_INTEGRATION_ISSUE);
            }*/
        }
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, (newOnboard) ? ActivityType.ADD_USER : ActivityType.UPDATE_USER));
        return userVO;
    }

    /**
     * Update user status
     *
     * @param status
     * @param userIds
     */
    @Transactional
    public void updateUsersStatus(Status status, List<BigInteger> userIds) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRepository.updateStatus(status, userIds);
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.UPDATE_USER));
    }

    /**
     * Get user bu Id
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public UserVO getUserById(BigInteger userId) {
        User user = userRepository.getUserById(userId);
        if (user != null) {
            UserVO userVO = null;
            userVO = new UserVO(user);
            List<String> roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            return userVO;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * Get Paginated user list
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<UserVO> getUsers(String email, String employeeId, Status status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        email = (!email.equals("")) ? "%" + email.toUpperCase() + "%" : null;
        employeeId = (!employeeId.equals("")) ? "%" + employeeId.toUpperCase() + "%" : null;
        Page<User> page = userRepository.getUsers(
                loggedInUser.getOrganisationId(),
                email,
                employeeId,
                status.ordinal(),
                pageable
        );
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = null;
        List<String> roleNames = null;
        for (User user : page.getContent()) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return new PaginatedList<UserVO>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), page.getPageable().getOffset(), page.getPageable().getPageNumber(), page.getPageable().getPageSize(), userVOS);
    }

    /**
     * Download Users as CSV
     *
     * @return
     */
    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadUsers() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = null;
        List<String> roleNames = null;
        Page<User> page = userRepository.getUsers(
                loggedInUser.getOrganisationId(),
                null,
                null,
                Status.ALL.ordinal(),
                Pageable.unpaged()
        );
        for (User user : page.getContent()) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList(
                    "First Name",
                    "Last Name",
                    "Email",
                    "Contact No",
                    "Sec. ContactNo",
                    "Employee Id.",
                    "Project Code",
                    "Roles",
                    "Location",
                    "Department",
                    "Status"
            );
            csvPrinter.printRecord(data);
            for (UserVO uv : userVOS) {
                data = Arrays.asList(
                        uv.getFirstName(),
                        uv.getLastName(),
                        uv.getEmail(),
                        uv.getContactNo(),
                        uv.getSecContactNo(),
                        uv.getEmployeeId(),
                        uv.getProjectCode(),
                        String.join(",", uv.getRoleNames()),
                        uv.getLocation(),
                        uv.getDepartment(),
                        uv.getStatus().toString()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    /**
     * Get filtered users b id
     *
     * @param ids
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<UserVO> getUserByIds(List<BigInteger> ids, Pageable pageable) {
        List<User> users = userRepository.getUserByIds(ids, pageable).getContent();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        for (User user : users) {
            userVOS.add(new UserVO(user));
        }
        return new PageImpl<>(userVOS);
    }

    /**
     * Get User by UserName
     *
     * @param userName
     * @return
     */
    @Transactional(readOnly = true)
    public UserVO getUserByUserName(String userName) {
        User user = userRepository.getUserByEmail(userName);
        UserVO userVO = null;
        if (user != null) {
            userVO = new UserVO(user);
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return userVO;
    }

    /**
     * Get User Profile Image
     *
     * @param userId
     * @return
     */
    public MediaFile getProfilePhoto(BigInteger userId) {
        return mediaService.getMediaFile(EntityType.USER, MediaType.USER_PROFILE_IMAGE, userId);
    }

    /**
     * Get Signed In User Profile
     *
     * @return
     */
    public UserDTO getUserSettings() {
        UserDTO userDTO = new UserDTO();
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDTO.setMediaFile(this.getProfilePhoto(loggedInUser.getUserId()));
        return userDTO;
    }

    /**
     * Change Signed In User Password
     *
     * @param userDTO
     */
    @Transactional
    public void changePassword(UserDTO userDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO userVO = this.getUserById(loggedInUser.getUserId());
        userRepository.changePassword(passwordEncoder.encode(userDTO.getNewPassword()), loggedInUser.getUserId());
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.RESET_PASSWORD));
    }

    /**
     * Upload Signed In User Profile Image
     *
     * @param request
     * @return
     */
    @Transactional
    public UserDTO uploadUserProfile(HttpServletRequest request) {
        UserDTO userDTO = new UserDTO();
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        MediaFile mediaFile = this.getProfilePhoto(loggedInUser.getUserId());
        mediaFile = (mediaFile == null) ? new MediaFile() : mediaFile;
        mediaFile.setEntityId(loggedInUser.getUserId());
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
                    /*OutputStream out = new FileOutputStream(filename);
                    IOUtils.copy(stream, out);
                    out.close();*/
                    stream.close();
                }
            }
        } catch (FileUploadException e) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
        }
        userDTO.setMediaFile(mediaService.save(mediaFile));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.USER_PROFILE_PHOTO_UPLOAD));
        return userDTO;
    }

    public Page<ActivityLog> getActivityLogs(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return activityLogService.getActivities(loggedInUser.getUserId(), pageable);
    }
}