package com.gca.repository;

import com.gca.domain.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para la entidad {@link Template}.
 * Proporciona métodos para acceder a las plantillas por nombre y descripción.
 */
public interface TemplateRepository extends CrudRepository<Template, Long> {
    boolean existsByName(String name);

    Page<Template> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String literal, String literal1, Pageable pageable);

    Page<Template> findAll(Pageable pageable);
}
