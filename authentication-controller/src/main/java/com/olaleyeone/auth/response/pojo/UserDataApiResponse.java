package com.olaleyeone.auth.response.pojo;

import lombok.Data;

@Data
public class UserDataApiResponse {

    private String key;
    private String value;

    public UserDataApiResponse(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
