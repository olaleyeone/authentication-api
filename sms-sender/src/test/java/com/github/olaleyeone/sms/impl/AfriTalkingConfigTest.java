package com.github.olaleyeone.sms.impl;

import com.github.olaleyeone.sms.test.ComponentTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AfriTalkingConfigTest extends ComponentTest {

    @Test
    void getGatewayUrl() {
        String baseUrl = "http://domain.com";
        AfriTalkingConfig afriTalkingConfig = AfriTalkingConfig.builder()
                .baseUrl(baseUrl)
                .build();
        assertEquals(baseUrl + "/", afriTalkingConfig.getBaseUrl());
    }

    @Test
    void getGatewayUrl2() {
        String baseUrl = "http://domain.com/";
        AfriTalkingConfig afriTalkingConfig = AfriTalkingConfig.builder()
                .baseUrl(baseUrl)
                .build();
        assertEquals(baseUrl, afriTalkingConfig.getBaseUrl());
    }
}