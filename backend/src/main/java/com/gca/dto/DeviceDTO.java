package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class DeviceDTO {

    @NotNull
    private String fingerprint;

    private String name;
    @NotNull

    @NotNull
    private Long group;

    private Long os;

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Long getOs() {
        return os;
    }

    public void setOs(Long os) {
        this.os = os;
    }
}
