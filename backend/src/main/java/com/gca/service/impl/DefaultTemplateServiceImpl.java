package com.gca.service.impl;

import com.gca.domain.*;
import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import com.gca.exception.CommandException;
import com.gca.exception.TemplateException;
import com.gca.repository.CommandRepository;
import com.gca.repository.OsRepository;
import com.gca.repository.TemplateRepository;
import com.gca.service.TemplateService;
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

@Service
public class DefaultTemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final CommandRepository commandRepository;
    private final OsRepository osRepository;

    public DefaultTemplateServiceImpl(TemplateRepository templateRepository, OsRepository osRepository,
                                      CommandRepository commandRepository) {
        this.templateRepository = templateRepository;
        this.osRepository = osRepository;
        this.commandRepository = commandRepository;
    }

    @Override
    public Long createTemplate(TemplateDTO templateDTO) {
        Template templateModel = new Template();
        checkIfTemplateExists(templateDTO);
        mapTemplateDTO2Entity(templateDTO, templateModel);
        return templateRepository.save(templateModel).getId();
    }

    @Override
    public Long updateTemplate(TemplateDTO templateDTO) {
        Template templateModel = templateRepository.findById(templateDTO.getId()).orElseThrow(throwNotFoundException());
        if(!templateModel.getName().equals(templateDTO.getName())) {
            checkIfTemplateExists(templateDTO);
        }
        mapTemplateDTO2Entity(templateDTO, templateModel);
        return templateRepository.save(templateModel).getId();
    }

    @Override
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    @Override
    public Page<TemplateDTO> searchTemplate(String literal, Pageable pageable) {
        Page<Template> templatePage;

        if (literal != null && !literal.isBlank()) {
            templatePage = templateRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(literal, literal, pageable);
        } else {
            templatePage = templateRepository.findAll(pageable);
        }

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
                .sorted(Comparator.comparingInt(TemplateCommandDTO::getExecutionOrder))
                .toList();
    }

    @Override
    public List<CommandDTO> getAvailableCommands(Long templateId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(throwNotFoundException());

        Set<Long> assignedCommandIds = template.getTemplateCommands().stream()
                .map(tc -> tc.getCommand().getId())
                .collect(Collectors.toSet());

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

    @Override
    @Transactional
    public void assignCommandsToTemplate(Long templateId, List<TemplateCommandDTO> comandos) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(throwNotFoundException());

        template.getTemplateCommands().clear();

        for (TemplateCommandDTO dto : comandos) {
            Command command = commandRepository.findById(dto.getCommandId())
                    .orElseThrow(() -> new CommandException("Command not found", CommandException.ErrorType.NOT_FOUND));

            TemplateCommand tc = new TemplateCommand();
            tc.setTemplate(template);
            tc.setCommand(command);
            tc.setExecutionOrder(dto.getExecutionOrder());
            tc.setParameterValues(dto.getParameterValues());
            tc.setId(new TemplateCommandId(templateId, command.getId()));

            template.getTemplateCommands().add(tc);
        }
        template.setUpdatedAt(LocalDateTime.now());
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
            throw new TemplateException("Ya existe un template con el mismo nombre", TemplateException.ErrorType.DUPLICATED);
        }
    }

    private static Supplier<TemplateException> throwNotFoundException() {
        return () -> new TemplateException("Template no encontrado", TemplateException.ErrorType.NOT_FOUND);
    }
}