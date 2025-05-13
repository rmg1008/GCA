package com.gca.service.impl;

import com.gca.domain.Group;
import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;
import com.gca.repository.GroupRepository;
import com.gca.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultGroupServiceImpl implements GroupService {


    private final GroupRepository groupRepository;

    @Autowired
    public DefaultGroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Transactional(readOnly = true)
    @Override
    public TreeNodeDTO getTree() {
        Group rootGroup = groupRepository.findByParentIsNull()
                .orElseThrow(() -> new RuntimeException("No root group"));

        return buildNode(rootGroup);
    }

    @Override
    public List<TreeNodeDTO> getAllGroups() {
        List<TreeNodeDTO> groups = new java.util.ArrayList<>();
        for (Group group : groupRepository.findAll()) {
            groups.add(new TreeNodeDTO(group.getId(), group.getName(),
                    group.getParent() != null ? group.getParent().getId() : null));
        }
        return groups;
    }

    @Override
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    public Long createGroup(GroupDTO group) {
        Group groupModel = new Group();
        mapGroupDTO2Entity(group, groupModel);
        return groupRepository.save(groupModel).getId();
    }

    @Override
    public Long updateGroup(GroupDTO group) throws Exception {
        Group groupModel = groupRepository.findById(group.getId()).orElseThrow(() -> new Exception("Cannot update non existing group"));
        mapGroupDTO2Entity(group, groupModel);
        return groupRepository.save(groupModel).getId();
    }

    private void mapGroupDTO2Entity(GroupDTO group, Group groupModel) {
        groupModel.setId(group.getId());
        groupModel.setName(group.getName());
        Optional<Group> parent = this.groupRepository.findById(group.getParent());
        groupModel.setParent(parent.orElseThrow(() -> new RuntimeException("Parent group not found")));
    }

    private TreeNodeDTO buildNode(Group group) {
        TreeNodeDTO node = new TreeNodeDTO(group.getId(), group.getName(),
                group.getParent() != null ? group.getParent().getId() : null);

        for (Group childGroup : group.getChildren()) {
            node.getChildren().add(buildNode(childGroup));
        }

        return node;
    }
}
