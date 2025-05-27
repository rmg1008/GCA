package com.gca.service;

import com.gca.domain.*;
import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import com.gca.exception.TemplateException;
import com.gca.repository.CommandRepository;
import com.gca.repository.OsRepository;
import com.gca.repository.TemplateRepository;
import com.gca.service.impl.DefaultTemplateServiceImpl;
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
class TemplateServiceTest {

    @InjectMocks
    private DefaultTemplateServiceImpl templateService;

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private OsRepository osRepository;

    private static final String TEST_NAME = "Test";
    private static final String TEST_DESC = "Test Desc";

    @Test
    void testCreateTemplateWithExistingOs() {
        TemplateDTO dto = createTemplateDTO();
        OperatingSystem os = createOperatingSystem();

        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(templateRepository.save(any())).thenAnswer(invocation -> {
            Template t = invocation.getArgument(0);
            t.setId(123L);
            return t;
        });

        Long id = templateService.createTemplate(dto);
        assertEquals(123L, id);
    }

    @Test
    void testUpdateTemplateSuccess() {
        Template existing = new Template();
        existing.setId(10L);
        existing.setName("Template for testing");
        TemplateDTO dto = createTemplateDTO();
        dto.setId(10L);
        dto.setName("Updated Template");

        when(templateRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(templateRepository.save(any())).thenReturn(existing);
        when(osRepository.findById(1L)).thenReturn(Optional.empty());

        Long result = templateService.updateTemplate(dto);
        assertEquals(10L, result);
    }

    @Test
    void testUpdateTemplateThrowsException() {
        TemplateDTO dto = new TemplateDTO();
        dto.setId(99L);

        when(templateRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TemplateException.class, () -> templateService.updateTemplate(dto));
    }

    @Test
    void testDeleteTemplate() {
        templateService.deleteTemplate(5L);
        verify(templateRepository, times(1)).deleteById(5L);
    }

    @Test
    void testSearchTemplateWithLiteral() {
        Pageable pageable = PageRequest.of(0, 10);
        Template template = createTemplate();

        when(templateRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(TEST_NAME, TEST_NAME, pageable))
                .thenReturn(new PageImpl<>(List.of(template)));

        Page<TemplateDTO> result = templateService.searchTemplate(TEST_NAME, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(TEST_NAME, result.getContent().get(0).getName());
    }

    @Test
    void testSearchTemplateWithoutLiteral() {
        Pageable pageable = PageRequest.of(0, 10);
        Template template = createTemplate();

        when(templateRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(template)));

        Page<TemplateDTO> result = templateService.searchTemplate(null, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(TEST_NAME, result.getContent().get(0).getName());
    }

    @Test
    void testGetAssignedCommands() {
        Template template = new Template();
        template.setId(1L);

        Command assignedCommand = new Command();
        assignedCommand.setId(89L);
        assignedCommand.setName(TEST_NAME);
        assignedCommand.setDescription(TEST_DESC);

        TemplateCommand templateCommand = new TemplateCommand();
        templateCommand.setCommand(assignedCommand);
        templateCommand.setExecutionOrder(1);

        template.setTemplateCommands(List.of(templateCommand));
        when(templateRepository.findById(1L)).thenReturn(Optional.of(template));

        List<TemplateCommandDTO> result = templateService.getAssignedCommands(1L);
        assertEquals(1, result.size());
        assertEquals(TEST_NAME, result.get(0).getCommandName());
    }

    @Test
    void testGetAssignedCommandsThrowsIfTemplateNotFound() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TemplateException.class, () -> templateService.getAssignedCommands(999L));
    }

    @Test
    void testGetAvailableCommands() {
        Template template = new Template();
        template.setId(1L);

        Command assigned = new Command();
        assigned.setId(89L);
        TemplateCommand templateCommand = new TemplateCommand();
        templateCommand.setCommand(assigned);
        template.setTemplateCommands(List.of(templateCommand));

        Command available = new Command();
        available.setId(98L);
        available.setName(TEST_NAME);
        available.setDescription(TEST_DESC);

        when(templateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(commandRepository.findAll()).thenReturn(List.of(available));

        List<CommandDTO> commands = templateService.getAvailableCommands(1L);
        assertEquals(1, commands.size());
        assertEquals(TEST_NAME, commands.get(0).getName());
    }

    @Test
    void testGetAvailableCommands_TemplateNotFound() {
        when(templateRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TemplateException.class, () -> templateService.getAvailableCommands(99L));
    }

    @Test
    void testAssignCommandsToTemplate_CommandNotFound() {
        Template template = new Template();
        template.setId(1L);
        template.setTemplateCommands(new ArrayList<>());

        TemplateCommandDTO dto = new TemplateCommandDTO();
        dto.setCommandId(100L);
        dto.setExecutionOrder(1);

        when(templateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(commandRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> templateService.assignCommandsToTemplate(1L, List.of(dto)));
    }

    @Test
    void testAssignCommandsToTemplate() {
        Template template = new Template();
        template.setId(1L);
        template.setTemplateCommands(new ArrayList<>());

        Command command = new Command();
        command.setId(100L);
        command.setName(TEST_NAME);
        command.setDescription(TEST_DESC);

        TemplateCommandDTO dto = new TemplateCommandDTO();
        dto.setCommandId(100L);
        dto.setExecutionOrder(1);

        when(templateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(commandRepository.findById(100L)).thenReturn(Optional.of(command));

        assertDoesNotThrow(() -> templateService.assignCommandsToTemplate(1L, List.of(dto)));
    }

    private TemplateDTO createTemplateDTO() {
        TemplateDTO dto = new TemplateDTO();
        dto.setName(TEST_NAME);
        dto.setDescription(TEST_DESC);
        dto.setOs(1L);
        return dto;
    }

    private Template createTemplate() {
        Template t = new Template();
        t.setId(1L);
        t.setName(TemplateServiceTest.TEST_NAME);
        t.setDescription(TemplateServiceTest.TEST_DESC);
        OperatingSystem os = new OperatingSystem();
        os.setId(1L);
        t.setOs(os);
        return t;
    }

    private OperatingSystem createOperatingSystem() {
        OperatingSystem os = new OperatingSystem();
        os.setId(1L);
        return os;
    }
}