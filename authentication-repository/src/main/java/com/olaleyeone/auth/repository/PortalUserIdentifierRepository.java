package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PortalUserIdentifierRepository extends JpaRepository<PortalUserIdentifier, Long> {

    @Query("SELECT i FROM PortalUserIdentifier i JOIN FETCH i.portalUser WHERE lower(i.identifier)=lower(?1)")
    Optional<PortalUserIdentifier> findByIdentifier(String identifier);
}
