package com.jordanrobin.financial_erp.shared.exception.resource;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    private final String resource;
    private final String identifierName;
    private final String identifierValue;

    public ResourceAlreadyExistsException(String resource, String identifierName, String identifierValue) {
        super(resource + " - " + identifierName + " déjà existant pour la valeur '" + identifierValue + "'.");
        this.resource = resource;
        this.identifierName = identifierName;
        this.identifierValue = identifierValue;
    }
}
