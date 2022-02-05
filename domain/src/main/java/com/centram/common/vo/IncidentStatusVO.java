package com.centram.common.vo;


import java.math.BigInteger;

public interface IncidentStatusVO {
    BigInteger getModuleId();
    String getStatus();
    String getStatusName();
    int getCount();
}