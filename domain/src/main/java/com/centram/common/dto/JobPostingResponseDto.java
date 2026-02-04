package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPostingResponseDto {

    BigInteger id;
    BigInteger requisitionId;
    String jobTitle;
    BigInteger locationId;
    LocalDate jobPortalPostingStartDate;
    LocalDate jobPortalPostingEndDate;
    String jobPortalCareerSite;
    String postingStatus;
    boolean repostAfterExpiration;
}
