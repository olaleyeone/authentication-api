package com.olaleyeone.auth.security.interceptors;

import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.access.Authorizer;
import com.olaleyeone.auth.security.annotations.AccessConstraint;
import com.olaleyeone.auth.security.annotations.Public;
import com.olaleyeone.auth.security.data.AuthorizedRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AccessConstraintHandlerInterceptor extends HandlerInterceptorAdapter {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationContext applicationContext;
    private final List<Class<?>> excludedHandlers;

    @Autowired
    private Provider<AuthorizedRequest> authorizedRequestProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        try {
            List<Annotation> accessConstraints = collectAccessConstraints(handlerMethod.getMethod().getDeclaringClass().getAnnotations());
            accessConstraints.addAll(collectAccessConstraints(handlerMethod.getMethod().getDeclaredAnnotations()));

            AuthorizedRequest authorizedRequest = authorizedRequestProvider.get();

            if (isPublicEndpoint(handlerMethod, accessConstraints)) {
                return true;
            }

            if (authorizedRequest.getAccessToken() == null) {
                return rejectGuestAccess(request, response);
            }
            if (authorizedRequest.getAccessClaims() == null) {
                return rejectInvalidToken(response);
            }

            return validateUserAccess(request, response, accessConstraints);
        } catch (IllegalStateException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean rejectInvalidToken(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "invalid_token");
        response.getWriter().append("Unauthorized");
        return false;
    }

    private boolean rejectGuestAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Guest denied access to {}", request.getRequestURL());
        response.setStatus(401);
        response.getWriter().append("Unauthorized");
        return false;
    }

    private boolean validateUserAccess(HttpServletRequest request, HttpServletResponse response, List<Annotation> accessConstraints) throws IOException {
        for (Annotation annotation : accessConstraints) {
            AccessStatus accessStatus = getAccessStatus(annotation);
            if (!accessStatus.hasAccess()) {
                logger.info("{}: User denied access to {}",
                        annotation.annotationType().getName(),
                        request.getRequestURL());
                response.setStatus(403);
                response.getWriter().append(accessStatus.reason());
                return false;
            }
        }
        return true;
    }

    private boolean isPublicEndpoint(HandlerMethod handlerMethod, List<Annotation> accessConstraints) {
        return accessConstraints.isEmpty() && (
                handlerMethod.hasMethodAnnotation(Public.class)
                        || handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(Public.class)
                        || excludedHandlers.contains(handlerMethod.getBeanType())
        );
    }

    private List<Annotation> collectAccessConstraints(Annotation[] stream) {
        return Arrays.asList(stream).stream()
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(AccessConstraint.class))
                .collect(Collectors.toList());
    }

    private <A extends Annotation> AccessStatus getAccessStatus(A annotation) {
        Class<? extends Authorizer<A>> aClass = (Class<Authorizer<A>>) annotation.annotationType().getAnnotation(AccessConstraint.class).value();
        Authorizer<A> authorizer = applicationContext.getBean(aClass);
        return authorizer.getStatus(annotation, authorizedRequestProvider.get().getAccessClaims());
    }
}
