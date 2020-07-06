package com.github.olaleyeone;

import com.github.olaleyeone.configuration.BeanValidationConfiguration;
import com.github.olaleyeone.configuration.JacksonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({BeanValidationConfiguration.class, JacksonConfiguration.class})
public class ConfigurationTest {

    @Test
    public void test() {
        //load context
    }
}
