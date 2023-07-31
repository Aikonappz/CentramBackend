package com.centram.core.repository;


import com.centram.domain.Priority;
import com.centram.domain.enumarator.PriorityType;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PriorityRepository extends PagingAndSortingRepository<Priority, BigInteger> {
    @Query("select p from Priority p where p.organisation.id = (:organisationId) and p.priorityType = (:priorityType) and " +
            " ( " +
            "   ((:accountId) is not null and p.account.id = (:accountId)) " +
            "   OR " +
            "   ((:accountId) is null) " +
            " )")
    Page getPriorityByOrganisation(
            @Param("accountId") BigInteger accountId,
            @Param("priorityType") PriorityType priorityType,
            @Param("organisationId") BigInteger organisationId,
            @Param("pageable") Pageable pageable
    );

    @Query("select p from Priority p where p.organisation.id = (:organisationId) and p.name = (:name) and p.account.id = (:accountId) ")
    Priority getPriorityByNameAndAccountIdAndOrganisationId(
            @Param("name") String name,
            @Param("accountId") BigInteger accountId,
            @Param("organisationId") BigInteger organisationId
    );

    @Modifying
    @Query("update Priority set status = (:status) where id in (:priorityIds)")
    Integer updateStatus(@Param("status") Status status, @Param("priorityIds") List<BigInteger> priorityIds);
}
