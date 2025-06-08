package com.gca.repository;

import com.gca.domain.OperatingSystem;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para la entidad {@link OperatingSystem}.
 */
public interface OsRepository extends CrudRepository<OperatingSystem, Long> {
}
