package com.github.olaleyeone.converter;

import com.github.olaleyeone.test.component.ComponentTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.github.olaleyeone.configuration.JacksonConfiguration.DEFAULT_DATE_TIME_FORMAT;
import static org.junit.jupiter.api.Assertions.*;

class GsonConverterTest extends ComponentTest {

    private Gson gson;

    @BeforeEach
    public void setUp() {
        gson = new GsonBuilder()
                .setDateFormat(DEFAULT_DATE_TIME_FORMAT)
                .registerTypeAdapterFactory(OffsetDateTimeTypeAdapter.FACTORY)
                .registerTypeAdapterFactory(LocalDateTimeTypeAdapter.FACTORY)
                .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
                .create();
    }

    @Test
    void readOffsetDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        OffsetDateTime offsetDateTime = gson.fromJson(String.format("\"%s\"", format), OffsetDateTime.class);
        assertNotNull(offsetDateTime);
        assertEquals(now, offsetDateTime);
    }

    @Test
    void readNullOffsetDateTime() {
        assertNull(gson.fromJson("null", OffsetDateTime.class));
    }

    @Test
    void writeOffsetDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        assertEquals(String.format("\"%s\"", format), gson.toJson(now));
    }

    @Test
    void writeNullOffsetDateTime() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            new OffsetDateTimeTypeAdapter().write(jsonWriter, null);
            assertEquals("null", writer.toString());
        }
    }

    @Test
    void readLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime localDateTime = gson.fromJson(String.format("\"%s\"", format), LocalDateTime.class);
        assertNotNull(localDateTime);
        assertEquals(now, localDateTime);
    }

    @Test
    void readNullLocalDateTime() {
        assertNull(gson.fromJson("null", LocalDateTime.class));
    }

    @Test
    void writeLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_DATE_TIME);
        assertEquals(String.format("\"%s\"", format), gson.toJson(now));
    }

    @Test
    void writeNullLocalDateTime() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            new LocalDateTimeTypeAdapter().write(jsonWriter, null);
            assertEquals("null", writer.toString());
        }
    }

    @Test
    void readLocalDate() {
        LocalDate now = LocalDate.now();
        String format = now.format(DateTimeFormatter.ISO_DATE);
        LocalDate localDate = gson.fromJson(String.format("\"%s\"", format), LocalDate.class);
        assertNotNull(localDate);
        assertEquals(now, localDate);
    }

    @Test
    void readNullLocalDate() {
        assertNull(gson.fromJson("null", LocalDate.class));
    }

    @Test
    void writeLocalDate() {
        LocalDate now = LocalDate.now();
        String format = now.format(DateTimeFormatter.ISO_DATE);
        assertEquals(String.format("\"%s\"", format), gson.toJson(now));
    }

    @Test
    void writeNullLocalDate() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            new LocalDateTypeAdapter().write(jsonWriter, null);
            assertEquals("null", writer.toString());
        }
    }

    @Test
    void writeDate() {
        Date now = new Date();
        String format = gson.toJson(now);
        assertEquals(String.format("\"%s\"", DateFormatUtils.format(now, DEFAULT_DATE_TIME_FORMAT)), format);
    }
}