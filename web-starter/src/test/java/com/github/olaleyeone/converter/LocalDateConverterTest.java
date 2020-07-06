package com.github.olaleyeone.converter;

import com.github.olaleyeone.test.component.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocalDateConverterTest extends ComponentTest {

    private DefaultFormattingConversionService formatterRegistry;

    @BeforeEach
    public void setUp() {
        formatterRegistry = new DefaultFormattingConversionService();
        formatterRegistry.addConverter(new LocalDateConverter("yyyy-MM-dd"));
    }

    @Test
    void convertNull() {
        assertNull(formatterRegistry.convert("", LocalDate.class));
    }

    @Test
    void convert() {
        LocalDate now = LocalDate.now();
        LocalDate output = formatterRegistry.convert(now.format(DateTimeFormatter.ISO_DATE), LocalDate.class);
        assertEquals(now, output);
    }
}