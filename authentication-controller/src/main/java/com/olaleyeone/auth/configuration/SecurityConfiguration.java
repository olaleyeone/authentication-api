package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.access.AccessTokenValidator;
import com.olaleyeone.auth.security.access.TrustedIpAddressAccessManager;
import com.olaleyeone.auth.security.annotations.TrustedIpAddress;
import com.olaleyeone.auth.security.data.RequestMetadataFactory;
import com.olaleyeone.auth.security.interceptors.AccessConstraintHandlerInterceptor;
import com.olaleyeone.auth.security.interceptors.RemoteAddressConstraintHandlerInterceptor;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration implements WebMvcConfigurer {

    private final AutowireCapableBeanFactory beanFactory;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(beanFactory.createBean(RemoteAddressConstraintHandlerInterceptor.class));
        registry.addInterceptor(beanFactory.createBean(AccessConstraintHandlerInterceptor.class));
    }

    @Bean
    public RequestMetadataFactory requestMetadataFactory() {
        return beanFactory.createBean(RequestMetadataFactory.class);
    }

    @Profile("!test")
    @Bean
    public TrustedIpAddressAccessManager trustedIpAddressAccessManager(SettingService settingService) {
        return new TrustedIpAddressAccessManager() {
            @Override
            public AccessStatus getStatus(TrustedIpAddress accessConstraint, String ipAddress) {
                Optional<String> value = settingService.getString(StringUtils.defaultIfBlank(accessConstraint.value(), "TRUSTED_IP"));
                if (value.isPresent()) {
                    return Arrays.asList(value.get().split(" *, *")).contains(ipAddress)
                            ? AccessStatus.allowed()
                            : AccessStatus.denied(ipAddress);
                }
                if (accessConstraint.defaultIpAddresses().length > 0) {
                    return Arrays.asList(accessConstraint.defaultIpAddresses()).contains(ipAddress)
                            ? AccessStatus.allowed()
                            : AccessStatus.denied(ipAddress);
                }
                return AccessStatus.denied("");
            }
        };
    }

    @Profile("!test")
    @Bean
    public AccessTokenValidator accessTokenValidator(JwtService jwtService) {
        return new AccessTokenValidator() {
            @Override
            public String resolveToUserId(String token) {
                try {
                    return jwtService.getSubject(token);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }
}
