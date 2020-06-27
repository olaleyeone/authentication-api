package com.olaleyeone.data.dto;

import lombok.Data;

@Data
public class RequestMetadata {

    private String host;
    private Long portalUserId;
    private Long refreshTokenId;
    private String ipAddress;
    private String userAgent;
}
