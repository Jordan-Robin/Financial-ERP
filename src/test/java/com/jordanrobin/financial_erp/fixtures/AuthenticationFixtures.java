package com.jordanrobin.financial_erp.fixtures;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.api.auth.dtos.LoginRequest;
import com.jordanrobin.financial_erp.api.auth.dtos.RefreshRequest;
import com.jordanrobin.financial_erp.domain.auth.token.model.TokenPair;

public class AuthenticationFixtures {

    public static AuthResponse.AuthResponseBuilder authResponseBuilder() {
        return AuthResponse.builder()
            .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lI" +
                "iwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .refreshToken("def456-ghi789-jkl012-mno345-pqr678")
            .type("Bearer")
            .expiresIn(3600L);
    }

    public static LoginRequest.LoginRequestBuilder loginRequestBuilder() {
        return LoginRequest.builder().email("email@mail.com").password("secret123");
    }

    public static RefreshRequest.RefreshRequestBuilder refreshRequestBuilder() {
        return RefreshRequest.builder().refreshToken("def456-ghi789-jkl012-mno345-pqr678");
    }

    public static TokenPair.TokenPairBuilder createTokenPair() {
        return TokenPair.builder()
            .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lI" +
                "iwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            .refreshToken("def456-ghi789-jkl012-mno345-pqr678");
    }
}
