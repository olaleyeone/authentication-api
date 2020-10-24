package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {

    @Query("SELECT v FROM OneTimePassword v" +
            " WHERE v.userIdentifier=?1" +
            " AND v.usedAt IS NULL" +
            " AND v.deactivatedAt IS NULL" +
            " AND v.expiresAt>=CURRENT_TIMESTAMP")
    List<OneTimePassword> getAllActive(PortalUserIdentifier identifier);

    @Query("SELECT v FROM OneTimePassword v" +
            " WHERE v.userIdentifier=?1" +
            " AND v.password=?2" +
            " AND v.usedAt IS NULL" +
            " AND v.deactivatedAt IS NULL" +
            " AND v.expiresAt>=CURRENT_TIMESTAMP")
    Optional<OneTimePassword> getActive(PortalUserIdentifier identifier, String password);
}
