package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommunicationType {
    BOTH_SIDE,
    THIRD_PARTY_TO_APP,
    APP_TO_THIRD_PARTY;
}