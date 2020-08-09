package com.olaleyeone.auth.integration.sms;


import com.olaleyeone.auth.data.entity.OneTimePassword;

public interface OtpSmsSender {

    void sendOtp(OneTimePassword oneTimePassword, String password);
}
