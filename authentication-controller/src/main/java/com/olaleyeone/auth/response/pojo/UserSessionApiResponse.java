package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserSessionApiResponse {

    private String userId;
    private String sessionId;
    private OffsetDateTime startedOn;
    private OffsetDateTime lastActiveOn;
    private OffsetDateTime deactivatedOn;

    private AuthenticationType authenticationType;

    private String userAgent;

    private List<UserDataApiResponse> data;
    private List<UserDataApiResponse> userData;

    public UserSessionApiResponse(PortalUserAuthentication portalUserAuthentication) {
        this.userId = portalUserAuthentication.getPortalUser().getId().toString();
        this.sessionId = portalUserAuthentication.getId().toString();
        this.userAgent = portalUserAuthentication.getUserAgent();
        this.startedOn = portalUserAuthentication.getDateCreated();
        this.lastActiveOn = portalUserAuthentication.getLastActiveAt();
        this.authenticationType = portalUserAuthentication.getType();
    }
}
