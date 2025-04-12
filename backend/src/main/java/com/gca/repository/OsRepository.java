package com.gca.repository;

import com.gca.domain.OperatingSystem;
import org.springframework.data.repository.CrudRepository;

public interface OsRepository extends CrudRepository<OperatingSystem, Long> {
}
