package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.PasswordUpdateApiRequest;
import com.olaleyeone.data.dto.RequestMetadata;
import com.github.olaleyeone.rest.exception.NotFoundException;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.security.constraint.NotClientToken;
import com.olaleyeone.auth.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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

    @NotClientToken
    @PostMapping("/password")
    public void changePassword(@RequestBody @Valid PasswordUpdateApiRequest apiRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findById(requestMetadataProvider.get().getRefreshTokenId())
                .orElseThrow(NotFoundException::new);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
    }
}
