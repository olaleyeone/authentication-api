package com.github.olaleyeone.sms.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Sms {

    private String from;
    private String to;
    private String message;
}
