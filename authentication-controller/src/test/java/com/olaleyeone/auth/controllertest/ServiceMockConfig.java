package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.integration.email.PasswordResetTokenEmailSender;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.service.*;
import com.olaleyeone.auth.integration.email.VerificationEmailSender;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ServiceMockConfig {

    @Bean
    public PhoneNumberService phoneNumberService() {
        return Mockito.mock(PhoneNumberService.class);
    }

    @Bean
    public LoginAuthenticationService authenticationService() {
        return Mockito.mock(LoginAuthenticationService.class);
    }

    @Bean
    public UserRegistrationService userRegistrationService() {
        return Mockito.mock(UserRegistrationService.class);
    }

    @Bean
    public PasswordUpdateService passwordUpdateService() {
        return Mockito.mock(PasswordUpdateService.class);
    }

    @Bean
    public PortalUserIdentifierVerificationService portalUserIdentifierVerificationService() {
        return Mockito.mock(PortalUserIdentifierVerificationService.class);
    }

    @Bean
    public VerificationEmailSender verificationEmailSender() {
        return Mockito.mock(VerificationEmailSender.class);
    }

    @Bean
    public PasswordResetTokenEmailSender passwordResetTokenEmailSender() {
        return Mockito.mock(PasswordResetTokenEmailSender.class);
    }

    @Bean
    public LogoutService logoutService() {
        return Mockito.mock(LogoutService.class);
    }

    @Bean
    public PasswordResetRequestService passwordResetRequestService() {
        return Mockito.mock(PasswordResetRequestService.class);
    }

    @Bean
    public OneTimePasswordService oneTimePasswordService() {
        return Mockito.mock(OneTimePasswordService.class);
    }
}
