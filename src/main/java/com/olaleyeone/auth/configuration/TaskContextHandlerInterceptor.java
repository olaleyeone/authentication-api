package com.olaleyeone.auth.configuration;

import com.olaleyeone.audittrail.embeddable.Duration;
import com.olaleyeone.audittrail.entity.Task;
import com.olaleyeone.audittrail.entity.WebRequest;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.audittrail.impl.TaskContextHolder;
import com.olaleyeone.audittrail.impl.TaskContextImpl;
import com.olaleyeone.audittrail.impl.TaskContextSaver;
import com.olaleyeone.data.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class TaskContextHandlerInterceptor extends HandlerInterceptorAdapter {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Provider<RequestMetadata> requestMetadataProvider;

    private final TaskContextFactory taskContextFactory;

    private final TaskContextHolder taskContextHolder;

    private final TaskContextSaver taskContextSaver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Task task = new Task();
        task.setName(request.getServletPath());
        task.setType(Task.WEB_REQUEST);
        task.setDuration(new Duration(LocalDateTime.now(), null));

        RequestMetadata requestMetadata = requestMetadataProvider.get();
        WebRequest webRequest = new WebRequest();
        webRequest.setIpAddress(requestMetadata.getIpAddress());
        webRequest.setUserAgent(requestMetadata.getUserAgent());
        webRequest.setUri(request.getRequestURI());
        if (requestMetadata.getRefreshTokenId() != null) {
            webRequest.setSessionId(requestMetadata.getRefreshTokenId().toString());
        }
        if (requestMetadata.getPortalUserId() != null) {
            webRequest.setUserId(requestMetadata.getPortalUserId().toString());
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
        WebRequest webRequest = task.getWebRequest();
        webRequest.setStatusCode(response.getStatus());
        Duration duration = task.getDuration();
        duration.setNanoSecondsTaken(duration.getStartedOn().until(LocalDateTime.now(), ChronoUnit.NANOS));
        taskContextSaver.save(taskContext);
    }
}
