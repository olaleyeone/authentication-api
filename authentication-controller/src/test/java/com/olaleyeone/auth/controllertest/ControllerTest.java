package com.olaleyeone.auth.controllertest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.configuration.AdditionalComponentsConfiguration;
import com.olaleyeone.auth.configuration.BeanValidationConfiguration;
import com.olaleyeone.auth.configuration.SecurityConfiguration;
import com.olaleyeone.entitysearch.util.PredicateExtractor;
import com.olaleyeone.entitysearch.util.SearchFilterPredicateExtractor;
import com.olaleyeone.auth.security.data.AccessClaims;
import com.olaleyeone.auth.security.data.AccessClaimsExtractor;
import com.olaleyeone.auth.security.interceptors.AccessConstraintHandlerInterceptor;
import com.olaleyeone.auth.security.interceptors.RemoteAddressConstraintHandlerInterceptor;
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

import javax.inject.Inject;
import java.util.Random;

@ActiveProfiles("test")
@WebMvcTest
@ContextConfiguration(classes = {ControllerTest.$Config.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Faker faker = Faker.instance(new Random());

    @Autowired
    protected AccessClaimsExtractor accessClaimsExtractor;

    protected AccessClaims accessClaims;

    @Inject
    private ApplicationContext applicationContext;

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
        accessClaims = Mockito.mock(AccessClaims.class);
        Mockito.doReturn(accessClaims).when(accessClaimsExtractor).getClaims(Mockito.any());
    }

    @Configuration
    @ComponentScan({
            "com.olaleyeone.auth.controller",
            "com.olaleyeone.auth.advice"
    })
    @Import({
            AdditionalComponentsConfiguration.class,
            BeanValidationConfiguration.class,
            SecurityConfiguration.class,
            SecurityMockConfig.class,
            ValidatorMockConfig.class,
            ServiceMockConfig.class,
            RepositoryMockConfig.class,
            ResponseHandlerMockConfig.class,
            SearchHandlerMockConfig.class
    })
    static class $Config implements WebMvcConfigurer {

        @Autowired
        private AutowireCapableBeanFactory beanFactory;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(beanFactory.createBean(RemoteAddressConstraintHandlerInterceptor.class));
            registry.addInterceptor(beanFactory.createBean(AccessConstraintHandlerInterceptor.class));
        }

        @Bean
        public PredicateExtractor predicateExtractor(ApplicationContext applicationContext) {
            return applicationContext.getAutowireCapableBeanFactory().createBean(PredicateExtractor.class);
        }

        @Bean
        public SearchFilterPredicateExtractor searchFilterPredicateExtractor(ApplicationContext applicationContext) {
            return applicationContext.getAutowireCapableBeanFactory().createBean(SearchFilterPredicateExtractor.class);
        }
    }

}
