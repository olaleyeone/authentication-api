package com.github.olaleyeone.advice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.github.olaleyeone.rest.exception.NotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handle(NotFoundException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<?> handle(ErrorResponse e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getResponse());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, List<ErrorMessage>>>> handleInvalidMethodArgument(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        ApiResponse<Map<String, List<ErrorMessage>>> apiResponse = new ApiResponse<>();

        Map<String, List<ErrorMessage>> data = bindingResult.getFieldErrors().stream()
                .map(it -> new ErrorMessage(
                        it.getField(),
                        it.getCode(),
                        it.getDefaultMessage()))
                .collect(Collectors.groupingBy(errorMessage -> errorMessage.path));
        data.putAll(bindingResult.getGlobalErrors().stream()
                .map(it -> new ErrorMessage(
                        it.getObjectName(),
                        it.getCode(),
                        it.getDefaultMessage()))
                .collect(Collectors.groupingBy(errorMessage -> errorMessage.path)));
        apiResponse.setData(data);

        apiResponse.setMessage(bindingResult.getAllErrors().iterator().next().getDefaultMessage());

        return new ResponseEntity<>(apiResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, List<ErrorMessage>>>> handleConstraintViolation(ConstraintViolationException ex) {

        ApiResponse<Map<String, List<ErrorMessage>>> apiResponse = new ApiResponse<>();

        apiResponse.setData(
                ex.getConstraintViolations().stream()
                        .map(it -> new ErrorMessage(
                                it.getPropertyPath().toString(),
                                it.getMessageTemplate(),
                                it.getMessage()))
                        .collect(Collectors.groupingBy(errorMessage -> errorMessage.path))
        );
        apiResponse.setMessage(ex.getConstraintViolations().iterator().next().getMessage());

        return new ResponseEntity<>(apiResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleConstraintViolation(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(request.getContentLength() == 0 ? "Missing request body" : "Could not parse request body");
        return new ResponseEntity<>(apiResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Data
    @RequiredArgsConstructor
    public static class ErrorMessage {

        @JsonIgnore
        private final String path;
        private final String code;
        private final String message;
    }
}
