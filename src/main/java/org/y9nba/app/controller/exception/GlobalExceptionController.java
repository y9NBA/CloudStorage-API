package org.y9nba.app.controller.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.exception.AbstractException;

@Hidden
@ControllerAdvice
@Slf4j
public class GlobalExceptionController {
    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> catchAbstractException(AbstractException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), e.getStatusCode());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response> catchNoResourceFoundException(NoResourceFoundException e) {
        return new ResponseEntity<>(new Response(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> catchException(Exception e) {
        return new ResponseEntity<>(new Response(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
