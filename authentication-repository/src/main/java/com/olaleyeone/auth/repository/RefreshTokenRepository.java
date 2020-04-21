package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT t FROM RefreshToken t WHERE t.timeDeactivated IS NULL AND t.actualAuthentication=?1 AND t.expiresAt>?2")
    List<RefreshToken> findActiveTokens(PortalUserAuthentication authenticationResponse, LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.timeDeactivated=CURRENT_TIMESTAMP" +
            " WHERE r.timeDeactivated IS NULL" +
            " AND r.expiresAt>CURRENT_TIMESTAMP" +
            " AND r.portalUser = (" +
            "SELECT r2.portalUser FROM RefreshToken r2 WHERE r2=?1" +
            ") AND r<>?1")
    int deactivateOtherSessions(RefreshToken refreshToken);
}
