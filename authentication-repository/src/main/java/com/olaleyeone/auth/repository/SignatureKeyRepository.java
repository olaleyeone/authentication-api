package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.SignatureKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureKeyRepository extends JpaRepository<SignatureKey, Long> {

    Optional<SignatureKey> findByKeyId(String keyId);
}
