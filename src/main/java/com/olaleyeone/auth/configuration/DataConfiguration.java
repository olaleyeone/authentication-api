package com.olaleyeone.auth.configuration;

import com.github.olaleyeone.configuration.EntitySearchConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({
        "com.olaleyeone.auth.repository",
        "com.olaleyeone.audittrail.repository"
})
@EntityScan({
        "com.olaleyeone.data",
        "com.olaleyeone.auth.data",
        "com.olaleyeone.audittrail.entity"
})
@Import(EntitySearchConfiguration.class)
public class DataConfiguration {
}
