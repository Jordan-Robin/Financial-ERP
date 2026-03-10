package com.jordanrobin.financial_erp.shared.exception.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RoleExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String identifier) {
            super("Rôle non trouvé : " + identifier);
        }
    }

}
