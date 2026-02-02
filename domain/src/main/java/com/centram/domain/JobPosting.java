package com.centram.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Audited
@Table(
        name = "job_posting",
        indexes = {
                @Index(name = "job_posting_requisition_id_idx", columnList = "requisition_id"),
                @Index(name = "job_posting_status_idx", columnList = "posting_status")
        }
)
public class JobPosting extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(name = "requisition_id", nullable = false)
    private BigInteger requisitionId;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "location_id")
    private BigInteger locationId;

    @Column(name = "posting_start_date", nullable = false)
    private LocalDate postingStartDate;

    @Column(name = "posting_end_date")
    private LocalDate postingEndDate;

    @Column(name = "posting_type")
    private String postingType;

    @Column(name = "posting_board")
    private String postingBoard;

    @Column(name = "posting_status", nullable = false)
    private String postingStatus;
}
