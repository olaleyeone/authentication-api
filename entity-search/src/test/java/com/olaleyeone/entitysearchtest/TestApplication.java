package com.olaleyeone.entitysearchtest;

import com.olaleyeone.entitysearch.configuration.SearchConfiguration;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@EntityScan({"com.olaleyeone.entitysearchtest.data"})
@Import(SearchConfiguration.class)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public QuerydslBindingsFactory querydslBindingsFactory() {
        return new QuerydslBindingsFactory(new SimpleEntityPathResolver(""));
    }

    @Bean
    public HttpServletRequest httpServletRequest() {
        return Mockito.mock(HttpServletRequest.class);
    }
}
