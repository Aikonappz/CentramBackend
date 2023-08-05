package com.centram.common.vo;

import com.centram.domain.Incident;
import com.centram.domain.Notification;
import com.centram.domain.enumarator.LicenseType;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectUATVO implements Serializable {
    private static final long serialVersionUID = -6554446908157662441L;
    private String mailSubjectKey;
    private String mailBodyKey;
    private String[] to;
    private String[] cc;
    private String[] bcc;
    private String recipientName;
    private String replyTo;
}