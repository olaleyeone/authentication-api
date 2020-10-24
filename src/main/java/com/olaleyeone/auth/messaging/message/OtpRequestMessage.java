package com.olaleyeone.auth.messaging.message;

import lombok.Data;

@Data
public class OtpRequestMessage {

    private String identifier;
    private String password;
}
