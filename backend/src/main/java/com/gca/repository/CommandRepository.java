package com.gca.repository;

import com.gca.domain.Command;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
public interface CommandRepository extends CrudRepository<Command, Long> {

    boolean existsByName(String name);

    Page<Command> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    Page<Command> findAll(Pageable pageable);
}
