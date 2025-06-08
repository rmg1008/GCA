package com.gca.controller;

import com.gca.dto.CommandDTO;
import com.gca.service.CommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller paera gestionar comandos.
 */
@RestController
@RequestMapping("/command")
public class CommandController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandController.class);

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Registra un nuevo comando.
     * @param command DTO con los datos del comando a registrar.
     * @return ID del comando registrado.
     */
    @PostMapping("/register")
    public ResponseEntity<Long> registerCommand(@RequestBody CommandDTO command) {
        LOGGER.debug("Registrar comando: {}", command);
        return ResponseEntity.ok(commandService.createCommand(command));
    }

    /**
     * Actualiza un comando existente.
     * @param command DTO con los datos del comando a actualizar.
     * @return ID del comando actualizado.
     */
    @PostMapping("/update")
    public ResponseEntity<Long> updateCommand(@RequestBody CommandDTO command) {
        LOGGER.debug("Actualizar comando: {}", command);
        return ResponseEntity.ok(commandService.updateCommand(command));
    }

    /**
     * Elimina un comando por su ID.
     * @param id ID del comando a obtener.
     * @return DTO del comando solicitado.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCommand(@PathVariable Long id) {
        LOGGER.debug("Eliminar comando: {}", id);
        commandService.deleteCommand(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Realiza una búsqueda de comandos por un literal.
     * @param literal Literal a buscar en los comandos.
     * @param pageable Parámetros de paginación.
     * @return Página de comandos que coinciden con el literal.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CommandDTO>> searchCommand(
            @RequestParam(required = false) String literal,
            Pageable pageable
    ) {
        LOGGER.debug("Buscando comandos...");
        return ResponseEntity.ok(commandService.searchCommand(literal, pageable));
    }
}
