package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.data.AuthorizedRequest;
import com.github.olaleyeone.rest.exception.NotFoundException;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;

@RequiredArgsConstructor
@RestController
public class AuthenticationUserDetailsController {

    private final UserApiResponseHandler responseHandler;
    private final Provider<AuthorizedRequest> authorizedRequestProvider;
    private final PortalUserRepository portalUserRepository;

    @GetMapping("/me")
    public UserApiResponse getAccountDetails() {
        Long userId = Long.valueOf(authorizedRequestProvider.get().getAccessClaims().getSubject());
        PortalUser portalUser = portalUserRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        return responseHandler.toUserApiResponse(portalUser);
    }
}
