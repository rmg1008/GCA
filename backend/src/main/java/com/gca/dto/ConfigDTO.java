package com.gca.dto;

import java.time.LocalDateTime;

public class ConfigDTO extends BaseDTO{

    private String config;

    private LocalDateTime lastUpdate;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}