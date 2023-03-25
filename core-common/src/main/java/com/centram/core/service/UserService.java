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
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.*;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
    private ObjectMapper objectMapper;

    @Autowired
    private MiscService miscService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private ProxyService proxyService;

    @Value("${jwt.token.prefix}")
    private String jwtTokenPrefix;

    /**
     * Sign In
     *
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            if (user.getStatus() != Status.ACTIVE) {
                throw new RuntimeException(GenericErrorCode.PROFILE_INACTIVE.getCode());
            }
            if (user.getOrganisation() != null && user.getOrganisation().getStatus() != Status.ACTIVE) {
                throw new RuntimeException(GenericErrorCode.PROFILE_INACTIVE.getCode());
            }
            UserVO userVO = new UserVO(user);
            userVO.setRoleNames(roleService.getByIds(userVO.getRoles()));
            List<Permission> permissions = permissionService.getPermissionByRoleIds(userVO.getRoles());
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
                                String actionNames = i.getActionName().concat(",").concat(permission.getAction().getName());
                                actionNames = String.join(",", new HashSet<String>(Arrays.asList(actionNames.split(","))));
                                i.setActionName(actionNames);
                            });
                } else {
                    modulePermissions.add(new PermissionVO(permission));
                }
            }
            LoggedInUser loggedInUser = new LoggedInUser(userVO, modulePermissions);
            UserAuth userAuth = userAuthService.save(new UserAuth(userVO.getId(), LocalDateTime.now(), null));
            loggedInUser.setUserAuthId(userAuth.getId());
            //save data in redis
            redisTemplate.opsForValue().set(email, loggedInUser);
            return loggedInUser;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
    }

    /**
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public LoggedInUser getUserByPrincipal(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            if (user.getStatus() != Status.ACTIVE) {
                throw new RuntimeException(GenericErrorCode.PROFILE_INACTIVE.getCode());
            }
            if (user.getOrganisation() != null && user.getOrganisation().getStatus() != Status.ACTIVE) {
                throw new RuntimeException(GenericErrorCode.PROFILE_INACTIVE.getCode());
            }
            UserVO userVO = new UserVO(user);
            userVO.setRoleNames(roleService.getByIds(userVO.getRoles()));
            List<Permission> permissions = permissionService.getPermissionByRoleIds(userVO.getRoles());
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
                                String actionNames = i.getActionName().concat(",").concat(permission.getAction().getName());
                                actionNames = String.join(",", new HashSet<String>(Arrays.asList(actionNames.split(","))));
                                i.setActionName(actionNames);
                            });
                } else {
                    modulePermissions.add(new PermissionVO(permission));
                }
            }
            LoggedInUser loggedInUser = new LoggedInUser(userVO, modulePermissions);
            UserAuth userAuth = userAuthService.save(new UserAuth(userVO.getId(), LocalDateTime.now(), null));
            loggedInUser.setUserAuthId(userAuth.getId());
            //save data in redis
            redisTemplate.opsForValue().set(email, loggedInUser);
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
    @Transactional(readOnly = false)
    public CommonResponse signOut() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = jwtTokenUtil.getUsernameFromToken(loggedInUser.getAuthToken().replaceAll("(?i)".concat(jwtTokenPrefix), ""));
        CommonResponse commonResponse = null;
        if (redisTemplate.delete(userName)) {
            UserAuth userAuth = userAuthService.getById(loggedInUser.getUserAuthId());
            userAuth.setSignOutAt(LocalDateTime.now());
            userAuthService.save(userAuth);
            commonResponse = new CommonResponse(Boolean.TRUE, "LOGGED_OUT_SUCCESS");
            SecurityContextHolder.clearContext();
        } else {
            commonResponse = new CommonResponse(Boolean.FALSE, "LOGGED_OUT_FAILED");
        }

        return commonResponse;
    }

    /**
     * Forgot Password
     *
     * @param authRequestDTO
     * @return
     */
    @Transactional(readOnly = true)
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
            String encodedPassword = passwordEncoder.encode(Utility.decode(authRequestDTO.getPassword()));
            userRepository.updatePassword(encodedPassword, LocalDateTime.now(), userVO.getId());
            Map<String, String> mailValues = new HashMap<>();
            appEmailService.sendResetPasswordMail(userVO, mailValues);

            commonResponse = new CommonResponse(Boolean.TRUE, "RESET_PASSWORD_SUCCESS");
            //commonResponse = new CommonResponse(Boolean.TRUE, link);
        } else {
            //commonResponse = new CommonResponse(Boolean.FALSE, "RESET_PASSWORD_FAILED");
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return commonResponse;
    }

    /**
     * Change Signed In User Password
     *
     * @param userDTO
     */
    @Transactional(readOnly = false)
    public void changePassword(UserDTO userDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRepository.updatePassword(passwordEncoder.encode(Utility.decode(userDTO.getNewPassword())), LocalDateTime.now(), loggedInUser.getUserId());

    }

    /**
     * Get Paginated user list
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<UserVO> getUsers(String email, String employeeId, Status status, String filterType, BigInteger vendorId, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        email = (!email.equals("")) ? "%" + email.toUpperCase() + "%" : null;
        employeeId = (!employeeId.equals("")) ? "%" + employeeId.toUpperCase() + "%" : null;
        filterType = (!filterType.equals("")) ? filterType.toUpperCase(Locale.ROOT) : null;
        Page<User> page = userRepository.getUsers(
                loggedInUser.getOrganisationId(),
                email,
                employeeId,
                status.ordinal(),
                filterType,
                vendorId,
                pageable
        );
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = null;
        List<String> roleNames = null;
        List<String> roleViewNames = null;
        List<Permission> permissions = null;
        List<Module> modules = null;
        List<Module> subModules = null;
        for (User user : page.getContent()) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            roleViewNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
                roleViewNames.add(roleService.getById(roleId).getDisplayName());
            }
            userVO.setRoleNames(roleNames);
            userVO.setRoleViewNames(roleViewNames);
            // prepare category & subcategory access list
            permissions = permissionService.getPermissionByRoleIds(userVO.getRoles());
            modules = permissions.stream()
                    .filter(i -> {
                        return !i.getModule().getAppModule() && i.getModule().getParentModuleId() == null;
                    })
                    .map(Permission::getModule)
                    .collect(Collectors.toList());
            subModules = permissions.stream()
                    .filter(i -> {
                        return !i.getModule().getAppModule() && i.getModule().getParentModuleId() != null;
                    })
                    .map(Permission::getModule)
                    .collect(Collectors.toList());
            userVO.setCategories(modules.stream().map(Module::getCustomerModuleName).map(i -> {
                return WordUtils.capitalizeFully(i);
            }).collect(Collectors.toSet()));
            userVO.setSubCategories(subModules.stream().map(Module::getCustomerModuleName).map(i -> {
                return WordUtils.capitalizeFully(i);
            }).collect(Collectors.toSet()));
            userVOS.add(userVO);
        }
        return new PaginatedList<UserVO>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), page.getPageable().getOffset(), page.getPageable().getPageNumber(), page.getPageable().getPageSize(), userVOS);
    }

    @Transactional(readOnly = true)
    public List<UserVO> getUsers(BigInteger id) {
        log.info("Pulling user data for {}.", id);
        Page<User> page = userRepository.getUsers(id, null, null, Status.ALL.ordinal(), null, null, Pageable.unpaged());
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = null;
        List<String> roleNames = null;
        List<String> roleViewNames = null;
        List<Permission> permissions = null;
        List<Module> modules = null;
        List<Module> subModules = null;
        for (User user : page.getContent()) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            roleViewNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
                roleViewNames.add(roleService.getById(roleId).getDisplayName());
            }
            userVO.setRoleNames(roleNames);
            userVO.setRoleViewNames(roleViewNames);
            // prepare category & subcategory access list
            permissions = permissionService.getPermissionByRoleIds(userVO.getRoles());
            modules = permissions.stream()
                    .filter(i -> {
                        return !i.getModule().getAppModule() && i.getModule().getParentModuleId() == null;
                    })
                    .map(Permission::getModule)
                    .collect(Collectors.toList());
            subModules = permissions.stream()
                    .filter(i -> {
                        return !i.getModule().getAppModule() && i.getModule().getParentModuleId() != null;
                    })
                    .map(Permission::getModule)
                    .collect(Collectors.toList());
            userVO.setCategories(modules.stream().map(Module::getCustomerModuleName).map(i -> {
                return WordUtils.capitalizeFully(i);
            }).collect(Collectors.toSet()));
            userVO.setSubCategories(subModules.stream().map(Module::getCustomerModuleName).map(i -> {
                return WordUtils.capitalizeFully(i);
            }).collect(Collectors.toSet()));
            userVOS.add(userVO);
        }
        return userVOS;
    }


    /**
     * Create or update user
     *
     * @param user
     * @return
     */
    @Transactional(readOnly = false)
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
        UserVO userVO = new UserVO(userRepository.save(user));
        List<String> roleNames = new ArrayList<>();
        for (BigInteger roleId : userVO.getRoles()) {
            roleNames.add(roleService.getById(roleId).getName());
        }
        userVO.setRoleNames(roleNames);
        if (newOnboard) {
            Map<String, Object> mailValues = new HashMap<>();
            mailValues.put("password", password);
            miscService.sendOnboardMail(userVO, mailValues);
        }
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
        userRepository.updateStatus(status, LocalDateTime.now(), userIds);

    }

    /**
     * Get user by Id
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
                null,
                null,
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
                    "Vendor",
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
                        uv.getVendor(),
                        uv.getStatus()
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
     * Get filtered users by module and acton
     *
     * @param moduleIds
     * @param actionName
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByModuleAndAction(List<BigInteger> moduleIds, String actionName) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<BigInteger> roleIds = permissionService.getRoleIdsByModuleAndAction(moduleIds, actionName);
        String roles = roleIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
        roles = ",(".concat(roles).concat("),");
        List<User> users = userRepository.getUsersByRoleIds(roles, loggedInUser.getOrganisationId());
        List<UserVO> userVOS = new ArrayList<UserVO>();
        List<String> roleNames = new ArrayList<>();
        UserVO userVO = null;
        for (User user : users) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return userVOS;
    }

    @Transactional(readOnly = true)
    public List<UserVO> getAgents(List<BigInteger> moduleIds, String actionName, BigInteger locationId, BigInteger organisationId) {
        List<BigInteger> roleIds = permissionService.getRoleIdsByModuleAndAction(moduleIds, actionName);
        String roles = roleIds.stream().map(String::valueOf).collect(Collectors.joining("|"));
        roles = ",(".concat(roles).concat("),");
        List<User> users = userRepository.getAgents(moduleIds.get(0), moduleIds.get(1), roles, locationId, organisationId);
        List<UserVO> userVOS = new ArrayList<UserVO>();
        List<String> roleNames = new ArrayList<>();
        UserVO userVO = null;
        for (User user : users) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return userVOS;
    }

    /**
     * get user by email
     *
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserByEmail(email, loggedInUser.getOrganisationId());
    }

    /**
     * get users by multiple email
     *
     * @param emails
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByEmails(List<String> emails) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> users = userRepository.getUsersByEmails(emails, loggedInUser.getOrganisationId());
        List<UserVO> userVOS = new ArrayList<UserVO>();
        for (User user : users) {
            userVOS.add(new UserVO(user));
        }
        return userVOS;
    }

    /**
     * get users by multiple email and organisation
     *
     * @param emails
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByEmailsAndOrganisation(List<String> emails, BigInteger organisationId) {
        List<User> users = userRepository.getUsersByEmails(emails, organisationId);
        List<UserVO> userVOS = new ArrayList<UserVO>();
        for (User user : users) {
            userVOS.add(new UserVO(user));
        }
        return userVOS;
    }

    /**
     * get user by emp id
     *
     * @param employeeId
     * @return
     */
    @Transactional(readOnly = true)
    public User getUserByEmployeeId(String employeeId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserByEmployeeId(employeeId, loggedInUser.getOrganisationId());
    }


    /**
     * upload users data
     *
     * @param multipartFile
     * @throws IOException
     */
    public void uploadUsersData(MultipartFile multipartFile) throws IOException {
        LoggedInUser loggedInUserDTO = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (multipartFile.getBytes().length == 0) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        List<Map<String, String>> values = new ArrayList<Map<String, String>>();
        List<String> commonHeaders = Arrays.asList("FIRST_NAME", "LAST_NAME", "EMAIL", "CONTACT_NO", "SEC_CONTACT_NO", "EMP_ID", "PROJECT_CODE", "ROLES", "DEPARTMENT", "LOCATION", "MANAGER_ID", "VENDOR");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
        ) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                values.add(
                        csvRecord.toMap()
                                .entrySet().stream()
                                .filter(i -> commonHeaders.contains(i.getKey()))
                                .collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()))
                );
            }
            //log.info("Uploaded Users data => {}", values);
            miscService.saveBulkUploadedData(values);
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    /**
     * get site admin users only
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getAdminUsers() {
        List<BigInteger> roleIds = new ArrayList<BigInteger>() {{
            add(BigInteger.valueOf(Long.valueOf("1")));
        }};
        List<UserVO> userVOS = new ArrayList<UserVO>();
        List<User> users = userRepository.getAdminUsers(roleIds.stream().map(String::valueOf).collect(Collectors.joining("|")));
        for (User user : users) {
            userVOS.add(new UserVO(user));
        }
        return userVOS;
    }

    /**
     * get user by roles
     *
     * @param roles
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByRoles(List<String> roles) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        List<Role> roleList = roleService.getByNames(roles);
        List<BigInteger> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toList());
        List<User> users = userRepository.getUsersByRoleIds(roleIds.stream().map(String::valueOf).collect(Collectors.joining("|")), loggedInUser.getOrganisationId());
        List<String> roleNames = new ArrayList<>();
        UserVO userVO = null;
        for (User user : users) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return userVOS;
    }

    /**
     * get user by roles Like
     *
     * @param roles
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByRoleNames(List<String> roles, BigInteger organisationId) {
        List<User> users = userRepository.getUsersByRoleNames(roles.stream().map(String::valueOf).collect(Collectors.joining("|")), organisationId);
        List<String> roleNames = new ArrayList<>();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = null;
        for (User user : users) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return userVOS;
    }

    /**
     * get users by roles and organisation
     *
     * @param roles
     * @param organisationId
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByRolesAndOrganisation(List<String> roles, BigInteger organisationId) {
        List<UserVO> userVOS = new ArrayList<UserVO>();
        List<Role> roleList = roleService.getByNames(roles);
        List<BigInteger> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toList());
        String roleFilterStr = ",(".concat(roleIds.stream().map(String::valueOf).collect(Collectors.joining("|"))).concat("),");
        List<User> users = userRepository.getUsersByRoleIds(roleFilterStr, organisationId);
        List<String> roleNames = new ArrayList<>();
        UserVO userVO = null;
        for (User user : users) {
            userVO = new UserVO(user);
            roleNames = new ArrayList<>();
            for (BigInteger roleId : userVO.getRoles()) {
                roleNames.add(roleService.getById(roleId).getName());
            }
            userVO.setRoleNames(roleNames);
            userVOS.add(userVO);
        }
        return userVOS;
    }

    /**
     * save multiple users
     *
     * @param users
     * @return
     */
    @Transactional(readOnly = false)
    public Iterable<User> saveUsers(List<User> users) {
        return userRepository.saveAll(users);
    }

    /*
    public Page<ActivityLog> getActivityLogs(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return activityLogService.getActivities(loggedInUser.getUserId(), pageable);
    }
     @Transactional(readOnly = true)
    public Page<UserVO> getUserByIds(List<BigInteger> ids, Pageable pageable) {
        List<User> users = userRepository.getUserByIds(ids, pageable).getContent();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        for (User user : users) {
            userVOS.add(new UserVO(user));
        }
        return new PageImpl<>(userVOS);
    }

    public MediaFile getProfilePhoto(BigInteger userId) {
        return mediaService.getMediaFile(EntityType.USER, MediaType.USER_PROFILE_IMAGE, userId);
    }
    public UserDTO getUserSettings() {
        UserDTO userDTO = new UserDTO();
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDTO.setMediaFile(this.getProfilePhoto(loggedInUser.getUserId()));
        return userDTO;
    }
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

                    stream.close();
                }
            }
        } catch (FileUploadException e) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
        }
        //userDTO.setMediaFile(mediaService.save(mediaFile));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.USER_PROFILE_PHOTO_UPLOAD));
        return userDTO;
    }
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
     */

    private User convert(User user, UserVO userVO) {
        UserVO manager = null;
        if (userVO.getManagerId() != null) {
            manager = this.getUserById(userVO.getManagerId());
        } else if (userVO.getMngrId() != null) {
            User mngr = this.getUserByEmployeeId(userVO.getMngrId());
            if (mngr != null) {
                manager = new UserVO(mngr);
            }
        }
        List<Role> roles = roleService.getByNames(userVO.getRoleNames());
        user.setId(userVO.getId());
        user.setFirstName(userVO.getFirstName());
        user.setLastName(userVO.getLastName());
        user.setEmail(userVO.getEmail());
        if (userVO.getPassword() != null)
            user.setPassword(passwordEncoder.encode(userVO.getPassword()));
        user.setEmployeeId(userVO.getEmployeeId());
        user.setManagerId(manager.getId());
        user.setContactNo(userVO.getContactNo());
        user.setSecContactNo(userVO.getSecContactNo());
        user.setProjectCode(userVO.getProjectCode());
        user.setStatus(Status.valueOf(userVO.getStatus()));
        user.setDepartment(departmentService.getById(userVO.getDepartmentId()));
        user.setLocation(locationService.getById(userVO.getLocationId()));
        user.setRoles(roles.stream().map(Role::getId).collect(Collectors.toList()));
        return user;
    }

    public void saveAll(List<UserVO> users, BigInteger id) {
        log.info("Pulling user data for {}.", id);
        Optional<User> optDepartment = Optional.empty();
        User user = null;
        if (users.size() > 0) {
            for (UserVO userVO : users) {
                try {
                    if (userVO.getId() != null) {
                        optDepartment = proxyService.getUser(userVO.getId());
                        if (optDepartment.isPresent()) {
                            user = this.convert(optDepartment.get(), userVO);
                            log.info("Saving user data {}.", user);
                            user = proxyService.saveUser(user);
                        } else {
                            user = this.convert(new User(), userVO);
                            user.setOrganisation(organisationService.getOrganisationById(id));
                            log.info("Saving user data {}.", user);
                            user = proxyService.saveUser(user);
                        }
                    } else {
                        user = this.convert(new User(), userVO);
                        user.setOrganisation(organisationService.getOrganisationById(id));
                        log.info("Saving user data {}.", user);
                        user = proxyService.saveUser(user);
                    }
                } catch (Exception e) {
                    //log.error(e.getStackTrace().toString());
                    //throw e;
                    continue;
                }
            }
        }
    }

}