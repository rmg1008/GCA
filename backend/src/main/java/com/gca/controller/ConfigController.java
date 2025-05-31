package com.gca.controller;

import com.gca.dto.ConfigDTO;
import com.gca.dto.FingerprintDTO;
import com.gca.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<ConfigDTO> searchTemplate(
            @RequestParam(required = false) String huella
    ) {
        LOGGER.debug("Buscar configuración para huella : {}", huella);
        return ResponseEntity.ok(configService.getConfig(huella));
    }

    @DeleteMapping("/device")
    public ResponseEntity<Void> deleteDevice(@RequestBody FingerprintDTO dto) {
        LOGGER.debug("Borrar dispositivo con huella : {}", dto.getFingerprint());
        configService.deleteDeviceByFingerprint(dto.getFingerprint());
        return ResponseEntity.ok().build();
    }
}
