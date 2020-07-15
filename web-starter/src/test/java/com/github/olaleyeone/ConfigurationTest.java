package com.github.olaleyeone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olaleyeone.configuration.BeanValidationConfiguration;
import com.github.olaleyeone.configuration.JacksonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Month;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@Import({BeanValidationConfiguration.class, JacksonConfiguration.class})
public class ConfigurationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws JsonProcessingException {
        OffsetDateTime offsetDateTime = objectMapper.readValue("\"2020-07-15T07:11:50.073841+01:00\"", OffsetDateTime.class);
        assertNotNull(offsetDateTime);
        assertEquals(2020, offsetDateTime.getYear());
        assertEquals(Month.JULY, offsetDateTime.getMonth());
        assertEquals(15, offsetDateTime.getDayOfMonth());
    }
}
