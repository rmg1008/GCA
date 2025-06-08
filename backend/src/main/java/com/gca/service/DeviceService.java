package com.gca.service;

import com.gca.domain.Device;
import com.gca.dto.DeviceDTO;

import java.util.List;

/**
 * Interfaz que define las operaciones del servicio de dispositivos.
 */
public interface DeviceService {

    /**
     * Crea un nuevo dispositivo.
     *
     * @param device los datos del dispositivo a crear
     * @return el ID del dispositivo creado
     */
    Long createDevice(DeviceDTO device);

    /**
     * Actualiza un dispositivo existente.
     *
     * @param device los datos del dispositivo a actualizar
     * @return el ID del dispositivo actualizado
     */
    Long updateDevice(DeviceDTO device);

    /**
     * Elimina un dispositivo por su ID.
     *
     * @param id el ID del dispositivo a eliminar
     */
    void deleteDevice(Long id);

    /**
     * Busca un dispositivo por su ID.
     * @param id el ID del dispositivo a buscar
     * @return el dispositivo encontrado, o null si no existe
     */
    Device searchDeviceById(Long id);

    /**
     * Devuelve una lista de dispositivos asignados a un grupo específico.
     * @param group el ID del grupo al que pertenecen los dispositivos
     * @return una lista de DeviceDTO que pertenecen al grupo especificado
     */
    List<DeviceDTO> searchDeviceByGroup(Long group);

    /**
     * Asigna una plantilla a un dispositivo.
     * @param templateId el ID de la plantilla a asignar
     * @param deviceId el ID del dispositivo al que se asignará la plantilla
     */
    void assignTemplateToDevice(Long templateId, Long deviceId);

    /**
     * Desasigna una plantilla de un dispositivo.
     * @param deviceId el ID del dispositivo del que se desasignará la plantilla
     */
    void unassignTemplateToDevice(Long deviceId);
}