package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.PasswordUpdateApiRequest;

public interface PasswordUpdateService {

    void updatePassword(RefreshToken portalUser, PasswordUpdateApiRequest passwordUpdateApiRequest);
}
