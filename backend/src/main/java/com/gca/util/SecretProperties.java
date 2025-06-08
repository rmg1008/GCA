package com.gca.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Componente utilizado para cargar la clave secreta desde las propiedades de configuración.
 */
@Component
@ConfigurationProperties(prefix = "security.secret")
public class SecretProperties {

    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
