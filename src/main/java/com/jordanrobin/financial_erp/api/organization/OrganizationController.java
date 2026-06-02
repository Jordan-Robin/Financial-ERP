package com.jordanrobin.financial_erp.api.organization;

import com.jordanrobin.financial_erp.api.organization.dtos.CreateOrganizationRequest;
import com.jordanrobin.financial_erp.domain.organization.dtos.OrganizationOutput;
import com.jordanrobin.financial_erp.api.organization.mappers.OrganizationApiMapper;
import com.jordanrobin.financial_erp.domain.organization.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Organizations", description = "Gestion des organisations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationApiMapper organizationApiMapper;

    @Operation(summary = "Créer une organisation")
    @ApiResponse(responseCode = "201", description = "Organisation créée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Organisation déjà existante avec ce Siren")
    @PostMapping
    public ResponseEntity<OrganizationOutput> create(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationOutput response = organizationService.create(organizationApiMapper.dtoToCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Récupérer une organisation avec son ID.")
    @ApiResponse(responseCode = "200", description = "Organisation trouvée")
    @ApiResponse(responseCode = "404", description = "Organisation non trouvée")
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationOutput> getById(@PathVariable UUID id) {
        OrganizationOutput response = organizationService.getById(id);
        return ResponseEntity.ok(response);
    }

}
