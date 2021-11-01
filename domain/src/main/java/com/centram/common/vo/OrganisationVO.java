package com.centram.common.vo;

import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrganisationVO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 6154359095128937633L;
    private BigInteger id;
    private String name;
    private String mnemonic;
    private Setting setting;
    private List<Address> addresses;
    private List<Contact> contacts;
    private List<BankDetail> bankDetails;
    private Status status;

    public OrganisationVO(LocalDateTime createdDate, LocalDateTime modifiedDate, Long version, BigInteger modifiedBy, BigInteger createdBy, BigInteger id, String name, String mnemonic, Setting setting, List<Address> addresses, List<Contact> contacts, List<BankDetail> bankDetails, Status status) {
        super(createdDate, modifiedDate, version, modifiedBy, createdBy);
        this.id = id;
        this.name = name;
        this.mnemonic = mnemonic;
        this.setting = setting;
        this.addresses = addresses;
        this.contacts = contacts;
        this.bankDetails = bankDetails;
        this.status = status;
    }
}
