package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserIdentifierApiResponse {

    private Long id;
    private UserIdentifierType type;
    private String identifier;
    private Boolean verified;

    public UserIdentifierApiResponse(PortalUserIdentifier userIdentifier) {
        this.id = userIdentifier.getId();
        this.type = userIdentifier.getIdentifierType();
        this.identifier = userIdentifier.getIdentifier();
        this.verified = userIdentifier.getVerified();
    }
}
