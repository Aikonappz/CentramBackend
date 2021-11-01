package com.centram.core.repository;


import com.centram.domain.SubModule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface SubModuleRepository extends CrudRepository<SubModule, BigInteger> {
}
