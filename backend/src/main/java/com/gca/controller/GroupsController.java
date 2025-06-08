package com.gca.controller;

import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;
import com.gca.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Rest Controller para gestionar grupos.
 * Proporciona endpoints para registrar, actualizar, eliminar y obtener grupos.
 */
@RestController
public class GroupsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupsController.class);

    private final GroupService groupService;

    @Autowired
    public GroupsController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Endpoint para registrar un nuevo grupo.
     *
     * @param group DTO que contiene la información del grupo a registrar.
     * @return ID del grupo registrado.
     */
    @PostMapping("/registerGroup")
    public ResponseEntity<Long> registerGroup(@RequestBody GroupDTO group) {
        LOGGER.debug("Registrar grupo: {}", group);
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    /**
     * Endpoint para actualizar un grupo existente.
     *
     * @param group DTO que contiene la información del grupo a actualizar.
     * @return ID del grupo actualizado.
     */
    @PostMapping("/updateGroup")
    public ResponseEntity<Long> updateGroup(@RequestBody GroupDTO group) {
        LOGGER.debug("Actualizar grupo: {}", group);
        return ResponseEntity.ok(groupService.updateGroup(group));
    }

    /**
     * Endpoint para obtener el árbol de grupos.
     *
     * @return Arbol de grupos como TreeNodeDTO.
     */
    @GetMapping("/tree")
    public TreeNodeDTO getTree() {
        LOGGER.debug("Obtener arbol");
        return groupService.getTree();
    }

    /**
     * Endpoint para obtener todos los grupos.
     *
     * @return Lista de grupos como TreeNodeDTO.
     */
    @GetMapping("/groups")
    public List<TreeNodeDTO> getAllGroups() {
        LOGGER.debug("Obtener todos los grupos");
        return groupService.getAllGroups();
    }

    /**
     * Endpoint para eliminar un grupo por su ID.
     *
     * @param id ID del grupo a eliminar.
     * @return OK si se elimina correctamente.
     */
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        LOGGER.debug("Borrar grupo con id: {}", id);
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Asigna un template a un grupo.
     * @param groupId ID del grupo al que se asigna el template.
     * @param templateId ID del template a asignar.
     * @return OK si se asigna correctamente.
     */
    @PostMapping("/groups/{groupId}/assign-template/{templateId}")
    public ResponseEntity<Void> assignTemplateToGroup(
            @PathVariable Long groupId,
            @PathVariable(required = false) Long templateId) {
        LOGGER.debug("Asignar template a grupo: {}", groupId);
        groupService.assignTemplateToGroup(groupId, templateId);
        return ResponseEntity.ok().build();
    }

    /**
     * Desasigna el template de un grupo.
     * @param groupId ID del grupo del que se desasigna el template.
     * @return OK si se desasigna correctamente.
     */
    @DeleteMapping("/groups/{groupId}/unassign-template")
    public ResponseEntity<Void> unassignTemplateToGroup(
            @PathVariable Long groupId) {
        LOGGER.debug("Desasignar template a grupo: {}", groupId);
        groupService.unassignTemplateToGroup(groupId);
        return ResponseEntity.ok().build();
    }
}