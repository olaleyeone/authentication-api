package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortalUserDataRepository extends JpaRepository<PortalUserData, Long> {

    List<PortalUserData> findByPortalUser(PortalUser portalUser);
}
