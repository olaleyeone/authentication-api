package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.dto.HtmlEmailDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MailGunApiClient mailGunApi;

    @SneakyThrows
    @Override
    public void sendEmail(HtmlEmailDto htmlEmailDto) {
        Response<ResponseBody> response = mailGunApi.sendMail(
                htmlEmailDto.getRecipientEmails(),
                htmlEmailDto.getHtmlMessage(),
                htmlEmailDto.getSubject())
                .execute();
        if (!response.isSuccessful()) {
//                logger.error("===> Mail sending to {} failed with code {} and message {} ",
//                        String.join(", ", htmlEmailDto.getRecipientEmails()), response.code(), response.errorBody() != null ? response.errorBody().string() : "null");
//                throw new ApiCallException(response.raw().request().url().url().toString(),
//                        response.code(),
//                        response.message(),
//                        response.errorBody().contentType().type());
        }
    }
}
