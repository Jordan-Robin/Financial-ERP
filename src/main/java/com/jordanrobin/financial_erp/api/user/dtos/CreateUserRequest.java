package com.jordanrobin.financial_erp.api.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateUserRequest(
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    String password,

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    String firstName,

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    String lastName,

    @NotEmpty(message = "Au moins un rôle est requis")
    Set<String> roles
) {}
