package com.olaleyeone.auth.integration.email;

import com.olaleyeone.auth.service.SettingService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Base64;

@Data
@RequiredArgsConstructor
public class MailGunApiClientFactory implements FactoryBean<MailGunApiClient> {

    @Getter(AccessLevel.NONE)
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SettingService settingService;

    @Value("${MAIL_GUN_MESSAGES_API_KEY}")
    private String mailGunMessagesApiKey;

    @Value("${MAIL_GUN_MESSAGES_URL}")
    private String mailGunMessageBaseUrl;

    @Value("${EMAIL_SENDER_ADDRESS}")
    private String emailSenderAddress;

    @Value("${EMAIL_SENDER_NAME}")
    private String emailSenderName;

    private String bearerToken;

    @PostConstruct
    public void init() {
        if (!mailGunMessageBaseUrl.endsWith("/")) {
            mailGunMessageBaseUrl += "/";
        }
        bearerToken = Base64.getEncoder().encodeToString(("api:" + mailGunMessagesApiKey).getBytes());
    }

    @Override
    public Class<?> getObjectType() {
        return MailGunApiClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public MailGunApiClient getObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mailGunMessageBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .validateEagerly(true)
                .build();
        return retrofit.create(MailGunApiClient.class);
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(this::getRequestInterceptor)
                .build();
    }

    protected Response getRequestInterceptor(Interceptor.Chain chain) throws IOException {

        Request.Builder newRequestBuilder = chain.request().newBuilder();
        newRequestBuilder.addHeader("Authorization", "Bearer " + bearerToken);
        newRequestBuilder.url(chain.request().url().newBuilder()
                .addQueryParameter("from", String.format("%s <%s>", emailSenderName, emailSenderAddress))
                .build());
        return chain.proceed(newRequestBuilder.build());
    }

//    private String getBearerToken() {
//        return Base64.getEncoder().encodeToString(("api:" + MAIL_GUN_MESSAGES_API_KEY).getBytes());
//    }
}
