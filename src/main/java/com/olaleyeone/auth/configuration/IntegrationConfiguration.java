package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.integration.email.MailGunApiClientFactory;
import com.olaleyeone.auth.integration.email.MailServiceImpl;
import com.olaleyeone.auth.integration.email.VerificationEmailSender;
import com.olaleyeone.auth.integration.email.VerificationEmailSenderImpl;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.integration.etc.PhoneNumberServiceImpl;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.integration.etc.TemplateEngineImpl;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KafkaProducerConfig.class, KafkaTopicConfig.class})
public class IntegrationConfiguration {

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("NG");
    }

    @Bean
    public MailGunApiClientFactory mailGunApiClientFactory(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(MailGunApiClientFactory.class);
    }

    @Bean
    public MailServiceImpl mailService(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(MailServiceImpl.class);
    }

    @Bean
    public VerificationEmailSender verificationEmailSender(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(VerificationEmailSenderImpl.class);
    }

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngineImpl();
    }
}
