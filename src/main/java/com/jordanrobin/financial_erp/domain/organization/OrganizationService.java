package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.domain.organization.models.OrganizationResponse;
import com.jordanrobin.financial_erp.domain.organization.mappers.OrganizationDomainMapper;
import com.jordanrobin.financial_erp.domain.organization.models.CreateOrganizationCommand;
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
    private final OrganizationDomainMapper organizationDomainMapper;

    public OrganizationResponse create(CreateOrganizationCommand request) {
        if (request.siren() != null && organizationRepository.existsBySiren(request.siren())) {
            throw new ResourceAlreadyExistsException(Organization.class.getSimpleName(), "siren", request.siren());
        }
        Organization organization = organizationDomainMapper.commandToEntity(request);
        return organizationDomainMapper.entityToResponse(organizationRepository.save(organization));
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getById(UUID id) {
        return organizationRepository.findById(id)
            .map(organizationDomainMapper::entityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException(Organization.class.getSimpleName(), id.toString()));
    }

}
