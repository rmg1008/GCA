package com.gca.controller;

import com.gca.dto.CommandDTO;
import com.gca.service.CommandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/command")
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/register")
    public ResponseEntity<Long> registerCommand(@RequestBody CommandDTO command) {
        return ResponseEntity.ok(commandService.createCommand(command));
    }

    @PostMapping("/update")
    public ResponseEntity<Long> updateCommand(@RequestBody CommandDTO command) {
        return ResponseEntity.ok(commandService.updateCommand(command));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCommand(@PathVariable Long id) {
        commandService.deleteCommand(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CommandDTO>> searchCommand(
            @RequestParam(required = false) String literal,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commandService.searchCommand(literal, pageable));
    }
}
