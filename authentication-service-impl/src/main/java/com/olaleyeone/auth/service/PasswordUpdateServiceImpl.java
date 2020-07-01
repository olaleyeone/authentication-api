package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.PasswordResetRequestRepository;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.PortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Named
public class PasswordUpdateServiceImpl implements PasswordUpdateService {

    private final Provider<TaskContext> taskContextProvider;
    private final HashService hashService;
    private final PortalUserRepository portalUserRepository;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final ImplicitAuthenticationService implicitAuthenticationService;

    @Activity("PASSWORD UPDATE")
    @Transactional
    @Override
    public void updatePassword(RefreshToken refreshToken, PasswordUpdateApiRequest passwordUpdateApiRequest) {
        taskContextProvider.get().setDescription(
                String.format("Updating password for logged in user %s", refreshToken.getPortalUser().getId()));
        PortalUser portalUser = refreshToken.getPortalUser();
        portalUser.setPassword(hashService.generateHash(passwordUpdateApiRequest.getPassword()));
        portalUser.setPasswordUpdateRequired(false);
        portalUserRepository.save(portalUser);
        if (BooleanUtils.isTrue(passwordUpdateApiRequest.getInvalidateOtherSessions())) {
            portalUserAuthenticationRepository.deactivateOtherSessions(refreshToken.getActualAuthentication());
        }
    }

    @Activity("PASSWORD RESET")
    @Transactional
    @Override
    public PortalUserAuthentication updatePassword(PasswordResetRequest passwordResetRequest, PasswordUpdateApiRequest passwordUpdateApiRequest) {
        PortalUser portalUser = passwordResetRequest.getPortalUser();
        taskContextProvider.get().setDescription(
                String.format("Password reset by user %s", portalUser.getId()));

        portalUser.setPassword(hashService.generateHash(passwordUpdateApiRequest.getPassword()));
        portalUserRepository.save(portalUser);

        passwordResetRequest.setUsedOn(LocalDateTime.now());
        passwordResetRequestRepository.save(passwordResetRequest);

        if (BooleanUtils.isTrue(passwordUpdateApiRequest.getInvalidateOtherSessions())) {
            portalUserAuthenticationRepository.deactivateOtherSessions(portalUser);
        }
        return implicitAuthenticationService.createPasswordResetAuthentication(passwordResetRequest);
    }
}
