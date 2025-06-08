package com.gca.service;

/**
 * Interfaz que define las operaciones de cifrado y hash.
 */
public interface CipherService {

    /**
     * Cifra un valor.
     *
     * @param value el valor a cifrar
     * @return el valor cifrado
     */
    String encrypt(String value);

    /**
     * Descifra un valor.
     *
     * @param value el valor a descifrar
     * @return el valor descifrado
     */
    String decrypt(String value);

    /**
     * Calcula el hash de un valor.
     *
     * @param value el valor del cual se calculará el hash
     * @return el hash del valor
     */
    String calculateHash(String value);
}
