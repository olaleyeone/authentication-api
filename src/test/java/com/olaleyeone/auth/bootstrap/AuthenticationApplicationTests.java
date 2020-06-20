package com.olaleyeone.auth.bootstrap;

import com.olaleyeone.auth.messaging.listeners.UserPublisherJob;
import com.olaleyeone.auth.messaging.producers.UserPublisher;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        topics = {"${user.topic.name}", "${task.publish_users.topic.name}"})
@Disabled
class AuthenticationApplicationTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public ApplicationContext applicationContext;

    @Test
    void contextLoads() throws IOException {

        applicationContext.getBean(UserPublisher.class);
        applicationContext.getBean(UserPublisherJob.class);

        Map<String, String> settings = new HashMap<>();
        settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
        settings.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");

        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build());

        entityManagerFactory.getMetamodel().getEntities().forEach(entityType -> metadata.addAnnotatedClass(entityType.getJavaType()));
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        Path output = Paths.get("build", "schema.sql");
        Files.deleteIfExists(output);
        schemaExport.setOutputFile(output.toString());
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata.buildMetadata(), metadata.getServiceRegistry());
    }

}
