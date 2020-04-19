package com.olaleyeone.auth.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "com.olaleyeone.auth.controller",
        "com.olaleyeone.auth.advice",
        "com.olaleyeone.auth.validator",
        "com.olaleyeone.auth.response.handler"
})
@Import({AdditionalComponentsConfiguration.class, BeanValidationConfiguration.class})
public class WebConfiguration {
}
