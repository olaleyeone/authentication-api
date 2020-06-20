package com.github.olaleyeone.test.entity;

import com.github.olaleyeone.configuration.PredicateConfiguration;
import com.github.olaleyeone.configuration.SearchConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EntityScan({"com.github.olaleyeone.test.entity.data"})
@Import({SearchConfiguration.class, PredicateConfiguration.class})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

//    @Bean
//    public QuerydslBindingsFactory querydslBindingsFactory() {
//        return new QuerydslBindingsFactory(new SimpleEntityPathResolver(""));
//    }
}
