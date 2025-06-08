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

/**
 * Implementación por defecto del servicio de grupos.
 */
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

    /**
     * Obtiene la estructura de grupos en forma de árbol.
     * @return Un objeto TreeNodeDTO que representa el grupo raíz y sus hijos.
     */
    @Transactional(readOnly = true) // Para evitar problemas de concurrencia
    @Override
    public TreeNodeDTO getTree() {
        Group rootGroup = groupRepository.findByParentIsNull()
                .orElseThrow(() -> groupNotFoundException("No root group"));

        return buildNode(rootGroup);
    }

    /**
     * Obtiene todos los grupos en formato de lista.
     * @return Una lista de TreeNodeDTO que representa todos los grupos.
     */
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

    /**
     * Elimina un grupo por su ID.
     * @param id ID del grupo a eliminar.
     */
    @Override
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    /**
     * Crea un nuevo grupo en la base de datos.
     * @param group DTO que contiene los datos del grupo a crear.
     * @return El ID del grupo creado.
     */
    @Override
    public Long createGroup(GroupDTO group) {
        Group groupModel = new Group();
        mapGroupDTO2Entity(group, groupModel);
        return groupRepository.save(groupModel).getId();
    }

    /**
     * Actualiza un grupo existente en la base de datos.
     * @param group DTO que contiene los datos del grupo a actualizar.
     * @return El ID del grupo actualizado.
     */
    @Override
    public Long updateGroup(GroupDTO group) {
        Group groupModel = getGroupById(group.getId());
        mapGroupDTO2Entity(group, groupModel);
        return groupRepository.save(groupModel).getId();
    }

    /**
     * Asigna un template a un grupo específico.
     * @param groupId ID del grupo al que se asignará el template.
     * @param templateId ID del template a asignar.
     */
    @Override
    public void assignTemplateToGroup(Long groupId, Long templateId) {
        LOGGER.info("Asignando template al grupo {}: {}", groupId, templateId);
        Group group = getGroupById(groupId);

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateException("Template not found", GCAException.ErrorType.NOT_FOUND));
        group.setTemplate(template);

        groupRepository.save(group);
    }

    /**
     * Desasigna un template de un grupo específico.
     * @param groupId ID del grupo del cual se desasignará el template.
     */
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

    /**
     * Construye un nodo del árbol a partir de un grupo.
     * @param group El grupo del cual se construirá el nodo.
     * @return Un objeto TreeNodeDTO que representa el grupo y sus hijos.
     */
    private TreeNodeDTO buildNode(Group group) {
        // Crea un nodo para el grupo actual
        TreeNodeDTO node = new TreeNodeDTO(group.getId(), group.getName(),
                group.getParent() != null ? group.getParent().getId() : null,
                group.getTemplate() != null ? group.getTemplate().getId() : null);

        // Añade los hijos del grupo al nodo
        for (Group childGroup : group.getChildren()) {
            node.getChildren().add(buildNode(childGroup));
        }
        return node;
    }

    private static GroupException groupNotFoundException(String message) {
        return new GroupException(message, GCAException.ErrorType.NOT_FOUND);
    }
}