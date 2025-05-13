package com.gca.repository;

import com.gca.domain.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends CrudRepository<Device, Long> {
    Device findByName(String name);

    Optional<List<Device>> findByGroup_Id(Long groupId);
}
