package com.jordanrobin.financial_erp.domain.auth.invitationtoken.dtos;

import com.jordanrobin.financial_erp.domain.auth.invitationtoken.InvitationToken;
import lombok.Builder;

@Builder
public record CreateTokenResult(
    InvitationToken invitationToken,
    String rawToken
) {}
