package com.centram.core.repository;

import com.centram.domain.FormTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface FormTemplateRepository extends JpaRepository<FormTemplate, BigInteger> {
    List<FormTemplate> findByFormType(@Param("formType") String formType);
}
