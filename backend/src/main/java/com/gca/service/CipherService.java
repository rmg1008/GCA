package com.gca.service;

public interface CipherService {

    String encrypt(String value);

    String decrypt(String value);

    String calculateHash(String value);
}
