package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.domain.auth.privilege.Privilege;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName.*;
import static com.jordanrobin.financial_erp.domain.auth.role.RoleName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService - loadUserByUsername()")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Retourne un UserDetails avec les rôles et privilèges quand l'utilisateur existe")
    void loadByUsername_shouldReturnUserDetails_whenUserExists() {
        String email = "user@test.com";
        Privilege p1 = Privilege.builder().name(PRIVILEGE_READ).build();
        Privilege p2 = Privilege.builder().name(USER_READ).build();
        Role role = Role.builder().name(TENANT_ADMIN).privileges(Set.of(p1, p2)).build();

        User user = User.builder()
            .email(email)
            .password("hash")
            .roles(Set.of(role))
            .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_TENANT_ADMIN", "PRIVILEGE_READ", "USER_READ");
    }

    @Test
    @DisplayName("Lève UsernameNotFoundException quand l'utilisateur est introuvable")
    void loadByUsername_shouldThrow_whenUserNotFound() {
        String email = "unknown@test.com";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("User not found.");
    }
}
