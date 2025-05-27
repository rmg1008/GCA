package com.gca.service;

import com.gca.dto.CommandDTO;
import com.gca.exception.CommandException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommandService {

    Long createCommand(CommandDTO commandDTO);

    Long updateCommand(CommandDTO commandDTO) throws CommandException;

    void deleteCommand(Long id);

    Page<CommandDTO> searchCommand(String literal, Pageable pageable);
}
