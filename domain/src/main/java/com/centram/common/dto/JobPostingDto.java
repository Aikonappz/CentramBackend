package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingDto {

    BigInteger requisitionId;
    LocalDate jobPortalPostingStartDate;
    LocalDate jobPortalPostingEndDate;
    String jobPortalCareerSite;
    boolean repostAfterExpiration;
}
