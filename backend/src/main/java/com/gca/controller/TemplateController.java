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

@RestController
@RequestMapping("/template")
public class TemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateController.class);

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping("/register")
    public ResponseEntity<Long> registerTemplate(@RequestBody TemplateDTO template) {
        LOGGER.debug("Registrar plantilla: {}", template);
        return ResponseEntity.ok(templateService.createTemplate(template));
    }

    @PostMapping("/update")
    public ResponseEntity<Long> updateTemplate(@RequestBody TemplateDTO template) {
        LOGGER.debug("Actualizar plantilla: {}", template);
        return ResponseEntity.ok(templateService.updateTemplate(template));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        LOGGER.debug("Borrar plantilla con id: {}", id);
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TemplateDTO>> searchTemplate(
            @RequestParam(required = false) String literal,
            Pageable pageable
    ) {
        LOGGER.debug("Buscar plantilla por literal: {}", literal);
        return ResponseEntity.ok(templateService.searchTemplate(literal, pageable));
    }

    @GetMapping("/{templateId}/commands")
    public ResponseEntity<List<TemplateCommandDTO>> getAssignedCommands(@PathVariable Long templateId) {
        LOGGER.debug("Buscar comandos asignados a plantilla: {}", templateId);
        List<TemplateCommandDTO> commands = templateService.getAssignedCommands(templateId);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/{templateId}/available-commands")
    public ResponseEntity<List<CommandDTO>> getAvailableCommands(@PathVariable Long templateId) {
        LOGGER.debug("Buscar comandos disponibles para plantilla: {}", templateId);
        return ResponseEntity.ok(templateService.getAvailableCommands(templateId));
    }

    @PostMapping("/{templateId}/assign-commands")
    public ResponseEntity<Void> assignCommandsToTemplate(
            @PathVariable Long templateId,
            @RequestBody List<TemplateCommandDTO> comandos) {
        LOGGER.debug("Asignar comandos a plantilla: {}", templateId);
        templateService.assignCommandsToTemplate(templateId, comandos);
        return ResponseEntity.ok().build();
    }
}
