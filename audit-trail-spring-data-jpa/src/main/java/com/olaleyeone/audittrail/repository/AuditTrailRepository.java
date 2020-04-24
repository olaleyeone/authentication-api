package com.olaleyeone.audittrail.repository;

import com.olaleyeone.audittrail.entity.AuditTrail;
import com.olaleyeone.audittrail.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    List<AuditTrail> getAllByRequest(RequestLog request);
}
