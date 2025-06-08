package com.gca.service;

import com.gca.dto.ConfigDTO;

/**
 * Interfaz que define las operaciones del servicio de configuración.
 */
public interface ConfigService {

    /**
     * Obtiene la configuración para un dispositivo específico basado en su huella digital.
     * @param huella la huella digital del dispositivo
     * @return ConfigDTO que contiene la configuración del dispositivo
     */
    ConfigDTO getConfig(String huella);

    /**
     * Elimina la configuración de un dispositivo específico basado en su huella digital.
     * @param huella la huella digital del dispositivo
     */
    void deleteDeviceByFingerprint(String huella);
}
