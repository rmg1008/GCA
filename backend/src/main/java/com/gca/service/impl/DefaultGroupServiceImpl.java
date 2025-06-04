package com.gca.service.impl;

import com.gca.domain.Group;
import com.gca.domain.Template;
import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;
import com.gca.exception.GCAException;
import com.gca.exception.GroupException;
import com.gca.exception.TemplateException;
import com.gca.repository.GroupRepository;
import com.gca.repository.TemplateRepository;
import com.gca.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultGroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGroupServiceImpl.class);

    private final GroupRepository groupRepository;
    private final TemplateRepository templateRepository;

    @Autowired
    public DefaultGroupServiceImpl(GroupRepository groupRepository, TemplateRepository templateRepository) {
        this.groupRepository = groupRepository;
        this.templateRepository = templateRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public TreeNodeDTO getTree() {
        Group rootGroup = groupRepository.findByParentIsNull()
                .orElseThrow(() -> groupNotFoundException("No root group"));

        return buildNode(rootGroup);
    }

    @Override
    public List<TreeNodeDTO> getAllGroups() {
        List<TreeNodeDTO> groups = new java.util.ArrayList<>();
        for (Group group : groupRepository.findAll()) {
            groups.add(new TreeNodeDTO(group.getId(), group.getName(),
                    group.getParent() != null ? group.getParent().getId() : null,
                    group.getTemplate() != null ? group.getTemplate().getId() : null));
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
    public Long updateGroup(GroupDTO group) {
        Group groupModel = getGroupById(group.getId());
        mapGroupDTO2Entity(group, groupModel);
        return groupRepository.save(groupModel).getId();
    }

    @Override
    public void assignTemplateToGroup(Long groupId, Long templateId) {
        LOGGER.info("Asignando template al grupo {}: {}", groupId, templateId);
        Group group = getGroupById(groupId);

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateException("Template not found", GCAException.ErrorType.NOT_FOUND));
        group.setTemplate(template);

        groupRepository.save(group);
    }

    @Override
    public void unassignTemplateToGroup(Long groupId) {
        LOGGER.info("Desasignando template al grupo {}", groupId);
        Group group = getGroupById(groupId);
        group.setTemplate(null);
        groupRepository.save(group);
    }

    private Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> groupNotFoundException("Group not found"));
    }

    private void mapGroupDTO2Entity(GroupDTO group, Group groupModel) {
        groupModel.setId(group.getId());
        groupModel.setName(group.getName());
        if (group.getParent() != null) {
            Optional<Group> parent = this.groupRepository.findById(group.getParent());
            groupModel.setParent(parent.orElseThrow(() -> groupNotFoundException("Parent group not found")));
        }
    }

    private TreeNodeDTO buildNode(Group group) {
        TreeNodeDTO node = new TreeNodeDTO(group.getId(), group.getName(),
                group.getParent() != null ? group.getParent().getId() : null,
                group.getTemplate() != null ? group.getTemplate().getId() : null);

        for (Group childGroup : group.getChildren()) {
            node.getChildren().add(buildNode(childGroup));
        }
        return node;
    }

    private static GroupException groupNotFoundException(String message) {
        return new GroupException(message, GCAException.ErrorType.NOT_FOUND);
    }
}