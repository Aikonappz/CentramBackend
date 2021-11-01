package com.centram.core.repository;


import com.centram.domain.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ActionRepository extends JpaRepository<Action, BigInteger> {
}