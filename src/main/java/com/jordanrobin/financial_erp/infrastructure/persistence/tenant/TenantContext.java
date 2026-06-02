package com.jordanrobin.financial_erp.infrastructure.persistence.tenant;

public class TenantContext {
    // TODO ajouter implements AutoCloseable ou
//    public static AutoCloseable setContext(String schemaName) {
//        setCurrentSchema(schemaName);
//        return TenantContext::clear; // La méthode close() exécutera clear()
//    }

    private static final ThreadLocal<String> CURRENT_SCHEMA = new ThreadLocal<>();

    public static void setCurrentSchema(String schema) {
        CURRENT_SCHEMA.set(schema);
    }

    public static String getCurrentSchema() {
        return CURRENT_SCHEMA.get();
    }

    public static void clear() {
        CURRENT_SCHEMA.remove();
    }
}
