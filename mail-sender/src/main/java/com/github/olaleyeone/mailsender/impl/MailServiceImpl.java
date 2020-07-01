package com.github.olaleyeone.mailsender.impl;

import com.github.olaleyeone.mailsender.api.HtmlEmailDto;
import com.github.olaleyeone.mailsender.api.MailService;
import com.github.olaleyeone.mailsender.api.MailGunApiClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.Optional;

@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MailGunApiClient mailGunApi;

//    @Activity("SEND EMAIL")
    @SneakyThrows
    @Override
    public void sendEmail(HtmlEmailDto htmlEmailDto) {
        Response<ResponseBody> response = mailGunApi.sendMail(
                htmlEmailDto.getRecipientEmails(),
                htmlEmailDto.getHtmlMessage(),
                htmlEmailDto.getSubject())
                .execute();
        if (response.isSuccessful()) {
            return;
        }
        logger.error("===> Mail sending to {} failed with code {} and message {} ",
                String.join(", ", htmlEmailDto.getRecipientEmails()),
                response.code(),
                Optional.ofNullable(response.errorBody()).map(this::getString).orElse(null));
        throw new HttpException(response);
    }

    @SneakyThrows
    protected String getString(ResponseBody responseBody) {
        return responseBody.string();
    }
}
