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

/**
 * Implementación por defecto del servicio de configuración.
 */
@Service
public class DefaultConfigServiceImpl implements ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigServiceImpl.class);

    private final DeviceRepository deviceRepository;
    private final CipherService cipherService;

    public DefaultConfigServiceImpl(DeviceRepository deviceRepository, CipherService cipherService) {
        this.deviceRepository = deviceRepository;
        this.cipherService = cipherService;
    }

    /**
     * Obtiene la configuración asociada a un dispositivo por su huella digital.
     * Si el dispositivo tiene una plantilla asignada directamente, se utiliza esa.
     * Si no, se busca en el grupo al que pertenece el dispositivo.
     *
     * @param huella Huella digital del dispositivo.
     * @return Configuración del dispositivo.
     */
    @Override
    public ConfigDTO getConfig(String huella) {
        Device device = getDevice(huella);
        Template template = findTemplate(device);
        return buildConfigDTO(device, template, huella);

    }

    /**
     * Elimina un dispositivo por su huella digital.
     * Si el dispositivo no existe, lanza una excepción.
     * @param huella Huella digital del dispositivo a eliminar.
     */
    @Override
    public void deleteDeviceByFingerprint(String huella) {
        Device device = getDevice(huella);
        deviceRepository.delete(device);
    }

    private Device getDevice(String huella) {
        String hash = cipherService.calculateHash(huella);
        return deviceRepository.findByFingerprintHash(hash).orElseThrow(throwNotFoundException());
    }

    /**
     * Busca la plantilla asociada a un dispositivo.
     * Primero verifica si el dispositivo tiene una plantilla asignada directamente.
     * Si no, busca en el grupo al que pertenece el dispositivo y sus grupos padres.
     *
     * @param device Dispositivo del cual se busca la plantilla.
     * @return Plantilla asociada al dispositivo o null si no se encuentra ninguna.
     */
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

    private ConfigDTO buildConfigDTO(Device device, Template template, String huella) {
        ConfigDTO configDTO = new ConfigDTO();
        if (template == null) {
            LOGGER.info("No se ha encontrado ninguna configuración para este dispositivo con hash {}",
                    huella != null ? huella.hashCode() : "null");
        } else {
            configDTO.setId(template.getId());
            configDTO.setLastUpdate(template.getUpdatedAt());
            configDTO.setName(template.getName());
            configDTO.setConfig(parseTemplate(template.getTemplateCommands()));
        }
        configDTO.setFingerprint(huella);
        configDTO.setGroupName(device.getGroup() != null ? device.getGroup().getName() : "Sin grupo");
        return configDTO;
    }

    /**
     * Parsea los comandos de la plantilla y construye una cadena de comandos.
     * Los comandos se ordenan por su orden de ejecución y se unen con "&&".
     *
     * @param templateCommands Lista de comandos de la plantilla.
     * @return Cadena de comandos formateada.
     */
    private String parseTemplate(List<TemplateCommand> templateCommands) {
        return templateCommands.stream()
                .sorted(Comparator.comparingInt(TemplateCommand::getExecutionOrder))
                .map(this::buildCommandString)
                .collect(Collectors.joining(" && "));
    }

    /**
     * Construye una cadena de comandos a partir de un TemplateCommand.
     * Reemplaza los parámetros en el comando con sus valores correspondientes.
     *
     * @param templateCommand Comando de plantilla que contiene el comando y sus parámetros.
     * @return Cadena de comando con los parámetros reemplazados.
     */
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

    private static Supplier<ConfigException> throwNotFoundException() {
        return () -> new ConfigException("Dispositivo no encontrado", GCAException.ErrorType.NOT_FOUND);
    }
}
