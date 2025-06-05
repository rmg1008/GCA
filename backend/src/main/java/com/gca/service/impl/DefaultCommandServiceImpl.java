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

/**
 * Implementación por defecto del servicio de comandos.
 */
@Service
public class DefaultCommandServiceImpl implements CommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandServiceImpl.class);

    private final CommandRepository commandRepository;

    public DefaultCommandServiceImpl(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    /**
     * Crea un nuevo comando en la base de datos.
     * @param commandDTO DTO que contiene los datos del comando a crear.
     * @return El ID del comando creado.
     */
    @Override
    public Long createCommand(CommandDTO commandDTO) {
        Command commandModel = new Command();
        checkIfCommandExists(commandDTO);
        mapCommandDTO2Entity(commandDTO, commandModel);
        return commandRepository.save(commandModel).getId();
    }

    /**
     * Actualiza un comando existente en la base de datos.
     * @param commandDTO DTO que contiene los datos del comando a actualizar.
     * @return El ID del comando actualizado.
     */
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

    /**
     * Elimina un comando de la base de datos.
     * @param id ID del comando a eliminar.
     */
    @Override
    public void deleteCommand(Long id) {
        commandRepository.deleteById(id);
    }

    /**
     * Busca comandos por un literal en su nombre o descripción.
     * Si no se proporciona un literal, devuelve todos los comandos.
     * @param literal Literal para buscar en el nombre o descripción de los comandos.
     * @param pageable Información de paginación.
     * @return Una página de DTOs de comandos que coinciden con la búsqueda.
     */
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
        // Convertimos la lista de entidades a una lista paginada de DTOs
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
