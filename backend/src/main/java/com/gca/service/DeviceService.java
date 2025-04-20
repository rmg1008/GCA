package com.gca.service;

import com.gca.domain.Device;
import com.gca.dto.DeviceDTO;

public interface DeviceService {

    Device createDevice(DeviceDTO device);
}
