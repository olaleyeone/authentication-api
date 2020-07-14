package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PortalUserAuthenticationRepository extends JpaRepository<PortalUserAuthentication, Long> {

    @Query("SELECT t FROM PortalUserAuthentication t" +
            " WHERE t.responseType='SUCCESSFUL'" +
            " AND t.deactivatedAt IS NULL" +
            " AND t.loggedOutAt IS NULL" +
            " AND t.portalUser=?1" +
            " AND t.autoLogoutAt>CURRENT_TIMESTAMP")
    List<PortalUserAuthentication> findActiveSessions(PortalUser portalUser);

    @Query("SELECT MAX(t.becomesInactiveAt) FROM PortalUserAuthentication t" +
            " WHERE t.responseType='SUCCESSFUL'" +
            " AND t.portalUser=?1")
    Optional<OffsetDateTime> getLastActive(PortalUser portalUser);

    @Modifying
    @Query("UPDATE PortalUserAuthentication auth SET auth.deactivatedAt=CURRENT_TIMESTAMP" +
            " WHERE auth.responseType='SUCCESSFUL'" +
            " AND auth.deactivatedAt IS NULL" +
            " AND auth.loggedOutAt IS NULL" +
            " AND (auth.autoLogoutAt IS NULL OR auth.autoLogoutAt>CURRENT_TIMESTAMP)" +
            " AND auth.portalUser = (" +
            "SELECT i.portalUser FROM PortalUserAuthentication i WHERE i=?1" +
            ") AND auth<>?1")
    int deactivateOtherSessions(PortalUserAuthentication portalUserAuthentication);

    @Modifying
    @Query("UPDATE PortalUserAuthentication auth SET auth.deactivatedAt=CURRENT_TIMESTAMP" +
            " WHERE auth.responseType='SUCCESSFUL'" +
            " AND auth.deactivatedAt IS NULL" +
            " AND auth.loggedOutAt IS NULL" +
            " AND (auth.autoLogoutAt IS NULL OR auth.autoLogoutAt>CURRENT_TIMESTAMP)" +
            " AND auth.portalUser = ?1")
    int deactivateOtherSessions(PortalUser portalUser);
}
