package com.olaleyeone.auth.controllertest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.olaleyeone.advice.ErrorAdvice;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.interceptors.AccessConstraintHandlerInterceptor;
import com.github.olaleyeone.configuration.BeanValidationConfiguration;
import com.github.olaleyeone.configuration.JacksonConfiguration;
import com.github.olaleyeone.configuration.PredicateConfiguration;
import com.olaleyeone.auth.configuration.RequestMetadataConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.creation.bytebuddy.MockAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.Random;

@ActiveProfiles("test")
@WebMvcTest
@ContextConfiguration(classes = {ControllerTest.$Config.class})
public class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected AccessClaims accessClaims;

    protected final Faker faker = Faker.instance(new Random());

    protected RequestPostProcessor loggedInUser = request -> {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ");
        return request;
    };

    protected RequestPostProcessor body(Object body) {
        return request -> {
            request.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                request.setContent(objectMapper.writeValueAsBytes(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return request;
        };
    }

    @BeforeEach
    public void resetMocks() {
        applicationContext.getBeansOfType(MockAccess.class)
                .values().forEach(Mockito::reset);
    }

    @Configuration
    @ComponentScan({
            "com.olaleyeone.auth.controller"
    })
    @Import({
            RequestMetadataConfiguration.class,
            BeanValidationConfiguration.class,
            JacksonConfiguration.class,
            PredicateConfiguration.class,
            SecurityMockConfig.class,
            ValidatorMockConfig.class,
            ServiceMockConfig.class,
            RepositoryMockConfig.class,
            ResponseHandlerMockConfig.class,
            SearchHandlerMockConfig.class
    })
    static class $Config implements WebMvcConfigurer {

        @Autowired
        private ApplicationContext applicationContext;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
            AccessConstraintHandlerInterceptor accessConstraintHandlerInterceptor = new AccessConstraintHandlerInterceptor(
                    applicationContext,
                    Collections.emptyList()
            );
            beanFactory.autowireBean(accessConstraintHandlerInterceptor);
            registry.addInterceptor(accessConstraintHandlerInterceptor);
        }

        @Bean
        public ErrorAdvice errorAdvice() {
            return new ErrorAdvice();
        }
    }
}
