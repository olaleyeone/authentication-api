package com.olaleyeone.auth.integration.etc;

import com.olaleyeone.auth.test.ComponentTest;
import freemarker.core.InvalidReferenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TemplateEngineImplTest extends ComponentTest {

    private TemplateEngineImpl templateEngine;

    @BeforeEach
    void setUp() {
        templateEngine = new TemplateEngineImpl();
    }

    @Test
    void getAsString() {
        String name = faker.funnyName().name();
        String result = templateEngine.getAsString("Hello ${user}", Collections.singletonMap("user", name));
        assertEquals(String.format("Hello %s", name), result);
    }

    @Test
    void testMissingParam() {
        assertThrows(InvalidReferenceException.class,
                () -> templateEngine.getAsString("Hello ${user}", Collections.emptyMap()));
    }
}