package com.gca.controller;

import com.gca.domain.Device;
import com.gca.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceRepository deviceRepository;

    @Test
    public void testRequestWithoutAuth() throws Exception {

        this.mockMvc.perform(post("/registerDevice")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("test1@alu.ubu.es")
    @Test
    public void testCreateDeviceWithEmptyBody() throws Exception {

        this.mockMvc.perform(post("/registerDevice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @WithUserDetails("test1@alu.ubu.es")
    @Test
    public void testCreateDeviceSuccessfully() throws Exception
    {
        Device deviceModel = new Device();
        deviceModel.setName("Random");
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
                .andExpect(status().isOk()).andExpect(content().string(containsString("Random")));
    }
}
