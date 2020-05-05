package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.dto.HtmlEmailDto;
import com.olaleyeone.auth.test.ComponentTest;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MailServiceImplTest extends ComponentTest {

    @Mock
    private MailGunApiClient mailGunApi;

    private MailServiceImpl mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailServiceImpl(mailGunApi);
    }

    @Test
    void sendEmailSuccessfully() throws IOException {
        Call<?> call = Mockito.mock(Call.class);
        Mockito.doReturn(call).when(mailGunApi).sendMail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.success(HttpStatus.OK.value(), null)).when(call).execute();
        HtmlEmailDto emailDto = new HtmlEmailDto();
        mailService.sendEmail(emailDto);
    }

    @Test
    void sendEmailWithFailure() throws IOException {
        Call<?> call = Mockito.mock(Call.class);
        Mockito.doReturn(call).when(mailGunApi).sendMail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(Response.error(HttpStatus.FORBIDDEN.value(), ResponseBody.create(
                MediaType.get("text/plain"),
                faker.backToTheFuture().quote()
        ))).when(call).execute();
        HtmlEmailDto emailDto = new HtmlEmailDto();
        mailService.sendEmail(emailDto);
    }

    @Test
    void sendEmailWithError() throws IOException {
        Call<?> call = Mockito.mock(Call.class);
        Mockito.doReturn(call).when(mailGunApi).sendMail(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doThrow(new RuntimeException()).when(call).execute();
        HtmlEmailDto emailDto = new HtmlEmailDto();
        assertThrows(RuntimeException.class, () -> mailService.sendEmail(emailDto));
    }
}