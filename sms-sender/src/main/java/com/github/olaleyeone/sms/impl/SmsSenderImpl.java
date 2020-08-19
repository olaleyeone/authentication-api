package com.github.olaleyeone.sms.impl;

import com.github.olaleyeone.sms.api.AfriTalkingClient;
import com.github.olaleyeone.sms.api.Sms;
import com.github.olaleyeone.sms.api.SmsSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.Optional;

@RequiredArgsConstructor
public class SmsSenderImpl implements SmsSender {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AfriTalkingClient afriTalkingClient;
    private final AfriTalkingConfig afriTalkingConfig;

    @SneakyThrows
    @Override
    public void send(Sms sms) {
        Response<ResponseBody> response = afriTalkingClient.sendSms(
                sms.getFrom(),
                sms.getTo(),
                sms.getMessage(),
                afriTalkingConfig.getUsername()).execute();

        if (response.isSuccessful()) {
            logger.info("{}: SMS sent to {}", response.code(), sms.getTo());
            return;
        }
        logger.error("===> SMS delivery to {} failed with code {} and message {} ",
                String.join(", ", sms.getTo()),
                response.code(),
                Optional.ofNullable(response.errorBody()).map(this::getString).orElse(null));
        throw new HttpException(response);
    }

    @SneakyThrows
    protected String getString(ResponseBody responseBody) {
        return responseBody.string();
    }
}
