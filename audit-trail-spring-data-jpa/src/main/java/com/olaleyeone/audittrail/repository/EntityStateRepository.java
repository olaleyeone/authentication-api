package com.olaleyeone.audittrail.repository;

import com.olaleyeone.audittrail.entity.EntityState;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EntityStateRepository extends JpaRepository<EntityState, Long> {

    @Query("SELECT h FROM EntityState h WHERE h.unitOfWork=?1 AND h.entityName=?2 AND h.entityId=?3")
    Optional<EntityState> getByUnitOfWork(UnitOfWork unitOfWork, String name, String id);
}
