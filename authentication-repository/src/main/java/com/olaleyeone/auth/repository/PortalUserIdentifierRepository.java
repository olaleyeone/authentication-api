package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortalUserIdentifierRepository extends JpaRepository<PortalUserIdentifier, Long> {

    @Query("SELECT i FROM PortalUserIdentifier i JOIN FETCH i.portalUser WHERE lower(i.identifier)=lower(?1)")
    Optional<PortalUserIdentifier> findActiveByIdentifier(String identifier);

    @Query("SELECT i FROM PortalUserIdentifier i JOIN FETCH i.portalUser" +
            " WHERE lower(i.identifier)=lower(?1) AND i.identifierType=?2")
    Optional<PortalUserIdentifier> findActiveByIdentifier(String identifier, UserIdentifierType type);

    List<PortalUserIdentifier> findByPortalUser(PortalUser portalUser);

    @Query("SELECT i FROM PortalUserIdentifier i JOIN FETCH i.portalUser WHERE i.portalUser=?1 AND i.identifierType=?2")
    List<PortalUserIdentifier> findByPortalUserAndType(PortalUser portalUser, UserIdentifierType type);
}
