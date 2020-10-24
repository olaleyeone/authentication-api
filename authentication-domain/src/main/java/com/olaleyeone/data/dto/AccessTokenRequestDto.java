package com.olaleyeone.data.dto;

import java.util.Optional;

public interface AccessTokenRequestDto {

    String getRefreshToken();

    Optional<String> getFirebaseToken();
}
