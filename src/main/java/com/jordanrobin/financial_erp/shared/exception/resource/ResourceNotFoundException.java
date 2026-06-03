package com.jordanrobin.financial_erp.shared.exception.resource;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String identifierName;
    private final String identifierValue;

    public ResourceNotFoundException(String resource,String identifierName, String identifierValue) {
        super(resource + " - " + identifierName + " non trouvé pour la valeur '" + identifierValue + "'.");
        this.resource = resource;
        this.identifierName = identifierName;
        this.identifierValue = identifierValue;
    }
}
