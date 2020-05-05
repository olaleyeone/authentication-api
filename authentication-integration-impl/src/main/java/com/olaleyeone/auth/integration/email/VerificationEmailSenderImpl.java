package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.dto.HtmlEmailDto;
import com.olaleyeone.auth.integration.etc.TemplateEngine;
import com.olaleyeone.auth.integration.exception.TemplateNotFoundException;
import com.olaleyeone.auth.service.SettingService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class VerificationEmailSenderImpl implements VerificationEmailSender {

    public static final String EMAIL_VERIFICATION_TEMPLATE = "EMAIL_VERIFICATION_TEMPLATE";
    public static final String EMAIL_VERIFICATION_MAIL_SUBJECT = "Email Verification";

    private final SettingService settingService;
    private final TemplateEngine templateEngine;
    private final MailService mailService;

    @Override
    public void sendVerificationCode(PortalUserIdentifierVerification user, String verificationCode) {
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
    }
}
