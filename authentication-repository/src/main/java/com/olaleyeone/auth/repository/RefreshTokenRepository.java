package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT t FROM RefreshToken t WHERE t.timeDeactivated IS NULL AND t.actualAuthentication=?1 AND t.expiresAt>?2")
    List<RefreshToken> findActiveTokens(AuthenticationResponse authenticationResponse, LocalDateTime now);
}
