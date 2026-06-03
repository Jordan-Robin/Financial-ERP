package com.jordanrobin.financial_erp.infrastructure.persistence.tenant;

public class TenantContext {

    private TenantContext() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

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
