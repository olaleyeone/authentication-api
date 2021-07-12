package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class UserSessionApiResponse {

    private String userId;
    private String sessionId;
    private String identifier;

    private OffsetDateTime startedAt;
    private OffsetDateTime lastUpdatedAt;
    private OffsetDateTime lastActiveAt;
    private OffsetDateTime deactivatedAt;

    private AuthenticationType authenticationType;

    private String userAgent;
    private String firebaseToken;

    private List<UserDataApiResponse> data;
//    private List<UserDataApiResponse> userData;

    public UserSessionApiResponse(PortalUserAuthentication portalUserAuthentication) {
        this.authenticationType = portalUserAuthentication.getType();

        this.userId = portalUserAuthentication.getPortalUser().getId().toString();
        this.sessionId = portalUserAuthentication.getId().toString();
        this.identifier = portalUserAuthentication.getIdentifier();

        this.userAgent = portalUserAuthentication.getUserAgent();

        this.startedAt = portalUserAuthentication.getDateCreated();
        this.lastActiveAt = portalUserAuthentication.getLastActiveAt();
        this.lastUpdatedAt = portalUserAuthentication.getLastUpdatedAt();
        this.deactivatedAt = Optional.ofNullable(portalUserAuthentication.getDeactivatedAt())
                .orElse(portalUserAuthentication.getLoggedOutAt());

        this.firebaseToken = portalUserAuthentication.getFirebaseToken();
    }
}
