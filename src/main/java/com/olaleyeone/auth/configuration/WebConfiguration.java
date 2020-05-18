package com.olaleyeone.auth.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.olaleyeone.auth.constant.TimeFormatConstants;
import com.olaleyeone.auth.interceptor.TaskContextHandlerInterceptor;
import com.olaleyeone.auth.security.interceptors.AccessConstraintHandlerInterceptor;
import com.olaleyeone.auth.security.interceptors.RemoteAddressConstraintHandlerInterceptor;
import com.olaleyeone.entitysearch.configuration.SearchConfiguration;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springdoc.webmvc.ui.SwaggerWelcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
    private ApplicationContext applicationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        registry.addInterceptor(beanFactory.createBean(TaskContextHandlerInterceptor.class));
        registry.addInterceptor(beanFactory.createBean(RemoteAddressConstraintHandlerInterceptor.class));
        AccessConstraintHandlerInterceptor accessConstraintHandlerInterceptor = new AccessConstraintHandlerInterceptor(
                applicationContext,
                Arrays.asList(BasicErrorController.class,
                        OpenApiResource.class,
                        SwaggerWelcome.class)
        );
        beanFactory.autowireBean(accessConstraintHandlerInterceptor);
        registry.addInterceptor(accessConstraintHandlerInterceptor);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.setDateFormat(new SimpleDateFormat(TimeFormatConstants.DEFAULT_DATE_TIME_FORMAT));

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());


        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new StdSerializer<LocalDateTime>(LocalDateTime.class) {

            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }
        });
        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper());
        return jsonConverter;
    }
}
