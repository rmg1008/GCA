package com.gca.service;

import com.gca.domain.Group;
import com.gca.domain.Template;
import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;
import com.gca.repository.GroupRepository;
import com.gca.repository.TemplateRepository;
import com.gca.service.impl.DefaultGroupServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private DefaultGroupServiceImpl groupService;

    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setName("TestGroup");
        group.setParent(null);
        group.setChildren(new java.util.ArrayList<>());
    }

    @Test
    @DisplayName("Should get the root tree")
    void testGetTree() {
        Group group2 = new Group();
        group2.setName("TestGroup");
        group2.setParent(group);
        group2.setChildren(new java.util.ArrayList<>());
        group.getChildren().add(group2);

        // Given
        given(groupRepository.findByParentIsNull()).willReturn(Optional.ofNullable(group));

        // When
        TreeNodeDTO tree = groupService.getTree();

        // Then
        Assertions.assertEquals("TestGroup", tree.getName());
    }

    @Test
    @DisplayName("Should throw exception when root group not found")
    void testGetTree_NoRoot() {
        // Given
        given(groupRepository.findByParentIsNull()).willReturn(Optional.empty());

        // Then
        Assertions.assertThrows(Exception.class, () -> groupService.getTree());
    }


    @Test
    @DisplayName("Should get all the groups")
    void testGetAllGroups() {
        ArrayList<Group> groups = new ArrayList<>();
        Group group2 = new Group();
        group2.setName("TestGroup");
        group2.setParent(group);
        group2.setChildren(new java.util.ArrayList<>());
        group.getChildren().add(group2);
        groups.add(group);
        groups.add(group2);
        // Given
        given(groupRepository.findAll()).willReturn(groups);

        // When
        List<TreeNodeDTO> allGroups = groupService.getAllGroups();

        // Then
        Assertions.assertEquals(2, allGroups.size());
        Assertions.assertEquals("TestGroup", allGroups.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list when no groups exist")
    void testGetAllGroups_NoGroups() {
        // Given
        given(groupRepository.findAll()).willReturn(new ArrayList<>());

        // When
        List<TreeNodeDTO> allGroups = groupService.getAllGroups();

        // Then
        Assertions.assertTrue(allGroups.isEmpty());
    }


    @Test
    @DisplayName("Should delete the group")
    void testDeleteGroup() {
        Group group2 = new Group();
        group2.setName("TestGroup");
        group2.setParent(group);
        group2.setChildren(new java.util.ArrayList<>());

        // Given
        willDoNothing().given(groupRepository).deleteById(group2.getId());

        // When
        groupService.deleteGroup(group2.getId());

        // Then
        verify(groupRepository, times(1)).deleteById(group2.getId());
    }

    @Test
    @DisplayName("Should call delete even if group does not exist")
    void testDeleteGroup_NotExisting() {
        // Given
        Long nonExistentId = 999L;
        willDoNothing().given(groupRepository).deleteById(nonExistentId);

        // When
        groupService.deleteGroup(nonExistentId);

        // Then
        verify(groupRepository, times(1)).deleteById(nonExistentId);
    }


    @Test
    @DisplayName("Should create the group")
    void testCreateGroup() {
        group.setId(7L);

        Group group2 = new Group();
        group2.setName("TestGroup");
        group2.setParent(group);

        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName("TestGroup");
        groupDTO.setParent(7L);

        // Given
        given(groupRepository.findById(group.getId())).willReturn(Optional.ofNullable(group));
        given(groupRepository.save(any())).willReturn(group2);

        // When
        Long id = groupService.createGroup(groupDTO);

        // Then
        Assertions.assertEquals(group2.getId(), id);
    }

    @Test
    @DisplayName("Should throw exception when parent group not found")
    void testCreateGroup_ParentNotFound() {
        // Given
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName("TestGroup");
        groupDTO.setParent(99L);

        given(groupRepository.findById(99L)).willReturn(Optional.empty());

        // Then
        Assertions.assertThrows(Exception.class, () -> groupService.createGroup(groupDTO));
    }

    @Test
    @DisplayName("Should update the group")
    void testUpdateGroup() throws Exception {
        group.setId(7L);

        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(7L);
        groupDTO.setName("TestGroup updated");
        groupDTO.setParent(null);

        given(groupRepository.save(any())).willReturn(group);
        given(groupRepository.findById(7L)).willReturn(Optional.of(group));

        // When
        Long updateGroupID = groupService.updateGroup(groupDTO);

        // Then
        verify(groupRepository, times(1)).save(group);
        Assertions.assertEquals(updateGroupID, group.getId());
    }

    @Test
    @DisplayName("Should throw exception when group not found")
    void testUpdateGroup_NotFound() {
        // Given
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(99L);
        given(groupRepository.findById(99L)).willReturn(Optional.empty());

        // Then
        Assertions.assertThrows(Exception.class, () -> groupService.updateGroup(groupDTO));
    }

    @Test
    @DisplayName("Should assign the template to the group")
    void testAssignTemplateToGroup() {
        group.setId(7L);

        Template template = new Template();
        template.setId(15L);

        given(groupRepository.save(any())).willReturn(group);
        given(groupRepository.findById(7L)).willReturn(Optional.of(group));
        when(templateRepository.findById(15L)).thenReturn(Optional.of(template));

        // When
        groupService.assignTemplateToGroup(7L, 15L);

        // Then
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    @DisplayName("Should throw exception when group not found")
    void testAssignTemplateToGroup_GroupNotFound() {
        given(groupRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(Exception.class, () -> groupService.assignTemplateToGroup(null, null));
    }

    @Test
    @DisplayName("Should throw exception when template not found")
    void testAssignTemplateToGroup_TemplateNotFound() {
        group.setId(7L);
        given(groupRepository.findById(7L)).willReturn(Optional.of(group));

        Assertions.assertThrows(Exception.class, () -> groupService.assignTemplateToGroup(7L, null));
    }

    @Test
    @DisplayName("Should unassign the template to the group")
    void testUnassignTemplateToGroup() {
        group.setId(7L);
        given(groupRepository.findById(7L)).willReturn(Optional.of(group));

        groupService.unassignTemplateToGroup(7L);

        verify(groupRepository, times(1)).save(group);
    }
}