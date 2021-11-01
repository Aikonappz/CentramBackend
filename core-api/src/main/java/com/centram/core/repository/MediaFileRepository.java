package com.centram.core.repository;


import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, BigInteger> {

    @Query("select mf from MediaFile mf where mf.entityType = (:entityType) and mf.mediaType = (:mediaType) and mf.entityId = (:entityId)")
    MediaFile getMediaFile(@Param("entityType") EntityType entityType, @Param("mediaType") MediaType mediaType, @Param("entityId") BigInteger entityId);

    /*@Query(value = "SELECT " +
            " new com.erp.domain.MediaFile(" +
            " mf.id as id, " +
            " mf.file_name as fileName, " +
            " mf.file_type as fileType, " +
            " mf.entity_type as entityType, " +
            " mf.media_type as mediaType, " +
            " mf.content as content, " +
            " mf.entity_id as entityId ) " +
            " from " +
            " media_file mf " +
            " join item_media_relation imr on " +
            " (mf.id = imr.media_id) " +
            " join item i on " +
            " (i.id = imr.item_id) " +
            " WHERE " +
            " i.organisation_id = (:organisationId) " +
            " and " +
            " upper(i.name) like '%(:productName)%' " +
            " order by mf.id desc", nativeQuery = true)
    List<MediaFile> getMediasByProductName(@Param("organisationId") BigInteger organisationId, @Param("productName") String productName);*/

    /*@Query(value = "SELECT " +
            " new com.erp.domain.MediaFile(" +
            " mf.id as id, mf.fileName as fileName, mf.fileType as fileType," +
            " mf.entityType as entityType, mf.mediaType as mediaType,"+
            " mf.content as content, mf.entityId as entityId)"+
            " from " +
            " MediaFile mf " +
            " join ItemMediaRelation imr on " +
            " (mf.id = imr.mediaId) " +
            " join Item i on " +
            " (i.id = imr.itemId) " +
            " WHERE " +
            " i.organisation.id = (:organisationId) " +
            " and " +
            " (i.name) like '%'||:productName||'%' " +
            " order by mf.id desc", nativeQuery = false)
    List<MediaFile> getMediasByProductName(@Param("organisationId") BigInteger organisationId, @Param("productName") String productName);*/

}