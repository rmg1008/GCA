package com.gca.controller;

import com.gca.dto.DeviceDTO;
import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;
import com.gca.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupsController {

    private final GroupService groupService;

    @Autowired
    public GroupsController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/registerGroup")
    public ResponseEntity<?> registerDevice(@RequestBody GroupDTO group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @GetMapping("/tree")
    public TreeNodeDTO getTree() {
        return groupService.getTree();
    }

    @GetMapping("/groups")
    public List<TreeNodeDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}
