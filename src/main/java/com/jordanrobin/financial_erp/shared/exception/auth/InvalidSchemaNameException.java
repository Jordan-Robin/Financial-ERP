package com.jordanrobin.financial_erp.shared.exception.auth;

public class InvalidSchemaNameException extends RuntimeException {
    public static final String SUGGESTION = "Le nom du schema (tenantSlug) ne doit contenir que des lettres " +
        "minuscules, chiffres et underscores.";

    public InvalidSchemaNameException(String schemaName) {
        super("Nom de schema invalide : " + schemaName);
    }
}
