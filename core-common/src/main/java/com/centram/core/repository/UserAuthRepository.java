package com.centram.core.repository;

import com.centram.domain.UserAuth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserAuthRepository extends CrudRepository<UserAuth, BigInteger> {
}