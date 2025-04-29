package com.gca.repositories;

import com.gca.domain.Device;
import com.gca.domain.Group;
import com.gca.repository.DeviceRepository;
import com.gca.repository.GroupRepository;
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
    @DisplayName("Should find device by name")
    void testFindByName() {
        // Given
        Device device = new Device();
        device.setName("DeviceTest");
        device.setFingerprint("fp-123");
        deviceRepository.save(device);

        // When
        Device found = deviceRepository.findByName("DeviceTest");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("DeviceTest");
        assertThat(found.getFingerprint()).isEqualTo("fp-123");
    }

    @Test
    @DisplayName("Should return null when device name not found")
    void testFindByName_NotFound() {
        // When
        Device found = deviceRepository.findByName("NonExistingDevice");

        // Then
        assertThat(found).isNull();
    }


    @Test
    @DisplayName("Should find devices by group id")
    void testFindByGroupId() {
        // Given
        Group group = new Group();
        group.setParent(null);

       groupRepository.save(group);

        Device device1 = new Device();
        device1.setName("Device1");
        device1.setFingerprint("fp-001");
        device1.setGroup(group);

        Device device2 = new Device();
        device2.setName("Device2");
        device2.setFingerprint("fp-002");
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
