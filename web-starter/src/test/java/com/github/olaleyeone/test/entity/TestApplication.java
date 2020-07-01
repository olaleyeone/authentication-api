package com.github.olaleyeone.test.entity;

import com.github.olaleyeone.configuration.EntitySearchConfiguration;
import com.github.olaleyeone.configuration.PredicateConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EntityScan({"com.github.olaleyeone.test.entity.data"})
@Import({EntitySearchConfiguration.class, PredicateConfiguration.class})
public class TestApplication {
}
