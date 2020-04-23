package com.olaleyeone.audittrail.repository;

import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitOfWorkRepository extends JpaRepository<UnitOfWork, Long> {

    List<UnitOfWork> getAllByRequest(RequestLog request);
}
