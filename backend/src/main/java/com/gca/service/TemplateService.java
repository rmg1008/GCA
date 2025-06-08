package com.gca.service;

import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz que define las operaciones del servicio de plantillas.
 */
public interface TemplateService {

    /**
     * Crea una nueva plantilla.
     *
     * @param templateDTO los datos de la plantilla a crear
     * @return el ID de la plantilla creada
     */
    Long createTemplate(TemplateDTO templateDTO);

    /**
     * Actualiza una plantilla existente.
     *
     * @param templateDTO los datos de la plantilla a actualizar
     * @return el ID de la plantilla actualizada
     */
    Long updateTemplate(TemplateDTO templateDTO);

    /**
     * Elimina una plantilla por su ID.
     *
     * @param id el ID de la plantilla a eliminar
     */
    void deleteTemplate(Long id);

    /**
     * Busca una plantilla por literal.
     * @param literal el literal a buscar en las plantillas
     * @param pageable los parámetros de paginación
     * @return una página de plantillas que coinciden con el literal
     */
    Page<TemplateDTO> searchTemplate(String literal, Pageable pageable);

    /**
     * Obtiene los comandos asignados a una plantilla.
     * @param templateId el ID de la plantilla
     * @return una lista de comandos asignados a la plantilla
     */
    List<TemplateCommandDTO> getAssignedCommands(Long templateId);

    /**
     * Obtiene los comandos disponibles para asignar a una plantilla.
     * @param templateId el ID de la plantilla
     * @return una lista de comandos disponibles
     */
    List<CommandDTO> getAvailableCommands(Long templateId);

    /**
     * Asigna comandos a una plantilla.
     * @param templateId el ID de la plantilla
     * @param comandos la lista de comandos a asignar
     */
    void assignCommandsToTemplate(Long templateId, List<TemplateCommandDTO> comandos);
}
