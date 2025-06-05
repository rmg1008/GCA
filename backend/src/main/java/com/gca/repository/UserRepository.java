package com.gca.repository;


import com.gca.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}.
 * Proporciona métodos para acceder a los usuarios por correo electrónico.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
