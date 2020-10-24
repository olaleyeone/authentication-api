package com.olaleyeone.auth.data.dto;

public interface LoginApiRequest {

    String getIdentifier();

    String getPassword();

    Boolean getInvalidateOtherSessions();

    String getFirebaseToken();

    java.util.List<UserDataApiRequest> getData();
}
