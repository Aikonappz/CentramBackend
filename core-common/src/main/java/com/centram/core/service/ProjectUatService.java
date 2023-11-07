package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.ProjectUATRequestDTO;
import com.centram.common.dto.UatScriptReportDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ProjectUatRepository;
import com.centram.core.repository.ProjectUatScriptDetailRepository;
import com.centram.core.repository.ProjectUatScriptRepository;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.centram.domain.enumarator.Technology;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectUatService {

    private static final Logger log = LoggerFactory.getLogger(ProjectUatService.class);
    @Value("${app.data-file.path}")
    private String appDataFilePath;
    @Autowired
    private ProjectUatRepository projectUatRepository;
    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectUatScriptDetailRepository projectUatScriptDetailRepository;

    @Autowired
    private ProjectUatScriptRepository projectUatScriptRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MiscService miscService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProxyService proxyService;

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;

    /**
     * @param projectUatId
     * @return
     */
    @Transactional(readOnly = false)
    public ProjectUat markProjectUatComplete(LoggedInUser loggedInUser, BigInteger projectUatId) throws JsonProcessingException, InterruptedException {
        ProjectUat projectUat = projectUatRepository.getById(projectUatId);
        if (projectUat != null) {
            projectUat.setUatCycleComplete(true);
            projectUat.getProjectUatScripts().forEach(i -> {
                i.setUatComplete(true);
                /*i.getProjectUatScriptDetails().forEach(k -> {
                    k.setPass(true);
                    k.setRetestPass(true);
                });*/
            });
            projectUat = projectUatRepository.save(projectUat);
            //miscService.notifyUatScriptCompletion(loggedInUser, projectUatScript);
            return projectUat;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @param uatScriptId
     * @return
     */
    @Transactional(readOnly = false)
    public ProjectUatScript markProjectUatScriptComplete(LoggedInUser loggedInUser, BigInteger uatScriptId) throws JsonProcessingException, InterruptedException {
        ProjectUatScript projectUatScript = projectUatScriptRepository.getById(uatScriptId);
        if (projectUatScript != null) {
            projectUatScript.setUatComplete(true);
            /*projectUatScript.getProjectUatScriptDetails().forEach(i -> {
                i.setPass(true);
                i.setRetestPass(true);
            });*/
            projectUatScript = projectUatScriptRepository.save(projectUatScript);
            miscService.notifyUatScriptCompletion(projectUatScript);
            return projectUatScript;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @param projectUatScriptDetail
     * @return
     */
    @Transactional(readOnly = false)
    public ProjectUatScriptDetail updateProjectUatScriptDetail(LoggedInUser loggedInUser, ProjectUatScriptDetail projectUatScriptDetail) throws JsonProcessingException, InterruptedException {
        ProjectUatScriptDetail oldObj = projectUatScriptDetailRepository.getById(projectUatScriptDetail.getId());
        Boolean lastTestStatus = oldObj.getPass();
        Boolean lastReTestStatus = oldObj.getRetestPass();
        if (oldObj != null) {
            oldObj.setActualResult(projectUatScriptDetail.getActualResult());
            oldObj.setPass(projectUatScriptDetail.getPass());
            oldObj.setRetestDate(projectUatScriptDetail.getRetestDate());
            oldObj.setRetestPass(projectUatScriptDetail.getRetestPass());
            oldObj.setRemarks(projectUatScriptDetail.getRemarks());
            oldObj = projectUatScriptDetailRepository.save(oldObj);
            if (oldObj.getPass() != lastTestStatus || oldObj.getRetestPass() != lastReTestStatus)
                miscService.notifyParticipant(oldObj);
            oldObj.setSaved(Boolean.TRUE);
            return oldObj;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @param projectUatId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<ProjectUatScript> getProjectUatScripts(LoggedInUser loggedInUser, BigInteger projectUatId, Boolean isCustomer, Boolean isProjectManager, Boolean isAdmin, Boolean isConsultant, Pageable pageable) {
        String userType = null;
        if (isCustomer) userType = "PROJECT_OWNER";
        else if (isProjectManager) userType = "PROJECT_MANAGER";
        else if (isAdmin) userType = "ADMIN";
        else if (isConsultant) userType = "PROJECT_CONSULTANT";
        String emailExp = Stream.of(loggedInUser.getEmail()).map(String::valueOf).collect(Collectors.joining("|"));
        Page<BigInteger> page = projectUatRepository.getProjectUatScripts(userType, emailExp, projectUatId, pageable);
        List<ProjectUatScript> projectUatScripts = new LinkedList<ProjectUatScript>();
        ProjectUatScript projectUatScript;
        for (BigInteger i : page.getContent()) {
            projectUatScript = projectUatRepository.findProjectUATScriptById(i);
            long noOftestCase = projectUatScript.getProjectUatScriptDetails().size();
            long noOftestCasePassed = projectUatScript.getProjectUatScriptDetails().stream().filter(k -> {
                return ((k.getRetestPass() != null && k.getRetestPass()) || (k.getPass() != null && k.getPass()));
            }).count();
            if (projectUatScript.getUatComplete() && noOftestCase == noOftestCasePassed) {
                projectUatScript.setStatus("Completed");
            } else if (noOftestCasePassed == 0) {
                projectUatScript.setStatus("Not Started");
            } else {
                projectUatScript.setStatus("In Progress");
            }
            projectUatScripts.add(projectUatScript);
        }
        return new PaginatedList<ProjectUatScript>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), page.getPageable().getOffset(), page.getPageable().getPageNumber(), page.getPageable().getPageSize(), projectUatScripts);
    }

    /**
     * @param projectUATScriptId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<ProjectUatScriptDetail> findByProjectUATScriptId(BigInteger projectUATScriptId, Pageable pageable) {
        if (projectUATScriptId.compareTo(BigInteger.valueOf(-1)) == 0) {
            return new PaginatedList<ProjectUatScriptDetail>(Page.empty());
        }
        Page<ProjectUatScriptDetail> page = projectUatRepository.findByProjectUATScriptId(projectUATScriptId, pageable);
        List<ProjectUatScriptDetail> projectUatScriptDetails = page.getContent();
        for (int k = 0; k < projectUatScriptDetails.size(); k++) {
            if (!StringUtils.isBlank(projectUatScriptDetails.get(k).getActualResult()) && (projectUatScriptDetails.get(k).getActualResult().equalsIgnoreCase("As Expected") || projectUatScriptDetails.get(k).getActualResult().equalsIgnoreCase("Not as Expected"))) {
                projectUatScriptDetails.get(k).setSaved(Boolean.TRUE);
            } else {
                projectUatScriptDetails.get(k).setSaved(Boolean.FALSE);
            }
            if (k == 0) {
                projectUatScriptDetails.get(k).setPreviousStepPassed(true);
            } else {
                if ((projectUatScriptDetails.get(k - 1).getPass() != null && projectUatScriptDetails.get(k - 1).getPass()) || (projectUatScriptDetails.get(k - 1).getRetestPass() != null && projectUatScriptDetails.get(k - 1).getRetestPass())) {
                    projectUatScriptDetails.get(k).setPreviousStepPassed(true);
                } else {
                    break;
                }
            }
        }
        return new PaginatedList<ProjectUatScriptDetail>(page.getTotalElements(), page.getNumberOfElements(), page.getTotalPages(), page.getPageable().getOffset(), page.getPageable().getPageNumber(), page.getPageable().getPageSize(), projectUatScriptDetails);
    }

    /**
     * @param projectUATScriptId
     * @return
     */
    @Transactional(readOnly = true)
    public ProjectUatScript canMarkScriptComplete(BigInteger projectUATScriptId) {
        ProjectUatScript projectUatScript = projectUatRepository.findProjectUATScriptById(projectUATScriptId);
        long passed = projectUatScript.getProjectUatScriptDetails().stream().filter(i -> {
            //return ((i.getPass() != null && i.getPass()) && (i.getRetestPass() != null && i.getRetestPass()));
            return (i.getPass() != null && i.getPass() || i.getRetestPass() != null && i.getRetestPass());
        }).count();
        long total = projectUatScript.getProjectUatScriptDetails().size();
        projectUatScript.setCanMarkComplete(total == passed);
        return projectUatScript;
    }

    /**
     * @param projectId
     * @param moduleId
     * @param subModuleId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ProjectUat> getProjectUats(BigInteger projectId, BigInteger moduleId, BigInteger subModuleId) {
        List<ProjectUat> projectUats = projectUatRepository.getByProjectIdAndModuleIdAndSubModuleId(projectId, moduleId, subModuleId);
        projectUats.stream().forEach(i -> {
            long noOfScript = i.getProjectUatScripts().size();
            long noOftestCasePassed = i.getProjectUatScripts().stream().filter(ProjectUatScript::getUatComplete).count();
            //i.setCanMarkComplete(true);
            if (noOfScript == noOftestCasePassed) {
                i.setCanMarkComplete(true);
            }
            /*Module module = moduleService.getModule(i.getModuleId());
            i.setModuleName(module.getCustomerModuleName());
            module = moduleService.getModule(i.getSubModuleId());
            i.setSubModuleName(module.getCustomerModuleName());
            i.setUatScript(mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_SCRIPT, i.getId()));
            i.setUatManual(mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_MANUAL, i.getId()));*/
        });

        return projectUats;
    }

    /**
     * @param userId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<ProjectUat> uploadedScripts(BigInteger userId, Pageable pageable) {
        Page<ProjectUat> page = projectUatRepository.uploadedScripts(userId, pageable);
        page.getContent().forEach(i -> {
            Module module = moduleService.getModule(i.getModuleId());
            i.setModuleName(module.getCustomerModuleName());
            module = moduleService.getModule(i.getSubModuleId());
            i.setSubModuleName(module.getCustomerModuleName());
            i.setUatScript(mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_SCRIPT, i.getId()));
            i.setUatManual(mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_MANUAL, i.getId()));
        });
        return new PaginatedList<ProjectUat>(page);
    }

    /**
     * @param uatProjectId
     * @return
     */
    @Transactional(readOnly = true)
    public Set<ProjectUatScript> getProjectUatScriptsByUatProjectId(final BigInteger uatProjectId) {
        Set<ProjectUatScript> projectUatScripts = projectUatRepository.getProjectUatScriptsByUatProjectId(uatProjectId);
        projectUatScripts.forEach(i -> {
            i.setUatManual(mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_MANUAL, uatProjectId));
            i.setUatScript(mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_SCRIPT, uatProjectId));
        });
        return projectUatScripts;
    }

    /**
     * @param loggedInUser
     * @param multipartFile
     * @param projectUATRequestDTO
     * @return
     */
    public ProjectUat uploadScripts(LoggedInUser loggedInUser, MultipartFile multipartFile, ProjectUATRequestDTO projectUATRequestDTO) {
        try {
            if (multipartFile.getBytes().length == 0) {
                log.error("FILE_UPLOAD_ISSUE file don't have any content!");
                throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
            }
            String filePath = appDataFilePath + File.separator + multipartFile.getOriginalFilename();
            Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            return this.processUATExcel(filePath, loggedInUser, projectUATRequestDTO, multipartFile.getOriginalFilename(), multipartFile);
        } catch (IOException e) {
            log.error("FILE_UPLOAD_ISSUE {}", e.getMessage());
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
    }

    private String getRemark(LinkedHashSet<UATRemark> remarks) {
        StringBuilder str = new StringBuilder();
        if (!CollectionUtils.isEmpty(remarks)) {
            for (UATRemark uatRemark : remarks) {
                str.append(Optional.of(uatRemark.getName()).orElse("NA")).append("-").append(Optional.of(uatRemark.getEmail()).orElse("NA")).append("-").append(uatRemark.getDateTime() != null ? uatRemark.getDateTime().format(DateTimeFormatter.ofPattern(dateTimeFormat)) : "").append("-").append(uatRemark.getComment() != null ? uatRemark.getComment().replace("\n", "").replace("\r", "") : "").append("\n");
            }
        }
        return str.toString();
    }

    private Object[] prepareExcelRow(ProjectUatScript projectUatScript, ProjectUatScriptDetail projectUatScriptDetail, int rowNo) {
        if (rowNo == 2) {
            return new Object[]{projectUatScript.getTestCaseId() != null ? projectUatScript.getTestCaseId() : "", projectUatScript.getTestCaseDescription() != null ? projectUatScript.getTestCaseDescription() : "", projectUatScript.getTestScenario() != null ? projectUatScript.getTestScenario() : "", projectUatScriptDetail.getStep() != null ? String.valueOf(projectUatScriptDetail.getStep().intValue()) : "", projectUatScriptDetail.getAction() != null ? projectUatScriptDetail.getAction() : "", projectUatScriptDetail.getExpectedResult() != null ? projectUatScriptDetail.getExpectedResult() : "", projectUatScriptDetail.getActualResult() != null ? projectUatScriptDetail.getActualResult() : "", projectUatScriptDetail.getPass() != null ? projectUatScriptDetail.getPass() ? "Pass" : "Fail" : "", projectUatScriptDetail.getRetestDate() != null ? projectUatScriptDetail.getRetestDate().format(DateTimeFormatter.ofPattern(dateFormat)) : "", projectUatScriptDetail.getRetestPass() != null ? projectUatScriptDetail.getRetestPass() ? "Pass" : "Fail" : "", this.getRemark(projectUatScriptDetail.getRemarks())};
        } else {
            return new Object[]{"", "", "", projectUatScriptDetail.getStep() != null ? String.valueOf(projectUatScriptDetail.getStep().intValue()) : "", projectUatScriptDetail.getAction() != null ? projectUatScriptDetail.getAction() : "", projectUatScriptDetail.getExpectedResult() != null ? projectUatScriptDetail.getExpectedResult() : "", projectUatScriptDetail.getActualResult() != null ? projectUatScriptDetail.getActualResult() : "", projectUatScriptDetail.getPass() != null ? projectUatScriptDetail.getPass() ? "Pass" : "Fail" : "", projectUatScriptDetail.getRetestDate() != null ? projectUatScriptDetail.getRetestDate().format(DateTimeFormatter.ofPattern(dateFormat)) : "", projectUatScriptDetail.getRetestPass() != null ? projectUatScriptDetail.getRetestPass() ? "Pass" : "Fail" : "", this.getRemark(projectUatScriptDetail.getRemarks())};
        }
    }

    public MediaFile getUatScriptFileName(BigInteger projectUatId) {
        return mediaService.getMediaFile(EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_SCRIPT, projectUatId);
    }

    /**
     * @param loggedInUser
     * @param projectUatId
     * @return
     */
    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadUploadedScript(LoggedInUser loggedInUser, BigInteger projectUatId) {
        try {
            Optional<ProjectUat> optionalProjectUat = projectUatRepository.findById(projectUatId);
            if (optionalProjectUat.isPresent()) {
                ProjectUat projectUat = optionalProjectUat.get();
                //Blank workbook
                XSSFWorkbook workbook = new XSSFWorkbook();
                //Create a blank sheet
                XSSFSheet sheet;
                Map<String, Object[]> data = new TreeMap<String, Object[]>();
                int row = 1;
                Set<String> keys;
                int rowNo = 0;
                Row newRow;
                List<ProjectUatScriptDetail> projectUatScriptDetails;
                for (ProjectUatScript projectUatScript : projectUat.getProjectUatScripts()) {
                    sheet = workbook.createSheet(projectUatScript.getTestScriptName());
                    data = new LinkedHashMap<String, Object[]>();
                    row = 1;
                    data.put(String.valueOf(row), new Object[]{"Test Case ID", "Test Case Description", "Test Scenario", "Step", "Action", "Expected Result", "Actual Result", "Pass/Fail ", "Retest Date", "Retest Pass/Fail", "Remarks"});
                    projectUatScriptDetails = new ArrayList<ProjectUatScriptDetail>(projectUatScript.getProjectUatScriptDetails());
                    projectUatScriptDetails = projectUatScriptDetails.stream().sorted(Comparator.comparing(ProjectUatScriptDetail::getStep)).collect(Collectors.toList());
                    for (ProjectUatScriptDetail projectUatScriptDetail : projectUatScriptDetails) {
                        data.put(String.valueOf(++row), prepareExcelRow(projectUatScript, projectUatScriptDetail, row));
                    }
                    keys = data.keySet();
                    rowNo = 0;
                    for (String key : keys) {
                        newRow = sheet.createRow(rowNo++);
                        Object[] objArr = data.get(key);
                        int cellnum = 0;
                        for (Object obj : objArr) {
                            Cell cell = newRow.createCell(cellnum++);
                            if (obj instanceof String) cell.setCellValue((String) obj);
                            else if (obj instanceof Integer) cell.setCellValue((Integer) obj);
                        }
                    }
                }
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    workbook.write(bos);
                    return new ByteArrayInputStream(bos.toByteArray());
                }
            } else {
                throw new AppException(GenericErrorCode.RELEVANT_DATA_NOT_FOUND, new HashMap<String, Object>() {{
                    put("entity", "Project Uat");
                }});
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(GenericErrorCode.UAT_EXCEL_GENERATION_ISSUE);
        }
    }

    public ProjectUat uatScriptAlreadyUploaded(BigInteger moduleId, BigInteger subModuleId, BigInteger projectId) {
        Optional<ProjectUat> optionalProjectUat = Optional.ofNullable(projectUatRepository.findByProjectIdAndModuleIdAndSubModuleId(projectId, moduleId, subModuleId));
        if (optionalProjectUat.isPresent()) {
            return optionalProjectUat.get();
        }
        return null;
    }

    /**
     * @param filePath
     * @param loggedInUser
     * @param projectUATRequestDTO
     * @param fileName
     * @return
     */
    @Transactional(readOnly = true)
    private ProjectUat processUATExcel(String filePath, LoggedInUser loggedInUser, ProjectUATRequestDTO projectUATRequestDTO, String fileName, MultipartFile multipartFile) {
        try {
            Optional<ProjectUat> optionalProjectUat = Optional.ofNullable(projectUatRepository.findByProjectIdAndModuleIdAndSubModuleId(projectUATRequestDTO.getProjectId(), projectUATRequestDTO.getModuleId(), projectUATRequestDTO.getSubModuleId()));
            if (optionalProjectUat.isPresent()) {
                //delete if previously present
                projectUatRepository.delete(optionalProjectUat.get());
            }
            FileInputStream file = new FileInputStream(new File(filePath));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet;
            Iterator<Row> rowIterator;
            Row row;
            Iterator<Cell> cellIterator;
            Cell cell;
            String cellValue;
            int rw = 0;
            ProjectUatScript projectUatScript = new ProjectUatScript();
            ProjectUatScriptDetail projectUatScriptDetail = new ProjectUatScriptDetail();
            //project uat
            ProjectUat projectUat = new ProjectUat();
            projectUat.setUatCycleName(FilenameUtils.removeExtension(fileName).concat("-").concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYYYHHmmss"))));
            projectUat.setProject(projectService.getById(projectUATRequestDTO.getProjectId()));
            projectUat.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
            projectUat.setUploadedBy(new User(userService.getUserById(loggedInUser.getUserId())));
            projectUat.setTechnology(projectUATRequestDTO.getTechnology());
            projectUat.setModuleId(projectUATRequestDTO.getModuleId());
            projectUat.setSubModuleId(projectUATRequestDTO.getSubModuleId());
            projectUat.setProjectUatScripts(new LinkedHashSet<ProjectUatScript>());
            //project uat

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                //Get sheet from the workbook
                sheet = workbook.getSheet(workbook.getSheetName(i));
                projectUatScript = new ProjectUatScript();
                projectUatScript.setTestScriptName(workbook.getSheetName(i));
                projectUatScript.setProjectUatScriptDetails(new LinkedHashSet<ProjectUatScriptDetail>());
                //Iterate through each rows one by one
                rw = 0;
                rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    if (rw == 0) {
                        rw++;
                        continue;
                    }
                    //For each row, iterate through all the columns
                    cellIterator = row.cellIterator();
                    projectUatScriptDetail = new ProjectUatScriptDetail();
                    while (cellIterator.hasNext()) {
                        cell = cellIterator.next();
                        cellValue = this.getCellValue(cell);
                        if (cell.getAddress().getColumn() == 0) {
                            //Test Case ID
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Test Case ID!"));
                                }
                                projectUatScript.setTestCaseId(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 1) {
                            //Test Case Description
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Test Case Description!"));
                                }
                                projectUatScript.setTestCaseDescription(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 2) {
                            //Test Scenario
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Test Scenario!"));
                                }
                                projectUatScript.setTestScenario(cellValue);
                                projectUat.getProjectUatScripts().add(projectUatScript);
                            }
                        } else if (cell.getAddress().getColumn() == 3) {
                            //Step
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Step is mandatory!"));
                            }
                            projectUatScriptDetail.setStep(Double.valueOf(cellValue));
                        } else if (cell.getAddress().getColumn() == 4) {
                            //Action
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Action is mandatory!"));
                            }
                            projectUatScriptDetail.setAction(cellValue);
                        } else if (cell.getAddress().getColumn() == 5) {
                            //Expected Result
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Expected Result is mandatory!"));
                            }
                            projectUatScriptDetail.setExpectedResult(cellValue);
                        } else if (cell.getAddress().getColumn() == 6) {
                            //Actual Result
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setActualResult(cellValue);
                            } else {
                                projectUatScriptDetail.setActualResult(null);
                            }
                        } else if (cell.getAddress().getColumn() == 7) {
                            //Pass/Fail
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setPass(cellValue.equalsIgnoreCase("Pass"));
                            } else {
                                projectUatScriptDetail.setPass(null);
                            }
                        } else if (cell.getAddress().getColumn() == 8) {
                            // Retest Date
                            if (!cellValue.trim().isEmpty()) {
                                try {
                                    //isCellDateFormatted(cell);
                                    projectUatScriptDetail.setRetestDate(LocalDate.parse(cellValue, DateTimeFormatter.ofPattern(dateFormat)));
                                } catch (Exception e) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Retest Date has wrong date value!"));
                                }
                            } else {
                                projectUatScriptDetail.setRetestDate(null);
                            }
                        } else if (cell.getAddress().getColumn() == 9) {
                            //Retest Pass/Fail
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setRetestPass(cellValue.equalsIgnoreCase("Pass"));
                            }
                        } else if (cell.getAddress().getColumn() == 10) {
                            //Remarks
                            if (!cellValue.trim().isEmpty()) {
                                //UATRemark uatRemark = new UATRemark(projectUat.getUploadedBy().getFirstName() + " " + projectUat.getUploadedBy().getLastName(), projectUat.getUploadedBy().getEmail(), cellValue, LocalDateTime.now());
                                UATRemark uatRemark = new UATRemark(projectUat.getUploadedBy().getFirstName() + " " + projectUat.getUploadedBy().getLastName(), projectUat.getUploadedBy().getEmail(), cellValue);
                                projectUatScriptDetail.setRemarks(new LinkedHashSet<UATRemark>() {{
                                    add(uatRemark);
                                }});
                            } else {
                                projectUatScriptDetail.setRemarks(null);
                            }
                        }
                    }
                    if (projectUatScriptDetail.getStep() != null) {
                        projectUat.getProjectUatScripts().stream().reduce((first, second) -> second).get().getProjectUatScriptDetails().add(projectUatScriptDetail);
                    }
                    rw++;
                }
            }
            //log.info("projectUat => {} ", objectMapper.writeValueAsString(projectUat));
            projectUat = proxyService.saveProjectUAT(projectUat);
            mediaService.uploadMediaFile(projectUat.getId(), EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_SCRIPT, "NA", new MultipartFile[]{multipartFile}, loggedInUser);
            file.close();
            miscService.notifyUatScriptUpload(projectUat);
            return projectUat;
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.UAT_DATA_EXIST, new HashMap<String, Object>() {{
                put("entity", "UAT");
            }});
        } catch (IOException | InterruptedException e) {
            log.error("Error detail - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
        String cellValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
            case FORMULA:
            case BLANK:
                cellValue = cell.getStringCellValue();
                break;
        }
        return cellValue;
    }

    /**
     * @param cell
     * @param sheetName
     * @param errorMessage
     * @return
     */

    private Map<String, Object> prepareErrorContext(Cell cell, String sheetName, String errorMessage) {
        return new HashMap<String, Object>() {{
            put("sheet", sheetName);
            put("col", cell.getAddress().getColumn() + 1);
            put("row", cell.getAddress().getRow() + 1);
            put("errorMessage", errorMessage);
        }};
    }

    /**
     * @param start
     * @param end
     * @param technology
     * @param moduleId
     * @param subModuleId
     * @param projectId
     * @param projectUatId
     * @param projectUatScriptId
     * @param uploadedByUserId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<UatScriptReportDTO> uatScriptReport(LocalDateTime start, LocalDateTime end, Technology technology, BigInteger moduleId, BigInteger subModuleId, BigInteger projectId, BigInteger projectUatId, BigInteger projectUatScriptId, BigInteger uploadedByUserId, Pageable pageable) {
        return projectUatRepository.uatScriptReport(start, end, technology, moduleId, subModuleId, projectId, projectUatId, projectUatScriptId, uploadedByUserId, pageable);
    }

    /**
     * @param start
     * @param end
     * @param technology
     * @param moduleId
     * @param subModuleId
     * @param projectId
     * @param projectUatId
     * @param uploadedByUserId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ProjectUat> uatReport(LoggedInUser loggedInUser, LocalDateTime start, LocalDateTime end, Integer technology, BigInteger moduleId, BigInteger subModuleId, BigInteger projectId, BigInteger projectUatId, BigInteger uploadedByUserId, String status, Pageable pageable) {
        List<String> roleList = roleService.getByIds(loggedInUser.getRoles());
        String userType = "";
        if (roleList.contains("ORG_ADMIN")) {
            userType = "ADMIN";
        } else if (roleList.contains("ORG_UAT_CONSULTANT")) {
            userType = "PROJECT_CONSULTANT";
        } else if (roleList.contains("ORG_PROJECT_STAKEHOLDER")) {
            userType = "PROJECT_OWNER";
        } else if (roleList.contains("ORG_ADMIN_PROJECT")) {
            userType = "PROJECT_MANAGER";
        }
        List<String> emails = new ArrayList<String>() {{
            add(loggedInUser.getEmail());
        }};
        String emailExp = emails.stream().map(String::valueOf).collect(Collectors.joining("|"));
        return projectUatRepository.uatReport(userType, emailExp, start, end, technology, moduleId, subModuleId, projectId, projectUatId, uploadedByUserId, status, pageable);
    }
}