package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.data.PasswordUpdateApiRequest;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Named;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Named
public class PasswordUpdateServiceImpl implements PasswordUpdateService {

    private final PasswordService passwordService;
    private final PortalUserRepository portalUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void updatePassword(RefreshToken refreshToken, PasswordUpdateApiRequest passwordUpdateApiRequest) {
        PortalUser portalUser = refreshToken.getPortalUser();
        portalUser.setPassword(passwordService.hashPassword(passwordUpdateApiRequest.getPassword()));
        portalUserRepository.save(portalUser);
        if (BooleanUtils.isTrue(passwordUpdateApiRequest.getInvalidateOtherSessions())) {
            refreshTokenRepository.deactivateOtherSessions(refreshToken);
        }
    }
}
