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
    public Device createDevice(DeviceDTO device) {
        Device deviceModel = new Device();
        deviceModel.setName(device.getName());
        deviceModel.setFingerprint(device.getFingerprint());
        Optional<Group> group = this.groupRepository.findById(device.getGroup());
        Optional<OperatingSystem> os = this.osRepository.findById(device.getOs());
        group.ifPresent(deviceModel::setGroup);
        os.ifPresent(deviceModel::setOs);
        return deviceRepository.save(deviceModel);
    }
}
