package com.olaleyeone.auth.data.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class LoginApiRequest {

    @NotBlank
    private String identifier;
    @NotBlank
    private String password;

    private Boolean invalidateOtherSessions;

    private List<UserDataApiRequest> data;
}
