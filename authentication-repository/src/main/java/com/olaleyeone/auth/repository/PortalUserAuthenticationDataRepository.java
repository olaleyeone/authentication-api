package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthenticationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortalUserAuthenticationDataRepository extends JpaRepository<PortalUserAuthenticationData, Long> {

    List<PortalUserAuthenticationData> findByPortalUserAuthentication(PortalUserAuthentication portalUser);
}
