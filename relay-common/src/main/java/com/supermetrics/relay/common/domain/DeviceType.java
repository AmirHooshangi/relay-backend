package com.supermetrics.relay.common.domain;

public enum DeviceType {
    THERMOSTAT("thermostat"),
    HEART_RATE_METER("heart-rate-meter"),
    CAR_FUEL("car-fuel");
    
    private final String name;
    
    DeviceType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

