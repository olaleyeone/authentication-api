package com.github.olaleyeone.mailsender.impl;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
public class MailGunConfig {

    private final String mailGunMessagesApiKey;
    private final String mailGunMessageBaseUrl;
    private final String emailSenderAddress;
    private final String emailSenderName;

    public String getMailGunMessageBaseUrl() {
        if (!mailGunMessageBaseUrl.endsWith("/")) {
            return mailGunMessageBaseUrl + "/";
        }
        return this.mailGunMessageBaseUrl;
    }
}
