package com.github.olaleyeone.sms.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AfriTalkingClient {

    @POST("version1/messaging")
    @FormUrlEncoded
    Call<ResponseBody> sendSms(
            @Field("to") String recipient,
            @Field("message") String message,
            @Field("username") String username);

    @POST("version1/messaging")
    @FormUrlEncoded
    Call<ResponseBody> sendSms(
            @Field("from") String senderId,
            @Field("to") String recipient,
            @Field("message") String message,
            @Field("username") String username);
}
