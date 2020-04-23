package com.olaleyeone.audittrail.repository;

import com.olaleyeone.audittrail.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    List<RequestLog> getAllBySessionId(String sessionId);
}
