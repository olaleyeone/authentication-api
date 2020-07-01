package com.github.olaleyeone.mailsender.impl;

import com.github.olaleyeone.mailsender.test.ComponentTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailGunConfigTest extends ComponentTest {

    @Test
    void getMailGunMessageBaseUrlWithoutTrailingSlash() {
        assertEquals("http://domain.com/", MailGunConfig.builder()
                .mailGunMessageBaseUrl("http://domain.com")
                .build().getMailGunMessageBaseUrl());
    }

    @Test
    void getMailGunMessageBaseUrl() {
        assertEquals("http://domain.com/", MailGunConfig.builder()
                .mailGunMessageBaseUrl("http://domain.com/")
                .build().getMailGunMessageBaseUrl());
    }
}