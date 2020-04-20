package com.olaleyeone.auth.security.data;

import com.olaleyeone.auth.security.access.AccessTokenValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
public class RequestMetadataFactory implements FactoryBean<RequestMetadata> {

    private final HttpServletRequest httpServletRequest;
    private final AccessTokenValidator accessTokenValidator;

    @Getter
    @Setter
    private String proxyIpHeader = "X-FORWARDED-FOR";

    private String tokenPrefix = "Bearer ";

    @Override
    public RequestMetadata getObject() {
//        if (RequestContextHolder.getRequestAttributes() == null) {
//            return null;
//        }
        return getRequestMetadata();
    }

    @Override
    public Class<?> getObjectType() {
        return RequestMetadata.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private RequestMetadataImpl getRequestMetadata() {
        return Optional.ofNullable((RequestMetadataImpl) httpServletRequest.getAttribute(RequestMetadataImpl.class.getName()))
                .orElseGet(() -> {
                    RequestMetadataImpl requestMetadata = new RequestMetadataImpl();
                    requestMetadata.setIpAddress(getActualIpAddress(httpServletRequest));
                    requestMetadata.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
                    requestMetadata.setLocalhost(isLocalhost(requestMetadata.getIpAddress()));
                    String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                    if (StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.startsWith(tokenPrefix)) {
                        requestMetadata.setAccessToken(authorizationHeader.substring(tokenPrefix.length()));
                        requestMetadata.setUserId(accessTokenValidator.resolveToUserId(requestMetadata.getAccessToken()));
                    }
                    httpServletRequest.setAttribute(RequestMetadataImpl.class.getName(), requestMetadata);
                    return requestMetadata;
                });
    }

    public String getActualIpAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (isLocalhost(ipAddress) && StringUtils.isNotBlank(request.getHeader(proxyIpHeader))) {
            ipAddress = request.getHeader(proxyIpHeader);
        }
        return ipAddress;
    }

    public boolean isLocalhost(String ipAddress) {
        return ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1");
    }
}
