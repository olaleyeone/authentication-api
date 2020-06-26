package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {

    @Query("SELECT v FROM PasswordResetRequest v" +
            " WHERE v.portalUserIdentifier.portalUser=?1" +
            " AND v.usedOn IS NULL" +
            " AND v.deactivatedOn IS NULL" +
            " AND v.expiresOn>=CURRENT_TIMESTAMP")
    List<PasswordResetRequest> getAllActive(PortalUser portalUser);
}
