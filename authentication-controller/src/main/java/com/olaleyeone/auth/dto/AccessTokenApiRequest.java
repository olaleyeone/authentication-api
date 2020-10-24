package com.olaleyeone.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.olaleyeone.data.dto.AccessTokenRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenApiRequest implements AccessTokenRequestDto {

    @JsonProperty("refresh_token")
    private String refreshToken;

    @Valid
    private Optional<@NotBlank String> firebaseToken;
}
