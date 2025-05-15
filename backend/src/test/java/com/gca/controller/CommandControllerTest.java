package com.gca.controller;

import com.gca.dto.CommandDTO;
import com.gca.service.CommandService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class CommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandService commandService;

    @WithUserDetails("admin@testubu.es")
    @Test
    void testRegisterCommand() throws Exception {
        when(commandService.createCommand(any())).thenReturn(1L);

        mockMvc.perform(post("/command/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Reboot",
                                  "description": "Reboot the system",
                                  "value": "reboot"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testUpdateCommand() throws Exception {
        when(commandService.updateCommand(any())).thenReturn(2L);

        mockMvc.perform(post("/command/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 2,
                                  "name": "Shutdown",
                                  "description": "Shutdown the system",
                                  "value": "shutdown"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testDeleteCommand() throws Exception {
        doNothing().when(commandService).deleteCommand(3L);

        mockMvc.perform(delete("/command/delete/3"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testSearchCommand() throws Exception {
        CommandDTO dto = new CommandDTO();
        dto.setId(1L);
        dto.setName("Status");
        dto.setDescription("Check status");
        dto.setValue("status");

        when(commandService.searchCommand(eq("status"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/command/search")
                        .param("literal", "status")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Status"));
    }
}