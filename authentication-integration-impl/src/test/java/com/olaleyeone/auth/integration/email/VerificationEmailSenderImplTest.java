package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.integration.exception.TemplateNotFoundException;
import com.olaleyeone.auth.service.SettingService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificationEmailSenderImplTest extends ComponentTest {

    @Mock
    private SettingService settingService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MailService mailService;

    private VerificationEmailSenderImpl verificationEmailSender;

    @BeforeEach
    void setUp() {
        verificationEmailSender = new VerificationEmailSenderImpl(settingService, templateEngine, mailService);
    }

    @Test
    void testNoEmailTemplate() {
        assertThrows(TemplateNotFoundException.class,
                () -> verificationEmailSender.sendVerificationCode(new PortalUserIdentifierVerification(), faker.code().asin()));
    }

    @Test
    void sendVerificationCode() {
        String templateStr = faker.internet().slug();
        Mockito.doReturn(Optional.of(templateStr)).when(settingService).getString(Mockito.any());

        String body = faker.backToTheFuture().quote();
        Mockito.doReturn(body).when(templateEngine).getAsString(Mockito.any(), Mockito.any());

        PortalUserIdentifierVerification verification = new PortalUserIdentifierVerification();
        verification.setIdentifier(faker.internet().emailAddress());
        String verificationCode = faker.code().asin();
        verificationEmailSender.sendVerificationCode(verification, verificationCode);

        Mockito.verify(templateEngine, Mockito.times(1))
                .getAsString(Mockito.eq(templateStr), Mockito.argThat(argument -> {
                    assertEquals(verification.getIdentifier(), argument.get("email"));
                    assertEquals(verificationCode, argument.get("verificationCode"));
                    return true;
                }));

        Mockito.verify(mailService, Mockito.times(1))
                .sendEmail(Mockito.argThat(argument -> {
                    argument.getRecipientEmails().contains(verification.getIdentifier());
                    assertEquals(body, argument.getHtmlMessage());
                    assertEquals(VerificationEmailSenderImpl.EMAIL_VERIFICATION_MAIL_SUBJECT, argument.getSubject());
                    return true;
                }));
    }
}