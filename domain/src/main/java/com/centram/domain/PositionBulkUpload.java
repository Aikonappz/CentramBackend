package com.centram.domain;

import com.centram.domain.enumarator.BulkUploadStatus;
import com.centram.domain.enumarator.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "position_bulk_upload")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionBulkUpload {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private BulkUploadStatus status; // PROCESSING, SUCCESS, FAILED

    @Lob
    private byte[] fileData;

    @Lob
    private String errorJson;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

}
