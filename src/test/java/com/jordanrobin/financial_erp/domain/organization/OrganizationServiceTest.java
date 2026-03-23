package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.domain.organization.mappers.OrganizationDomainMapper;
import com.jordanrobin.financial_erp.domain.organization.models.OrganizationResponse;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceAlreadyExistsException;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.jordanrobin.financial_erp.fixtures.OrganizationFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationService")
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationDomainMapper organizationDomainMapper;

    @InjectMocks
    private OrganizationService organizationService;

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Succès : crée une organisation")
        void shouldReturnOrganizationResponse_whenSuccess() {
            // Arrange
            var request = createOrganizationCommandBuilder().build();
            var entity = createOrganization().build();
            var response = organizationResponseBuilder().build();

            when(organizationRepository.existsBySiren(request.siren())).thenReturn(false);
            when(organizationDomainMapper.commandToEntity(request)).thenReturn(entity);
            when(organizationRepository.save(entity)).thenReturn(entity);
            when(organizationDomainMapper.entityToResponse(entity)).thenReturn(response);

            // Act
            OrganizationResponse result = organizationService.create(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.siren()).isEqualTo(request.siren());
            verify(organizationRepository).save(entity);
        }

        @Test
        @DisplayName("Succès y compris si le SIREN est null")
        void shouldCreate_whenSirenIsNull() {
            // Arrange
            var request = createOrganizationCommandBuilder().siren(null).build();
            var entity = createOrganization().siren(null).build();
            var response = organizationResponseBuilder().siren(null).build();

            when(organizationDomainMapper.commandToEntity(request)).thenReturn(entity);
            when(organizationRepository.save(entity)).thenReturn(entity);
            when(organizationDomainMapper.entityToResponse(entity)).thenReturn(response);

            // Act
            organizationService.create(request);

            // Assert
            verify(organizationRepository, never()).existsBySiren(any());
            verify(organizationRepository).save(any());
        }

        @Test
        @DisplayName("Erreur ResourceAlreadyExistsException : SIREN déjà existant")
        void shouldRaiseResourceAlreadyExistsException_whenSirenAlreadyExists() {
            // Arrange
            var request = createOrganizationCommandBuilder().build();
            when(organizationRepository.existsBySiren(request.siren())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> organizationService.create(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining(request.siren());
            verify(organizationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("Succès : retourne l'organisation")
        void shouldReturnOrganizationResponse_whenExists() {
            // Arrange
            UUID id = UUID.randomUUID();
            var entity = createOrganization().build();
            entity.setId(id);
            var response = organizationResponseBuilder().id(id).build();

            when(organizationRepository.findById(id)).thenReturn(Optional.of(entity));
            when(organizationDomainMapper.entityToResponse(entity)).thenReturn(response);

            // Act
            OrganizationResponse result = organizationService.getById(id);

            // Assert
            assertThat(result.id()).isEqualTo(id);
            verify(organizationRepository).findById(id);
        }

        @Test
        @DisplayName("Erreur ResourceNotFoundException : organization inexistante")
        void shouldRaiseResourceNotFoundException_whenOrganizationNotFound() {
            // Arrange
            UUID unknownId = UUID.randomUUID();
            when(organizationRepository.findById(unknownId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> organizationService.getById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
            verify(organizationDomainMapper, never()).entityToResponse(any());
        }
    }
}
