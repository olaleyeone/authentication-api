package com.olaleyeone.auth.integration.sms;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import retrofit2.Response;

@RequiredArgsConstructor
@Component
public class OtpSmsSenderImpl implements OtpSmsSender {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AfriTalkingClient afriTalkingClient;
    private final AfriTalkingConfig afriTalkingConfig;

    @SneakyThrows
    @Override
    public void sendOtp(OneTimePassword oneTimePassword, String password) {
        Response<ResponseBody> response = afriTalkingClient.sendSms(
                oneTimePassword.getUserIdentifier().getIdentifier(),
                String.format("%s is your Quiqit passcode", password),
//                "AFRICASTKNG",
                afriTalkingConfig.getUsername()
        ).execute();

        if (!response.isSuccessful()) {
            throw new HttpClientErrorException(
                    HttpStatus.valueOf(response.code()),
                    response.message(),
                    HttpHeaders.EMPTY,
                    response.errorBody().bytes(),
                    response.errorBody().contentType().charset());
        }
//        logger.info("Padisoft response: {}", response.body().string());
    }
}
