package com.jordanrobin.financial_erp.api.admin;

import com.jordanrobin.financial_erp.domain.auth.user.dtos.UserOutput;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.shared.constants.ApiRoutes;
import com.jordanrobin.financial_erp.api.admin.dtos.CreateSuperUserRequest;
import com.jordanrobin.financial_erp.api.admin.mappers.SuperUserApiMapper;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.ADMIN + "/users")
public class AdminUserController {

    private final UserService userService;
    private final SuperUserApiMapper superUserApiMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createSuperUser(@Valid @RequestBody CreateSuperUserRequest request) {
        UserOutput userOutput = userService.createSuperUser(superUserApiMapper.requestToCommand(request));
        // TODO renvoyer Location
        return ResponseEntity.status(HttpStatus.CREATED).body(superUserApiMapper.outputToResponse(userOutput));
    }
}
