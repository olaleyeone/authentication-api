package com.olaleyeone.auth.configuration;

import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.github.olaleyeone.auth.data.AuthorizedRequestFactory;
import com.google.gson.Gson;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.integration.security.*;
import com.olaleyeone.auth.integration.security.auth.AccessClaimsExtractorImpl;
import com.olaleyeone.auth.integration.security.auth.AccessTokenGenerator;
import com.olaleyeone.auth.integration.security.auth.RefreshTokenGenerator;
import com.olaleyeone.auth.integration.security.auth.AuthJwsGenerator;
import com.olaleyeone.auth.integration.security.passwordreset.PasswordResetJwsGenerator;
import com.olaleyeone.auth.integration.security.passwordreset.PasswordResetTokenGeneratorImpl;
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
    public AuthTokenGenerator accessTokenGenerator(KeyGenerator keyGenerator, TaskContextFactory taskContextFactory) {
        return AccessTokenGenerator.builder()
                .jwsGenerator(new AuthJwsGenerator())
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
    public AuthTokenGenerator refreshTokenGenerator(KeyGenerator keyGenerator, TaskContextFactory taskContextFactory) {
        return RefreshTokenGenerator.builder()
                .jwsGenerator(new AuthJwsGenerator())
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

    @JwtToken(JwtTokenType.PASSWORD_RESET)
    @Bean
    public PasswordResetTokenGenerator passwordResetTokenGenerator(KeyGenerator keyGenerator, TaskContextFactory taskContextFactory) {
        return PasswordResetTokenGeneratorImpl.builder()
                .jwsGenerator(new PasswordResetJwsGenerator())
                .keyGenerator(keyGenerator)
                .signingKeyResolver(refreshTokenKeyResolver(null))
                .taskContextFactory(taskContextFactory)
                .build();
    }

    @JwtToken(JwtTokenType.PASSWORD_RESET)
    @Bean
    public AccessClaimsExtractor passwordResetTokenClaimsExtractor(Gson gson) {
        return new AccessClaimsExtractorImpl(passwordResetTokenKeyResolver(null), gson);
    }

    @JwtToken(JwtTokenType.PASSWORD_RESET)
    @Bean
    public SimpleSigningKeyResolver passwordResetTokenKeyResolver(SignatureKeyRepository signatureKeyRepository) {
        return new SimpleSigningKeyResolver(signatureKeyRepository, JwtTokenType.PASSWORD_RESET);
    }
}
