package com.olaleyeone.auth.exception;

import com.olaleyeone.auth.response.pojo.ApiResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper=false)
public class ErrorResponse extends RuntimeException {

    private final HttpStatus httpStatus;
    private ApiResponse<?> response;

    public ErrorResponse(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
