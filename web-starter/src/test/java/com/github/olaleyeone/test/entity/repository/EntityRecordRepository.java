package com.github.olaleyeone.test.entity.repository;

import com.github.olaleyeone.test.entity.data.EntityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRecordRepository extends JpaRepository<EntityRecord, Long> {
}
