package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {

    @Query("SELECT v FROM OneTimePassword v" +
            " WHERE v.userIdentifier=?1" +
            " AND v.usedOn IS NULL" +
            " AND v.deactivatedOn IS NULL" +
            " AND v.expiresOn>=CURRENT_TIMESTAMP")
    List<OneTimePassword> getAllActive(PortalUserIdentifier identifier);
}
