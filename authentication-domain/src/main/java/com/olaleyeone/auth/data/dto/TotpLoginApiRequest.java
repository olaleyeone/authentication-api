package com.olaleyeone.auth.data.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class TotpLoginApiRequest implements LoginApiRequest {

//    @NotBlank
    @Pattern(regexp = "\\d+")
    private String transactionId;

    @NotBlank
    private String identifier;
    @NotBlank
    private String password;

    private Boolean invalidateOtherSessions;

    private String firebaseToken;

    private List<@NotNull @Valid UserDataApiRequest> data;

    private Integer refreshTokenDurationInSeconds;
}
