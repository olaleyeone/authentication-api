package com.olaleyeone.auth.entitytest.data;

import com.github.heywhy.springentityfactory.FactoryConfiguration;
import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class TestDataFactoryConfiguration extends FactoryConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ModelFactory entityFactory(Faker faker, EntityManager entityManager) {
        ModelFactory modelFactory = super.entityFactory(faker, this.entityManager);
        register(modelFactory);
        return modelFactory;
    }

    @SneakyThrows
    public void register(ModelFactory modelFactory) {
        String className = getClass().getName();
        String packageName = className.substring(0, className.length() - (getClass().getSimpleName().length() + 1));
        ClassPath.from(this.getClass().getClassLoader()).getTopLevelClassesRecursive(packageName)
                .stream()
                .map(classInfo -> classInfo.load())
                .filter(javaClass -> FactoryHelper.class.isAssignableFrom(javaClass))
                .forEach(aClass -> modelFactory.register((Class) aClass));
    }
}