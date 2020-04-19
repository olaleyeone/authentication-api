package com.olaleyeone.auth.dto.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequestDto {

    @NotBlank
    private String identifier;
    @NotBlank
    private String password;
}
