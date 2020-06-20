package com.olaleyeone.auth.configuration;

import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.github.olaleyeone.auth.data.AuthorizedRequestFactory;
import com.google.gson.Gson;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.integration.security.*;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import com.olaleyeone.auth.service.KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

//    @Autowired
//    private AutowireCapableBeanFactory beanFactory;

    @Bean
    public HashService hashService() {
        return new HashServiceImpl();
    }

    @Bean
    public AuthorizedRequestFactory requestMetadataFactory(AutowireCapableBeanFactory beanFactory) {
        return beanFactory.createBean(AuthorizedRequestFactory.class);
    }

//    @Profile("!test")
//    @Bean
//    public TrustedIpAddressAuthorizer trustedIpAddressAccessManager(SettingService settingService) {
//        return (accessConstraint, ipAddress) -> {
//            Optional<String> value = settingService.getString(StringUtils.defaultIfBlank(accessConstraint.value(), "TRUSTED_IP"));
//            if (value.isPresent()) {
//                return Arrays.asList(value.get().split(" *, *")).contains(ipAddress)
//                        ? AccessStatus.allowed()
//                        : AccessStatus.denied(ipAddress);
//            }
//            if (accessConstraint.defaultIpAddresses().length > 0) {
//                return Arrays.asList(accessConstraint.defaultIpAddresses()).contains(ipAddress)
//                        ? AccessStatus.allowed()
//                        : AccessStatus.denied(ipAddress);
//            }
//            return AccessStatus.denied("");
//        };
//    }

    @JwtToken(JwtTokenType.ACCESS)
    @Bean
    public TokenGenerator accessTokenGenerator(KeyGenerator keyGenerator, TaskContextFactory taskContextFactory) {
        return AccessTokenGenerator.builder()
                .jwsGenerator(new SimpleJwsGenerator())
                .keyGenerator(keyGenerator)
                .signingKeyResolver(accessTokenKeyResolver(null))
                .taskContextFactory(taskContextFactory)
                .build();
    }

    @JwtToken(JwtTokenType.ACCESS)
    @Bean
    public AccessClaimsExtractor accessClaimsExtractor(Gson gson) {
        return new AccessClaimsExtractorImpl(accessTokenKeyResolver(null), gson);
    }

    @JwtToken(JwtTokenType.ACCESS)
    @Bean
    public SimpleSigningKeyResolver accessTokenKeyResolver(SignatureKeyRepository signatureKeyRepository) {
        return new SimpleSigningKeyResolver(signatureKeyRepository, JwtTokenType.ACCESS);
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public TokenGenerator refreshTokenGenerator(KeyGenerator keyGenerator, TaskContextFactory taskContextFactory) {
        return RefreshTokenGenerator.builder()
                .jwsGenerator(new SimpleJwsGenerator())
                .keyGenerator(keyGenerator)
                .signingKeyResolver(refreshTokenKeyResolver(null))
                .taskContextFactory(taskContextFactory)
                .build();
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public AccessClaimsExtractor refreshTokenClaimsExtractor(Gson gson) {
        return new AccessClaimsExtractorImpl(refreshTokenKeyResolver(null), gson);
    }

    @JwtToken(JwtTokenType.REFRESH)
    @Bean
    public SimpleSigningKeyResolver refreshTokenKeyResolver(SignatureKeyRepository signatureKeyRepository) {
        return new SimpleSigningKeyResolver(signatureKeyRepository, JwtTokenType.REFRESH);
    }
}
