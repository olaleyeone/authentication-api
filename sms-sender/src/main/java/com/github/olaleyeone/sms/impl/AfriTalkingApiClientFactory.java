package com.github.olaleyeone.sms.impl;

import com.github.olaleyeone.sms.api.AfriTalkingClient;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.FactoryBean;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

@RequiredArgsConstructor
public class AfriTalkingApiClientFactory implements FactoryBean<AfriTalkingClient> {

    public static final String API_KEY = "apiKey";
    private final AfriTalkingConfig afriTalkingConfig;

    @Override
    public Class<?> getObjectType() {
        return AfriTalkingClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public AfriTalkingClient getObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(afriTalkingConfig.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .validateEagerly(true)
                .build();
        return retrofit.create(AfriTalkingClient.class);
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(this::getRequestInterceptor)
                .build();
    }

    protected Response getRequestInterceptor(Interceptor.Chain chain) throws IOException {
        Request.Builder newRequestBuilder = chain.request().newBuilder();
        newRequestBuilder.addHeader(API_KEY, afriTalkingConfig.getApiKey());
        return chain.proceed(newRequestBuilder.build());
    }
}
