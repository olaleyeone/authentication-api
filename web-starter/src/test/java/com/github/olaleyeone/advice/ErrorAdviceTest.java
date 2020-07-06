package com.github.olaleyeone.advice;

import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.github.olaleyeone.rest.exception.NotFoundException;
import com.github.olaleyeone.test.component.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ErrorAdviceTest extends ComponentTest {

    private ErrorAdvice errorAdvice;

    @BeforeEach
    void setUp() {
        errorAdvice = new ErrorAdvice();
    }

    @Test
    void handleNotFoundException() {
        ResponseEntity<?> responseEntity = errorAdvice.handle(new NotFoundException());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void handleNotFoundExceptionWithMessage() {
        String message = faker.lordOfTheRings().location();
        ResponseEntity<ApiResponse<String>> responseEntity = errorAdvice.handle(new NotFoundException(message));
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse<String> body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals(message, body.getMessage());
    }

    @Test
    void testHandleErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, faker.book().publisher());
        ResponseEntity<?> responseEntity = errorAdvice.handle(errorResponse);
        assertNotNull(responseEntity);
        assertEquals(errorResponse.getHttpStatus(), responseEntity.getStatusCode());
        assertEquals(errorResponse.getResponse(), responseEntity.getBody());
    }

    @Test
    void testHandleErrorResponseWithoutBody() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN);
        ResponseEntity<?> responseEntity = errorAdvice.handle(errorResponse);
        assertNotNull(responseEntity);
        assertEquals(errorResponse.getHttpStatus(), responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void handleBadRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ResponseEntity<ApiResponse<String>> responseEntity = errorAdvice.handleBadRequest(null, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void handleInvalidMethodArgument() {
        BindingResult bindingResult = new BindException(LocalDate.now(), "obj");
        bindingResult.reject(
                faker.lordOfTheRings().character(),
                faker.lordOfTheRings().location());
        bindingResult.rejectValue(
                "year",
                faker.superhero().name(),
                faker.superhero().descriptor());
        ResponseEntity<ApiResponse<Map<String, List<ErrorAdvice.ErrorMessage>>>> responseEntity = errorAdvice.handleInvalidMethodArgument(new MethodArgumentNotValidException(null, bindingResult));
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        Map<String, List<ErrorAdvice.ErrorMessage>> data = responseEntity.getBody().getData();
        assertNotNull(data);
        assertEquals(2, data.size());
    }

    @Test
    void handleConstraintViolation() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<MrBean>> violations = validator.validate(new MrBean());

        ResponseEntity<ApiResponse<Map<String, List<ErrorAdvice.ErrorMessage>>>> responseEntity = errorAdvice.handleConstraintViolation(new ConstraintViolationException(violations));
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        Map<String, List<ErrorAdvice.ErrorMessage>> data = responseEntity.getBody().getData();
        assertNotNull(data);
        assertEquals(3, data.size());
    }


    private static class MrBean {

        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @NotNull
        private LocalDate dob;
    }
}