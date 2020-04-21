/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olaleyeone.auth.dto.data;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordResetApiRequest {

    @NotBlank
    private String identifier;

    @NotBlank
    private UserIdentifierType identifierType;

    @NotBlank
    private String password;

    @NotBlank
    private String resetToken;
}
