package com.centram.core.service;


import com.centram.common.dto.ProjectUATRequestDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ProjectUatRepository;
import com.centram.core.repository.ProjectUatScriptDetailRepository;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import com.centram.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.ZoneId;
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
    private ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ProjectUatScriptDetail saveProjectUatScriptDetail(ProjectUatScriptDetail projectUatScriptDetail){
        return projectUatScriptDetailRepository.save(projectUatScriptDetail);
    }

    @Transactional(readOnly = true)
    public PaginatedList<ProjectUatScriptDetail> getProjectUatScriptDetails(BigInteger projectId, BigInteger moduleId, BigInteger subModuleId, BigInteger projectUATScriptId, Pageable pageable) {
        Page<ProjectUatScriptDetail> page = projectUatRepository.findByProjectIdAndModuleIdAndSubModuleIdAndProjectUATScriptId(projectId, moduleId, subModuleId, projectUATScriptId, pageable)
                .getContent().stream()
                .forEach(i->{
                    if(!i.getRemarks().isEmpty()){
                        i.setRemark(i.getRemarks().get(i.getRemarks().size()));
                    }
                });
        return new PaginatedList<ProjectUatScriptDetail>();
    }

    /**
     * @param projectId
     * @param moduleId
     * @param subModuleId
     * @return
     */
    @Transactional(readOnly = true)
    public Set<ProjectUatScript> getProjectUatScripts(BigInteger projectId, BigInteger moduleId, BigInteger subModuleId) {
        Set<ProjectUatScript> projectUatScripts = new LinkedHashSet<ProjectUatScript>();
        for (Set<ProjectUatScript> projectUatScript : projectUatRepository.findByProjectIdAndModuleIdAndSubModuleId(projectId, moduleId, subModuleId)) {
            projectUatScripts.addAll(projectUatScript);
        }
        return projectUatScripts;
    }

    /**
     * @param userId
     * @param organisationId
     * @param multipartFile
     * @param projectUATRequestDTO
     * @return
     */
    public ProjectUat uploadScripts(BigInteger userId, BigInteger organisationId, MultipartFile multipartFile, ProjectUATRequestDTO projectUATRequestDTO) {
        try {
            if (multipartFile.getBytes().length == 0) {
                log.error("FILE_UPLOAD_ISSUE file don't have any content!");
                throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
            }
            String filePath = appDataFilePath + File.separator + multipartFile.getOriginalFilename();
            Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            return this.processUATExcel(filePath, userId, organisationId, projectUATRequestDTO);
        } catch (IOException e) {
            log.error("FILE_UPLOAD_ISSUE {}", e.getMessage());
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
    }

    /**
     * @param filePath
     * @param userId
     * @param organisationId
     * @param projectUATRequestDTO
     * @return
     */
    @Transactional(readOnly = false)
    private ProjectUat processUATExcel(String filePath, BigInteger userId, BigInteger organisationId, ProjectUATRequestDTO projectUATRequestDTO) {
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet;
            Iterator<Row> rowIterator;
            Row row;
            Iterator<Cell> cellIterator;
            Cell cell;
            ProjectUat projectUat = new ProjectUat();
            projectUat.setUatComplete(false);
            projectUat.setProject(projectService.getById(projectUATRequestDTO.getProjectId()));
            projectUat.setOrganisation(organisationService.getOrganisationById(organisationId));
            projectUat.setUploadedBy(new User(userService.getUserById(userId)));
            projectUat.setModuleId(projectUATRequestDTO.getModuleId());
            projectUat.setSubModuleId(projectUATRequestDTO.getSubModuleId());
            projectUat.setProjectUatScripts(new LinkedHashSet<ProjectUatScript>());
            ProjectUatScript projectUatScript = new ProjectUatScript();
            ProjectUatScriptDetail projectUatScriptDetail = new ProjectUatScriptDetail();
            String cellValue;
            int rw = 0;
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
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Second row and first column should have Test Scenario!"));
                                }
                                projectUatScript.setTestScenario(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 1) {
                            if (rw == 1) {
                                try {
                                    HSSFDateUtil.isCellDateFormatted(cell);
                                    projectUatScript.setPlannedDate(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                    projectUat.getProjectUatScripts().add(projectUatScript);
                                } catch (Exception e) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Second row and second column should have Planned Date!"));
                                }
                            }
                        } else if (cell.getAddress().getColumn() == 2) {
                            projectUatScriptDetail.setTestScenarioJobId(cellValue);
                        } else if (cell.getAddress().getColumn() == 3) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Step is mandatory!"));
                            }
                            projectUatScriptDetail.setStep(Double.valueOf(cellValue));
                        } else if (cell.getAddress().getColumn() == 4) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Action is mandatory!"));
                            }
                            projectUatScriptDetail.setAction(cellValue);
                        } else if (cell.getAddress().getColumn() == 5) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUatScript.getTestScriptName(), "Expected Result is mandatory!"));
                            }
                            projectUatScriptDetail.setExpectedResult(cellValue);
                        } else if (cell.getAddress().getColumn() == 6) {
                            projectUatScriptDetail.setActualResult(null);
                        } else if (cell.getAddress().getColumn() == 7) {
                            projectUatScriptDetail.setPass(false);
                        } else if (cell.getAddress().getColumn() == 8) {
                            projectUatScriptDetail.setRetestDate(null);
                        } else if (cell.getAddress().getColumn() == 9) {
                            projectUatScriptDetail.setRetestPass(false);
                        } else if (cell.getAddress().getColumn() == 10) {
                            projectUatScriptDetail.setRemarks(null);
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
            return projectUatRepository.save(projectUat);
        } catch (IOException e) {
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