package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class CommandDTO extends BaseDTO {

    @NotNull
    private String description;

    @NotNull
    private String value;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}