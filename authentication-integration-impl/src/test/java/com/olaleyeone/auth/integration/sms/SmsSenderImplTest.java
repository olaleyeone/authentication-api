package com.olaleyeone.auth.integration.sms;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class SmsSenderImplTest extends ComponentTest {

    @Mock
    private com.github.olaleyeone.sms.api.SmsSender smsSender;

    private PortalUserIdentifier portalUserIdentifier;
    private OneTimePassword oneTimePassword;
    private SmsSenderImpl smsSenderImpl;

    @BeforeEach
    void setUp() {
        portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setIdentifier(faker.internet().emailAddress());
        oneTimePassword = new OneTimePassword();
        oneTimePassword.setUserIdentifier(portalUserIdentifier);

        smsSenderImpl = new SmsSenderImpl(smsSender);
    }

    @Test
    void sendOtp() {
        String password = faker.internet().password();
        smsSenderImpl.sendOtp(oneTimePassword, password);
        Mockito.verify(smsSender, Mockito.times(1))
                .send(Mockito.argThat(argument -> argument.getTo().equals(portalUserIdentifier.getIdentifier())
                        && argument.getMessage().contains(password)));
    }

    @Test
    void sendVerificationCode() {
        PortalUserIdentifierVerification identifierVerification = new PortalUserIdentifierVerification();
        identifierVerification.setIdentifier(faker.phoneNumber().cellPhone());
        String code = faker.internet().password();
        smsSenderImpl.sendVerificationCode(identifierVerification, code);
        Mockito.verify(smsSender, Mockito.times(1))
                .send(Mockito.argThat(argument -> argument.getTo().equals(identifierVerification.getIdentifier())
                        && argument.getMessage().contains(code)));
    }
}