package com.gca.repository;

import com.gca.domain.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<Template, Long> {
    boolean existsByName(String name);

    Page<Template> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String literal, String literal1, Pageable pageable);

    Page<Template> findAll(Pageable pageable);
}
