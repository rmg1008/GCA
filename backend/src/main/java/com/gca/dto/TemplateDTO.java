package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class TemplateDTO extends BaseDTO {

    @NotNull
    private String description;

    @NotNull
    private Long os;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOs() {
        return os;
    }

    public void setOs(Long os) {
        this.os = os;
    }
}