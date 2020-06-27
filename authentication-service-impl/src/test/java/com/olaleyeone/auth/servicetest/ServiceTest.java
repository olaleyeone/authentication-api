package com.olaleyeone.auth.servicetest;

import com.olaleyeone.audittrail.embeddable.Duration;
import com.olaleyeone.audittrail.entity.Task;
import com.olaleyeone.audittrail.impl.TaskContextHolder;
import com.olaleyeone.audittrail.impl.TaskContextImpl;
import com.olaleyeone.audittrail.impl.TaskTransactionContextFactory;
import com.olaleyeone.auth.entitytest.EntityTest;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.integration.security.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.internal.creation.bytebuddy.MockAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;

@Import({ServiceTest.$Config.class})
public class ServiceTest extends EntityTest {

    @Autowired
    private TaskContextHolder taskContextHolder;

    @Autowired
    private TaskTransactionContextFactory taskTransactionContextFactory;

    @BeforeEach
    public void resetMocks() {
        applicationContext.getBeansOfType(MockAccess.class)
                .values().forEach(Mockito::reset);
        Task task = new Task();
        task.setDuration(new Duration(LocalDateTime.now(), null));
        task.setName(faker.funnyName().name());
        task.setType(faker.app().name());
        taskContextHolder.registerContext(new TaskContextImpl(task, null, taskContextHolder, taskTransactionContextFactory));
    }

    @Configuration
    @ComponentScan("com.olaleyeone.auth.service")
    @EnableJpaRepositories("com.olaleyeone.audittrail.repository")
    @EntityScan("com.olaleyeone.audittrail.entity")
    @Import(TestAuditTrailConfiguration.class)
    static class $Config {

        @Bean
        public HashService passwordService() {
            return Mockito.mock(HashService.class);
        }

        @Bean
        public PhoneNumberService phoneNumberService() {
            return Mockito.mock(PhoneNumberService.class);
        }

    }
}
