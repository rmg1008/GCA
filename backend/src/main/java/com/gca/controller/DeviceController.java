package com.gca.controller;

import com.gca.dto.DeviceDTO;
import com.gca.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/registerDevice")
    public ResponseEntity<?> registerDevice(@RequestBody @Valid DeviceDTO device) {
        return ResponseEntity.ok(deviceService.createDevice(device));
    }

    @PostMapping("/updateDevice")
    public ResponseEntity<?> updateDevice(@RequestBody @Valid DeviceDTO device) throws Exception {
        return ResponseEntity.ok(deviceService.updateDevice(device));
    }

    @DeleteMapping("/device/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/device/{id}")
    public ResponseEntity<?> searchDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.searchDeviceById(id));
    }

    @GetMapping("/device/group/{groupId}")
    public List<DeviceDTO> searchDeviceByGroup(@PathVariable Long groupId) {
        return deviceService.searchDeviceByGroup(groupId);
    }

    @PostMapping("/device/{deviceId}/assign-template/{templateId}")
    public ResponseEntity<Void> assignTemplateToDevice(
            @PathVariable Long deviceId,
            @PathVariable(required = false) Long templateId) {
        deviceService.assignTemplateToDevice(templateId, deviceId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/device/{deviceId}/unassign-template")
    public ResponseEntity<Void> unassignTemplateToDevice(
            @PathVariable Long deviceId) {
        deviceService.unassignTemplateToDevice(deviceId);
        return ResponseEntity.ok().build();
    }
}