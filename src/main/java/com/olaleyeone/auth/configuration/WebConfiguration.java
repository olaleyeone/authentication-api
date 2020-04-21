package com.olaleyeone.auth.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "com.olaleyeone.auth.controller",
        "com.olaleyeone.auth.advice",
        "com.olaleyeone.auth.validator",
        "com.olaleyeone.auth.response.handler",
        "com.olaleyeone.auth.security.authorizer",
        "com.olaleyeone.auth.search"
})
@Import({
        AdditionalComponentsConfiguration.class,
        BeanValidationConfiguration.class,
        SecurityConfiguration.class
})
public class WebConfiguration {
}
