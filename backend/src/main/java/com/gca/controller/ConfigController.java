package com.gca.controller;

import com.gca.dto.ConfigDTO;
import com.gca.dto.FingerprintDTO;
import com.gca.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller para gestionar las configuraciones de dispositivos.
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Endpoint para buscar la configuración de un dispositivo por su huella.
     *
     * @param huella Huella del dispositivo.
     * @return Configuración del dispositivo.
     */
    @GetMapping
    public ResponseEntity<ConfigDTO> searchTemplate(
            @RequestParam(required = false) String huella
    ) {
        LOGGER.debug("Buscando configuración...");
        return ResponseEntity.ok(configService.getConfig(huella));
    }

    /**
     * Endpoint para eliminar un dispositivo por su huella.
     * @param dto DTO que contiene la huella del dispositivo a eliminar.
     * @return OK si se elimina correctamente.
     */
    @DeleteMapping("/device")
    public ResponseEntity<Void> deleteDevice(@RequestBody FingerprintDTO dto) {
        LOGGER.debug("Borrando dispositivo por huella...");
        configService.deleteDeviceByFingerprint(dto.getFingerprint());
        return ResponseEntity.ok().build();
    }
}
