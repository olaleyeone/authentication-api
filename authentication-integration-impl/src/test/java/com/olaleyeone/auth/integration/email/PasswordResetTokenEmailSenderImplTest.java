package com.olaleyeone.auth.integration.email;

import com.github.olaleyeone.mailsender.api.MailService;
import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.integration.security.PasswordResetTokenGenerator;
import com.olaleyeone.auth.service.SettingService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordResetTokenEmailSenderImplTest extends ComponentTest {

    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private MailService mailService;

    @Mock
    private PasswordResetTokenGenerator passwordResetTokenGenerator;

    @Mock
    private TaskContextFactory taskContextFactory;

    @Mock
    private JwtDto jwtDto;

    @InjectMocks
    private PasswordResetTokenEmailSenderImpl passwordResetTokenEmailSender;

    private PasswordResetRequest passwordResetRequest;
    private PortalUserIdentifier portalUserIdentifier;
    private PortalUser portalUser;

    @BeforeEach
    void setUp() {
        portalUser = new PortalUser();
        portalUser.setDisplayName(faker.name().fullName());
        portalUser.setId(faker.number().randomNumber());

        portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setIdentifierType(UserIdentifierType.EMAIL);
        portalUserIdentifier.setIdentifier(faker.internet().emailAddress());
        portalUserIdentifier.setPortalUser(portalUser);

        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setPortalUserIdentifier(portalUserIdentifier);
        passwordResetRequest.setId(faker.number().randomNumber());

        Mockito.doAnswer(invocation -> {
            Action action = invocation.getArgument(2);
            action.execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void sendResetLinkToPhoneNumber() {
        portalUserIdentifier.setIdentifierType(UserIdentifierType.PHONE_NUMBER);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetTokenEmailSender.sendResetLink(passwordResetRequest, faker.internet().domainName()));
    }

    @Test
    void sendResetLink() {
        Mockito.doReturn(jwtDto).when(passwordResetTokenGenerator).generateJwt(Mockito.any());
        String resetToken = faker.internet().password();
        Mockito.doReturn(resetToken).when(jwtDto).getToken();
        String domainName = faker.internet().domainName();

        String body = faker.backToTheFuture().quote();
        Mockito.doReturn(body).when(templateEngine).getAsString(Mockito.any(), Mockito.any());

        passwordResetTokenEmailSender.sendResetLink(passwordResetRequest, domainName);

        Mockito.verify(templateEngine, Mockito.times(1))
                .getAsString(Mockito.eq(PasswordResetTokenEmailSenderImpl.TEMPLATE_NAME), Mockito.argThat(argument -> {
                    assertEquals(domainName, argument.get("host"));
                    assertEquals(portalUserIdentifier.getIdentifier(), argument.get("email"));
                    assertEquals(resetToken, argument.get("resetToken"));
                    assertEquals(portalUser.getDisplayName(), argument.get("displayName"));
                    return true;
                }));

        Mockito.verify(mailService, Mockito.times(1))
                .sendEmail(Mockito.argThat(argument -> {
                    argument.getRecipientEmails().contains(portalUserIdentifier.getIdentifier());
                    assertEquals(body, argument.getHtmlMessage());
                    assertEquals(PasswordResetTokenEmailSenderImpl.MAIL_SUBJECT, argument.getSubject());
                    return true;
                }));
    }
}