package com.gca.controller;

import com.gca.dto.DeviceDTO;
import com.gca.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/registerDevice")
    public ResponseEntity<?> login(@RequestBody DeviceDTO device) {
        return ResponseEntity.ok(deviceService.createDevice(device));
    }
}
