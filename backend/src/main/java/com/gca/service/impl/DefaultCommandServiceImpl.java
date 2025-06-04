package com.gca.service.impl;

import com.gca.domain.Command;
import com.gca.dto.CommandDTO;
import com.gca.exception.CommandException;
import com.gca.exception.GCAException;
import com.gca.repository.CommandRepository;
import com.gca.service.CommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DefaultCommandServiceImpl implements CommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandServiceImpl.class);

    private final CommandRepository commandRepository;

    public DefaultCommandServiceImpl(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @Override
    public Long createCommand(CommandDTO commandDTO) {
        Command commandModel = new Command();
        checkIfCommandExists(commandDTO);
        mapCommandDTO2Entity(commandDTO, commandModel);
        return commandRepository.save(commandModel).getId();
    }

    @Override
    public Long updateCommand(CommandDTO commandDTO) {
        Command commandModel = commandRepository.findById(commandDTO.getId()).orElseThrow(() ->
                new CommandException("No se pude actualizar, el comando no existe", GCAException.ErrorType.NOT_FOUND));
        if (!commandModel.getName().equals(commandDTO.getName())) {
            checkIfCommandExists(commandDTO);
        }
        mapCommandDTO2Entity(commandDTO, commandModel);
        return commandRepository.save(commandModel).getId();
    }

    @Override
    public void deleteCommand(Long id) {
        commandRepository.deleteById(id);
    }

    @Override
    public Page<CommandDTO> searchCommand(String literal, Pageable pageable) {
        Page<Command> commandPage;

        if (literal != null && !literal.isBlank()) {
            commandPage = commandRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(literal, literal, pageable);
        } else {
            LOGGER.info("No se ha proporcionado un literal para la búsqueda, se devuelven todos los comandos");
            commandPage = commandRepository.findAll(pageable);
        }
        LOGGER.debug("Se han encontrado {} comandos", commandPage.getTotalElements());
        List<CommandDTO> dtoList = commandPage.getContent().stream()
                .map(command -> {
                    CommandDTO dto = new CommandDTO();
                    dto.setId(command.getId());
                    dto.setName(command.getName());
                    dto.setDescription(command.getDescription());
                    dto.setValue(command.getValue());
                    return dto;
                })
                .toList();

        return new PageImpl<>(dtoList, pageable, commandPage.getTotalElements());
    }

    private void checkIfCommandExists(CommandDTO commandDTO) {
        if (commandRepository.existsByName(commandDTO.getName())) {
            throw new CommandException("Ya existe un comando con el mismo nombre", GCAException.ErrorType.DUPLICATED);
        }
    }

    private void mapCommandDTO2Entity(CommandDTO command, Command commandModel) {
        commandModel.setName(command.getName());
        commandModel.setDescription( command.getDescription());
        commandModel.setValue( command.getValue());
    }
}
