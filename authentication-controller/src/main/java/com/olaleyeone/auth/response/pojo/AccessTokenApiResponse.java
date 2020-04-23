package com.olaleyeone.auth.response.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccessTokenApiResponse {

    private Long id;
    private String firstName;
    private String lastName;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private final String tokenType = "Bearer";
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long secondsTillExpiry;

    public AccessTokenApiResponse(PortalUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
