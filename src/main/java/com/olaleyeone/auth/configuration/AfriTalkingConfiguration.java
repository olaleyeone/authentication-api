package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.integration.sms.AfriTalkingApiClientFactory;
import com.olaleyeone.auth.integration.sms.AfriTalkingConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AfriTalkingConfiguration {

    @Bean
    public AfriTalkingApiClientFactory afriTalkingApiClientFactory(AfriTalkingConfig afriTalkingConfig) {
        return new AfriTalkingApiClientFactory(afriTalkingConfig);
    }

    @Bean
    public AfriTalkingConfig afriTalkingConfig(Environment environment) {
        return AfriTalkingConfig.builder()
                .baseUrl(environment.getProperty("AFRICASTKG_URL"))
                .username(environment.getProperty("AFRICASTKG_USERNAME"))
                .apiKey(environment.getProperty("AFRICASTKG_API_KEY"))
                .build();
    }
}
