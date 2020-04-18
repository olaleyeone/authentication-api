package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationResponseRepository extends JpaRepository<AuthenticationResponse, Long> {
}
