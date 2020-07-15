package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.repository.PortalUserAuthenticationDataRepository;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.response.pojo.UserSessionApiResponse;
import com.olaleyeone.auth.response.pojo.UserDataApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserSessionApiResponseHandler {

    private final PortalUserDataRepository portalUserDataRepository;
    private final PortalUserAuthenticationDataRepository portalUserAuthenticationDataRepository;

    public UserSessionApiResponse toApiResponse(PortalUserAuthentication userAuthentication) {
        UserSessionApiResponse userApiResponse = new UserSessionApiResponse(userAuthentication);

        userApiResponse.setData(portalUserAuthenticationDataRepository.findByPortalUserAuthentication(userAuthentication)
                .stream()
                .map(portalUserData -> new UserDataApiResponse(portalUserData.getName(), portalUserData.getValue()))
                .collect(Collectors.toList()));

        userApiResponse.setUserData(portalUserDataRepository.findByPortalUser(userAuthentication.getPortalUser())
                .stream()
                .map(portalUserData -> new UserDataApiResponse(portalUserData.getName(), portalUserData.getValue()))
                .collect(Collectors.toList()));

        return userApiResponse;
    }
}
