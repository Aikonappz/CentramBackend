package com.centram.core.repository;


import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, BigInteger> {
    @Query("select mf from MediaFile mf where mf.entityType = (:entityType) and mf.mediaType = (:mediaType) and mf.entityId = (:entityId)")
    MediaFile getMediaFile(@Param("entityType") EntityType entityType, @Param("mediaType") MediaType mediaType, @Param("entityId") BigInteger entityId);
}