package com.gca.service.impl;

import com.gca.exception.CipherException;
import com.gca.service.CipherService;
import com.gca.util.SecretProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Implementación por defecto del servicio de cifrado.
 */
@Service
public class DefaultCipherServiceImpl implements CipherService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;

    private final SecretProperties properties;
    private SecretKeySpec keySpec;

    public DefaultCipherServiceImpl(SecretProperties properties) {
        this.properties = properties;
    }

    /**
     * Inicializa la clave secreta a partir de las propiedades configuradas.
     * La clave debe tener exactamente 16 bytes (AES-128).
     * @throws IllegalArgumentException si la clave no tiene 16 bytes.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = properties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("La clave debe tener 16 bytes (AES-128)");
        }
        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Cifra un valor utilizando un algoritmo simétrico:  AES en modo GCM.
     * @param value el valor a cifrar.
     * @return el valor cifrado en Base64.
     * @throws CipherException si ocurre un error durante el cifrado.
     */
    @Override
    public String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv); // Genera una clave IV aleatoria

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CipherException("Error al cifrar", CipherException.ErrorType.ENCRYPTION, e);
        }
    }

    /**
     * Descifra un valor cifrado en Base64 utilizando AES en modo GCM.
     * @param encryptedBase64 el valor cifrado en Base64.
     * @return el valor descifrado.
     * @throws CipherException si ocurre un error durante el descifrado.
     */
    @Override
    public String decrypt(String encryptedBase64) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[combined.length - IV_LENGTH];

            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CipherException("Error al descifrar", CipherException.ErrorType.DECRYPTION, e);
        }
    }

    /**
     * Calcula un hash SHA-256 de un valor.
     * @param value el valor a hashear.
     * @return el hash en Base64.
     * @throws CipherException si ocurre un error durante el cálculo del hash.
     */
    public String calculateHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new CipherException("Error al generar el hash", CipherException.ErrorType.HASH);
        }
    }
}
