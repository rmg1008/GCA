package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class DeviceDTO extends BaseDTO {

    @NotNull
    private Long group;

    @NotNull
    private Long os;

    private Long templateId;

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