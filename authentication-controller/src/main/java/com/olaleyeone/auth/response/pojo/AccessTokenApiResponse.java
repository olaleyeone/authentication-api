package com.olaleyeone.auth.response.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class AccessTokenApiResponse {

    private Long id;
    private String displayName;
    private String firstName;
    private String lastName;

    private Boolean passwordUpdateRequired;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private static final String tokenType = "Bearer";
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long secondsTillExpiry;

    @JsonProperty("expires_at")
    private OffsetDateTime expiresAt;

    public AccessTokenApiResponse(PortalUser user) {
        this.id = user.getId();
        this.displayName = user.getDisplayName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.passwordUpdateRequired = user.getPasswordUpdateRequired();
    }
}
