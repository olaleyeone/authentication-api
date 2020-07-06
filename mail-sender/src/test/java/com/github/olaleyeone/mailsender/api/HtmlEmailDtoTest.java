package com.github.olaleyeone.mailsender.api;

import com.github.olaleyeone.mailsender.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlEmailDtoTest extends ComponentTest {

    private HtmlEmailDto htmlEmailDto;

    @BeforeEach
    void setUp() {
        htmlEmailDto = new HtmlEmailDto();
    }

    @Test
    void addRecipientEmails() {
        List<String> recipientEmails = Arrays.asList(faker.internet().emailAddress(), faker.internet().emailAddress());
        recipientEmails.forEach(htmlEmailDto::addRecipientEmail);
        assertEquals(recipientEmails, htmlEmailDto.getRecipientEmails());
    }

    @Test
    void addAttachments() {
        List<DataSource> dataSources = Arrays.asList(
                new URLDataSource(getClass().getResource(".")),
                new URLDataSource(getClass().getResource(".")));
        dataSources.forEach(htmlEmailDto::addAttachment);
        assertEquals(dataSources, htmlEmailDto.getAttachments());
    }
}