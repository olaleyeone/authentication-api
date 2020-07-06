package com.github.olaleyeone.converter;

import com.github.olaleyeone.test.component.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OffsetDateTimeConverterTest extends ComponentTest {

    private DefaultFormattingConversionService formatterRegistry;

    @BeforeEach
    public void setUp() {
        formatterRegistry = new DefaultFormattingConversionService();
        formatterRegistry.addConverter(new OffsetDateTimeConverter(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new LocalDateConverter("yyyy-MM-dd"));
//        registry.addConverter(new DateConverter("yyyy-MM-dd"));
////        registry.addConverter(new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ss.SSS"));
//    }

    @Test
    void convertNull() {
        assertNull(formatterRegistry.convert("", OffsetDateTime.class));
    }

    @Test
    void convert() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime output = formatterRegistry.convert(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), OffsetDateTime.class);
        assertEquals(now, output);
    }
}