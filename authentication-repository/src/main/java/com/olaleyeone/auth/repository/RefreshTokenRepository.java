package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT t FROM RefreshToken t JOIN FETCH t.actualAuthentication auth" +
            " WHERE t.id=?1" +
            " AND t.timeDeactivated IS NULL" +
            " AND t.expiresAt>CURRENT_TIMESTAMP" +
            " AND auth.loggedOutAt IS NULL" +
            " AND auth.deactivatedAt IS NULL" +
            " AND auth.autoLogoutAt>CURRENT_TIMESTAMP")
    Optional<RefreshToken> findActiveToken(Long id);

    @Query("SELECT t FROM RefreshToken t WHERE t.timeDeactivated IS NULL AND t.actualAuthentication=?1 AND t.expiresAt>CURRENT_TIMESTAMP")
    List<RefreshToken> findActiveTokens(PortalUserAuthentication authenticationResponse);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.timeDeactivated=CURRENT_TIMESTAMP" +
            " WHERE r.timeDeactivated IS NULL" +
            " AND r.expiresAt>CURRENT_TIMESTAMP" +
            " AND r.portalUser = (" +
            "SELECT r2.portalUser FROM RefreshToken r2 WHERE r2=?1" +
            ") AND r<>?1")
    int deactivateOtherSessions(RefreshToken refreshToken);
}
