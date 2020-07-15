package com.olaleyeone.auth.data.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LoginApiRequest {

    @NotBlank
    private String identifier;
    @NotBlank
    private String password;

    private Boolean invalidateOtherSessions;

    private List<@NotNull @Valid UserDataApiRequest> data;
}
