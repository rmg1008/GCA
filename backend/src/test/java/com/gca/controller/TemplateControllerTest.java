package com.gca.controller;

import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import com.gca.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemplateService templateService;

    @WithUserDetails("admin@testubu.es")
    @Test
    void testRegisterTemplate() throws Exception {
        when(templateService.createTemplate(any())).thenReturn(1L);

        mockMvc.perform(post("/template/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Template 1",
                                  "description": "Desc",
                                  "os": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testUpdateTemplate() throws Exception {
        when(templateService.updateTemplate(any())).thenReturn(2L);

        mockMvc.perform(post("/template/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 2,
                                  "name": "Updated Template",
                                  "description": "Updated Desc",
                                  "os": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testDeleteTemplate() throws Exception {
        doNothing().when(templateService).deleteTemplate(3L);

        mockMvc.perform(delete("/template/delete/3"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testSearchTemplate() throws Exception {
        TemplateDTO dto = new TemplateDTO();
        dto.setId(1L);
        dto.setName("T1");
        dto.setDescription("Test");
        dto.setOs(1L);

        when(templateService.searchTemplate(eq("Test"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/template/search")
                        .param("literal", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("T1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testGetAssignedCommands() throws Exception {
        TemplateCommandDTO dto = new TemplateCommandDTO();
        dto.setCommandName("Cmd1");

        when(templateService.getAssignedCommands(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/template/1/commands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commandName").value("Cmd1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testGetAvailableCommands() throws Exception {
        CommandDTO dto = new CommandDTO();
        dto.setName("AvailableCmd");

        when(templateService.getAvailableCommands(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/template/1/available-commands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("AvailableCmd"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testAssignCommandsToTemplate() throws Exception {
        doNothing().when(templateService).assignCommandsToTemplate(eq(1L), anyList());

        mockMvc.perform(post("/template/1/assign-commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {
                                    "commandId": 101,
                                    "executionOrder": 1
                                  }
                                ]
                                """))
                .andExpect(status().isOk());
    }
}