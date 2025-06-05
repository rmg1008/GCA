package com.gca.repository;

import com.gca.domain.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link Group}.
 */
public interface GroupRepository extends CrudRepository<Group, Long> {
    Optional<Group> findByParentIsNull();
}
