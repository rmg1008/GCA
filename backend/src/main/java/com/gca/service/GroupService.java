package com.gca.service;

import com.gca.dto.GroupDTO;
import com.gca.dto.TreeNodeDTO;

import java.util.List;

/**
 * Interfaz que define las operaciones del servicio de grupos.
 */
public interface GroupService {

    /**
     * Obtiene el árbol de grupos.
     *
     * @return un objeto TreeNodeDTO que representa la estructura del árbol de grupos
     */
    TreeNodeDTO getTree();

    /**
     * Obtiene todos los grupos.
     *
     * @return una lista de todos los TreeNodeDTO
     */
    List<TreeNodeDTO> getAllGroups();

    /**
     * Elimina un grupo por su ID.
     * @param id el ID del grupo a eliminar
     */
    void deleteGroup(Long id);

    /**
     * Crea un nuevo grupo.
     *
     * @param group los datos del grupo a crear
     * @return el ID del grupo creado
     */
    Long createGroup(GroupDTO group);

    /**
     * Actualiza un grupo existente.
     *
     * @param group los datos del grupo a actualizar
     * @return el ID del grupo actualizado
     */
    Long updateGroup(GroupDTO group);

    /**
     * Asigna una plantilla a un grupo.
     * @param groupId el ID del grupo al que se asignará la plantilla
     * @param templateId el ID de la plantilla a asignar
     */
    void assignTemplateToGroup(Long groupId, Long templateId);

    /**
     * Desasigna una plantilla de un grupo.
     * @param groupId el ID del grupo del que se desasignará la plantilla
     */
    void unassignTemplateToGroup(Long groupId);
}