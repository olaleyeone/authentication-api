//package com.olaleyeone.auth.configuration;
//
//import com.github.olaleyeone.mailsender.api.MailGunApiClient;
//import com.github.olaleyeone.mailsender.impl.MailGunApiClientFactory;
//import com.github.olaleyeone.mailsender.impl.MailGunConfig;
//import com.github.olaleyeone.mailsender.impl.MailServiceImpl;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//@Configuration
//public class MailConfiguration {
//
//    @Bean
//    public MailServiceImpl mailService(MailGunApiClient mailGunApi) {
//        return new MailServiceImpl(mailGunApi);
//    }
//
//    @Bean
//    public MailGunApiClientFactory mailGunApiClientFactory(AutowireCapableBeanFactory beanFactory) {
//        return new MailGunApiClientFactory(mailGunConfig(null));
//    }
//
//    @Bean
//    public MailGunConfig mailGunConfig(Environment environment) {
//        return MailGunConfig.builder()
//                .mailGunMessageBaseUrl(environment.getProperty("MAIL_GUN_MESSAGES_URL"))
//                .mailGunMessagesApiKey(environment.getProperty("MAIL_GUN_MESSAGES_API_KEY"))
//                .emailSenderAddress(environment.getProperty("EMAIL_SENDER_ADDRESS"))
//                .emailSenderName(environment.getProperty("EMAIL_SENDER_NAME"))
//                .build();
//    }
//}
