package com.centram.common.dto;

import java.math.BigInteger;


public interface CommonProjection {
    String getName();

    String getVersion();

    BigInteger getId();

    String getStatus();

    BigInteger getMapperId();
    String getCode();
}
