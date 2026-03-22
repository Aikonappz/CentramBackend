package com.centram.core.api;

import com.centram.core.repository.PositionBulkUploadRepository;
import com.centram.core.service.PositionBulkService;
import com.centram.domain.PositionBulkUpload;
import com.centram.domain.enumarator.BulkUploadStatus;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RequestMapping(value = "/api/v1/position/bulk")
@RestController
public class PositionBulkController {

    @Autowired
    PositionBulkService positionBulkService;

    @Autowired
    PositionBulkUploadRepository positionBulkUploadRepository;

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        positionBulkService.generateTemplate(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {

        PositionBulkUpload audit = new PositionBulkUpload();
        audit.setFileName(file.getOriginalFilename());
        audit.setStatus(BulkUploadStatus.PROCESSING);
        audit.setFileData(file.getBytes());
        audit.setCreatedAt(LocalDateTime.now());

        audit = positionBulkUploadRepository.save(audit);

        positionBulkService.processFileAsync(audit.getId());

        return ResponseEntity.ok(Map.of("message", "File uploaded successfully", "uploadId", audit.getId()));
    }
}
