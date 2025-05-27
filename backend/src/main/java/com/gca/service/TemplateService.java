package com.gca.service;

import com.gca.dto.CommandDTO;
import com.gca.dto.TemplateCommandDTO;
import com.gca.dto.TemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TemplateService {

    Long createTemplate(TemplateDTO templateDTO);

    Long updateTemplate(TemplateDTO templateDTO);

    void deleteTemplate(Long id);

    Page<TemplateDTO> searchTemplate(String literal, Pageable pageable);

    List<TemplateCommandDTO> getAssignedCommands(Long templateId);

    List<CommandDTO> getAvailableCommands(Long templateId);

    void assignCommandsToTemplate(Long templateId, List<TemplateCommandDTO> comandos);
}
