package com.olaleyeone.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateApiRequest {

    @NotBlank
    private String password;

    private Boolean invalidateOtherSessions;
}
