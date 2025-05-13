package com.gca.controller;

import com.gca.domain.Device;
import com.gca.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceRepository deviceRepository;

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
    void testCreateDeviceSuccessfully() throws Exception
    {
        Device deviceModel = new Device();
        deviceModel.setId(1L);
        when(deviceRepository.save((any()))).thenReturn(deviceModel);

        this.mockMvc.perform(post("/registerDevice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fingerprint": "eh13je2j/Random",
                                    "name": "Random",
                                    "group": 1,
                                    "os": 1
                                }"""))
                .andExpect(status().isOk());
    }
}
