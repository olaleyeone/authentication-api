package com.olaleyeone.auth.dto;

import lombok.Data;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.List;

@Data
public class HtmlEmailDto {

    private String subject;
    private String htmlMessage;
    private List<String> recipientEmails;
    private List<DataSource> attachments;

    public void addRecipientEmails(String recipientEmail) {
        if (recipientEmails == null) {
            recipientEmails = new ArrayList<>();
        }
        this.recipientEmails.add(recipientEmail);
    }

    public void addAttachments(DataSource attachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(attachment);
    }
}
