package com.gca.controller;

import com.gca.dto.DeviceDTO;
import com.gca.service.DeviceService;
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
    public ResponseEntity<?> registerDevice(@RequestBody DeviceDTO device) {
        return ResponseEntity.ok(deviceService.createDevice(device));
    }

    @PostMapping("/updateDevice")
    public ResponseEntity<?> updateDevice(@RequestBody DeviceDTO device) throws Exception {
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

    @GetMapping("/device/name")
    public ResponseEntity<?> searchDeviceByName(@RequestBody String name) {
        return ResponseEntity.ok(deviceService.searchDeviceByName(name));
    }

    @GetMapping("/device/group/{groupId}")
    public List<DeviceDTO> searchDeviceByGroup(@PathVariable Long groupId) {
        return deviceService.searchDeviceByGroup(groupId);
    }
}
