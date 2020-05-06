package com.olaleyeone.auth.integration.exception;

public class TemplateNotFoundException extends RuntimeException {

    public TemplateNotFoundException(String templateName) {
        super(String.format("Template named %s not found", templateName));
    }
}
