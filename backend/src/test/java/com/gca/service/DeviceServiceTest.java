package com.gca.service;

import com.gca.domain.Device;
import com.gca.domain.Group;
import com.gca.domain.OperatingSystem;
import com.gca.dto.DeviceDTO;
import com.gca.repository.DeviceRepository;
import com.gca.repository.GroupRepository;
import com.gca.repository.OsRepository;
import com.gca.service.impl.DefaultDeviceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private OsRepository osRepository;

    @InjectMocks
    private DefaultDeviceServiceImpl deviceService;

    private Device device;
    private Group group;
    private OperatingSystem os;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("TestGroup");
        group.setParent(null);
        group.setChildren(new java.util.ArrayList<>());

        os = new OperatingSystem();
        os.setName("TestOS");

        device = new Device();
        device.setName("TestDevice");
        device.setFingerprint("fp-123");
        device.setGroup(group);
        device.setOs(os);
    }

    @Test
    @DisplayName("Should create a device")
    void testCreateDevice() {
        device.setId(7L);

        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("TestDevice");
        deviceDTO.setFingerprint("fp-123");
        deviceDTO.setGroup(1L);
        deviceDTO.setOs(1L);

        // Given
        given(groupRepository.findById(anyLong())).willReturn(Optional.ofNullable(group));
        given(osRepository.findById(anyLong())).willReturn(Optional.ofNullable(os));
        given(deviceRepository.save(any())).willReturn(device);

        // When
        Long id = deviceService.createDevice(deviceDTO);

        // Then
        Assertions.assertEquals(device.getId(), id);
    }

    @Test
    @DisplayName("Should throw exception when group id not found on createDevice")
    void testCreateDevice_GroupNotFound() {
        // Given
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("TestDevice");
        deviceDTO.setFingerprint("fp-123");
        deviceDTO.setGroup(99L);
        deviceDTO.setOs(1L);

        given(groupRepository.findById(99L)).willReturn(Optional.empty());

        // Then
        Assertions.assertThrows(Exception.class, () -> deviceService.createDevice(deviceDTO));
    }


    @Test
    @DisplayName("Should update a device")
    void testUpdateDevice() throws Exception {
        device.setId(7L);

        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("TestDevice");
        deviceDTO.setFingerprint("fp-123");
        deviceDTO.setGroup(1L);
        deviceDTO.setOs(1L);

        // Given
        given(deviceRepository.findById(any())).willReturn(Optional.ofNullable(device));
        given(groupRepository.findById(anyLong())).willReturn(Optional.ofNullable(group));
        given(osRepository.findById(anyLong())).willReturn(Optional.ofNullable(os));
        given(deviceRepository.save(any())).willReturn(device);

        // When
        Long id = deviceService.updateDevice(deviceDTO);

        // Then
        Assertions.assertEquals(device.getId(), id);
    }

    @Test
    @DisplayName("Should throw exception when device not found on updateDevice")
    void testUpdateDevice_DeviceNotFound() {
        // Given
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("TestDevice");
        deviceDTO.setFingerprint("fp-123");
        deviceDTO.setGroup(1L);
        deviceDTO.setOs(1L);

        given(deviceRepository.findById(anyLong())).willReturn(Optional.empty());

        // Then
        Assertions.assertThrows(Exception.class, () -> deviceService.updateDevice(deviceDTO));
    }


    @Test
    @DisplayName("Should delete a device")
    void testDeleteDevice() {
        // Given
        willDoNothing().given(deviceRepository).deleteById(anyLong());

        // When
        deviceService.deleteDevice(anyLong());

        // Then
        verify(deviceRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should get the device by id")
    void testSearchDeviceById() {
        device.setId(7L);

        // Given
        given(deviceRepository.findById(7L)).willReturn(Optional.ofNullable(device));

        // When
        Device deviceFound = deviceService.searchDeviceById(7L);

        // Then
        Assertions.assertEquals(device, deviceFound);
    }

    @Test
    @DisplayName("Should return null when device id not found on searchDeviceById")
    void testSearchDeviceById_NotFound() {
        // Given
        given(deviceRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        Device deviceFound = deviceService.searchDeviceById(99L);

        // Then
        Assertions.assertNull(deviceFound);
    }


    @Test
    @DisplayName("Should get the device by name")
    void testSearchDeviceByName() {

        // Given
        given(deviceRepository.findByName(device.getName())).willReturn(device);

        // When
        Device deviceFound = deviceService.searchDeviceByName("TestDevice");

        // Then
        Assertions.assertEquals(device, deviceFound);
    }

    @Test
    @DisplayName("Should return null when device name not found on searchDeviceByName")
    void testSearchDeviceByName_NotFound() {
        // Given
        given(deviceRepository.findByName(any())).willReturn(null);

        // When
        Device deviceFound = deviceService.searchDeviceByName("UnknownDevice");

        // Then
        Assertions.assertNull(deviceFound);
    }


    @Test
    @DisplayName("Should get the device by group")
    void testSearchDeviceByGroup() {
        group.setId(7L);

        // Given
        given(deviceRepository.findByGroup_Id(device.getGroup().getId())).willReturn(Optional.of(List.of(device)));

        // When
        List<DeviceDTO> devicesFound = deviceService.searchDeviceByGroup(7L);

        // Then
        Assertions.assertFalse(devicesFound.isEmpty());
        Assertions.assertEquals(device.getName(), devicesFound.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list when no devices found for group")
    void testSearchDeviceByGroup_NoDevices() {
        // Given
        given(deviceRepository.findByGroup_Id(anyLong())).willReturn(Optional.of(List.of()));

        // When
        List<DeviceDTO> devicesFound = deviceService.searchDeviceByGroup(99L);

        // Then
        Assertions.assertTrue(devicesFound.isEmpty());
    }

}
