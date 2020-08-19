package com.github.olaleyeone.sms.impl;

import com.github.olaleyeone.sms.api.AfriTalkingClient;
import com.github.olaleyeone.sms.api.Sms;
import com.github.olaleyeone.sms.test.ComponentTest;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SmsSenderImplTest extends ComponentTest {

    @Mock
    private AfriTalkingClient afriTalkingClient;
    @Mock
    private AfriTalkingConfig afriTalkingConfig;

    @Mock
    private Call<ResponseBody> call;

    @InjectMocks
    private SmsSenderImpl smsSender;

    @Test
    void send() throws IOException {
        String username = faker.internet().emailAddress();
        Mockito.doReturn(username).when(afriTalkingConfig).getUsername();
        Mockito.doReturn(call).when(afriTalkingClient).sendSms(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.success("")).when(call).execute();
        Sms sms = Sms.builder()
                .to(faker.phoneNumber().cellPhone())
                .message(faker.backToTheFuture().quote())
                .from(faker.lordOfTheRings().character())
                .build();
        smsSender.send(sms);
        Mockito.verify(afriTalkingClient, Mockito.times(1))
                .sendSms(Mockito.eq(sms.getFrom()),
                        Mockito.eq(sms.getTo()),
                        Mockito.eq(sms.getMessage()),
                        Mockito.eq(username));
    }

    @Test
    void sendWithError() throws IOException {
        String username = faker.internet().emailAddress();
        Mockito.doReturn(username).when(afriTalkingConfig).getUsername();
        Mockito.doReturn(call).when(afriTalkingClient).sendSms(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.error(500, ResponseBody.create(MediaType.get("text/plain"), faker.howIMetYourMother().quote())))
                .when(call).execute();
        assertThrows(HttpException.class, () -> smsSender.send(Sms.builder().build()));
    }
}