package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserApiResponse {

    private Long id;
    private String firstName;
    private String lastName;

    public UserApiResponse(PortalUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
