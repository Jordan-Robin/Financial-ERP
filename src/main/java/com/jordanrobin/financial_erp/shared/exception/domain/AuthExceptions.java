package com.jordanrobin.financial_erp.shared.exception.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AuthExceptions {

    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public static class InvalidRefreshTokenException extends RuntimeException {
        public InvalidRefreshTokenException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public static class TokenRevokedException extends RuntimeException {
        public TokenRevokedException() {
            super("Ce jeton a été révoqué et ne peut plus être utilisé.");
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
