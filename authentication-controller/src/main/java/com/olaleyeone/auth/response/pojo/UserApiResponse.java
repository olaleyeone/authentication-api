package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.PortalUser;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserApiResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String refreshToken;

    public UserApiResponse(PortalUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
