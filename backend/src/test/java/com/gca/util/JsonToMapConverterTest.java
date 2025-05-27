package com.gca.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonToMapConverterTest {

    JsonToMapConverter converter = new JsonToMapConverter();

    @Test
    void testConvertToDatabaseColumn() {
        var attribute = Map.of("name", "test");

        var result = converter.convertToDatabaseColumn(attribute);

        Assertions.assertEquals("{\"name\":\"test\"}", result);
    }

    @Test
    void testConvertToDatabaseColumn_ThrowsException() {
        Map attribute = Map.of("key", new Object());
        @SuppressWarnings("unchecked")
        Map<String, String> wrongAttribute = (Map<String, String>) attribute;

        assertThrows(IllegalArgumentException.class, () -> converter.convertToDatabaseColumn(wrongAttribute));
    }

    @Test
    void testConvertToEntityAttribute() {
        var dbData = "{\"name\":\"test\"}";

        var result = converter.convertToEntityAttribute(dbData);

        Assertions.assertEquals(Map.of("name", "test"), result);
    }

    @Test
    void testConvertToEntityAttribute_Null() {

        var result = converter.convertToEntityAttribute(null);

        Assertions.assertEquals(new HashMap<>(),result);
    }

    @Test
    void testConvertToEntityAttribute_ThrowsException() {
        var dbData = "{\"name\":\"test\"";

        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(dbData));
    }
}
