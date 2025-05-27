package com.gca.controller;

import com.gca.dto.TreeNodeDTO;
import com.gca.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class GroupsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @WithUserDetails("admin@testubu.es")
    @Test
    void testRegisterGroup() throws Exception {
        when(groupService.createGroup(any())).thenReturn(1L);

        mockMvc.perform(post("/registerGroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Random",
                                  "parent": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testUpdateGroup() throws Exception {
        when(groupService.updateGroup(any())).thenReturn(2L);

        mockMvc.perform(post("/updateGroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 2,
                                  "name": "Updated Group"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testGetTree() throws Exception {
        when(groupService.getTree()).thenReturn(new TreeNodeDTO());

        mockMvc.perform(get("/tree"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testGetAllGroups() throws Exception {
        when(groupService.getAllGroups()).thenReturn(List.of(new TreeNodeDTO()));

        mockMvc.perform(get("/groups"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testDeleteGroup() throws Exception {
        doNothing().when(groupService).deleteGroup(3L);

        mockMvc.perform(delete("/groups/3"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testAssignTemplateToGroup() throws Exception {
        doNothing().when(groupService).assignTemplateToGroup(1L, 2L);

        mockMvc.perform(post("/groups/1/assign-template/2"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testUnassignTemplateToGroup() throws Exception {
        doNothing().when(groupService).unassignTemplateToGroup(1L);

        mockMvc.perform(delete("/groups/1/unassign-template"))
                .andExpect(status().isOk());
    }
}