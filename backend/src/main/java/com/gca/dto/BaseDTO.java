package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class BaseDTO {

    private Long id;
    @NotNull
    private String name;

    @NotNull
    private String fingerprint;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

}
