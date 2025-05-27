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
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultDeviceServiceImpl implements DeviceService {

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

    @Override
    public Long createDevice(DeviceDTO device) {
        Device deviceModel = new Device();
        mapDeviceDTO2Entity(device, deviceModel);
        return deviceRepository.save(deviceModel).getId();
    }

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

    @Override
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public Device searchDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    @Override
    public List<DeviceDTO> searchDeviceByGroup(Long group) {
        List<DeviceDTO> deviceDTOS = new ArrayList<>();
        List<Device> devices = deviceRepository.findByGroup_Id(group).orElse(List.of());
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

    @Override
    public void assignTemplateToDevice(Long templateId, Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceException("Device not found", GCAException.ErrorType.NOT_FOUND));
        Template template = templateRepository.findById(templateId)
                    .orElseThrow(() -> new TemplateException("Template not found", GCAException.ErrorType.NOT_FOUND));
        device.setTemplate(template);

        deviceRepository.save(device);
    }

    @Override
    public void unassignTemplateToDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceException("Device not found", GCAException.ErrorType.NOT_FOUND));
        device.setTemplate(null);
        deviceRepository.save(device);
    }
}
