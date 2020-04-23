package com.olaleyeone.data;

import lombok.Data;

@Data
public class RequestMetadata {

    private Long portalUserId;
    private Long refreshTokenId;
    private String ipAddress;
    private String userAgent;
}
