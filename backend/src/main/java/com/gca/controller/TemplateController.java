package com.gca.controller;

import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import com.gca.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/template")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping("/register")
    public ResponseEntity<Long> registerTemplate(@RequestBody TemplateDTO template) {
        return ResponseEntity.ok(templateService.createTemplate(template));
    }

    @PostMapping("/update")
    public ResponseEntity<Long> updateTemplate(@RequestBody TemplateDTO template) {
        return ResponseEntity.ok(templateService.updateTemplate(template));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TemplateDTO>> searchTemplate(
            @RequestParam(required = false) String literal,
            Pageable pageable
    ) {
        return ResponseEntity.ok(templateService.searchTemplate(literal, pageable));
    }

    @GetMapping("/{templateId}/commands")
    public ResponseEntity<List<TemplateCommandDTO>> getAssignedCommands(@PathVariable Long templateId) {
        List<TemplateCommandDTO> commands = templateService.getAssignedCommands(templateId);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/{templateId}/available-commands")
    public ResponseEntity<List<CommandDTO>> getAvailableCommands(@PathVariable Long templateId) {
        return ResponseEntity.ok(templateService.getAvailableCommands(templateId));
    }

    @PostMapping("/{templateId}/assign-commands")
    public ResponseEntity<Void> assignCommandsToTemplate(
            @PathVariable Long templateId,
            @RequestBody List<TemplateCommandDTO> comandos) {
        templateService.assignCommandsToTemplate(templateId, comandos);
        return ResponseEntity.ok().build();
    }
}
