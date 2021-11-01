package com.erp.auth.repository;


import com.erp.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ModuleRepository extends JpaRepository<Module, BigInteger> {}
