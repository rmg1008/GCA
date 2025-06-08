package com.gca.controller;

import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import com.gca.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest Controller para gestionar plantillas.
 * Proporciona endpoints para registrar, actualizar, eliminar y buscar plantillas,
 * así como asignar comandos a las mismas.
 */
@RestController
@RequestMapping("/template")
public class TemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateController.class);

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Endpoint para registrar una nueva plantilla.
     *
     * @param template DTO que contiene la información de la plantilla a registrar.
     * @return ID de la plantilla registrada.
     */
    @PostMapping("/register")
    public ResponseEntity<Long> registerTemplate(@RequestBody TemplateDTO template) {
        LOGGER.debug("Registrar plantilla: {}", template);
        return ResponseEntity.ok(templateService.createTemplate(template));
    }

    /**
     * Endpoint para actualizar una plantilla existente.
     *
     * @param template DTO que contiene la información de la plantilla a actualizar.
     * @return ID de la plantilla actualizada.
     */
    @PostMapping("/update")
    public ResponseEntity<Long> updateTemplate(@RequestBody TemplateDTO template) {
        LOGGER.debug("Actualizar plantilla: {}", template);
        return ResponseEntity.ok(templateService.updateTemplate(template));
    }

    /**
     * Endpoint para eliminar una plantilla por su ID.
     *
     * @param id ID de la plantilla a eliminar.
     * @return OK si se elimina correctamente.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        LOGGER.debug("Borrar plantilla con id: {}", id);
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para buscar una plantilla por literal.
     * @param literal Literal de búsqueda de la plantilla.
     * @param pageable Parámetros de paginación.
     * @return Página de plantillas que coinciden con el literal de búsqueda.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<TemplateDTO>> searchTemplate(
            @RequestParam(required = false) String literal,
            Pageable pageable
    ) {
        LOGGER.debug("Buscando plantilla...");
        return ResponseEntity.ok(templateService.searchTemplate(literal, pageable));
    }

    /**
     * Endpoint para obtener los comanandos asignados a una plantilla.
     * @param templateId ID de la plantilla para la que se buscan los comandos asignados.
     * @return Lista de comandos asignados a la plantilla.
     */
    @GetMapping("/{templateId}/commands")
    public ResponseEntity<List<TemplateCommandDTO>> getAssignedCommands(@PathVariable Long templateId) {
        LOGGER.debug("Buscar comandos asignados a plantilla: {}", templateId);
        List<TemplateCommandDTO> commands = templateService.getAssignedCommands(templateId);
        return ResponseEntity.ok(commands);
    }

    /**
     * Endpoint para buscar comandos disponibles para una plantilla.
     * @param templateId ID de la plantilla para la que se buscan los comandos disponibles.
     * @return Lista de comandos disponibles para la plantilla.
     */
    @GetMapping("/{templateId}/available-commands")
    public ResponseEntity<List<CommandDTO>> getAvailableCommands(@PathVariable Long templateId) {
        LOGGER.debug("Buscar comandos disponibles para plantilla: {}", templateId);
        return ResponseEntity.ok(templateService.getAvailableCommands(templateId));
    }

    /**
     * Endpoint para asignar comandos a una plantilla.
     * @param templateId ID de la plantilla a la que se le asignarán los comandos.
     * @param comandos Lista de comandos a asignar a la plantilla.
     * @return OK si se asignan correctamente los comandos.
     */
    @PostMapping("/{templateId}/assign-commands")
    public ResponseEntity<Void> assignCommandsToTemplate(
            @PathVariable Long templateId,
            @RequestBody List<TemplateCommandDTO> comandos) {
        LOGGER.debug("Asignar comandos a plantilla: {}", templateId);
        templateService.assignCommandsToTemplate(templateId, comandos);
        return ResponseEntity.ok().build();
    }
}
