package com.gca.service.impl;

import com.gca.domain.Device;
import com.gca.domain.Group;
import com.gca.domain.OperatingSystem;
import com.gca.domain.Template;
import com.gca.dto.DeviceDTO;
import com.gca.exception.DeviceException;
import com.gca.exception.GCAException;
import com.gca.exception.TemplateException;
import com.gca.repository.DeviceRepository;
import com.gca.repository.GroupRepository;
import com.gca.repository.OsRepository;
import com.gca.repository.TemplateRepository;
import com.gca.service.CipherService;
import com.gca.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación por defecto del servicio de dispositivos.
 */
@Service
public class DefaultDeviceServiceImpl implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeviceServiceImpl.class);

    private final DeviceRepository deviceRepository;
    private final GroupRepository groupRepository;
    private final OsRepository osRepository;
    private final TemplateRepository templateRepository;
    private final CipherService cipherService;

    public DefaultDeviceServiceImpl(DeviceRepository deviceRepository, GroupRepository groupRepository,
                                    OsRepository osRepository, TemplateRepository templateRepository,
                                    CipherService cipherService) {
        this.deviceRepository = deviceRepository;
        this.groupRepository = groupRepository;
        this.osRepository = osRepository;
        this.templateRepository = templateRepository;
        this.cipherService = cipherService;
    }

    /**
     * Crea un nuevo dispositivo en la base de datos.
     * @param device DTO que contiene los datos del dispositivo a crear.
     * @return El ID del dispositivo creado.
     */
    @Override
    public Long createDevice(DeviceDTO device) {
        Device deviceModel = new Device();
        mapDeviceDTO2Entity(device, deviceModel);
        return deviceRepository.save(deviceModel).getId();
    }

    /**
     * Actualiza un dispositivo existente en la base de datos.
     * @param device DTO que contiene los datos del dispositivo a actualizar.
     * @return El ID del dispositivo actualizado.
     */
    @Override
    public Long updateDevice(DeviceDTO device) {
        Device deviceModel = deviceRepository.findById(device.getId()).orElseThrow(() ->
                new DeviceException("Cannot update non existing device", GCAException.ErrorType.NOT_FOUND));
        mapDeviceDTO2Entity(device, deviceModel);
        return deviceRepository.save(deviceModel).getId();
    }

    private void mapDeviceDTO2Entity(DeviceDTO device, Device deviceModel) {
        deviceModel.setName(device.getName());
        deviceModel.setFingerprint(cipherService.encrypt(device.getFingerprint()));
        deviceModel.setFingerprintHash(cipherService.calculateHash(device.getFingerprint()));
        Optional<Group> group = this.groupRepository.findById(device.getGroup());
        Optional<OperatingSystem> os = this.osRepository.findById(device.getOs());
        group.ifPresent(deviceModel::setGroup);
        os.ifPresent(deviceModel::setOs);
    }

    /**
     * Elimina un dispositivo de la base de datos.
     * @param id ID del dispositivo a eliminar.
     */
    @Override
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    /**
     * Busca un dispositivo por su ID.
     * @param id ID del dispositivo a buscar.
     * @return El dispositivo encontrado o null si no existe.
     */
    @Override
    public Device searchDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    /**
     * Busca dispositivos por un grupo específico.
     * @param group ID del grupo al que pertenecen los dispositivos.
     * @return Una lista de DTOs de dispositivos que pertenecen al grupo.
     */
    @Override
    public List<DeviceDTO> searchDeviceByGroup(Long group) {
        List<DeviceDTO> deviceDTOS = new ArrayList<>();
        List<Device> devices = deviceRepository.findByGroup_Id(group).orElse(List.of());
        LOGGER.debug("Se han encontrado {} dispositivos para el grupo {}", devices.size(), group);
        for (Device device : devices) {
            DeviceDTO deviceDTO = new DeviceDTO();
            deviceDTO.setId(device.getId());
            deviceDTO.setName(device.getName());
            deviceDTO.setFingerprint(cipherService.decrypt(device.getFingerprint()));
            deviceDTO.setGroup(device.getGroup().getId());
            deviceDTO.setOs(device.getOs().getId());
            deviceDTO.setTemplateId(device.getTemplate() != null ? device.getTemplate().getId() : null);
            deviceDTOS.add(deviceDTO);
        }
        return deviceDTOS;
    }

    /**
     * Asigna una plantilla a un dispositivo.
     * @param templateId ID de la plantilla a asignar.
     * @param deviceId ID del dispositivo al que se asignará la plantilla.
     */
    @Override
    public void assignTemplateToDevice(Long templateId, Long deviceId) {
        LOGGER.info("Asignando template {} al dispositivo {}", templateId, deviceId);
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceException("Device not found", GCAException.ErrorType.NOT_FOUND));
        Template template = templateRepository.findById(templateId)
                    .orElseThrow(() -> new TemplateException("Template not found", GCAException.ErrorType.NOT_FOUND));
        device.setTemplate(template);

        deviceRepository.save(device);
    }

    /**
     * Desasigna la plantilla de un dispositivo.
     * @param deviceId ID del dispositivo del cual se desasignará la plantilla.
     */
    @Override
    public void unassignTemplateToDevice(Long deviceId) {
        LOGGER.info("Desasignando template al dispositivo {}", deviceId);
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceException("Device not found", GCAException.ErrorType.NOT_FOUND));
        device.setTemplate(null);
        deviceRepository.save(device);
    }
}
