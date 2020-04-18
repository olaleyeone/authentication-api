package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPojo {

    private Long id;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String refreshToken;

    public UserPojo(PortalUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
