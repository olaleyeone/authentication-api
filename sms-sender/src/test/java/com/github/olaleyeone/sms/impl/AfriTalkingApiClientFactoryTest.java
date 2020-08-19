package com.github.olaleyeone.sms.impl;

import com.github.olaleyeone.sms.api.AfriTalkingClient;
import com.github.olaleyeone.sms.test.ComponentTest;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import retrofit2.Call;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AfriTalkingApiClientFactoryTest extends ComponentTest {

    @Mock
    private AfriTalkingConfig afriTalkingConfig;

    @InjectMocks
    private AfriTalkingApiClientFactory afriTalkingApiClientFactory;

    @Test
    void getObjectType() {
        assertEquals(AfriTalkingClient.class, afriTalkingApiClientFactory.getObjectType());
    }

    @Test
    void isSingleton() {
        assertTrue(afriTalkingApiClientFactory.isSingleton());
    }

    @Test
    void getObject() throws IOException {
        String apiKey = faker.code().asin();
        Mockito.doReturn(apiKey).when(afriTalkingConfig).getApiKey();
        Mockito.doReturn("http://domain.com/").when(afriTalkingConfig).getBaseUrl();
        Call<ResponseBody> call = afriTalkingApiClientFactory.getObject().sendSms(faker.phoneNumber().cellPhone(),
                faker.backToTheFuture().quote(), faker.internet().domainName());
        assertEquals(apiKey, call.execute().raw().request().header(AfriTalkingApiClientFactory.API_KEY));
    }
}