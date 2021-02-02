package com.olaleyeone.auth.data.dto;

import java.util.List;

public interface LoginApiRequest {

    String getIdentifier();

    String getPassword();

    Boolean getInvalidateOtherSessions();

    String getFirebaseToken();

    List<UserDataApiRequest> getData();

    Integer getRefreshTokenDurationInSeconds();
}
