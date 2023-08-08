package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.ProjectUATRequestDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ProjectUatRepository;
import com.centram.core.repository.ProjectUatScriptDetailRepository;
import com.centram.core.repository.ProjectUatScriptRepository;
import com.centram.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    /**
     * @param uatScriptId
     * @return
     */
    @Transactional(readOnly = false)
    public ProjectUatScript markUATScriptTestComplete(LoggedInUser loggedInUser, BigInteger uatScriptId) throws JsonProcessingException, InterruptedException {
        ProjectUatScript projectUatScript = projectUatScriptRepository.getById(uatScriptId);
        if (projectUatScript != null) {
            projectUatScript.setUatComplete(true);
            projectUatScript.getProjectUatScriptDetails().forEach(i -> {
                i.setPass(true);
            });
            projectUatScript = projectUatScriptRepository.save(projectUatScript);
            miscService.notifyUatScriptCompletion(loggedInUser, projectUatScript);
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
        if (oldObj != null) {
            oldObj.setActualResult(projectUatScriptDetail.getActualResult());
            oldObj.setPass(projectUatScriptDetail.getPass());
            oldObj.setRetestDate(projectUatScriptDetail.getRetestDate());
            oldObj.setRetestPass(projectUatScriptDetail.getRetestPass());
            oldObj.setRemarks(projectUatScriptDetail.getRemarks());
            oldObj = projectUatScriptDetailRepository.save(oldObj);
            miscService.notifyParticipant(loggedInUser, oldObj);
            return oldObj;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @param projectUATScriptId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<ProjectUatScriptDetail> findByProjectUATScriptId(BigInteger projectUATScriptId, Pageable pageable) {
        Page<ProjectUatScriptDetail> page = projectUatRepository.findByProjectUATScriptId(projectUATScriptId, pageable);
        return new PaginatedList<ProjectUatScriptDetail>(page);
    }

    /**
     * @param projectId
     * @param moduleId
     * @param subModuleId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ProjectUat> getProjectUats(BigInteger projectId, BigInteger moduleId, BigInteger subModuleId) {
        return projectUatRepository.getByProjectIdAndModuleIdAndSubModuleId(projectId, moduleId, subModuleId);
    }

    /**
     * @param uatProjectId
     * @return
     */
    @Transactional(readOnly = true)
    public Set<ProjectUatScript> getProjectUatScriptsByUatProjectId(BigInteger uatProjectId) {
        return projectUatRepository.getProjectUatScriptsByUatProjectId(uatProjectId);
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
            return this.processUATExcel(filePath, loggedInUser, projectUATRequestDTO, multipartFile.getOriginalFilename());
        } catch (IOException e) {
            log.error("FILE_UPLOAD_ISSUE {}", e.getMessage());
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
    }

    /**
     * @param filePath
     * @param loggedInUser
     * @param projectUATRequestDTO
     * @param fileName
     * @return
     */
    @Transactional(readOnly = false)
    private ProjectUat processUATExcel(String filePath, LoggedInUser loggedInUser, ProjectUATRequestDTO projectUATRequestDTO, String fileName) {
        try {
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
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Test Case ID!"));
                                }
                                projectUatScript.setTestCaseId(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 1) {
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Test Case Description!"));
                                }
                                projectUatScript.setTestCaseDescription(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 2) {
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Test Scenario!"));
                                }
                                projectUatScript.setTestScenario(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 3) {
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setTestScenarioJobId(cellValue);
                            } else {
                                projectUatScriptDetail.setTestScenarioJobId(null);
                            }
                        } else if (cell.getAddress().getColumn() == 4) {
                            if (rw == 1) {
                                try {
                                    HSSFDateUtil.isCellDateFormatted(cell);
                                    projectUatScript.setPlannedDate(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                    projectUat.getProjectUatScripts().add(projectUatScript);
                                } catch (Exception e) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), " should have Planned Date!"));
                                }
                            }
                        } else if (cell.getAddress().getColumn() == 5) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Step is mandatory!"));
                            }
                            projectUatScriptDetail.setStep(Double.valueOf(cellValue));
                        } else if (cell.getAddress().getColumn() == 6) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Action is mandatory!"));
                            }
                            projectUatScriptDetail.setAction(cellValue);
                        } else if (cell.getAddress().getColumn() == 7) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Expected Result is mandatory!"));
                            }
                            projectUatScriptDetail.setExpectedResult(cellValue);
                        } else if (cell.getAddress().getColumn() == 8) {
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setActualResult(cellValue);
                            } else {
                                projectUatScriptDetail.setActualResult(null);
                            }
                        } else if (cell.getAddress().getColumn() == 9) {
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setPass(cellValue.equalsIgnoreCase("Pass"));
                            } else {
                                projectUatScriptDetail.setPass(false);
                            }
                        } else if (cell.getAddress().getColumn() == 10) {
                            if (!cellValue.trim().isEmpty()) {
                                try {
                                    HSSFDateUtil.isCellDateFormatted(cell);
                                    projectUatScriptDetail.setRetestDate(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                } catch (Exception e) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Retest Date has wrong date value!"));
                                }
                            } else {
                                projectUatScriptDetail.setRetestDate(null);
                            }
                        } else if (cell.getAddress().getColumn() == 11) {
                            if (!cellValue.trim().isEmpty()) {
                                projectUatScriptDetail.setRetestPass(cellValue.equalsIgnoreCase("Pass"));
                            } else {
                                projectUatScriptDetail.setRetestPass(false);
                            }
                        } else if (cell.getAddress().getColumn() == 12) {
                            if (!cellValue.trim().isEmpty()) {
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
            file.close();
            log.debug("projectUat => {} ", objectMapper.writeValueAsString(projectUat));
            projectUat = projectUatRepository.save(projectUat);
            miscService.notifyUatScriptUpload(loggedInUser, projectUat);
            return projectUat;
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
}