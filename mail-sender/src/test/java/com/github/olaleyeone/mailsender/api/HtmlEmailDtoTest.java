package com.github.olaleyeone.mailsender.api;

import com.github.olaleyeone.mailsender.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlEmailDtoTest extends ComponentTest {

    private HtmlEmailDto htmlEmailDto;

    @BeforeEach
    void setUp() {
        htmlEmailDto = new HtmlEmailDto();
    }

    @Test
    void addRecipientEmails() {
        String recipientEmail = faker.internet().emailAddress();
        htmlEmailDto.addRecipientEmails(recipientEmail);
        assertEquals(Collections.singletonList(recipientEmail), htmlEmailDto.getRecipientEmails());
    }

    @Test
    void addAttachments() {
        DataSource dataSource = new URLDataSource(getClass().getResource("."));
        htmlEmailDto.addAttachments(dataSource);
        assertEquals(Collections.singletonList(dataSource), htmlEmailDto.getAttachments());
    }
}