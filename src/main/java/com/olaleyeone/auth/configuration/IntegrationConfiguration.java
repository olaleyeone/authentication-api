package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.integration.etc.PhoneNumberServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MailConfiguration.class, KafkaProducerConfig.class, KafkaTopicConfig.class})
@ComponentScan({
        "com.olaleyeone.auth.integration",
        "com.olaleyeone.auth.messaging"
})
public class IntegrationConfiguration {

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("NG");
    }
}
