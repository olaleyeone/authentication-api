package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserIdentifierVerificationRepository extends JpaRepository<PortalUserIdentifierVerification, Long> {
}
