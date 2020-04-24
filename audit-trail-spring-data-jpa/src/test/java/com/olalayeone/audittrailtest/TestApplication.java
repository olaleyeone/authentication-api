package com.olalayeone.audittrailtest;

import com.olaleyeone.audittrail.advice.AuditTrailAdvice;
import com.olaleyeone.audittrail.impl.UnitOfWorkLogger;
import com.olaleyeone.audittrail.impl.UnitOfWorkLoggerFactory;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

@SpringBootApplication
@EnableJpaRepositories({"com.olaleyeone.audittrail.repository"})
@EntityScan({"com.olaleyeone.audittrail.entity", "com.olalayeone.audittrailtest.data.entity"})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public AuditTrailAdvice auditTrailAdvice() {
        return Mockito.mock(AuditTrailAdvice.class);
    }

    @Bean
    public UnitOfWorkLoggerFactory auditTrailLoggerFactory() {
        return new UnitOfWorkLoggerFactory() {

            @Override
            public UnitOfWorkLogger createLogger(EntityManager entityManager, TransactionTemplate transactionTemplate) {
                return Mockito.mock(UnitOfWorkLogger.class);
            }
        };
    }
}
