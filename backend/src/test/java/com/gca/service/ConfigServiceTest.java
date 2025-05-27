package com.gca.service;

import com.gca.domain.*;
import com.gca.dto.ConfigDTO;
import com.gca.exception.ConfigException;
import com.gca.repository.DeviceRepository;
import com.gca.service.impl.DefaultConfigServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock private DeviceRepository deviceRepository;
    @Mock private CipherService cipherService;

    @InjectMocks
    private DefaultConfigServiceImpl configService;

    private final String huella = "1234567890";
    private final String hash = "1234";
    private final Device device = new Device();

    @BeforeEach
    void setUp() {
        device.setName("TestDevice");
        device.setFingerprint("fp-123");

        given(cipherService.calculateHash(huella)).willReturn(hash);
    }

    @Test
    void getConfig_fromDeviceTemplate() {
        Template template = createTemplate("cs", "ping {{ip}}", Map.of("ip", "192.168.1.1"));
        device.setTemplate(template);

        given(deviceRepository.findByFingerprintHash(hash)).willReturn(Optional.of(device));

        ConfigDTO config = configService.getConfig(huella);

        assertAll(
                () -> assertThat(config).isNotNull(),
                () -> assertThat(Objects.requireNonNull(config).getId()).isEqualTo(template.getId()),
                () -> {
                    Assertions.assertNotNull(config);
                    assertThat(config.getName()).isEqualTo(template.getName());
                },
                () -> {
                    Assertions.assertNotNull(config);
                    assertThat(config.getConfig()).isEqualTo("cs && ping 192.168.1.1");
                },
                () -> {
                    Assertions.assertNotNull(config);
                    assertThat(config.getLastUpdate()).isEqualTo(template.getUpdatedAt());
                }
        );
    }

    @Test
    void getConfig_fromDeviceGroupTemplate() {
        Template template = createTemplate();
        Group group = new Group();
        group.setTemplate(template);
        group.setChildren(List.of());
        device.setGroup(group);

        given(deviceRepository.findByFingerprintHash(hash)).willReturn(Optional.of(device));

        ConfigDTO config = configService.getConfig(huella);

        assertThat(config).isNotNull();
        assertThat(config.getId()).isEqualTo(template.getId());
    }

    @Test
    void getConfig_fromParentGroupTemplate() {
        Template template = createTemplate();
        Group parent = new Group(); parent.setTemplate(template);
        Group child = new Group();  child.setParent(parent);
        parent.setChildren(List.of(child));

        device.setGroup(child);

        given(deviceRepository.findByFingerprintHash(hash)).willReturn(Optional.of(device));

        ConfigDTO config = configService.getConfig(huella);

        assertThat(config).isNotNull();
        assertThat(config.getId()).isEqualTo(template.getId());
    }

    @Test
    void getConfig_throws_when_noTemplateFound() {
        given(deviceRepository.findByFingerprintHash(hash)).willReturn(Optional.of(device));

        assertThrows(ConfigException.class, () -> configService.getConfig(huella));
    }

    @Test
    void deleteDeviceByFingerprint_successful() {
        given(deviceRepository.findByFingerprintHash(hash)).willReturn(Optional.of(device));

        configService.deleteDeviceByFingerprint(huella);

        verify(deviceRepository).delete(device);
    }

    @Test
    void deleteDeviceByFingerprint_throws_when_notFound() {
        given(deviceRepository.findByFingerprintHash(hash)).willReturn(Optional.empty());

        assertThrows(ConfigException.class, () -> configService.deleteDeviceByFingerprint(huella));
    }

    private Template createTemplate() {
        return createTemplate(null, null, null);
    }

    private Template createTemplate(String val1, String val2, Map<String, String> params) {
        Template template = new Template();
        template.setId(1L);
        template.setName("Template");
        template.setUpdatedAt(LocalDateTime.now());

        if (val1 != null && val2 != null) {
            Command cmd1 = createCommand(val1);
            Command cmd2 = createCommand(val2);

            TemplateCommand tc1 = new TemplateCommand(); tc1.setCommand(cmd1); tc1.setExecutionOrder(1);
            TemplateCommand tc2 = new TemplateCommand(); tc2.setCommand(cmd2); tc2.setExecutionOrder(2);
            tc2.setParameterValues(params);

            template.setTemplateCommands(List.of(tc1, tc2));
        } else {
            template.setTemplateCommands(List.of());
        }
        return template;
    }

    private Command createCommand(String value) {
        Command cmd = new Command();
        cmd.setName("TestCommand");
        cmd.setDescription("desc");
        cmd.setValue(value);
        return cmd;
    }
}