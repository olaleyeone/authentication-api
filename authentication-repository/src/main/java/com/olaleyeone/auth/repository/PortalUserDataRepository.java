package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserDataRepository extends JpaRepository<PortalUserData, Long> {
}
