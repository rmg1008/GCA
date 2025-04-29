package com.gca.service;

import com.gca.domain.Device;
import com.gca.dto.DeviceDTO;

import java.util.List;

public interface DeviceService {

    Long createDevice(DeviceDTO device);

    Long updateDevice(DeviceDTO device) throws Exception;

    void deleteDevice(Long id);

    Device searchDeviceById(Long id);

    Device searchDeviceByName(String name);

    List<DeviceDTO> searchDeviceByGroup(Long group);
}
