package com.olaleyeone.auth.integration.sms;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.test.ComponentTest;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OtpSmsSenderImplTest extends ComponentTest {

    @Mock
    private AfriTalkingClient afriTalkingClient;

    @Mock
    private Call<?> call;

    private PortalUserIdentifier portalUserIdentifier;
    private OneTimePassword oneTimePassword;
    private AfriTalkingConfig afriTalkingConfig;
    private OtpSmsSender otpSmsSender;

    @BeforeEach
    void setUp() {
        portalUserIdentifier = new PortalUserIdentifier();
        oneTimePassword = new OneTimePassword();
        oneTimePassword.setUserIdentifier(portalUserIdentifier);

        afriTalkingConfig = AfriTalkingConfig.builder().build();
        otpSmsSender = new OtpSmsSenderImpl(afriTalkingClient, afriTalkingConfig);
    }

    @Test
    void sendOtp() throws IOException {
        Mockito.doReturn(call).when(afriTalkingClient).sendSms(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.success(faker.lordOfTheRings().location())).when(call).execute();
        otpSmsSender.sendOtp(oneTimePassword, faker.internet().password());
    }

    @Test
    void sendOtpWithError() throws IOException {
        Mockito.doReturn(call).when(afriTalkingClient).sendSms(Mockito.any(), Mockito.any(), Mockito.any());
        ResponseBody responseBody = ResponseBody.create(MediaType.get(faker.file().mimeType()),
                faker.howIMetYourMother().character());
        Mockito.doReturn(Response.error(400, responseBody)).when(call).execute();
        assertThrows(Exception.class, () -> otpSmsSender.sendOtp(oneTimePassword, faker.internet().password()));
    }
}