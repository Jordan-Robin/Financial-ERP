package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.api.organization.dtos.CreateOrganizationRequest;
import com.jordanrobin.financial_erp.api.organization.dtos.OrganizationResponse;
import com.jordanrobin.financial_erp.api.organization.mappers.OrganizationMapper;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceAlreadyExistsException;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationResponse create(CreateOrganizationRequest request) {
        if (request.siren() != null && organizationRepository.existsBySiren(request.siren())) {
            throw new ResourceAlreadyExistsException(Organization.class.getSimpleName(), "siren", request.siren());
        }
        Organization organization = organizationMapper.toEntity(request);
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getById(UUID id) {
        return organizationRepository.findById(id)
            .map(organizationMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(Organization.class.getSimpleName(), id.toString()));
    }

}
