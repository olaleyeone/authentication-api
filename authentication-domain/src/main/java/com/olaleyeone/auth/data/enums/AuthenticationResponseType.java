package com.olaleyeone.auth.data.enums;

public enum AuthenticationResponseType {

    UNKNOWN_ACCOUNT,
    INCORRECT_CREDENTIAL,
    SUCCESSFUL,
    INACTIVE_ACCOUNT,
    UNKNOWN_OTP,
    EXPIRED_OTP,
    OTP_ALREADY_USED,
    INCORRECT_IDENTIFIER
}
