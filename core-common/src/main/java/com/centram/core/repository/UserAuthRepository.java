package com.centram.core.repository;

import com.centram.domain.UserAuth;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserAuthRepository extends CrudRepository<UserAuth, BigInteger> {
    @Query(value = "select count(id) from user_auth u where user_id in (:userIds) and sign_out_at is null", nativeQuery = true)
    int anyUserOnline(@Param("userIds") List<BigInteger> userIds);
}