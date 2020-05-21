package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.response.pojo.UserIdentifierApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserApiResponseHandler {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PortalUserDataRepository portalUserDataRepository;

    public UserApiResponse toUserApiResponse(PortalUser portalUser) {
        UserApiResponse userApiResponse = new UserApiResponse(portalUser);
        userApiResponse.setIdentifiers(portalUserIdentifierRepository.findByPortalUser(portalUser)
                .stream().map(UserIdentifierApiResponse::new)
                .collect(Collectors.toList()));

        userApiResponse.setData(portalUserDataRepository.findByPortalUser(portalUser)
                .stream()
                .map(portalUserData -> Pair.of(portalUserData.getName(), portalUserData.getValue()))
                .collect(Collectors.toList()));

        return userApiResponse;
    }
}
