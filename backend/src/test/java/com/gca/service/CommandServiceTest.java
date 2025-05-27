package com.gca.service;

import com.gca.domain.Command;
import com.gca.dto.CommandDTO;
import com.gca.exception.CommandException;
import com.gca.repository.CommandRepository;
import com.gca.service.impl.DefaultCommandServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @InjectMocks
    private DefaultCommandServiceImpl commandService;

    @Mock
    private CommandRepository commandRepository;

    private static final String NAME = "Command Name";
    private static final String DESC = "Command Desc";
    private static final String VALUE = "Value";

    @Test
    void testCreateCommand() {
        CommandDTO dto = createCommandDTO(null);
        Command saved = new Command();
        saved.setId(10L);

        when(commandRepository.save(any())).thenReturn(saved);

        Long id = commandService.createCommand(dto);
        assertEquals(10L, id);
    }

    @Test
    void testCreateCommand_Duplicated() {
        CommandDTO dto = createCommandDTO(null);

        when(commandRepository.existsByName(any())).thenReturn(Boolean.TRUE);

        assertThrows(CommandException.class, () -> commandService.createCommand(dto));

    }

    @Test
    void testUpdateCommandSuccess() {
        CommandDTO dto = createCommandDTO(1L);
        Command existing = new Command();
        existing.setId(1L);
        existing.setName(NAME);

        when(commandRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(commandRepository.save(any())).thenReturn(existing);

        Long id = commandService.updateCommand(dto);
        assertEquals(1L, id);
    }

    @Test
    void testUpdateCommandThrowsException() {
        CommandDTO dto = createCommandDTO(99L);
        when(commandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CommandException.class, () -> commandService.updateCommand(dto));
    }

    @Test
    void testDeleteCommand() {
        commandService.deleteCommand(5L);
        verify(commandRepository, times(1)).deleteById(5L);
    }

    @Test
    void testSearchCommandWithLiteral() {
        Pageable pageable = PageRequest.of(0, 10);
        Command cmd = new Command();
        cmd.setId(1L);
        cmd.setName(NAME);
        cmd.setDescription(DESC);
        cmd.setValue(VALUE);

        when(commandRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(NAME, NAME, pageable))
                .thenReturn(new PageImpl<>(List.of(cmd)));

        Page<CommandDTO> result = commandService.searchCommand(NAME, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(NAME, result.getContent().get(0).getName());
    }

    @Test
    void testSearchCommandWithoutLiteral() {
        Pageable pageable = PageRequest.of(0, 10);
        Command cmd = new Command();
        cmd.setId(1L);
        cmd.setName(NAME);
        cmd.setDescription(DESC);
        cmd.setValue(VALUE);

        when(commandRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(cmd)));

        Page<CommandDTO> result = commandService.searchCommand(null, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(NAME, result.getContent().get(0).getName());
    }

    private CommandDTO createCommandDTO(Long id) {
        CommandDTO dto = new CommandDTO();
        dto.setId(id);
        dto.setName(NAME);
        dto.setDescription(DESC);
        dto.setValue(VALUE);
        return dto;
    }
}
