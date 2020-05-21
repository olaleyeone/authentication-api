package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class UserApiResponse {

    private Long id;
    private String firstName;
    private String lastName;

    private List<UserIdentifierApiResponse> identifiers;

    private List<Map.Entry<String, String>> data;

    public UserApiResponse(PortalUser portalUser) {
        this.id = portalUser.getId();
        this.firstName = portalUser.getFirstName();
        this.lastName = portalUser.getLastName();
    }
}
