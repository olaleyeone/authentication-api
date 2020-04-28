package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.exception.NotFoundException;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class UserApiResponseHandler {

    private final PortalUserRepository portalUserRepository;

    public UserApiResponse getUserApiResponse(Long userId) {
        Optional<PortalUser> optionalPortalUser = portalUserRepository.findById(userId);
        if (!optionalPortalUser.isPresent()) {
            throw new NotFoundException();
        }
        PortalUser portalUser = optionalPortalUser.get();

        return new UserApiResponse(portalUser);
    }
}
