package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortalUserIdentifierRepository extends JpaRepository<PortalUserIdentifier, Long> {

    @Query("SELECT i FROM PortalUserIdentifier i JOIN FETCH i.portalUser WHERE lower(i.identifier)=lower(?1)")
    Optional<PortalUserIdentifier> findActiveByIdentifier(String identifier);

    List<PortalUserIdentifier> findByPortalUser(PortalUser portalUser);
}
