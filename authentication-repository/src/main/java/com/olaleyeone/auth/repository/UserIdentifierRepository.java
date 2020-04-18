package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.UserIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserIdentifierRepository extends JpaRepository<UserIdentifier, Long> {

    @Query("SELECT i FROM UserIdentifier i JOIN FETCH i.user WHERE lower(i.identifier)=lower(?1)")
    Optional<UserIdentifier> findByIdentifier(String identifier);
}
