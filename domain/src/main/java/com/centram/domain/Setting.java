package com.centram.domain;

import com.centram.domain.enumarator.IncidentTicketAllocationType;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Setting implements Serializable {
    private static final long serialVersionUID = 2149040960400918629L;
    @Enumerated(EnumType.ORDINAL)
    private IncidentTicketAllocationType ticketAllocationType;
    private String incidentPrefix;
    private String assetPrefix;

    public Setting(IncidentTicketAllocationType ticketAllocationType) {
        this.ticketAllocationType = ticketAllocationType;
    }
}
