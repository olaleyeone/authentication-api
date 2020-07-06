package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.response.pojo.UserDataApiResponse;
import com.olaleyeone.auth.response.pojo.UserIdentifierApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserApiResponseHandler {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PortalUserDataRepository portalUserDataRepository;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    public UserApiResponse toUserApiResponse(PortalUser portalUser) {
        UserApiResponse userApiResponse = new UserApiResponse(portalUser);

        List<PortalUserIdentifier> userIdentifiers = portalUserIdentifierRepository.findByPortalUser(portalUser);
        userApiResponse.setIdentifiers(userIdentifiers
                .stream().map(UserIdentifierApiResponse::new)
                .collect(Collectors.toList()));
        userApiResponse.setEmailAddresses(getIdentifiers(userIdentifiers, UserIdentifierType.EMAIL));
        userApiResponse.setPhoneNumbers(getIdentifiers(userIdentifiers, UserIdentifierType.PHONE_NUMBER));

        userApiResponse.setData(portalUserDataRepository.findByPortalUser(portalUser)
                .stream()
                .map(portalUserData -> new UserDataApiResponse(portalUserData.getName(), portalUserData.getValue()))
                .collect(Collectors.toList()));

        portalUserAuthenticationRepository.getLastActive(portalUser)
                .ifPresent(userApiResponse::setLastActiveOn);

        return userApiResponse;
    }

    public static Set<String> getIdentifiers(List<PortalUserIdentifier> userIdentifiers, UserIdentifierType phoneNumber) {
        return userIdentifiers
                .stream()
                .filter(portalUserIdentifier -> portalUserIdentifier.getIdentifierType() == phoneNumber)
                .map(PortalUserIdentifier::getIdentifier)
                .collect(Collectors.toSet());
    }
}
