package com.centram.core.service;

import com.centram.core.repository.DepartmentRepository;
import com.centram.core.repository.PositionBulkUploadRepository;
import com.centram.core.repository.PositionRepository;
import com.centram.domain.Department;
import com.centram.domain.Position;
import com.centram.domain.PositionBulkUpload;
import com.centram.domain.enumarator.BulkUploadStatus;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PositionBulkService {

    @Autowired
    PositionBulkUploadRepository positionBulkUploadRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    PositionService positionService;

    public void generateTemplate(HttpServletResponse response) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Position Template");

        Row header = sheet.createRow(0);

        String[] columns = {
                "name*", "code", "status*", "start_date*", "job_code", "fte",
                "department_id*", "location", "cost_center",
                "end_date", "pay_grad", "standard_hour",
                "to_be_hired", "currency", "min_pay", "mid_pay", "max_pay", "recruiter_name"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=position_template.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Async
    @Transactional
    public void processFileAsync(Long uploadId) {

        long positionCodeCount = Long.parseLong(positionService.generatePositionCode());

        PositionBulkUpload audit = positionBulkUploadRepository.findById(uploadId).orElseThrow();

        List<Map<String, Object>> errorList = new ArrayList<>();
        List<Position> validList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(
                new ByteArrayInputStream(audit.getFileData()))) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                List<String> errors = new ArrayList<>();

                // ✅ Read values
                String name = getCell(row, 0);
                String code = getCell(row, 1);
                String statusStr = getCell(row, 2);
                String startDateStr = getCell(row, 3);
                String jobCode = getCell(row, 4);
                String fteStr = getCell(row, 5);
                String deptIdStr = getCell(row, 6);
                String locationStr = getCell(row, 7);
                String costCenter = getCell(row, 8);
                String endDateStr = getCell(row, 9);
                String payGrad = getCell(row, 10);
                String stdHourStr = getCell(row, 11);
                String toBeHiredStr = getCell(row, 12);
                String currency = getCell(row, 13);
                String minPayStr = getCell(row, 14);
                String midPayStr = getCell(row, 15);
                String maxPayStr = getCell(row, 16);
                String recruiterName = getCell(row, 17);

                // ================= VALIDATIONS =================

                if (isEmpty(name)) errors.add("name is required");
                if (isEmpty(statusStr)) errors.add("status is required");
                if (isEmpty(startDateStr)) errors.add("start_date is required");
                if (isEmpty(deptIdStr)) errors.add("department_id is required");

                // status
                Status status = null;
                try {
                    status = Status.valueOf(statusStr);
                } catch (Exception e) {
                    errors.add("invalid status");
                }

                // start date
                LocalDate startDate = null;
                try {
                    if (!isEmpty(startDateStr))
                        startDate = LocalDate.parse(startDateStr);
                } catch (Exception e) {
                    errors.add("invalid start_date (yyyy-MM-dd)");
                }

                // end date
                LocalDate endDate = null;
                try {
                    if (!isEmpty(endDateStr))
                        endDate = LocalDate.parse(endDateStr);
                } catch (Exception e) {
                    errors.add("invalid end_date (yyyy-MM-dd)");
                }

                // department
                Department dept = null;
                if (!isEmpty(deptIdStr)) {
                    try {
                        BigInteger deptId = parseBigInteger(deptIdStr, "department_id", errors);
                        dept = departmentRepository
                                .findById(deptId)
                                .orElse(null);

                        if (dept == null) errors.add("department not found");

                    } catch (Exception e) {
                        errors.add("invalid department_id");
                    }
                }

                // fte
                BigDecimal fte = null;
                try {
                    if (!isEmpty(fteStr))
                        fte = new BigDecimal(fteStr);
                } catch (Exception e) {
                    errors.add("invalid fte");
                }

                // location
                Long location = null;
                try {
                    if (!isEmpty(locationStr))
                        location = Long.parseLong(locationStr);
                } catch (Exception e) {
                    errors.add("invalid location");
                }

                // standard hour
                Integer stdHour = null;
                try {
                    if (!isEmpty(stdHourStr))
                        stdHour = Integer.parseInt(stdHourStr);
                } catch (Exception e) {
                    errors.add("invalid standard_hour");
                }

                // boolean
                Boolean toBeHired = null;
                if (!isEmpty(toBeHiredStr)) {
                    toBeHired = Boolean.parseBoolean(toBeHiredStr);
                }

                // salary fields
                BigDecimal minPay = parseDecimal(minPayStr, "min_pay", errors);
                BigDecimal midPay = parseDecimal(midPayStr, "mid_pay", errors);
                BigDecimal maxPay = parseDecimal(maxPayStr, "max_pay", errors);

                // business validation
                if (minPay != null && maxPay != null && minPay.compareTo(maxPay) > 0) {
                    errors.add("min_pay cannot be greater than max_pay");
                }

                // ================= ERROR HANDLE =================

                if (!errors.isEmpty()) {
                    errorList.add(Map.of(
                            "row", i + 1,
                            "errors", errors
                    ));
                    continue;
                }

                // ================= ENTITY SET =================

                Position p = new Position();
                p.setName(name);
                p.setCode(String.valueOf(positionCodeCount++));
                p.setStatus(status);
                p.setStartDate(startDate);
                p.setEndDate(endDate);
                p.setJobCode(jobCode);
                p.setFte(fte);
                p.setDepartment(dept);
                p.setLocationId(location);
                p.setCostCenter(costCenter);
                p.setPayGrad(payGrad);
                p.setStandardHour(stdHour);
                p.setToBeHired(toBeHired);
                p.setCurrency(currency);
                p.setMinPay(minPay);
                p.setMidPay(midPay);
                p.setMaxPay(maxPay);
                p.setRecruiterName(recruiterName);

                validList.add(p);
            }

            // ================= SAVE / FAIL =================

            if (!errorList.isEmpty()) {
                audit.setStatus(BulkUploadStatus.FAILED);
                audit.setErrorJson(new ObjectMapper().writeValueAsString(errorList));
            } else {
                positionRepository.saveAll(validList);
                audit.setStatus(BulkUploadStatus.SUCCESS);
            }

        } catch (Exception e) {
            audit.setStatus(BulkUploadStatus.FAILED);
            audit.setErrorJson("Internal error: " + e.getMessage());
        }

        audit.setCompletedAt(LocalDateTime.now());
        positionBulkUploadRepository.save(audit);
    }

    private boolean isEmpty(String val) {
        return val == null || val.isEmpty();
    }

    private String getCell(Row row, int index) {
        if (row == null || row.getCell(index) == null) return null;
        return row.getCell(index).toString().trim();
    }

    private BigDecimal parseDecimal(String val, String field, List<String> errors) {
        try {
            if (val != null && !val.isEmpty()) {
                return new BigDecimal(val);
            }
        } catch (Exception e) {
            errors.add("invalid " + field);
        }
        return null;
    }

    private BigInteger parseBigInteger(String val, String field, List<String> errors) {
        try {
            if (val != null && !val.trim().isEmpty()) {

                // handle decimal (101.0 → 101)
                Double d = Double.parseDouble(val);
                return BigDecimal.valueOf(d).toBigInteger();

            }
        } catch (Exception e) {
            errors.add("invalid " + field);
        }
        return null;
    }
}
