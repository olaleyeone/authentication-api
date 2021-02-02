/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olaleyeone.auth.data.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordResetApiRequest {

    @NotBlank
    private String password;

    private Boolean invalidateOtherSessions;

//    @NotBlank
//    private String identifier;
//
//    @NotBlank
//    private String resetToken;

    private Integer refreshTokenDurationInSeconds;
}
