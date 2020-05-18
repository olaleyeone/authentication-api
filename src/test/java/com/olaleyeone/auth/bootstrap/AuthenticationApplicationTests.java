package com.olaleyeone.auth.bootstrap;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class AuthenticationApplicationTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void contextLoads() throws IOException {
        //noop
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
