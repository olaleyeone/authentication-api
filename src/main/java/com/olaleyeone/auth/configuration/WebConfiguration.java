package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.security.interceptors.AccessConstraintHandlerInterceptor;
import com.olaleyeone.auth.security.interceptors.RemoteAddressConstraintHandlerInterceptor;
import com.olaleyeone.entitysearch.configuration.SearchConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        SecurityConfiguration.class,
        SearchConfiguration.class
})
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(beanFactory.createBean(TaskContextHandlerInterceptor.class));
        registry.addInterceptor(beanFactory.createBean(RemoteAddressConstraintHandlerInterceptor.class));
        registry.addInterceptor(beanFactory.createBean(AccessConstraintHandlerInterceptor.class));
    }
}
