package com.jordanrobin.financial_erp.shared.exception.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PrivilegeExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public static class PrivilegeNotFoundException extends RuntimeException {
        public PrivilegeNotFoundException(String identifier) {
            super("Privilège non trouvé : " + identifier);
        }
    }
}
