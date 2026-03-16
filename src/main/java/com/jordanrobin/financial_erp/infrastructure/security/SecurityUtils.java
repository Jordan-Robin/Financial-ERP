package com.jordanrobin.financial_erp.infrastructure.security;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.shared.exception.ErrorResponse;
import com.jordanrobin.financial_erp.shared.exception.api.SecurityExceptions;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.UUID;

@Component
@NullMarked
public class SecurityUtils {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (
            auth == null
            || !auth.isAuthenticated()
            || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)
        ) {
            throw new SecurityExceptions.UnauthorizedException("No authenticated user found.");
        }

        return ((CustomUserDetails) Objects.requireNonNull(auth.getPrincipal())).getUser();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    @EnableConfigurationProperties(RsaKeyProperties.class)
    @RequiredArgsConstructor
    public static class SecurityConfig {

        private final CustomUserDetailsService userDetailsService;
        private final RsaKeyProperties rsaKeys;

        private static final String[] SWAGGER_PATHS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/error"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(SWAGGER_PATHS).permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                    .accessDeniedHandler((request, response, ex) -> {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Accès interdit : vous n'avez pas les droits nécessaires")
                        ));
                    })
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.decoder(jwtDecoder()))
                    .authenticationEntryPoint((request, response, ex) -> {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write(objectMapper.writeValueAsString(
                            new ErrorResponse("Authentification requise : token invalide ou manquant")
                        ));
                    })
                );
            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
            provider.setPasswordEncoder(passwordEncoder());
            return new ProviderManager(provider);
        }

        @Bean
        public JwtEncoder jwtEncoder() {
            RSAKey jwk = new RSAKey.Builder(rsaKeys.publicKey())
                .privateKey(rsaKeys.privateKey())
                .build();
            return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
        }

        @Bean
        public JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    }

    @ConfigurationProperties(prefix = "rsa")
    public static record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {}
}
