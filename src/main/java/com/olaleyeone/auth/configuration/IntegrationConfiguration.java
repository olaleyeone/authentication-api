package com.olaleyeone.auth.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
//        MailConfiguration.class,
        KafkaProducerConfig.class,
//        AfriTalkingConfiguration.class
})
@ComponentScan({
        "com.olaleyeone.auth.integration",
        "com.olaleyeone.auth.event",
        "com.olaleyeone.auth.messaging"
})
public class IntegrationConfiguration {
}
