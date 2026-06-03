package com.jordanrobin.financial_erp.shared.constants;

// TODO variabiliser
public class ApiRoutes {
    public static final String API_V1 = "/api/v1";

    public static final String ADMIN = API_V1 + "/admin";
    public static final String AUTH = API_V1 + "/auth";

    private ApiRoutes() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }
}
