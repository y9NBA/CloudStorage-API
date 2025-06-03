package org.y9nba.app.controller.advice;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.exception.web.AbstractException;
import org.y9nba.app.exception.web.token.TokenNotValidException;

@Hidden
@ControllerAdvice
@Slf4j
public class GlobalExceptionController {
    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> catchAbstractException(AbstractException e) {
        return new ResponseEntity<>(new ErrorResponse(e), e.getStatusCode());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> catchNoResourceFoundException(NoResourceFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getMessage(),
                        e.getStatusCode().value()
                ),
                e.getStatusCode()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> catchIllegalArgumentException(BadCredentialsException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> catchAuthenticationException(InternalAuthenticationServiceException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> catchDisabledException() {
        return new ResponseEntity<>(
                new ErrorResponse(
                        "Аккаунт заблокирован. Попробуйте восстановить аккаунт.",
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> catchLockedException(LockedException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getMessage(),
                        HttpStatus.FORBIDDEN.value()
                )
                , HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> catchAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getMessage(),
                        HttpStatus.NO_CONTENT.value()
                ),
                HttpStatus.NO_CONTENT
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> catchAuthorizationDeniedException() {
        return new ResponseEntity<>(
                new ErrorResponse(
                        "Нет доступа",
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> catchMethodArgumentTypeMismatchException() {
        return new ResponseEntity<>(
                new ErrorResponse(
                        "Некорректно переданы данные",
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> catchException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
