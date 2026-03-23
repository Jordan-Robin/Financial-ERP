package com.jordanrobin.financial_erp.domain.organization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LegalStatus {
    // Sociétés Commerciales
    SAS("Société par Actions Simplifiée"),
    SASU("Société par Actions Simplifiée Unipersonnelle"),
    SARL("Société à Responsabilité Limitée"),
    EURL("Entreprise Unipersonnelle à Responsabilité Limitée"),
    SA("Société Anonyme"),
    SNC("Société en Nom Collectif"),
    SCA("Société en Commandite par Actions"),
    SCS("Société en Commandite Simple"),

    // Entreprises Individuelles
    EI("Entreprise Individuelle (incl. Micro-entreprise)"),
    EIRL("Entreprise Individuelle à Responsabilité Limitée"),

    // Secteur Non-Marchand (ESS)
    ASSOCIATION_LOI_1901("Association Loi 1901"),
    ASSOCIATION_UTILITE_PUBLIQUE("Association reconnue d'utilité publique"),
    FONDATION("Fondation"),
    COOPERATIVE("Société Coopérative (SCOP/SCIC)"),

    // Organisations Publiques
    COLLECTIVITE_TERRITORIALE("Collectivité Territoriale"),
    ETABLISSEMENT_PUBLIC("Établissement Public"),
    ADMINISTRATION_ETAT("Administration de l'État"),

    // Professions Libérales et Sociétés Civiles
    SELARL("Société d'Exercice Libéral à Responsabilité Limitée"),
    SELAS("Société d'Exercice Libéral par Actions Simplifiée"),
    SCP("Société Civile Professionnelle"),
    SCI("Société Civile Immobilière"),
    SCM("Société Civile de Moyens"),

    // International et Divers
    FOREIGN_COMPANY("Société étrangère"),
    OTHER("Autre type d'organisation");

    private final String label;
}
