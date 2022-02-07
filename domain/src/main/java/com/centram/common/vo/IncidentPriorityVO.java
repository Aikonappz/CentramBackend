package com.centram.common.vo;


import java.math.BigInteger;

public interface IncidentPriorityVO {
    BigInteger getPriorityId();

    String getPriority();

    int getCount();
}