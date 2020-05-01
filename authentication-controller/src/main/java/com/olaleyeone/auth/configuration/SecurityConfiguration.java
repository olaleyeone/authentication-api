package com.olaleyeone.auth.configuration;

import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.access.TrustedIpAddressAuthorizer;
import com.olaleyeone.auth.security.annotations.TrustedIpAddress;
import com.olaleyeone.auth.security.data.AccessClaims;
import com.olaleyeone.auth.security.data.AccessClaimsExtractor;
import com.olaleyeone.auth.security.data.AuthorizedRequestFactory;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    private final AutowireCapableBeanFactory beanFactory;

    @Bean
    public AuthorizedRequestFactory requestMetadataFactory() {
        return beanFactory.createBean(AuthorizedRequestFactory.class);
    }

    @Profile("!test")
    @Bean
    public TrustedIpAddressAuthorizer trustedIpAddressAccessManager(SettingService settingService) {
        return new TrustedIpAddressAuthorizer() {
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
    public AccessClaimsExtractor accessTokenValidator(@JwtToken(JwtTokenType.ACCESS) JwtService jwtService) {
        return new AccessClaimsExtractor() {
            @Override
            public AccessClaims getClaims(String token) {
                try {
                    return jwtService.parseAccessToken(token);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }
}
