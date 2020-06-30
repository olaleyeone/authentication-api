package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserApiResponse {

    private Long id;
    private String displayName;
    private String firstName;
    private String lastName;
    private Boolean passwordUpdateRequired;

    private List<UserIdentifierApiResponse> identifiers;

    private List<UserDataApiResponse> data;

    public UserApiResponse(PortalUser portalUser) {
        this.id = portalUser.getId();
        this.displayName = portalUser.getDisplayName();
        this.firstName = portalUser.getFirstName();
        this.lastName = portalUser.getLastName();
        this.passwordUpdateRequired = portalUser.getPasswordUpdateRequired();
    }
}
