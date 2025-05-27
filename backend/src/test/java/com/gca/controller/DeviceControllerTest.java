package com.gca.controller;

import com.gca.domain.Device;
import com.gca.dto.DeviceDTO;
import com.gca.service.DeviceService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @Test
    void testRequestWithoutAuth() throws Exception {
        this.mockMvc.perform(post("/registerDevice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testCreateDeviceWithEmptyBody() throws Exception {
        this.mockMvc.perform(post("/registerDevice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testCreateDeviceSuccessfully() throws Exception {
        when(deviceService.createDevice(any())).thenReturn(1L);

        this.mockMvc.perform(post("/registerDevice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fingerprint": "eh13je2j/Random",
                                  "name": "Random",
                                  "group": 1,
                                  "os": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testUpdateDeviceSuccessfully() throws Exception {
        when(deviceService.updateDevice(any())).thenReturn(1L);

        this.mockMvc.perform(post("/updateDevice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "fingerprint": "updated",
                                  "name": "Updated Name",
                                  "group": 1,
                                  "os": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testDeleteDeviceSuccessfully() throws Exception {
        doNothing().when(deviceService).deleteDevice(1L);

        this.mockMvc.perform(delete("/device/1"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testSearchDeviceById() throws Exception {
        when(deviceService.searchDeviceById(1L)).thenReturn(new Device());

        this.mockMvc.perform(get("/device/1"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testSearchDeviceByGroup() throws Exception {
        when(deviceService.searchDeviceByGroup(1L)).thenReturn(List.of(new DeviceDTO()));

        this.mockMvc.perform(get("/device/group/1"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testAssignTemplateToDevice() throws Exception {
        doNothing().when(deviceService).assignTemplateToDevice(1L, 1L);

        this.mockMvc.perform(post("/device/1/assign-template/1"))
                .andExpect(status().isOk());
    }

    @WithUserDetails("admin@testubu.es")
    @Test
    void testUnassignTemplateToDevice() throws Exception {
        doNothing().when(deviceService).unassignTemplateToDevice(1L);

        this.mockMvc.perform(delete("/device/1/unassign-template"))
                .andExpect(status().isOk());
    }
}
