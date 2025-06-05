package com.gca.service.impl;

import com.gca.domain.*;
import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import com.gca.exception.CommandException;
import com.gca.exception.GCAException;
import com.gca.exception.TemplateException;
import com.gca.repository.CommandRepository;
import com.gca.repository.OsRepository;
import com.gca.repository.TemplateRepository;
import com.gca.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementación por defecto del servicio de plantillas.
 */
@Service
public class DefaultTemplateServiceImpl implements TemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTemplateServiceImpl.class);

    private final TemplateRepository templateRepository;
    private final CommandRepository commandRepository;
    private final OsRepository osRepository;

    public DefaultTemplateServiceImpl(TemplateRepository templateRepository, OsRepository osRepository,
                                      CommandRepository commandRepository) {
        this.templateRepository = templateRepository;
        this.osRepository = osRepository;
        this.commandRepository = commandRepository;
    }

    /**
     * Crea una nueva plantilla en la base de datos.
     * @param templateDTO DTO que contiene los datos de la plantilla a crear.
     * @return El ID de la plantilla creada.
     */
    @Override
    public Long createTemplate(TemplateDTO templateDTO) {
        Template templateModel = new Template();
        checkIfTemplateExists(templateDTO);
        mapTemplateDTO2Entity(templateDTO, templateModel);
        return templateRepository.save(templateModel).getId();
    }

    /**
     * Actualiza una plantilla existente en la base de datos.
     * @param templateDTO DTO que contiene los datos de la plantilla a actualizar.
     * @return El ID de la plantilla actualizada.
     */
    @Override
    public Long updateTemplate(TemplateDTO templateDTO) {
        Template templateModel = templateRepository.findById(templateDTO.getId()).orElseThrow(throwNotFoundException());
        if(!templateModel.getName().equals(templateDTO.getName())) {
            checkIfTemplateExists(templateDTO);
        }
        mapTemplateDTO2Entity(templateDTO, templateModel);
        return templateRepository.save(templateModel).getId();
    }

    /**
     * Elimina una plantilla de la base de datos.
     * @param id ID de la plantilla a eliminar.
     */
    @Override
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    /**
     * Busca plantillas por un literal por su nombre o descripción.
     * @param literal Literal a buscar en el nombre o descripción de las plantillas.
     * @param pageable Información de paginación.
     * @return Una página de plantillas que coinciden con el literal proporcionado.
     */
    @Override
    public Page<TemplateDTO> searchTemplate(String literal, Pageable pageable) {
        Page<Template> templatePage;

        if (literal != null && !literal.isBlank()) {
            templatePage = templateRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(literal, literal, pageable);
        } else {
            templatePage = templateRepository.findAll(pageable);
            LOGGER.info("No se ha proporcionado un literal para la búsqueda, se devuelven todas los templates");
        }

        LOGGER.debug("Se han encontrado {} templates", templatePage.getTotalElements());
        List<TemplateDTO> dtoList = templatePage.getContent().stream()
                .map(template -> {
                    TemplateDTO dto = new TemplateDTO();
                    dto.setId(template.getId());
                    dto.setName(template.getName());
                    dto.setDescription(template.getDescription());
                    dto.setOs(template.getOs().getId());
                    return dto;
                })
                .toList();
        return new PageImpl<>(dtoList, pageable, templatePage.getTotalElements());
    }

    /**
     * Asigna un template a un dispositivo.
     * @param templateId ID del template a asignar.
     * @return Lista de comandos asignados al template.
     */
    @Override
    public List<TemplateCommandDTO> getAssignedCommands(Long templateId) {
        Template template = templateRepository.findById(templateId)
                    .orElseThrow(throwNotFoundException());

        return template.getTemplateCommands().stream()
                .map(tc -> {
                    TemplateCommandDTO dto = new TemplateCommandDTO();
                    dto.setCommandId(tc.getCommand().getId());
                    dto.setCommandName(tc.getCommand().getName());
                    dto.setCommandDescription(tc.getCommand().getDescription());
                    dto.setCommandValue(tc.getCommand().getValue());
                    dto.setExecutionOrder(tc.getExecutionOrder());
                    dto.setParameterValues(tc.getParameterValues());
                    return dto;
                })
                .sorted(Comparator.comparingInt(TemplateCommandDTO::getExecutionOrder)) // Ordena por el orden de ejecución
                .toList();
    }

    /**
     * Obtiene todos los comandos disponibles que no están asignados a un template específico.
     * @param templateId ID del template para el cual se buscan comandos disponibles.
     * @return Lista de DTOs de comandos disponibles.
     */
    @Override
    public List<CommandDTO> getAvailableCommands(Long templateId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(throwNotFoundException());

        Set<Long> assignedCommandIds = template.getTemplateCommands().stream()
                .map(tc -> tc.getCommand().getId())
                .collect(Collectors.toSet());

        // Filtra los comandos que no están asignados al template
        return StreamSupport.stream(commandRepository.findAll().spliterator(), false)
                .filter(c -> !assignedCommandIds.contains(c.getId()))
                .map(c -> {
                    CommandDTO dto = new CommandDTO();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    dto.setDescription(c.getDescription());
                    dto.setValue(c.getValue());
                    return dto;
                })
                .toList();
    }

    /**
     * Asigna comandos a un template específico.
     * @param templateId ID del template al que se asignarán los comandos.
     * @param comandos Lista de DTOs de comandos a asignar al template.
     */
    @Override
    @Transactional // Para asegurar que la operación es atómica y se maneja correctamente en caso de error
    public void assignCommandsToTemplate(Long templateId, List<TemplateCommandDTO> comandos) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(throwNotFoundException());

        template.getTemplateCommands().clear(); // Limpia comandos existentes antes de asignar nuevos

        for (TemplateCommandDTO dto : comandos) {
            Command command = commandRepository.findById(dto.getCommandId())
                    .orElseThrow(() -> new CommandException("Command not found", GCAException.ErrorType.NOT_FOUND));

            TemplateCommand tc = new TemplateCommand();
            tc.setTemplate(template);
            tc.setCommand(command);
            tc.setExecutionOrder(dto.getExecutionOrder());
            tc.setParameterValues(dto.getParameterValues());
            tc.setId(new TemplateCommandId(templateId, command.getId()));

            template.getTemplateCommands().add(tc);
        }
        template.setUpdatedAt(LocalDateTime.now()); // Actualiza la fecha de modificación del template
        templateRepository.save(template);
    }

    private void mapTemplateDTO2Entity(TemplateDTO template, Template templateModel) {
        templateModel.setName(template.getName());
        templateModel.setDescription( template.getDescription());
        templateModel.setUpdatedAt(LocalDateTime.now());
        Optional<OperatingSystem> os = this.osRepository.findById(template.getOs());
        os.ifPresent(templateModel::setOs);
    }

    private void checkIfTemplateExists(TemplateDTO templateDTO) {
        if (templateRepository.existsByName(templateDTO.getName())) {
            throw new TemplateException("Ya existe un template con el mismo nombre", GCAException.ErrorType.DUPLICATED);
        }
    }

    private static Supplier<TemplateException> throwNotFoundException() {
        return () -> new TemplateException("Template no encontrado", GCAException.ErrorType.NOT_FOUND);
    }
}