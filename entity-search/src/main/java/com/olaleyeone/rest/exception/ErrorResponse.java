package com.olaleyeone.rest.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends RuntimeException {

    private final HttpStatus httpStatus;
    private Object response;

    public ErrorResponse(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ErrorResponse(HttpStatus httpStatus, Object response) {
        this.httpStatus = httpStatus;
        this.response = response;
    }
}
