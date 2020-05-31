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
import com.github.olaleyeone.auth.data.AuthorizedRequest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.inject.Provider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Configuration
public class AdditionalComponentsConfiguration {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Bean
    @Scope(ConfigurableWebApplicationContext.SCOPE_REQUEST)
    public RequestMetadata requestMetadata(Provider<AuthorizedRequest> requestMetadataProvider) {
        AuthorizedRequest authorizedRequest = requestMetadataProvider.get();

        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(authorizedRequest.getIpAddress());
        requestMetadata.setUserAgent(authorizedRequest.getUserAgent());
        if (authorizedRequest.getAccessClaims() != null) {
            Optional.ofNullable(authorizedRequest.getAccessClaims().getSubject())
                    .map(Long::valueOf)
                    .ifPresent(requestMetadata::setPortalUserId);
            Optional.ofNullable(authorizedRequest.getAccessClaims().getId())
                    .map(Long::valueOf)
                    .ifPresent(requestMetadata::setRefreshTokenId);
        }
        return requestMetadata;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT));

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
