package com.olaleyeone.auth.response.pojo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OtpApiResponse {

    private String transactionId;
    private String identifier;
}
