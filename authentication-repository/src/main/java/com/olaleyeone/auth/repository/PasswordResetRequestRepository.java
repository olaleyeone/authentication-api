package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {

    @Query("SELECT v FROM PasswordResetRequest v" +
            " WHERE v.portalUserIdentifier.portalUser=?1" +
            " AND v.usedAt IS NULL" +
            " AND v.deactivatedAt IS NULL" +
            " AND v.expiresAt>=CURRENT_TIMESTAMP")
    List<PasswordResetRequest> getAllActive(PortalUser portalUser);

    @Query("SELECT p FROM PasswordResetRequest p" +
            " JOIN FETCH p.portalUser" +
            " JOIN FETCH p.portalUserIdentifier" +
            " WHERE p.id=?1")
    Optional<PasswordResetRequest> findById(Long id);

    @Query("SELECT p FROM PasswordResetRequest p" +
            " JOIN FETCH p.portalUser" +
            " JOIN FETCH p.portalUserIdentifier" +
            " WHERE p.portalUserIdentifier=?1 AND p.resetCode=?2")
    Optional<PasswordResetRequest> findByIdentifierAndCode(PortalUserIdentifier userIdentifier, String code);
}
