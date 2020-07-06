package com.github.olaleyeone.converter;

import com.github.olaleyeone.test.component.ComponentTest;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.text.ParseException;
import java.util.Date;

import static com.github.olaleyeone.configuration.JacksonConfiguration.DEFAULT_DATE_TIME_FORMAT;
import static org.junit.jupiter.api.Assertions.*;

class DateConverterTest extends ComponentTest {

    private DefaultFormattingConversionService formatterRegistry;

    @BeforeEach
    public void setUp() {
        formatterRegistry = new DefaultFormattingConversionService();
        formatterRegistry.addConverter(new DateConverter(DEFAULT_DATE_TIME_FORMAT));
    }

    @Test
    void convertNull() {
        assertNull(formatterRegistry.convert("", Date.class));
    }

    @Test
    void convert() {
        Date now = new Date();
        Date output = formatterRegistry.convert(DateFormatUtils.format(now, DEFAULT_DATE_TIME_FORMAT), Date.class);
        assertEquals(now, output);
    }

    @Test
    void convertWithError() {
        Date now = new Date();
        assertThrows(ConversionFailedException.class,
                () -> formatterRegistry.convert(DateFormatUtils.format(now, "yyyy-MM-dd"), Date.class));
    }
}