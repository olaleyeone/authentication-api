package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureKeyRepository extends JpaRepository<SignatureKey, Long> {

    Optional<SignatureKey> findByKeyIdAndType(String keyId, JwtTokenType jwtTokenType);
}
