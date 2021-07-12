//package com.olaleyeone.auth.integration.email;
//
//import com.github.olaleyeone.mailsender.api.HtmlEmailDto;
//import com.github.olaleyeone.mailsender.api.MailService;
//import com.olaleyeone.audittrail.impl.TaskContextFactory;
//import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
//import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
//import com.olaleyeone.auth.data.enums.UserIdentifierType;
//import com.olaleyeone.auth.integration.etc.TemplateEngine;
//import com.olaleyeone.auth.integration.security.PasswordResetTokenGenerator;
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@Builder
//@Component
//public class PasswordResetTokenEmailSenderImpl implements PasswordResetTokenEmailSender {
//
//    public static final String MAIL_SUBJECT = "Password Reset";
//    public static final String TEMPLATE_NAME = "password-reset-link.ftl.html";
//
//    private final TemplateEngine templateEngine;
//    private final MailService mailService;
//
//    private final PasswordResetTokenGenerator passwordResetTokenGenerator;
//
//    private final TaskContextFactory taskContextFactory;
//
//    @Async
//    @Override
//    public void sendResetLink(PasswordResetRequest passwordResetRequest, String host) {
//        PortalUserIdentifier portalUserIdentifier = passwordResetRequest.getPortalUserIdentifier();
//        taskContextFactory.startBackgroundTask("SEND PASSWORD RESET LINK",
//                String.format("Send password reset link to %s", portalUserIdentifier.getIdentifier()),
//                () -> {
//                    if (portalUserIdentifier.getIdentifierType() != UserIdentifierType.EMAIL) {
//                        throw new IllegalArgumentException();
//                    }
//                    sendResetLink(portalUserIdentifier, host, passwordResetTokenGenerator.generateJwt(passwordResetRequest).getToken());
//                });
//    }
//
//    private void sendResetLink(PortalUserIdentifier portalUserIdentifier, String host, String token) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("displayName", portalUserIdentifier.getPortalUser().getDisplayName());
//        params.put("host", host);
//        params.put("email", portalUserIdentifier.getIdentifier());
//        params.put("resetToken", token);
//
//        HtmlEmailDto emailDto = new HtmlEmailDto();
//        emailDto.setSubject(MAIL_SUBJECT);
//        emailDto.setHtmlMessage(templateEngine.getAsString(TEMPLATE_NAME, params));
//        emailDto.setRecipientEmails(Collections.singletonList(portalUserIdentifier.getIdentifier()));
//        mailService.sendEmail(emailDto);
//    }
//}
