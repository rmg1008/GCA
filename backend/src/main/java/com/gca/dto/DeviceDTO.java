package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class DeviceDTO {

    private Long id;

    @NotNull
    private String fingerprint;

    private String name;
    @NotNull

    @NotNull
    private Long group;

    @NotNull
    private Long os;

    private Long templateId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}