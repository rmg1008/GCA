package com.gca.repository;

import com.gca.domain.Command;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para gestionar las operaciones de la entidad {@link Command}.
 */
public interface CommandRepository extends CrudRepository<Command, Long> {

    boolean existsByName(String name);

    /**
     * Busca comandos por nombre o descripción, ignorando mayúsculas y minúsculas.
     *
     * @param name        el nombre del comando a buscar
     * @param description la descripción del comando a buscar
     * @param pageable    información de paginación
     * @return una página de comandos que coinciden con el criterio de búsqueda
     */
    Page<Command> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    Page<Command> findAll(Pageable pageable);
}
