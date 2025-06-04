package com.gca.service.impl;

import com.gca.domain.Device;
import com.gca.domain.Group;
import com.gca.domain.Template;
import com.gca.domain.TemplateCommand;
import com.gca.dto.ConfigDTO;
import com.gca.exception.ConfigException;
import com.gca.exception.GCAException;
import com.gca.repository.DeviceRepository;
import com.gca.service.CipherService;
import com.gca.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultConfigServiceImpl implements ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigServiceImpl.class);

    private final DeviceRepository deviceRepository;
    private final CipherService cipherService;

    public DefaultConfigServiceImpl(DeviceRepository deviceRepository, CipherService cipherService) {
        this.deviceRepository = deviceRepository;
        this.cipherService = cipherService;
    }

    @Override
    public ConfigDTO getConfig(String huella) {
        Device device = getDevice(huella);
        Template template = findTemplate(device);
        if (template != null) {
            return buildConfigDTO(template);
        }
        LOGGER.info("No se ha encontrado ninguna configuración para este dispositivo {}", huella);
        throw throwNotFoundException("No se ha encontrado ninguna configuración para este dispositivo").get();
    }

    @Override
    public void deleteDeviceByFingerprint(String huella) {
        Device device = getDevice(huella);
        deviceRepository.delete(device);
    }

    private Device getDevice(String huella) {
        String hash = cipherService.calculateHash(huella);
        return deviceRepository.findByFingerprintHash(hash).orElseThrow(throwNotFoundException("Dispositivo no encontrado"));
    }

    private Template findTemplate(Device device) {
        if (device.getTemplate() != null) {
            LOGGER.info("El dispositivo con huella {} tiene asignada directamente una configuración", device.getFingerprint());
            return device.getTemplate();
        }
        Group group = device.getGroup();
        while (group != null) {
            if (group.getTemplate() != null) {
                LOGGER.debug("El grupo {} tiene asignada una configuración", group.getName());
                return group.getTemplate();
            }
            group = group.getParent();
        }
        return null;
    }

    private ConfigDTO buildConfigDTO(Template template) {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setId(template.getId());
        configDTO.setLastUpdate(template.getUpdatedAt());
        configDTO.setName(template.getName());
        configDTO.setConfig(parseTemplate(template.getTemplateCommands()));
        return configDTO;
    }

    private String parseTemplate(List<TemplateCommand> templateCommands) {
        return templateCommands.stream()
                .sorted(Comparator.comparingInt(TemplateCommand::getExecutionOrder))
                .map(this::buildCommandString)
                .collect(Collectors.joining(" && "));
    }

    private String buildCommandString(TemplateCommand templateCommand) {
        String rawCommand = templateCommand.getCommand().getValue();
        if (rawCommand == null) {
            LOGGER.warn("El comando {} no tiene valor", templateCommand.getCommand().getName());
            return "";
        }
        Map<String, String> params = templateCommand.getParameterValues();

        if (params == null || params.isEmpty()) {
            return rawCommand;
        }

        Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
        Matcher matcher = pattern.matcher(rawCommand);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = params.getOrDefault(key, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static Supplier<ConfigException> throwNotFoundException(String message) {
        return () -> new ConfigException(message, GCAException.ErrorType.NOT_FOUND);
    }
}
