package com.gca.controller;

import com.gca.dto.ConfigDTO;
import com.gca.dto.FingerprintDTO;
import com.gca.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<ConfigDTO> searchTemplate(
            @RequestParam(required = false) String huella
    ) {
        return ResponseEntity.ok(configService.getConfig(huella));
    }

    @DeleteMapping("/device")
    public ResponseEntity<Void> deleteDevice(@RequestBody FingerprintDTO dto) {
        configService.deleteDeviceByFingerprint(dto.getFingerprint());
        return ResponseEntity.ok().build();
    }
}
