package com.centram.core.repository;

import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;


public interface OrganisationRepository extends CrudRepository<Organisation, BigInteger> {
    @Modifying
    @Query("update Organisation set status = (:status) where id in (:organisationIds)")
    Integer updateStatus(@Param("status") Status status, @Param("organisationIds") List<BigInteger> organisationId);

    @Modifying
    @Query("update Organisation set setting = (:setting) where id = (:organisationId)")
    Integer updateSetting(@Param("setting") Setting setting, @Param("organisationId") BigInteger organisationId);

    //@Query("select o from Organisation o where o.status = (:status)")
    //Page<Organisation> findByStatus(@Param("status") Status status, Pageable pageable);

    //@Query("select o from Organisation o where upper(o.name) like %:name%")
    //Page<Organisation> findByName(@Param("name") String name, Pageable pageable);

    //@Query("select o from Organisation o where upper(o.name) like %:name% and o.status = (:status)")
    //Page<Organisation> findByNameAndStatus(@Param("name") String name, @Param("status") Status status, Pageable pageable);

    @Query("select o from Organisation o where 1 = 1 and " +
            " ( " +
            "   ((:status) <> 2 and o.status = (:status)) " +
            "   OR  " +
            "   ((:status) = 2) " +
            " ) and " +
            " ( " +
            "   ((:name) is not null and upper(o.name) like (:name)) " +
            "   OR " +
            "   ((:name) is null) " +
            " ) "
    )
    Page<Organisation> findAll(
            @Param("name") String name,
            @Param("status") Integer status,
            Pageable pageable
    );

    @Query("select o from Organisation o where o.status = 1")
    List<Organisation> findAll();

    /*@Query("select new com.erp.common.vo.OrganisationVO(o.createdDate, o.modifiedDate, o.version, o.modifiedBy, o.createdBy,o.id,o.name,o.mnemonic,o.setting,o.addresses,o.contacts,o.bankDetails,o.status) from Organisation o")
    Page<OrganisationVO> getOrganisations(Pageable pageable);*/

    /*@Query("select new com.erp.common.vo.OrganisationVO(o.createdDate, o.modifiedDate, o.version, o.modifiedBy, o.createdBy,o.id,o.name,o.mnemonic,o.setting,o.addresses,o.contacts,o.bankDetails,o.status) from Organisation o where o.id = (:id)")
    OrganisationVO getById(@Param("id") BigInteger id);*/

    //Organisation findByMnemonic(@Param("mnemonic") String mnemonic);
}