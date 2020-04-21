package com.olaleyeone.auth.controllertest;

import com.olaleyeone.auth.search.handler.PortalUserAuthenticationSearchHandler;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SearchHandlerMockConfig {

    @Bean
    public FactoryBean<PortalUserAuthenticationSearchHandler> portalUserAuthenticationSearchHandler() {
        return preventAutowire(Mockito.mock(PortalUserAuthenticationSearchHandler.class));
    }

    public static <T> FactoryBean<T> preventAutowire(T bean) {
        return new FactoryBean<T>() {
            public T getObject() throws Exception {
                return bean;
            }

            public Class<?> getObjectType() {
                return bean.getClass();
            }

            public boolean isSingleton() {
                return true;
            }
        };
    }
}
