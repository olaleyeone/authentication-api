package com.olaleyeone.auth.security.data;

import lombok.Data;

@Data
class RequestMetadataImpl implements RequestMetadata {

    private String ipAddress;
    private String userAgent;
    private String accessToken;
    private boolean localhost;
    private String userId;
}
