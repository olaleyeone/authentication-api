//package com.olaleyeone.auth.configuration;
//
//import com.github.olaleyeone.sms.api.AfriTalkingClient;
//import com.github.olaleyeone.sms.api.SmsSender;
//import com.github.olaleyeone.sms.impl.AfriTalkingApiClientFactory;
//import com.github.olaleyeone.sms.impl.AfriTalkingConfig;
//import com.github.olaleyeone.sms.impl.SmsSenderImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//@Configuration
//public class AfriTalkingConfiguration {
//
//    @Bean
//    public AfriTalkingApiClientFactory afriTalkingApiClientFactory(AfriTalkingConfig afriTalkingConfig) {
//        return new AfriTalkingApiClientFactory(afriTalkingConfig);
//    }
//
//    @Bean
//    public AfriTalkingConfig afriTalkingConfig(Environment environment) {
//        return AfriTalkingConfig.builder()
//                .baseUrl(environment.getProperty("AFRICASTKG_URL"))
//                .username(environment.getProperty("AFRICASTKG_USERNAME"))
//                .apiKey(environment.getProperty("AFRICASTKG_API_KEY"))
//                .build();
//    }
//
//    @Bean
//    public SmsSender smsSender(AfriTalkingClient afriTalkingClient, AfriTalkingConfig afriTalkingConfig) {
//        return new SmsSenderImpl(afriTalkingClient, afriTalkingConfig);
//    }
//}
