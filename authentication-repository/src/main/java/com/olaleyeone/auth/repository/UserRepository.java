package com.olaleyeone.auth.repository;

import com.olaleyeone.auth.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
