package com.jordanrobin.financial_erp.shared.exception.domain.resources;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String identifier;

    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " non trouvé avec l'identifiant " + identifier + ".");
        this.resource = resource;
        this.identifier = identifier;
    }

}
