package com.olaleyeone.auth.integration.email;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.dto.HtmlEmailDto;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.integration.exception.TemplateNotFoundException;
import com.olaleyeone.auth.service.SettingService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
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

    @Async
    @Override
    public void sendVerificationCode(PortalUserIdentifierVerification user, String verificationCode) {
        taskContextFactory.startBackgroundTask("SEND EMAIL VERIFICATION CODE",
                String.format("Send verification code to %s", user.getIdentifier()),
                () -> {
                    String template = settingService.getString(EMAIL_VERIFICATION_TEMPLATE)
                            .orElseThrow(() -> new TemplateNotFoundException(EMAIL_VERIFICATION_TEMPLATE));

                    Map<String, Object> params = new HashMap<>();
                    params.put("email", user.getIdentifier());
                    params.put("verificationCode", verificationCode);

                    HtmlEmailDto emailDto = new HtmlEmailDto();
                    emailDto.setSubject(EMAIL_VERIFICATION_MAIL_SUBJECT);
                    emailDto.setHtmlMessage(templateEngine.getAsString(template, params));
                    emailDto.setRecipientEmails(Collections.singletonList(user.getIdentifier()));
                    mailService.sendEmail(emailDto);
                });
    }
}
