package com.github.olaleyeone.interceptor;

import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AuthorizedRequest;
import com.github.olaleyeone.test.component.ComponentTest;
import com.olaleyeone.audittrail.embeddable.Duration;
import com.olaleyeone.audittrail.entity.Task;
import com.olaleyeone.audittrail.entity.WebRequest;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.audittrail.impl.TaskContextHolder;
import com.olaleyeone.audittrail.impl.TaskContextImpl;
import com.olaleyeone.audittrail.impl.TaskContextSaver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskContextHandlerInterceptorTest extends ComponentTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private TaskContextFactory taskContextFactory;

    @Mock
    private TaskContextHolder taskContextHolder;

    @Mock
    private TaskContextSaver taskContextSaver;

    @Mock
    private AuthorizedRequest authorizedRequest;

    @Mock
    private AccessClaims accessClaims;

    @Mock
    private Provider<AuthorizedRequest> authorizedRequestProvider;

    @InjectMocks
    private TaskContextHandlerInterceptor taskContextHandlerInterceptor;

    private String httpMethod;
    private String url;

    @BeforeEach
    void setUp() {
        httpMethod = "post";
        url = "https://api.domain.com/me/profile-picture";
    }

    @Test
    void preHandle() {
        Mockito.doReturn(new StringBuffer(url)).when(request).getRequestURL();
        Mockito.doReturn(authorizedRequest).when(authorizedRequestProvider).get();
        String path = faker.internet().slug();
        Mockito.doReturn(path).when(request).getServletPath();
        Mockito.doReturn(path).when(request).getRequestURI();

        String ipV4Address = faker.internet().ipV4Address();
        Mockito.doReturn(ipV4Address).when(request).getRemoteAddr();
        Mockito.doReturn(httpMethod).when(request).getMethod();

        String userAgent = faker.internet().userAgentAny();
        Mockito.doReturn(userAgent).when(request).getHeader(Mockito.eq(HttpHeaders.USER_AGENT));
        Mockito.doReturn(null).when(request).getHeader(Mockito.eq(taskContextHandlerInterceptor.getProxyIpHeader()));

        taskContextHandlerInterceptor.preHandle(request, response, null);
        Mockito.verify(taskContextFactory, Mockito.times(1))
                .start(Mockito.argThat(task -> {
                    assertNotNull(task);
                    assertNotNull(task.getDuration());
                    assertEquals(Task.WEB_REQUEST, task.getType());
                    assertTrue(task.getName().contains(path));

                    assertNotNull(task.getWebRequest());
                    WebRequest webRequest = task.getWebRequest();
                    assertEquals(path, webRequest.getUri());
                    assertEquals("https", webRequest.getScheme());
                    assertEquals("api.domain.com", webRequest.getHost());
                    assertEquals("/me/profile-picture", webRequest.getPath());

                    assertEquals(ipV4Address, webRequest.getIpAddress());
                    assertEquals(userAgent, webRequest.getUserAgent());
                    return true;
                }));
    }

    @Test
    void preHandleAuthorizedRequest() {
        Mockito.doReturn(authorizedRequest).when(authorizedRequestProvider).get();
        Mockito.doReturn(accessClaims).when(authorizedRequest).getAccessClaims();
        Mockito.doReturn(faker.number().digit()).when(accessClaims).getSubject();
        Mockito.doReturn(faker.number().digit()).when(accessClaims).getId();

        taskContextHandlerInterceptor.setProxyIpHeader(null);

        String ipV4Address = faker.internet().ipV4Address();
        Mockito.doReturn(ipV4Address).when(request).getRemoteAddr();
        Mockito.doReturn(httpMethod).when(request).getMethod();

        taskContextHandlerInterceptor.preHandle(request, response, null);
        Mockito.verify(taskContextFactory, Mockito.times(1))
                .start(Mockito.argThat(task -> {
                    assertNotNull(task);

                    assertNotNull(task.getWebRequest());
                    WebRequest webRequest = task.getWebRequest();
                    assertEquals(accessClaims.getId(), webRequest.getSessionId());
                    assertEquals(accessClaims.getSubject(), webRequest.getUserId());
                    assertEquals(ipV4Address, webRequest.getIpAddress());
                    return true;
                }));
    }

    @Test
    void preHandleForProxy() {
        String ipV4Address = faker.internet().ipV4Address();

        Mockito.doReturn(ipV4Address).when(request).getHeader(Mockito.eq(taskContextHandlerInterceptor.getProxyIpHeader()));
        Mockito.doReturn(faker.internet().ipV4Address()).when(request).getRemoteAddr();
        Mockito.doReturn(httpMethod).when(request).getMethod();

        Mockito.doReturn(faker.internet().userAgentAny()).when(request).getHeader(Mockito.eq(HttpHeaders.USER_AGENT));

        taskContextHandlerInterceptor.preHandle(request, response, null);
        Mockito.verify(taskContextFactory, Mockito.times(1))
                .start(Mockito.argThat(task -> {
                    assertNotNull(task);
                    assertNotNull(task.getWebRequest());
                    WebRequest webRequest = task.getWebRequest();
                    assertEquals(ipV4Address, webRequest.getIpAddress());
                    return true;
                }));
    }

    @Test
    void afterCompletion() {
        Task task = new Task();
        task.setDuration(Duration.builder().startedOn(OffsetDateTime.now()).build());
        task.setWebRequest(new WebRequest());
        TaskContextImpl taskContext = new TaskContextImpl(task, null, taskContextHolder, null);
        Mockito.doReturn(taskContext).when(taskContextHolder).getObject();
        int statusCode = faker.number().randomDigit();
        Mockito.doReturn(statusCode).when(response).getStatus();

        taskContextHandlerInterceptor.afterCompletion(request, response, null, null);
        Mockito.verify(taskContextSaver, Mockito.times(1)).save(taskContext);
        assertNotNull(task.getDuration().getNanoSecondsTaken());
        assertEquals(statusCode, task.getWebRequest().getStatusCode());
    }
}