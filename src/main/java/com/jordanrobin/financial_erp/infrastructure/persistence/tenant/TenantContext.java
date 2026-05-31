package com.jordanrobin.financial_erp.infrastructure.persistence.tenant;

public class TenantContext {

    private static final ThreadLocal<String> currentSchema = new ThreadLocal<>();

    public static void setCurrentSchema(String schema) {
        currentSchema.set(schema);
    }

    public static String getCurrentSchema() {
        return currentSchema.get();
    }

    public static void clear() {
        currentSchema.remove();
    }
}
