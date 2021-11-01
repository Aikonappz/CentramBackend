package com.centram.core.repository;

import com.centram.common.vo.OrganisationVO;
import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;


public interface OrganisationRepository extends JpaRepository<Organisation, BigInteger> {
    @Modifying
    @Query("update Organisation set status = (:status) where id = (:organisationId)")
    Integer updateStatus(@Param("status") Status status, @Param("organisationId") BigInteger organisationId);

    @Modifying
    @Query("update Organisation set setting = (:setting) where id = (:organisationId)")
    Integer updateSetting(@Param("setting") Setting setting, @Param("organisationId") BigInteger organisationId);

    /*@Query("select new com.erp.common.vo.OrganisationVO(o.createdDate, o.modifiedDate, o.version, o.modifiedBy, o.createdBy,o.id,o.name,o.mnemonic,o.setting,o.addresses,o.contacts,o.bankDetails,o.status) from Organisation o")
    Page<OrganisationVO> getOrganisations(Pageable pageable);*/

    /*@Query("select new com.erp.common.vo.OrganisationVO(o.createdDate, o.modifiedDate, o.version, o.modifiedBy, o.createdBy,o.id,o.name,o.mnemonic,o.setting,o.addresses,o.contacts,o.bankDetails,o.status) from Organisation o where o.id = (:id)")
    OrganisationVO getById(@Param("id") BigInteger id);*/

    Organisation findByMnemonic(@Param("mnemonic") String mnemonic);
}