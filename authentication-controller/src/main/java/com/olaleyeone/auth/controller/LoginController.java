//package com.olaleyeone.auth.controller;
//
//import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
//import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
//import com.olaleyeone.auth.dto.data.LoginApiRequest;
//import com.olaleyeone.auth.dto.data.RequestMetadata;
//import com.olaleyeone.auth.exception.ErrorResponse;
//import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
//import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
//import com.olaleyeone.auth.security.annotations.Public;
//import com.olaleyeone.auth.service.LoginAuthenticationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.inject.Provider;
//import javax.validation.Valid;
//
//@RequiredArgsConstructor
//@RestController
//public class LoginController {
//
//    private final LoginAuthenticationService authenticationService;
//    private final Provider<RequestMetadata> requestMetadata;
//    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
//
//    @Public
//    @PostMapping("/login")
//    public ResponseEntity<AccessTokenApiResponse> login(@Valid @RequestBody LoginApiRequest dto) {
//        PortalUserAuthentication portalUserAuthentication = authenticationService.getAuthenticationResponse(dto, requestMetadata.get());
//        if (portalUserAuthentication.getResponseType() != AuthenticationResponseType.SUCCESSFUL) {
//            throw new ErrorResponse(HttpStatus.UNAUTHORIZED);
//        }
//        return accessTokenApiResponseHandler.getAccessToken(portalUserAuthentication);
//    }
//}
