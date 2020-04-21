package com.olaleyeone.auth.security.interceptors;

import com.olaleyeone.auth.security.access.TrustedIpAddressAuthorizer;
import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.annotations.Localhost;
import com.olaleyeone.auth.security.annotations.TrustedIpAddress;
import com.olaleyeone.auth.security.data.RequestMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RemoteAddressConstraintHandlerInterceptor extends HandlerInterceptorAdapter {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TrustedIpAddressAuthorizer trustedIpAddressAccessManager;
    private final Provider<RequestMetadata> requestMetadataProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestMetadata requestMetadata = requestMetadataProvider.get();

        try {
            if (expectsOnlyLocalhost(handlerMethod)) {
                if (!requestMetadata.isLocalhost()) {
                    logger.warn("Non local IP \"{}\" denied access to {}", requestMetadata, request.getServletPath());
                    sendForbiddenResponse(AccessStatus.denied(), response);
                    return false;
                }
            } else {
                Map.Entry<TrustedIpAddress, AccessStatus> failedTrustedIpAddressCheck = getFailedTrustedIpAddressCheck(handlerMethod, requestMetadata.getIpAddress());
                if (failedTrustedIpAddressCheck != null) {
                    logRequestFromUntrustedIpAddress(failedTrustedIpAddressCheck.getKey(), request, requestMetadata.getIpAddress());
                    sendForbiddenResponse(failedTrustedIpAddressCheck.getValue(), response);
                    return false;
                }
            }
        } catch (IllegalStateException e) {
            logger.error(e.getMessage(), e);
        }

        return true;
    }

    public Map.Entry<TrustedIpAddress, AccessStatus> getFailedTrustedIpAddressCheck(HandlerMethod handlerMethod, String ipAddress) throws IOException {
        List<TrustedIpAddress> accessConstraints = new ArrayList<>(Arrays.asList(handlerMethod.getMethod()
                .getDeclaringClass().getAnnotationsByType(TrustedIpAddress.class)));
        accessConstraints.addAll(Arrays.asList(handlerMethod.getMethod().getAnnotationsByType(TrustedIpAddress.class)));

        for (TrustedIpAddress annotation : accessConstraints) {
            AccessStatus accessStatus = trustedIpAddressAccessManager.getStatus(annotation, ipAddress);
            if (!accessStatus.hasAccess()) {
                return Pair.of(annotation, accessStatus);
            }
        }
        return null;
    }

    public void logRequestFromUntrustedIpAddress(TrustedIpAddress trustedIpAddress, HttpServletRequest request, String ipAddress) {
        logger.warn("{}: Untrusted IP \"{}\" denied access to {}", trustedIpAddress.value(),
                ipAddress, request.getServletPath());
    }

    @SneakyThrows
    public void sendForbiddenResponse(AccessStatus accessStatus, HttpServletResponse response) {
        response.setStatus(403);
        response.getWriter().append(accessStatus.reason());
    }

    public boolean expectsOnlyLocalhost(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getDeclaringClass().getAnnotationsByType(Localhost.class).length > 0
                || handlerMethod.getMethod().getAnnotationsByType(Localhost.class).length > 0;
    }
}
