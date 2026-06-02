package com.jordanrobin.financial_erp.domain.tenant;

import com.jordanrobin.financial_erp.domain.organization.OrganizationService;
import com.jordanrobin.financial_erp.domain.organization.dtos.CreateOrganizationCommand;
import com.jordanrobin.financial_erp.domain.organization.dtos.OrganizationOutput;
import com.jordanrobin.financial_erp.domain.tenant.dtos.CreateTenantCommand;
import com.jordanrobin.financial_erp.domain.tenant.dtos.CreateTenantOutput;
import com.jordanrobin.financial_erp.infrastructure.persistence.flyway.TenantFlywayMigrator;
import com.jordanrobin.financial_erp.infrastructure.persistence.tenant.TenantContext;
import com.jordanrobin.financial_erp.shared.exception.auth.InvalidSchemaNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantFlywayMigrator tenantFlywayMigrator;
    private final TenantRepository tenantRepository;
    private final OrganizationService organizationService;

    // TODO gestion du cas d'échec partiel (vérification que tenant pas déjà créé au autre solution)
    public CreateTenantOutput createTenant(CreateTenantCommand tenantCommand) {
        if (!tenantCommand.tenantSlug().matches("^[a-z0-9_]+$")) {
            throw new InvalidSchemaNameException(tenantCommand.tenantSlug());
        }
        String schemaName = "tenant_" + tenantCommand.tenantSlug();

        // 1. Créer public.tenants
        Tenant tenant = Tenant.builder()
            .slug(tenantCommand.tenantSlug())
            .schemaName(schemaName)
            .status(TenantStatus.ACTIVE)
            .build();
        tenantRepository.save(tenant);

        // 2. Créer le schema + jouer les migrations Flyway
        tenantFlywayMigrator.migrate(schemaName);

        // 3. Créer tenant_x.organizations
        try {
            TenantContext.setCurrentSchema(schemaName);
            CreateOrganizationCommand organizationCommand = CreateOrganizationCommand.builder()
                .name(tenantCommand.organizationName())
                .tenantId(tenant.getId())
                .build();
            OrganizationOutput organizationOutput = organizationService.create(organizationCommand);

            return CreateTenantOutput.builder()
                .tenantId(tenant.getId())
                .slug(tenant.getSlug())
                .schemaName(tenant.getSchemaName())
                .organizationId(organizationOutput.id())
                .build();
        } finally {
            TenantContext.clear();
        }
    }
}
