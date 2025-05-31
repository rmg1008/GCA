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

@RestController
public class GroupsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupsController.class);

    private final GroupService groupService;

    @Autowired
    public GroupsController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/registerGroup")
    public ResponseEntity<Long> registerGroup(@RequestBody GroupDTO group) {
        LOGGER.debug("Registrar grupo: {}", group);
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @PostMapping("/updateGroup")
    public ResponseEntity<Long> updateGroup(@RequestBody GroupDTO group) {
        LOGGER.debug("Actualizar grupo: {}", group);
        return ResponseEntity.ok(groupService.updateGroup(group));
    }

    @GetMapping("/tree")
    public TreeNodeDTO getTree() {
        LOGGER.debug("Obtener arbol");
        return groupService.getTree();
    }

    @GetMapping("/groups")
    public List<TreeNodeDTO> getAllGroups() {
        LOGGER.debug("Obtener todos los grupos");
        return groupService.getAllGroups();
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        LOGGER.debug("Borrar grupo con id: {}", id);
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/groups/{groupId}/assign-template/{templateId}")
    public ResponseEntity<Void> assignTemplateToGroup(
            @PathVariable Long groupId,
            @PathVariable(required = false) Long templateId) {
        LOGGER.debug("Asignar template a grupo: {}", groupId);
        groupService.assignTemplateToGroup(groupId, templateId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/groups/{groupId}/unassign-template")
    public ResponseEntity<Void> unassignTemplateToGroup(
            @PathVariable Long groupId) {
        LOGGER.debug("Desasignar template a grupo: {}", groupId);
        groupService.unassignTemplateToGroup(groupId);
        return ResponseEntity.ok().build();
    }
}