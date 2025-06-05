package com.gca.repository;

import com.gca.domain.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de la entidad {@link Device}.
 * Proporciona métodos para buscar dispositivos por grupo o por huella digital.
 */
public interface DeviceRepository extends CrudRepository<Device, Long> {

    Optional<List<Device>> findByGroup_Id(Long groupId);

    Optional<Device> findByFingerprintHash(String huella);
}
