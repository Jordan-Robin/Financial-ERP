package com.jordanrobin.financial_erp.api.organization;

import com.jordanrobin.financial_erp.api.organization.mappers.OrganizationApiMapperImpl;
import com.jordanrobin.financial_erp.base.BaseControllerTest;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.organization.Organization;
import com.jordanrobin.financial_erp.domain.organization.OrganizationService;
import com.jordanrobin.financial_erp.domain.organization.models.CreateOrganizationCommand;
import com.jordanrobin.financial_erp.infrastructure.security.SecurityConfig;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceAlreadyExistsException;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static com.jordanrobin.financial_erp.fixtures.JwtTokenFixtures.createTenantAdminToken;
import static com.jordanrobin.financial_erp.fixtures.OrganizationFixtures.createOrganizationRequestBuilder;
import static com.jordanrobin.financial_erp.fixtures.OrganizationFixtures.organizationResponseBuilder;
import static com.jordanrobin.financial_erp.utils.JsonUtils.fromPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(OrganizationController.class)
@Import({SecurityConfig.class, OrganizationApiMapperImpl.class})
@DisplayName("OrganizationController")
public class OrganizationControllerTest extends BaseControllerTest {

    @MockitoBean
    private OrganizationService organizationService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Nested
    @DisplayName("POST /api/organizations - create()")
    class Create {

        @Test
        @DisplayName("Succès : retourne 201")
        void shouldReturn201_whenValid() {
            var request = createOrganizationRequestBuilder().build();
            var response = organizationResponseBuilder().build();

            when(organizationService.create(any(CreateOrganizationCommand.class))).thenReturn(response);

            var result = post("/api/organizations", createTenantAdminToken(), request);

            assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .returns("123456789", fromPath("$.siren"));
        }

        @Test
        @DisplayName("Erreur 400 : siren invalide")
        void shouldReturn400_whenSirenInvalid() {
            var request = createOrganizationRequestBuilder().siren("invalid-siren").build();

            var result = post("/api/organizations", createTenantAdminToken(), request);

            assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
            verifyNoInteractions(organizationService);
        }

        @Test
        @DisplayName("Erreur 409 : siren déjà existant")
        void shouldReturn409_whenSirenAlreadyExists() {
            var request = createOrganizationRequestBuilder().build();
            when(organizationService.create(any(CreateOrganizationCommand.class)))
                .thenThrow(new ResourceAlreadyExistsException(Organization.class.getSimpleName(), "siren", request.siren()));

            var result = post("/api/organizations", createTenantAdminToken(), request);

            assertThat(result).hasStatus(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("GET /api/organizations/{id} - getById()")
    class GetById {

        @Test
        @DisplayName("Succès : retourne 200")
        void shouldReturn200_whenSuccess() {
            var response = organizationResponseBuilder().build();
            when(organizationService.getById(any(UUID.class))).thenReturn(response);

            var result = get("/api/organizations/{id}", createTenantAdminToken(), UUID.randomUUID());

            assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .returns("Organization", fromPath("$.name"));
        }

        @Test
        @DisplayName("Erreur 400 : mauvais format de UUID")
        void shouldReturn400_whenInvalidUUID() {
            var result = get("/api/organizations/{id}", createTenantAdminToken(), "invalid-UUID");
            assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Erreur 404 : UUID inexistant en base")
        void shouldReturn404_whenUUIDNotFound() {
            when(organizationService.getById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Organization", "id"));
            var result = get("/api/organizations/{id}", createTenantAdminToken(), UUID.randomUUID());

            assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        }
    }
}
