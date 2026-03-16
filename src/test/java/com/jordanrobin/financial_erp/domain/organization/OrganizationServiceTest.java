package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.api.organization.dtos.CreateOrganizationRequest;
import com.jordanrobin.financial_erp.api.organization.dtos.OrganizationResponse;
import com.jordanrobin.financial_erp.api.organization.mappers.OrganizationMapper;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceAlreadyExistsException;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationService")
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    private Organization organization;
    private CreateOrganizationRequest request;
    private OrganizationResponse response;
    private final String siren = "123456789";

    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        request = CreateOrganizationRequest.builder()
            .name("Test Company")
            .siren(siren)
            .build();

        organization = Organization.builder()
            .name("Test Company")
            .siren(siren)
            .build();

        response = OrganizationResponse.builder()
            .name("Test Company")
            .siren(siren)
            .build();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Doit créer et retourner l'organisation quand le SIREN n'existe pas")
        void shouldReturnOrganizationResponse_whenSuccess() {

            when(organizationRepository.existsBySiren(siren)).thenReturn(false);
            when(organizationMapper.toEntity(request)).thenReturn(organization);
            when(organizationRepository.save(organization)).thenReturn(organization);
            when(organizationMapper.toResponse(organization)).thenReturn(response);

            OrganizationResponse result = organizationService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.siren()).isEqualTo(request.siren());
            assertThat(result.name()).isEqualTo(request.name());
            verify(organizationRepository, times(1)).existsBySiren(request.siren());
            verify(organizationRepository, times(1)).save(organization);
        }

        @Test
        @DisplayName("Doit lever une ResourceAlreadyExistsException si le siren existe déjà")
        void shouldRaiseResourceAlreadyExistsException_whenSirenAlreadyExists() {

            when(organizationRepository.existsBySiren(siren)).thenReturn(true);

            assertThatThrownBy(() -> organizationService.create(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining(request.siren());
            verify(organizationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Doit créer l'organisation sans vérifier le SIREN s'il est null")
        void shouldCreate_whenSirenIsNull() {
            CreateOrganizationRequest nullSirenRequest = CreateOrganizationRequest.builder()
                .name("Company Without Siren")
                .siren(null)
                .build();

            when(organizationMapper.toEntity(nullSirenRequest)).thenReturn(organization);
            when(organizationRepository.save(organization)).thenReturn(organization);
            when(organizationMapper.toResponse(organization)).thenReturn(response);

            organizationService.create(nullSirenRequest);

            verify(organizationRepository, never()).existsBySiren(any());
            verify(organizationRepository).save(any());
        }

    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("Doit renvoyer une OrganizationResponse si elle existe")
        void shouldReturnOrganizationResponse_whenExists() {
            UUID id = UUID.randomUUID();
            organization.setId(id);

            OrganizationResponse localResponse = OrganizationResponse.builder()
                .id(id)
                .siren(siren)
                .build();

            when(organizationRepository.findById(id)).thenReturn(Optional.ofNullable(organization));
            when(organizationMapper.toResponse(organization)).thenReturn(localResponse);

            OrganizationResponse result = organizationService.getById(id);

            assertThat(result.id()).isEqualTo(id);
            verify(organizationRepository).findById(id);
        }

        @Test
        @DisplayName("Doit lever une ResourceNotFoundException si l'id n'existe pas")
        void shouldRaiseResourceNotFoundException_whenOrganizationNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(organizationRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> organizationService.getById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
            verify(organizationMapper, never()).toResponse(any());
        }

    }

}
