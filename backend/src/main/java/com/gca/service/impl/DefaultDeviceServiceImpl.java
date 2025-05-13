package com.gca.service.impl;

import com.gca.domain.Device;
import com.gca.domain.Group;
import com.gca.domain.OperatingSystem;
import com.gca.dto.DeviceDTO;
import com.gca.repository.DeviceRepository;
import com.gca.repository.GroupRepository;
import com.gca.repository.OsRepository;
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

    public DefaultDeviceServiceImpl(DeviceRepository deviceRepository, GroupRepository groupRepository, OsRepository osRepository) {
        this.deviceRepository = deviceRepository;
        this.groupRepository = groupRepository;
        this.osRepository = osRepository;
    }

    @Override
    public Long createDevice(DeviceDTO device) {
        Device deviceModel = new Device();
        mapDeviceDTO2Entity(device, deviceModel);
        return deviceRepository.save(deviceModel).getId();
    }

    @Override
    public Long updateDevice(DeviceDTO device) throws Exception {
        Device deviceModel = deviceRepository.findById(device.getId()).orElseThrow(() -> new Exception("Cannot update non existing device"));
        mapDeviceDTO2Entity(device, deviceModel);
        return deviceRepository.save(deviceModel).getId();
    }

    private void mapDeviceDTO2Entity(DeviceDTO device, Device deviceModel) {
        deviceModel.setName(device.getName());
        deviceModel.setFingerprint(device.getFingerprint());
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
    public Device searchDeviceByName(String name) {
        return deviceRepository.findByName(name);
    }

    @Override
    public List<DeviceDTO> searchDeviceByGroup(Long group) {
        List<DeviceDTO> deviceDTOS = new ArrayList<>();
        List<Device> devices = deviceRepository.findByGroup_Id(group).orElse(List.of());
        for (Device device : devices) {
            DeviceDTO deviceDTO = new DeviceDTO();
            deviceDTO.setId(device.getId());
            deviceDTO.setName(device.getName());
            deviceDTO.setFingerprint(device.getFingerprint());
            deviceDTO.setGroup(device.getGroup().getId());
            deviceDTO.setOs(device.getOs().getId());
            deviceDTOS.add(deviceDTO);
        }
        return deviceDTOS;
    }
}
