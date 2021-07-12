package com.olaleyeone.auth.dto;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class PasswordResetRequestMessage {

    private UserIdentifierType identifierType;

    private String identifier;

    private String resetCode;
    private String resetToken;

    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;

    private String requestHost;
    private String requestQuery;
}
