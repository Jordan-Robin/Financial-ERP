package com.jordanrobin.financial_erp.domain.auth.privilege;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrivilegeName {

    // --- GESTION DES UTILISATEURS (USER) ---
    USER_READ("Voir la liste et le détail des utilisateurs"),
    USER_CREATE("Créer de nouveaux utilisateurs"),
    USER_UPDATE("Modifier les informations des utilisateurs"),
    USER_DELETE("Supprimer un utilisateur du système"),

    // --- GESTION DES RÔLES (ROLE) ---
    ROLE_READ("Consulter les rôles existants"),
    ROLE_CREATE("Définir de nouveaux rôles métier"),
    ROLE_UPDATE("Modifier les permissions associées à un rôle"),
    ROLE_DELETE("Supprimer un rôle"),

    // --- GESTION DES PRIVILÈGES (PRIVILEGE) ---
    PRIVILEGE_READ("Lister les privilèges techniques du système"),

    // --- ADMINISTRATION & TENANT ---
    USER_MANAGE("Gérer les utilisateurs et leurs accès"),
    ROLE_MANAGE("Gérer les rôles de l'organisation"),
    TENANT_SETTINGS_WRITE("Gérer les paramètres du client");

    private final String description;
}
