package com.jordanrobin.financial_erp.config;

import com.jordanrobin.financial_erp.domain.auth.privilege.Privilege;
import com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName.PRIVILEGE_READ;
import static com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName.ROLE_READ;
import static com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName.USER_MANAGE;
import static com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName.USER_READ;
import static com.jordanrobin.financial_erp.domain.auth.role.RoleName.TENANT_ADMIN;
import static com.jordanrobin.financial_erp.domain.auth.role.RoleName.VIEWER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RolePrivilegeInitializer")
class RolePrivilegeInitializerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RolePrivilegeInitializer initializer;

    @Nested
    @DisplayName("upsertRole()")
    class UpsertRole {

        @Test
        @DisplayName("Ne sauvegarde pas quand les privilèges sont inchangés")
        void shouldNotSave_whenPrivilegesUnchanged() {
            Privilege p = Privilege.builder().name(USER_READ).build();
            Role existingRole = Role.builder()
                .name(TENANT_ADMIN)
                .privileges(new HashSet<>(Set.of(p)))
                .build();

            given(roleService.findByName(TENANT_ADMIN)).willReturn(Optional.of(existingRole));

            initializer.upsertRole(TENANT_ADMIN, new HashSet<>(Set.of(p)));

            verify(roleService, never()).save(any());
        }

        @Test
        @DisplayName("Sauvegarde le rôle existant quand les privilèges ont changé")
        void shouldSave_whenPrivilegesChanged() {
            Privilege p1 = Privilege.builder().name(USER_READ).build();
            Privilege p2 = Privilege.builder().name(USER_MANAGE).build();
            Role existingRole = Role.builder()
                .name(VIEWER)
                .privileges(new HashSet<>(Set.of(p1)))
                .build();

            given(roleService.findByName(VIEWER)).willReturn(Optional.of(existingRole));

            initializer.upsertRole(VIEWER, new HashSet<>(Set.of(p1, p2)));

            verify(roleService, times(1)).save(existingRole);
        }

        @Test
        @DisplayName("Crée et sauvegarde un nouveau rôle quand il n'existe pas encore")
        void shouldCreateAndSave_whenRoleDoesNotExist() {
            Privilege p = Privilege.builder().name(USER_READ).build();
            Role newRole = Role.builder()
                .name(VIEWER)
                .privileges(new HashSet<>())
                .build();

            given(roleService.findByName(VIEWER)).willReturn(Optional.empty());
            given(roleService.createRole(VIEWER)).willReturn(newRole);

            initializer.upsertRole(VIEWER, new HashSet<>(Set.of(p)));

            verify(roleService, times(1)).save(any(Role.class));
        }
    }

    @Nested
    @DisplayName("allExcept()")
    class AllExcept {

        @Test
        @DisplayName("Retourne tous les privilèges quand aucune exclusion n'est spécifiée")
        void shouldReturnAllPrivileges_whenNoExclusion() {
            Map<PrivilegeName, Privilege> privileges = Map.of(
                USER_READ, Privilege.builder().name(USER_READ).build(),
                ROLE_READ, Privilege.builder().name(ROLE_READ).build()
            );

            Set<Privilege> result = initializer.allExcept(privileges);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Exclut les privilèges spécifiés du résultat")
        void shouldExcludeGivenPrivileges() {
            Map<PrivilegeName, Privilege> privileges = Map.of(
                USER_READ,      Privilege.builder().name(USER_READ).build(),
                ROLE_READ,      Privilege.builder().name(ROLE_READ).build(),
                PRIVILEGE_READ, Privilege.builder().name(PRIVILEGE_READ).build()
            );

            Set<Privilege> result = initializer.allExcept(privileges, PRIVILEGE_READ);

            assertThat(result)
                .extracting(Privilege::getName)
                .containsExactlyInAnyOrder(USER_READ, ROLE_READ);
        }
    }
}