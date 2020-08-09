package com.olaleyeone.auth.integration.sms;

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
//            @Field("from") String senderId,
            @Field("username") String username);
}
