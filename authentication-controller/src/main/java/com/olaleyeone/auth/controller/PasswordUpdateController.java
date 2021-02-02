package com.olaleyeone.auth.controller;

import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.github.olaleyeone.rest.exception.NotFoundException;
import com.olaleyeone.auth.data.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.security.constraint.NotClientToken;
import com.olaleyeone.auth.service.PasswordUpdateService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class PasswordUpdateController {

    private final PasswordUpdateService passwordUpdateService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Provider<RequestMetadata> requestMetadataProvider;
    private final UserApiResponseHandler responseHandler;
    private final HashService hashService;

    @NotClientToken
    @PutMapping("/password")
    public UserApiResponse changePassword(@RequestBody @Valid PasswordUpdateApiRequest apiRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findById(requestMetadataProvider.get().getRefreshTokenId())
                .orElseThrow(NotFoundException::new);

        String password = refreshToken.getPortalUser().getPassword();

        if (StringUtils.isNotBlank(apiRequest.getCurrentPassword()) && (
                password == null ||
                        !hashService.isSameHash(apiRequest.getCurrentPassword(), password))) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN,
                    new ApiResponse<>(
                            null,
                            "Incorrect password",
                            null));
        }

        if (password != null && hashService.isSameHash(apiRequest.getPassword(), password)) {
            throw new ErrorResponse(HttpStatus.CONFLICT,
                    new ApiResponse<>(
                            null,
                            "Reuse of current password not allowed",
                            null));
        }
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        return responseHandler.toUserApiResponse(refreshToken.getPortalUser());
    }
}
