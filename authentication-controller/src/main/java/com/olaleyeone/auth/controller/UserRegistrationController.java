package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.integration.events.NewUserEvent;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.UserRegistrationService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserRegistrationController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRegistrationService userRegistrationService;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    private final Provider<RequestMetadata> requestMetadata;
    private final ApplicationContext applicationContext;

    @Public
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public HttpEntity<AccessTokenApiResponse> registerUser(@Valid @RequestBody UserRegistrationApiRequest dto) {
        PortalUserAuthentication portalUserAuthentication = userRegistrationService.registerUser(dto, requestMetadata.get());
        PortalUser portalUser = portalUserAuthentication.getPortalUser();
        applicationContext.publishEvent(NewUserEvent.builder()
                .portalUser(portalUser)
                .build());
        if (StringUtils.isBlank(dto.getPassword())) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AccessTokenApiResponse(portalUser));
        }
        HttpEntity<AccessTokenApiResponse> httpEntity = accessTokenApiResponseHandler.getAccessToken(portalUserAuthentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpEntity.getHeaders())
                .body(httpEntity.getBody());
    }
}
