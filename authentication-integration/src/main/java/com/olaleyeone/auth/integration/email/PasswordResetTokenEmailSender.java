package com.olaleyeone.auth.integration.email;


import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

public interface PasswordResetTokenEmailSender {

    void sendResetLink(PasswordResetRequest passwordResetRequest, String host);
}
