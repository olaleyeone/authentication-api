package com.olaleyeone.auth.dto;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.List;

public class HtmlEmailDto {

    private String subject;
    private String htmlMessage;
    private List<String> recipientEmails;
    private List<DataSource> attachments;

    public String getHtmlMessage() {
        return htmlMessage;
    }

    public void setHtmlMessage(String htmlMessage) {
        this.htmlMessage = htmlMessage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    public void addRecipientEmails(String recipientEmail) {
        if (recipientEmails == null) {
            recipientEmails = new ArrayList<>();
        }
        this.recipientEmails.add(recipientEmail);
    }

    public List<DataSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DataSource> attachments) {
        this.attachments = attachments;
    }

    public void addAttachments(DataSource attachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(attachment);
    }
}
