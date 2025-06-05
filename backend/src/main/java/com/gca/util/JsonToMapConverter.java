package com.gca.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Converter para transformar un {@code Map<String, String>} a JSON y viceversa.
 * Utilizado en JPA para almacenar mapas como cadenas JSON en la base de datos.
 */
@Converter
public class JsonToMapConverter implements AttributeConverter<Map<String, String>, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonToMapConverter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convierte un {@code Map<String, String>} a una cadena JSON.
     * @param attribute el mapa a convertir
     * @return una cadena JSON representando el mapa
     */
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        try {
            LOGGER.debug("Transformar Map a JSON: {}", attribute);
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    /**
     * Convierte una cadena JSON a un {@code Map<String, String>}.
     * @param dbData la cadena JSON a convertir
     * @return un mapa representando los datos de la cadena JSON
     */
    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) return new HashMap<>();
            LOGGER.debug("Transformar JSON a Map: {}", dbData);
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading JSON to map", e);
        }
    }
}

