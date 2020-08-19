package com.github.olaleyeone.sms.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AfriTalkingConfig {

    private final String baseUrl;
    private final String username;
    private final String apiKey;

    public String getBaseUrl() {
        if (!baseUrl.endsWith("/")) {
            return baseUrl + "/";
        }
        return this.baseUrl;
    }
}
