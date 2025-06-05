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

/**
 * Rest Controller para gestionar dispositivos.
 * Proporciona endpoints para registrar, actualizar, eliminar y buscar dispositivos.
 */
@RestController
public class DeviceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Endpoint para registrar un nuevo dispositivo.
     *
     * @param device DTO que contiene la información del dispositivo a registrar.
     * @return ID del dispositivo registrado.
     */
    @PostMapping("/registerDevice")
    public ResponseEntity<Long> registerDevice(@RequestBody @Valid DeviceDTO device) {
        LOGGER.debug("Registrar dispositivo: {}", device);
        return ResponseEntity.ok(deviceService.createDevice(device));
    }

    /**
     * Endpoint para actualizar un dispositivo existente.
     *
     * @param device DTO que contiene la información del dispositivo a actualizar.
     * @return ID del dispositivo actualizado.
     */
    @PostMapping("/updateDevice")
    public ResponseEntity<Long> updateDevice(@RequestBody @Valid DeviceDTO device) {
        LOGGER.debug("Actualizar dispositivo: {}", device);
        return ResponseEntity.ok(deviceService.updateDevice(device));
    }

    /**
     * Endpoint para eliminar un dispositivo por su ID.
     *
     * @param id ID del dispositivo a eliminar.
     * @return OK si se elimina correctamente.
     */
    @DeleteMapping("/device/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        LOGGER.debug("Borrando dispositivo...");
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para buscar un dispositivo por su ID.
     *
     * @param id ID del dispositivo a buscar.
     * @return Dispositivo encontrado.
     */
    @GetMapping("/device/{id}")
    public ResponseEntity<Device> searchDeviceById(@PathVariable Long id) {
        LOGGER.debug("Buscar dispositivo con id: {}", id);
        return ResponseEntity.ok(deviceService.searchDeviceById(id));
    }

    /**
     * Endpoint para buscar dispositivos por grupo.
     *
     * @param groupId ID del grupo al que pertenecen los dispositivos.
     * @return Lista de dispositivos encontrados en el grupo.
     */
    @GetMapping("/device/group/{groupId}")
    public List<DeviceDTO> searchDeviceByGroup(@PathVariable Long groupId) {
        LOGGER.debug("Buscar dispositivos por grupo: {}", groupId);
        return deviceService.searchDeviceByGroup(groupId);
    }

    /**
     * Endpoint para asignar un template a un dispositivo.
     * @param deviceId ID del dispositivo al que se le asignará el template.
     * @param templateId ID del template a asignar.
     * @return OK si se asigna correctamente, o un error si no se encuentra el dispositivo.
     */
    @PostMapping("/device/{deviceId}/assign-template/{templateId}")
    public ResponseEntity<Void> assignTemplateToDevice(
            @PathVariable Long deviceId,
            @PathVariable(required = false) Long templateId) {
        LOGGER.debug("Asignar template a dispositivo: {}", deviceId);
        deviceService.assignTemplateToDevice(templateId, deviceId);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para desasignar un template de un dispositivo.
     * @param deviceId ID del dispositivo del que se desasignará el template.
     * @return OK si se desasigna correctamente.
     */
    @DeleteMapping("/device/{deviceId}/unassign-template")
    public ResponseEntity<Void> unassignTemplateToDevice(
            @PathVariable Long deviceId) {
        LOGGER.debug("Desasignar template a dispositivo: {}", deviceId);
        deviceService.unassignTemplateToDevice(deviceId);
        return ResponseEntity.ok().build();
    }
}