package com.centram.core.repository;


import com.centram.domain.Account;
import com.centram.domain.Vendor;
import com.centram.domain.enumarator.AccountType;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.VendorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AccountRepository extends JpaRepository<Account, BigInteger> {
    @Query("select a from Account a join a.organisation org where UPPER(a.name) = UPPER((:name)) and org.id = (:organisationId)")
    Account getByName(@Param("name") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select a from Account a join a.organisation org where a.accountType = (:accountType) and UPPER(a.name) = UPPER((:name)) and org.id = (:organisationId)")
    Account getByNameAndType(@Param("accountType") AccountType accountType, @Param("name") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select a from Account a join a.organisation org where org.id = (:organisationId) ")
    Page getByOrganisation(
            @Param("organisationId") BigInteger organisationId,
            @Param("pageable") Pageable pageable
    );

    @Query("SELECT COUNT(a) FROM Account a WHERE a.organisation.id = (:organisationId)")
    long getCount(@Param("organisationId") BigInteger organisationId);

}