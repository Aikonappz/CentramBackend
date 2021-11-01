package com.centram.common.vo;


import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserVO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 9194601811170425900L;
    private BigInteger id;
    private String firstName;
    private String lastName;
    private String aadhar;
    private String pan;
    private String userName;
    private String password;
    private List<Address> addresses;
    private List<Contact> contacts;
    private List<BankDetail> bankDetails;
    private List<BigInteger> roles;
    private List<String> roleNames;
    private BigInteger organisationId;
    private Status status;

    public UserVO(User user) {
        super(user.getCreatedDate(), user.getModifiedDate(), user.getVersion(), user.getModifiedBy(), user.getCreatedBy());
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.aadhar = user.getAadhar();
        this.pan = user.getPan();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.addresses = user.getAddresses();
        this.contacts = user.getContacts();
        this.bankDetails = user.getBankDetails();
        this.roles = user.getRoles();
        this.organisationId = (user.getOrganisation() != null)? user.getOrganisation().getId() : null;
        this.status = user.getStatus();
    }
}

