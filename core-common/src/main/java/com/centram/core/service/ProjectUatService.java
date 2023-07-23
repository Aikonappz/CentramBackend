package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.ProjectUatRepository;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatDetail;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

@Service
public class ProjectUatService {

    private static final Logger log = LoggerFactory.getLogger(ProjectUatService.class);
    @Value("${app.temp.path}")
    private String appTmpPath;
    @Autowired
    private ProjectUatRepository projectUatRepository;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private ProjectService projectService;

    @Transactional(readOnly = false)
    public List<ProjectUat> processUATExcel() {
        try {
            String path = "/home/sumit/Downloads/sample-uat-scripts.xlsx";
            FileInputStream file = new FileInputStream(new File(path));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet;
            Iterator<Row> rowIterator;
            Row row;
            Iterator<Cell> cellIterator;
            Cell cell;
            List<ProjectUat> projectUats = new ArrayList<ProjectUat>();
            Set<ProjectUatDetail> projectUatDetails = new LinkedHashSet<ProjectUatDetail>();
            ProjectUatDetail projectUatDetail = new ProjectUatDetail();
            ProjectUat projectUat = new ProjectUat();
            Map<String, Object> errorContext = new HashMap<String, Object>();
            String cellValue;
            int rw = 0;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                //Get sheet from the workbook
                sheet = workbook.getSheet(workbook.getSheetName(i));
                projectUatDetails = new LinkedHashSet<ProjectUatDetail>();
                projectUat = new ProjectUat();
                //projectUat.setProject(projectService.getById(BigInteger.ONE));
                //projectUat.setOrganisation(organisationService.getOrganisationById(BigInteger.valueOf(4)));
                projectUat.setTestScriptName(workbook.getSheetName(i));
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
                    projectUatDetail = new ProjectUatDetail();
                    while (cellIterator.hasNext()) {
                        cell = cellIterator.next();
                        cellValue = this.getCellValue(cell);
                        if (cell.getAddress().getColumn() == 0) {
                            if (rw == 1) {
                                if (cellValue.trim().isEmpty()) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUat.getTestScriptName(), "Second row and first column should have Test Scenario!"));
                                }
                                projectUat.setTestScenario(cellValue);
                            }
                        } else if (cell.getAddress().getColumn() == 1) {
                            if (rw == 1) {
                                try {
                                    HSSFDateUtil.isCellDateFormatted(cell);
                                    projectUat.setPlannedDate(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                } catch (Exception e) {
                                    log.error("Error value - {}", cellValue);
                                    throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUat.getTestScriptName(), "Second row and second column should have Planned Date!"));
                                }
                            }
                        } else if (cell.getAddress().getColumn() == 2) {
                            projectUat.setTestScenarioJobId(cellValue);
                        } else if (cell.getAddress().getColumn() == 3) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUat.getTestScriptName(), "Step is mandatory!"));
                            }
                            projectUatDetail.setStep(Double.valueOf(cellValue));
                        } else if (cell.getAddress().getColumn() == 4) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUat.getTestScriptName(), "Action is mandatory!"));
                            }
                            projectUatDetail.setAction(cellValue);
                        } else if (cell.getAddress().getColumn() == 5) {
                            if (cellValue.trim().isEmpty()) {
                                log.error("Error value - {}", cellValue);
                                throw new AppException(GenericErrorCode.UPLOADED_FILE_DATA_ISSUE, prepareErrorContext(cell, projectUat.getTestScriptName(), "Expected Result is mandatory!"));
                            }
                            projectUatDetail.setExpectedResult(cellValue);
                        } else if (cell.getAddress().getColumn() == 6) {
                            projectUatDetail.setActualResult(null);
                        } else if (cell.getAddress().getColumn() == 7) {
                            projectUatDetail.setPass(false);
                        } else if (cell.getAddress().getColumn() == 8) {
                            projectUatDetail.setRetestDate(null);
                        } else if (cell.getAddress().getColumn() == 9) {
                            projectUatDetail.setRetestPass(false);
                        } else if (cell.getAddress().getColumn() == 10) {
                            projectUatDetail.setRetestPass(null);
                        }
                    }
                    if (projectUatDetail.getStep() != null) projectUatDetails.add(projectUatDetail);
                    rw++;
                }
                projectUat.setUatDetails(projectUatDetails);
                projectUats.add(projectUat);
                file.close();
                return projectUatRepository.saveAll(projectUats);
            }
            return projectUats;
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