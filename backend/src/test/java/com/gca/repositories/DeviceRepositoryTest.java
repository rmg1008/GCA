package com.gca.repositories;

import com.gca.domain.Device;
import com.gca.domain.Group;
import com.gca.domain.OperatingSystem;
import com.gca.repository.DeviceRepository;
import com.gca.repository.GroupRepository;
import com.gca.service.CipherService;
import com.gca.service.impl.DefaultCipherServiceImpl;
import com.gca.util.SecretProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("Should find devices by group id")
    void testFindByGroupId() {
        // Given
        Group group = new Group();
        group.setName("DeviceTest");
        group.setParent(null);

        OperatingSystem os = new OperatingSystem();
        os.setId(1L);

       groupRepository.save(group);

        SecretProperties props = new SecretProperties();
        props.setSecretKey("1234567890123456"); // 16 bytes
        CipherService cipherService = new DefaultCipherServiceImpl(props);
        ((DefaultCipherServiceImpl) cipherService).init();

        Device device1 = new Device();
        device1.setName("Device1");
        device1.setFingerprint(cipherService.encrypt("fp-001"));
        device1.setFingerprintHash(cipherService.calculateHash("hash-001"));
        device1.setOs(os);
        device1.setGroup(group);

        Device device2 = new Device();
        device2.setName("Device2");
        device2.setFingerprint(cipherService.encrypt("fp-002"));
        device2.setFingerprintHash(cipherService.calculateHash("hash-002"));
        device2.setOs(os);
        device2.setGroup(group);

        deviceRepository.save(device1);
        deviceRepository.save(device2);

        // When
        Optional<List<Device>> foundDevices = deviceRepository.findByGroup_Id(1L);

        // Then
        assertThat(foundDevices).isPresent();
        assertThat(foundDevices.get()).hasSize(2);
        assertThat(foundDevices.get())
                .extracting(Device::getName)
                .containsExactlyInAnyOrder("Device1", "Device2");
    }

    @Test
    @DisplayName("Should return empty Optional when group id not found")
    void testFindByGroupId_NotFound() {
        // When
        Optional<List<Device>> foundDevices = deviceRepository.findByGroup_Id(999L);

        // Then
        assertThat(foundDevices).isPresent();
        assertThat(foundDevices.get()).isEmpty();
    }
}
