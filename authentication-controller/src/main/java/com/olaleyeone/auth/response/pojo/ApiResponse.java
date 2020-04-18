package com.olaleyeone.auth.response.pojo;

import lombok.Data;

@Data
public class ApiResponse<E> {

    private E data;
    private String message;
    private String messageCode;
}
