package com.jordanrobin.financial_erp.shared.exception.domain.resources;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {

    private final String resource;
    private final String identifierName;
    private final String identifier;

    public ResourceAlreadyExistsException(String resource, String identifierName, String identifier) {
        super(resource + " - " + identifierName + " déjà existant pour " + identifier + ".");
        this.resource = resource;
        this.identifierName = identifierName;
        this.identifier = identifier;
    }
}
