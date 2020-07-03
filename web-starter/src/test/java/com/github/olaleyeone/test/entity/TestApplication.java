package com.github.olaleyeone.test.entity;

import com.github.olaleyeone.configuration.EntitySearchConfiguration;
import com.github.olaleyeone.configuration.PredicateConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({"com.github.olaleyeone.test.entity.data"})
@EnableJpaRepositories("com.github.olaleyeone.test.entity.repository")
@Import({EntitySearchConfiguration.class, PredicateConfiguration.class})
public class TestApplication {
}
