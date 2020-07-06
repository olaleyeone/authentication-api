package com.olaleyeone.auth.data.dto;

import com.olaleyeone.auth.constraints.HasPassword;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateApiRequest {

    @HasPassword
    private String currentPassword;

    @NotBlank
    private String password;

    private Boolean invalidateOtherSessions;
}
