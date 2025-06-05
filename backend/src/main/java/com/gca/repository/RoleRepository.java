package com.gca.repository;

import com.gca.domain.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Role}.
 * Proporciona métodos para acceder a los roles por nombre.
 */
public interface RoleRepository extends CrudRepository<Role, Integer> {
    List<Role> getRoleByName(String name);
}
