package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.integration.auth.*;
import com.olaleyeone.auth.integration.email.MailGunApiClientFactory;
import com.olaleyeone.auth.integration.email.MailServiceImpl;
import com.olaleyeone.auth.integration.email.VerificationEmailSender;
import com.olaleyeone.auth.integration.email.VerificationEmailSenderImpl;
import com.olaleyeone.auth.integration.etc.*;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class IntegrationConfiguration {

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl("US");
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
    public BaseJwtService baseJwtService(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(BaseJwtService.class);
    }

    @Scope(DefaultListableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public SigningKeyResolverImpl SigningKeyResolverImpl(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(SigningKeyResolverImpl.class);
    }

    @JwtToken(JwtTokenType.ACCESS)
    @Bean
    public JwtService accessTokenJwtService(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(AccessTokenJwtServiceImpl.class);
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public JwtService refreshTokenJwtService(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(RefreshTokenJwtServiceImpl.class);
    }

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngineImpl();
    }

    @Bean
    public HashService hashService() {
        return new HashServiceImpl();
    }
}
