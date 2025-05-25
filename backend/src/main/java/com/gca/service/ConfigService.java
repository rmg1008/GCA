package com.gca.service;

import com.gca.dto.ConfigDTO;

public interface ConfigService {

    ConfigDTO getConfig(String huella);

    void deleteDeviceByFingerprint(String huella);
}
