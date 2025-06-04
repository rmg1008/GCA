package com.gca.controller;

import com.gca.domain.Device;
import com.gca.dto.DeviceDTO;
import com.gca.service.DeviceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class DeviceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/registerDevice")
    public ResponseEntity<Long> registerDevice(@RequestBody @Valid DeviceDTO device) {
        LOGGER.debug("Registrar dispositivo: {}", device);
        return ResponseEntity.ok(deviceService.createDevice(device));
    }

    @PostMapping("/updateDevice")
    public ResponseEntity<Long> updateDevice(@RequestBody @Valid DeviceDTO device) {
        LOGGER.debug("Actualizar dispositivo: {}", device);
        return ResponseEntity.ok(deviceService.updateDevice(device));
    }

    @DeleteMapping("/device/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        LOGGER.debug("Borrar dispositivo con id: {}", id);
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/device/{id}")
    public ResponseEntity<Device> searchDeviceById(@PathVariable Long id) {
        LOGGER.debug("Buscar dispositivo con id: {}", id);
        return ResponseEntity.ok(deviceService.searchDeviceById(id));
    }

    @GetMapping("/device/group/{groupId}")
    public List<DeviceDTO> searchDeviceByGroup(@PathVariable Long groupId) {
        LOGGER.debug("Buscar dispositivos por grupo: {}", groupId);
        return deviceService.searchDeviceByGroup(groupId);
    }

    @PostMapping("/device/{deviceId}/assign-template/{templateId}")
    public ResponseEntity<Void> assignTemplateToDevice(
            @PathVariable Long deviceId,
            @PathVariable(required = false) Long templateId) {
        LOGGER.debug("Asignar template a dispositivo: {}", deviceId);
        deviceService.assignTemplateToDevice(templateId, deviceId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/device/{deviceId}/unassign-template")
    public ResponseEntity<Void> unassignTemplateToDevice(
            @PathVariable Long deviceId) {
        LOGGER.debug("Desasignar template a dispositivo: {}", deviceId);
        deviceService.unassignTemplateToDevice(deviceId);
        return ResponseEntity.ok().build();
    }
}