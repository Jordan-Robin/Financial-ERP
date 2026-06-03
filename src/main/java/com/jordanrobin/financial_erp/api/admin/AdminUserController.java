package com.jordanrobin.financial_erp.api.admin;

import com.jordanrobin.financial_erp.api.admin.dtos.SuperUserResponse;
import com.jordanrobin.financial_erp.api.shared.utils.ResourceUriFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jordanrobin.financial_erp.api.shared.openapi.OpenApiCreateResource;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.UserOutput;
import com.jordanrobin.financial_erp.shared.constants.ApiRoutes;
import com.jordanrobin.financial_erp.api.admin.dtos.CreateSuperUserRequest;
import com.jordanrobin.financial_erp.api.admin.mappers.SuperUserApiMapper;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;

@Tag(name = "Administration - Super-Admin", description = "Gestion des super admin (Super-Admin seulement)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.ADMIN + "/users")
public class AdminUserController {

    private final UserService userService;
    private final SuperUserApiMapper superUserApiMapper;

    @OpenApiCreateResource
    @PostMapping
    public ResponseEntity<SuperUserResponse> createSuperUser(@Valid @RequestBody CreateSuperUserRequest request) {
        UserOutput userOutput = userService.createSuperUser(superUserApiMapper.requestToCommand(request));
        SuperUserResponse superUserResponse = superUserApiMapper.outputToResponse(userOutput);
        return ResponseEntity.created(ResourceUriFactory.create(superUserResponse.id())).body(superUserResponse);
    }
}
