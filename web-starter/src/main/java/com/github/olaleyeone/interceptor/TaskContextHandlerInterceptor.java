package com.github.olaleyeone.interceptor;

import com.github.olaleyeone.auth.data.AuthorizedRequest;
import com.olaleyeone.audittrail.embeddable.Duration;
import com.olaleyeone.audittrail.entity.Task;
import com.olaleyeone.audittrail.entity.WebRequest;
import com.olaleyeone.audittrail.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
public class TaskContextHandlerInterceptor extends HandlerInterceptorAdapter {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Getter
//    @Setter
//    @Value("${IP_V4_LOCALHOST:127.0.0.1}")
//    private String IP_V4_LOCALHOST;
//    @Value("${IP_V6_LOCALHOST:0:0:0:0:0:0:0:1}")
//    private String IP_V6_LOCALHOST;

    @Getter
    @Setter
    private String proxyIpHeader = "X-REAL-IP";

    private final TaskContextFactory taskContextFactory;

    private final TaskContextHolder taskContextHolder;

    private final TaskContextSaver taskContextSaver;

    private final Provider<AuthorizedRequest> authorizedRequestProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Task task = new Task();
        task.setName(String.format("%s %s", StringUtils.upperCase(request.getMethod()),
                Optional.ofNullable(request.getAttribute(org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                        .map(Object::toString)
                        .orElse(request.getServletPath())));
        task.setType(Task.WEB_REQUEST);
        task.setDuration(new Duration(OffsetDateTime.now(), null));

        WebRequest webRequest = new WebRequest();
        webRequest.setIpAddress(getActualIpAddress(request));
        webRequest.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        webRequest.setHttpMethod(request.getMethod());

        try {
            URL url = new URL(request.getRequestURL().toString());
            webRequest.setScheme(url.getProtocol());
            webRequest.setHost(url.getHost());
            webRequest.setPath(url.getPath());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        webRequest.setUri(request.getRequestURI());
        try {
            AuthorizedRequest authorizedRequest = authorizedRequestProvider.get();
            if (authorizedRequest.getAccessClaims() != null) {
                webRequest.setUserId(authorizedRequest.getAccessClaims().getSubject());
                webRequest.setSessionId(authorizedRequest.getAccessClaims().getId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        task.setWebRequest(webRequest);

        taskContextFactory.start(task);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable Exception ex) {
        TaskContextImpl taskContext = taskContextHolder.getObject();
        Task task = taskContext.getTask();
        if (ex != null) {
            task.setFailure(CodeContextUtil.toFailure(ex));
        }
        WebRequest webRequest = task.getWebRequest();
        webRequest.setStatusCode(response.getStatus());

        Duration duration = task.getDuration();
        duration.setNanoSecondsTaken(duration.getStartedOn().until(OffsetDateTime.now(), ChronoUnit.NANOS));
        taskContextSaver.save(taskContext);
    }

    public String getActualIpAddress(HttpServletRequest request) {
        if (StringUtils.isBlank(proxyIpHeader)) {
            return request.getRemoteAddr();
        }
        return StringUtils.defaultIfBlank(request.getHeader(proxyIpHeader), request.getRemoteAddr());
    }
}
