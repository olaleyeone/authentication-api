package com.olaleyeone.auth.security.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AuthorizedRequestFactory implements FactoryBean<AuthorizedRequest> {

    @Value("${IP_V4_LOCALHOST:127.0.0.1}")
    private String IP_V4_LOCALHOST;
    @Value("${IP_V6_LOCALHOST:0:0:0:0:0:0:0:1}")
    private String IP_V6_LOCALHOST;

    private final HttpServletRequest httpServletRequest;
    private final AccessClaimsExtractor accessClaimsExtractor;

    @Getter
    @Setter
    private String proxyIpHeader = "X-FORWARDED-FOR";

    @Override
    public AuthorizedRequest getObject() {
//        if (RequestContextHolder.getRequestAttributes() == null) {
//            return null;
//        }
        return getRequestMetadata();
    }

    @Override
    public Class<?> getObjectType() {
        return AuthorizedRequest.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private AuthorizedRequestImpl getRequestMetadata() {
        return Optional.ofNullable((AuthorizedRequestImpl) httpServletRequest.getAttribute(AuthorizedRequestImpl.class.getName()))
                .orElseGet(() -> {
                    AuthorizedRequestImpl requestMetadata = new AuthorizedRequestImpl();
                    requestMetadata.setIpAddress(getActualIpAddress(httpServletRequest));
                    requestMetadata.setLocalhost(isLocalhost(requestMetadata.getIpAddress()));
                    requestMetadata.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));

                    getAccessToken(httpServletRequest)
                            .ifPresent(accessToken -> {
                                requestMetadata.setAccessToken(accessToken);
                                requestMetadata.setAccessClaims(accessClaimsExtractor.getClaims(accessToken));
                            });

                    httpServletRequest.setAttribute(AuthorizedRequestImpl.class.getName(), requestMetadata);
                    return requestMetadata;
                });
    }

    public String getActualIpAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (isWebServer(ipAddress) && StringUtils.isNotBlank(request.getHeader(proxyIpHeader))) {
            ipAddress = request.getHeader(proxyIpHeader);
        }
        return ipAddress;
    }

    public boolean isWebServer(String ipAddress) {
        return isLocalhost(ipAddress);
    }

    public boolean isLocalhost(String ipAddress) {
        return ipAddress.equals(IP_V4_LOCALHOST) || ipAddress.equals(IP_V6_LOCALHOST);
    }

    protected abstract Optional<String> getAccessToken(HttpServletRequest httpServletRequest);
}
