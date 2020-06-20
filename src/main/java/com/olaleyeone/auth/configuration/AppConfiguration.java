package com.olaleyeone.auth.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DataConfiguration.class})
@ComponentScan({"com.olaleyeone.auth.service"})
public class AppConfiguration {
}
