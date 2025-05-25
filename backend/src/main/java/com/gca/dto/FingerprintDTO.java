package com.gca.dto;

import jakarta.validation.constraints.NotNull;

public class FingerprintDTO {

    @NotNull
    private String fingerprint;

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
