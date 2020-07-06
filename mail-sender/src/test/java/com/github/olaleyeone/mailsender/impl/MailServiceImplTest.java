package com.github.olaleyeone.mailsender.impl;

import com.github.olaleyeone.mailsender.api.HtmlEmailDto;
import com.github.olaleyeone.mailsender.api.MailGunApiClient;
import com.github.olaleyeone.mailsender.test.ComponentTest;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MailServiceImplTest extends ComponentTest {

    private MailGunApiClient mailGunApi;

    private MailServiceImpl mailService;

    @BeforeEach
    void setUp() {
        mailGunApi = Mockito.mock(MailGunApiClient.class);
        mailService = new MailServiceImpl(mailGunApi);
    }

    @Test
    void sendEmailSuccessfully() throws IOException {
        Call<?> call = Mockito.mock(Call.class);
        Mockito.doReturn(call).when(mailGunApi).sendMail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.success(200, null)).when(call).execute();
        HtmlEmailDto emailDto = new HtmlEmailDto();
        emailDto.addRecipientEmail(faker.internet().emailAddress());
        mailService.sendEmail(emailDto);
    }

    @Test
    void sendEmailWithFailure() throws IOException {
        Call<?> call = Mockito.mock(Call.class);
        Mockito.doReturn(call).when(mailGunApi).sendMail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.error(403, ResponseBody.create(
                MediaType.get("text/plain"),
                faker.backToTheFuture().quote()
        ))).when(call).execute();
        HtmlEmailDto emailDto = new HtmlEmailDto();
        emailDto.setRecipientEmails(Collections.singletonList(faker.internet().emailAddress()));
        assertThrows(HttpException.class, () -> mailService.sendEmail(emailDto));
    }

    @Test
    void sendEmailWithError() throws IOException {
        Call<?> call = Mockito.mock(Call.class);
        Mockito.doReturn(call).when(mailGunApi).sendMail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doThrow(new RuntimeException()).when(call).execute();
        HtmlEmailDto emailDto = new HtmlEmailDto();
        assertThrows(RuntimeException.class, () -> mailService.sendEmail(emailDto));
    }

    @Test
    void getStringFromResponseBody() throws IOException {
        ResponseBody responseBody = Mockito.spy(ResponseBody.create(MediaType.get("text/plain"), faker.lordOfTheRings().character()));
        Mockito.when(responseBody.string()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> mailService.getString(responseBody));
    }
}