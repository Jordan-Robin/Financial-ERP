package com.jordanrobin.financial_erp.shared.exception.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserExceptions {

    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String email) {
            super("L'email " + email + " est déjà utilisé.");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String identifier) {
            super("Utilisateur non trouvé : " + identifier);
        }
    }
}
