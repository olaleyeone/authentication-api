package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {
}
