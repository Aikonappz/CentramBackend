package com.centram.common.vo;


import java.math.BigInteger;

public interface IncidentModuleVO {
    BigInteger getModuleId();

    String getModule();

    String getModuleName();

    int getCount();
}