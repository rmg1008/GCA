package com.gca.service;

import com.gca.domain.Device;
import com.gca.dto.DeviceDTO;

import java.util.List;

public interface DeviceService {

    Long createDevice(DeviceDTO device);

    Long updateDevice(DeviceDTO device);

    void deleteDevice(Long id);

    Device searchDeviceById(Long id);

    List<DeviceDTO> searchDeviceByGroup(Long group);

    void assignTemplateToDevice(Long templateId, Long deviceId);

    void unassignTemplateToDevice(Long deviceId);
}