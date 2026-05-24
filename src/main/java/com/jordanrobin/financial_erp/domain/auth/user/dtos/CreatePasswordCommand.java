package com.jordanrobin.financial_erp.domain.auth.user.dtos;

import lombok.Builder;

@Builder
public record CreatePasswordCommand(
    String invitationToken,
    String password
) {}
