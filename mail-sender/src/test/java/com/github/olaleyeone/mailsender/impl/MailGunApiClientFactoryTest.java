package com.github.olaleyeone.mailsender.impl;

import com.github.olaleyeone.mailsender.api.MailGunApiClient;
import com.github.olaleyeone.mailsender.test.ComponentTest;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import retrofit2.Call;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MailGunApiClientFactoryTest extends ComponentTest {

    private MailGunConfig mailGunConfig;
    private MailGunApiClientFactory mailGunApiClientFactory;

    @BeforeEach
    void setUp() {
        mailGunConfig = MailGunConfig.builder()
                .mailGunMessageBaseUrl("http://domain.com")
                .mailGunMessagesApiKey(faker.random().hex())
                .emailSenderAddress(faker.internet().emailAddress())
                .emailSenderName(faker.company().name())
                .build();
        mailGunApiClientFactory = new MailGunApiClientFactory(mailGunConfig);
    }

    @Test
    void testInitBearerToken() {
        mailGunApiClientFactory.init();
        assertNotNull(mailGunApiClientFactory.getBearerToken());

        String textToEncode = "api:" + mailGunConfig.getMailGunMessagesApiKey();
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
        MailGunApiClient apiClient = mailGunApiClientFactory.getObject();
        String body = faker.backToTheFuture().quote();
        Call<ResponseBody> call = apiClient.sendMail(
                Collections.singletonList(faker.internet().emailAddress()),
                body,
                "subject",
                Collections.singletonList(faker.internet().emailAddress()));
        assertEquals("domain.com", call.request().url().host());
        assertFalse(call.request().url().isHttps());
    }

    @Test
    void getRequestInterceptor() throws IOException {
        mailGunApiClientFactory.setBearerToken(faker.crypto().md5());

        Interceptor.Chain chain = Mockito.mock(Interceptor.Chain.class);

        Request.Builder builder = new Request.Builder();
        builder.url("http://" + faker.internet().url());
        Request request = builder.build();

        Mockito.doReturn(request).when(chain).request();

        mailGunApiClientFactory.getRequestInterceptor(chain);
        Mockito.verify(chain, Mockito.times(1))
                .proceed(Mockito.argThat(argument -> {
                    assertEquals(String.format("Bearer %s", mailGunApiClientFactory.getBearerToken()),
                            argument.header("Authorization"));
                    assertEquals(String.format("%s <%s>",
                            mailGunConfig.getEmailSenderName(),
                            mailGunConfig.getEmailSenderAddress()),
                            argument.url().queryParameter("from"));
                    return true;
                }));
    }
}