package com.jordanrobin.financial_erp.infrastructure.persistence.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String schema = TenantContext.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
