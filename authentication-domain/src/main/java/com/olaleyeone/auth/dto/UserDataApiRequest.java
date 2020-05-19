package com.olaleyeone.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDataApiRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String value;
}
