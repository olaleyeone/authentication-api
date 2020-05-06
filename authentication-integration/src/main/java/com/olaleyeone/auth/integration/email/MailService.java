package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.dto.HtmlEmailDto;

public interface MailService {

    void sendEmail(HtmlEmailDto htmlEmailDto);
}
