package com.gca.service;

import com.gca.exception.CipherException;
import com.gca.service.impl.DefaultCipherServiceImpl;
import com.gca.util.SecretProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultCipherServiceImplTest {

    private DefaultCipherServiceImpl cipherService;

    @BeforeEach
    void setUp() {
        SecretProperties props = new SecretProperties();
        props.setSecretKey("1234567890123456");

        cipherService = new DefaultCipherServiceImpl(props);
        cipherService.init();
    }

    @Test
    void testEncryptAndDecrypt() {
        String original = "texto-claro";
        String encrypted = cipherService.encrypt(original);
        String decrypted = cipherService.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(original);
        assertThat(encrypted).isNotEqualTo(original);
    }

    @Test
    void testCalculateHash() {
        String input = "test-hash";
        String hash = cipherService.calculateHash(input);

        assertAll(
                () -> assertThat(input).isNotNull(),
                () -> assertThat(hash).isNotEmpty(),
                () -> assertThat(hash).isNotEqualTo(input)
        );
    }

    @Test
    void testDecryptInvalidDataThrows() {
        String invalidBase64 = "not-a-valid-ciphertext";

        assertThatThrownBy(() -> cipherService.decrypt(invalidBase64))
                .isInstanceOf(CipherException.class)
                .hasMessageContaining("descifrar");
    }

    @Test
    void testInvalidKeyLength() {
        SecretProperties props = new SecretProperties();
        props.setSecretKey("short-key");

        DefaultCipherServiceImpl invalidService = new DefaultCipherServiceImpl(props);

        assertThatThrownBy(invalidService::init)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("16 bytes");
    }
}
