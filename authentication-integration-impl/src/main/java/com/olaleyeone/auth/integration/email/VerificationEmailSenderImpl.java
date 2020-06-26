package com.olaleyeone.auth.integration.email;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.github.olaleyeone.mailsender.api.HtmlEmailDto;
import com.github.olaleyeone.mailsender.api.MailService;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.service.SettingService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Builder
public class VerificationEmailSenderImpl implements VerificationEmailSender {

    public static final String EMAIL_VERIFICATION_TEMPLATE = "EMAIL_VERIFICATION_TEMPLATE";
    public static final String EMAIL_VERIFICATION_MAIL_SUBJECT = "Email Verification";

    private final SettingService settingService;
    private final TemplateEngine templateEngine;
    private final MailService mailService;

    private final TaskContextFactory taskContextFactory;

    private final ApplicationContext applicationContext;

    @Async
    @Override
    public void sendVerificationCode(PortalUserIdentifierVerification user, String verificationCode) {
        taskContextFactory.startBackgroundTask("SEND EMAIL VERIFICATION CODE",
                String.format("Send verification code to %s", user.getIdentifier()),
                () -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("email", user.getIdentifier());
                    params.put("verificationCode", verificationCode);

                    HtmlEmailDto emailDto = new HtmlEmailDto();
                    emailDto.setSubject(EMAIL_VERIFICATION_MAIL_SUBJECT);
                    emailDto.setHtmlMessage(templateEngine.getAsString("email-verification-code.ftl.html", params));
                    emailDto.setRecipientEmails(Collections.singletonList(user.getIdentifier()));
                    mailService.sendEmail(emailDto);
                });
    }
}
