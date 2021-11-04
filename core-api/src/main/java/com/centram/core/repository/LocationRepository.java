package com.centram.core.repository;


import com.centram.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface LocationRepository extends JpaRepository<Location, BigInteger> {
}
