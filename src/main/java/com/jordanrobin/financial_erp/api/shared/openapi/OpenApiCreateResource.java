package com.jordanrobin.financial_erp.api.shared.openapi;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "201",
    description = "Ressource créée avec succès"
)
@ApiResponse(
    responseCode = "400",
    description = "Données d'entrée invalides ou corrompues",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
@ApiResponse(
    responseCode = "401",
    description = "Authentification requise",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
@ApiResponse(
    responseCode = "403",
    description = "Droits insuffisants (Privilèges requis non accordés)",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
@ApiResponse(
    responseCode = "409",
    description = "Conflit : La ressource existe déjà",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
public @interface OpenApiCreateResource {}
