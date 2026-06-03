package com.jordanrobin.financial_erp.api.shared.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public final class ResourceUriFactory {

    private ResourceUriFactory() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static URI create(Object resourceId) {
        return ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(resourceId)
            .toUri();
    }
}
