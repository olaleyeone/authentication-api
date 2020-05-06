package com.olaleyeone.auth.integration.email;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.dto.HtmlEmailDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;
import retrofit2.Response;

@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MailGunApiClient mailGunApi;

    @Activity("SEND EMAIL")
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
                response.errorBody() != null ? response.errorBody().string() : "null");
        throw new HttpException(response);
    }
}
