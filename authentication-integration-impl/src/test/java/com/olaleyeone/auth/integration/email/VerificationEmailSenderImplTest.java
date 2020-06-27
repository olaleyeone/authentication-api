package com.olaleyeone.auth.integration.email;

import com.github.olaleyeone.mailsender.api.MailService;
import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class VerificationEmailSenderImplTest extends ComponentTest {

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MailService mailService;

    @Mock
    private TaskContextFactory taskContextFactory;

    @InjectMocks
    private VerificationEmailSenderImpl verificationEmailSender;

    @BeforeEach
    void setUp() {
        Mockito.doAnswer(invocation -> {
            Action action = invocation.getArgument(2);
            action.execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldCacheDefaultEmailTemplate() throws IOException {
        verificationEmailSender.sendVerificationCode(new PortalUserIdentifierVerification(), faker.code().asin());
        Mockito.verify(templateEngine, Mockito.times(1))
                .getAsString(Mockito.argThat(argument1 -> {
                    Mockito.verify(templateEngine, Mockito.times(1))
                            .getAsString(Mockito.argThat(argument2 -> {
                                assertSame(argument1, argument2);
                                return true;
                            }), Mockito.any());
                    return true;
                }), Mockito.any());
    }

    @Test
    void sendVerificationCode() {
        String body = faker.backToTheFuture().quote();
        Mockito.doReturn(body).when(templateEngine).getAsString(Mockito.any(), Mockito.any());

        PortalUserIdentifierVerification verification = new PortalUserIdentifierVerification();
        verification.setIdentifier(faker.internet().emailAddress());
        String verificationCode = faker.code().asin();
        verificationEmailSender.sendVerificationCode(verification, verificationCode);

        Mockito.verify(templateEngine, Mockito.times(1))
                .getAsString(Mockito.eq("email-verification-code.ftl.html"), Mockito.argThat(argument -> {
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