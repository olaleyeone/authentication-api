package com.olaleyeone.auth.dto;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class PortalUserIdentifierVerificationRequestMessage {

    private UserIdentifierType identifierType;

    private String identifier;

    private String verificationCode;

    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;

    private String requestHost;
    private String requestQuery;
}
