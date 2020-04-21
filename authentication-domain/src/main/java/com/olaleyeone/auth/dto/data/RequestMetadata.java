package com.olaleyeone.auth.dto.data;

import lombok.Data;

@Data
public class RequestMetadata {

    private Long refreshTokenId;
    private String ipAddress;
    private String userAgent;
}
