package com.jordanrobin.financial_erp.api.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jordanrobin.financial_erp.api.admin.dtos.CreateTenantRequest;
import com.jordanrobin.financial_erp.api.admin.dtos.CreateTenantResponse;
import com.jordanrobin.financial_erp.api.admin.mappers.AdminTenantApiMapper;
import com.jordanrobin.financial_erp.api.shared.openapi.OpenApiCreateResource;
import com.jordanrobin.financial_erp.domain.tenant.TenantService;
import com.jordanrobin.financial_erp.domain.tenant.dtos.CreateTenantOutput;
import com.jordanrobin.financial_erp.shared.constants.ApiRoutes;
import com.jordanrobin.financial_erp.api.shared.utils.ResourceUriFactory;

@Tag(name = "Administration - Tenants", description = "Gestion des tenants (Super-Admin seulement)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.ADMIN + "/tenants")
public class AdminTenantController {

    private final AdminTenantApiMapper tenantApiMapper;
    private final TenantService tenantService;

    @OpenApiCreateResource
    @PostMapping
    public ResponseEntity<CreateTenantResponse> createTenant(@RequestBody @Valid CreateTenantRequest request) {
        CreateTenantOutput tenantOutput = tenantService.createTenant(tenantApiMapper.requestToCommand(request));
        CreateTenantResponse response = tenantApiMapper.outputToResponse(tenantOutput);
        return ResponseEntity.created(ResourceUriFactory.create(response.tenantId())).body(response);
    }
}
