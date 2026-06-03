package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.domain.organization.dtos.OrganizationOutput;
import com.jordanrobin.financial_erp.domain.organization.mappers.OrganizationDomainMapper;
import com.jordanrobin.financial_erp.domain.organization.dtos.CreateOrganizationCommand;
import com.jordanrobin.financial_erp.shared.exception.resource.ResourceAlreadyExistsException;
import com.jordanrobin.financial_erp.shared.exception.resource.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationDomainMapper organizationDomainMapper;

    @Transactional
    public OrganizationOutput create(CreateOrganizationCommand command) {
        if (command.siren() != null && organizationRepository.existsBySiren(command.siren())) {
            throw new ResourceAlreadyExistsException(Organization.class.getSimpleName(), "siren", command.siren());
        }
        Organization organization = organizationDomainMapper.commandToEntity(command);
        return organizationDomainMapper.entityToOutput(organizationRepository.save(organization));
    }

    public OrganizationOutput getById(UUID id) {
        return organizationRepository.findById(id)
            .map(organizationDomainMapper::entityToOutput)
            .orElseThrow(() -> new ResourceNotFoundException(Organization.class.getSimpleName(), "id", id.toString()));
    }

}
