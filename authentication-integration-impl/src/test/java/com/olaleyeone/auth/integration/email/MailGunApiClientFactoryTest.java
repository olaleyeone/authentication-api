package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.service.SettingService;
import com.olaleyeone.auth.test.ComponentTest;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import retrofit2.Call;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MailGunApiClientFactoryTest extends ComponentTest {

    private MailGunApiClientFactory mailGunApiClientFactory;

    @Mock
    private SettingService settingService;

    @BeforeEach
    void setUp() {
        mailGunApiClientFactory = new MailGunApiClientFactory(settingService);
        mailGunApiClientFactory.setMailGunMessageBaseUrl("http://domain.com/");
    }

    @Test
    void testInitWithIncompleteUrl() {
        mailGunApiClientFactory.setMailGunMessageBaseUrl("http://domain.com");
        mailGunApiClientFactory.init();
        assertEquals("http://domain.com/", mailGunApiClientFactory.getMailGunMessageBaseUrl());
    }

    @Test
    void testInitWithCompleteUrl() {
        mailGunApiClientFactory.setMailGunMessageBaseUrl("http://domain.com/");
        mailGunApiClientFactory.init();
        assertEquals("http://domain.com/", mailGunApiClientFactory.getMailGunMessageBaseUrl());
    }

    @Test
    void testInitBearerToken() {
        String apiKey = faker.internet().password();
        mailGunApiClientFactory.setMailGunMessagesApiKey(apiKey);
        mailGunApiClientFactory.init();
        assertNotNull(mailGunApiClientFactory.getBearerToken());

        String textToEncode = "api:" + apiKey;
        assertEquals(Base64.getEncoder().encodeToString(textToEncode.getBytes()),
                mailGunApiClientFactory.getBearerToken());
    }

    @Test
    void getObjectType() {
        assertEquals(MailGunApiClient.class, mailGunApiClientFactory.getObjectType());
    }

    @Test
    void isSingleton() {
        assertTrue(mailGunApiClientFactory.isSingleton());
    }

    @Test
    void getObject() {
        mailGunApiClientFactory.setMailGunMessageBaseUrl("http://domain.com/");
        MailGunApiClient apiClient = mailGunApiClientFactory.getObject();
        String body = faker.backToTheFuture().quote();
        Call<ResponseBody> call = apiClient.sendMail(Collections.singletonList(faker.internet().emailAddress()),
                body, "subject");
        assertEquals("domain.com", call.request().url().host());
        assertFalse(call.request().url().isHttps());
    }

    @Test
    void getRequestInterceptor() throws IOException {
        mailGunApiClientFactory.setBearerToken(faker.crypto().md5());
        mailGunApiClientFactory.setEmailSenderName(faker.funnyName().name());
        mailGunApiClientFactory.setEmailSenderAddress(faker.internet().emailAddress());

        Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);

        Request.Builder builder = new Request.Builder();
        builder.url("http://" + faker.internet().url());
        Request request = builder.build();

        Mockito.doReturn(request).when(chain).request();

        mailGunApiClientFactory.getRequestInterceptor(chain);
        Mockito.verify(chain, Mockito.times(1))
                .proceed(Mockito.argThat(argument -> {
                    assertEquals(String.format("Bearer %s", mailGunApiClientFactory.getBearerToken()),
                            argument.header(HttpHeaders.AUTHORIZATION));
                    assertEquals(String.format("%s <%s>",
                            mailGunApiClientFactory.getEmailSenderName(),
                            mailGunApiClientFactory.getEmailSenderAddress()),
                            argument.url().queryParameter("from"));
                    return true;
                }));
    }
}