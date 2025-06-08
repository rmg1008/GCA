package com.gca.service;

import com.gca.dto.CommandDTO;
import com.gca.exception.CommandException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz que define las operaciones del servicio de comandos.
 */
public interface CommandService {

    /**
     * Crea un nuevo comando.
     *
     * @param commandDTO los datos del comando a crear
     * @return el ID del comando creado
     */
    Long createCommand(CommandDTO commandDTO);

    /**
     * Actualiza un comando existente.
     *
     * @param commandDTO los datos del comando a actualizar
     * @return el ID del comando actualizado
     * @throws CommandException si ocurre un error al actualizar el comando
     */
    Long updateCommand(CommandDTO commandDTO) throws CommandException;

    /**
     * Elimina un comando por su ID.
     *
     * @param id el ID del comando a eliminar
     */
    void deleteCommand(Long id);

    /**
     * Busca comandos por un literal.
     * @param literal el literal a buscar en los comandos
     * @param pageable los parámetros de paginación
     * @return una página de comandos que coinciden con el literal
     */
    Page<CommandDTO> searchCommand(String literal, Pageable pageable);
}
