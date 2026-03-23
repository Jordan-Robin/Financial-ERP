package com.jordanrobin.financial_erp.api.auth.mappers;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.domain.auth.token.model.TokenPair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthApiMapper {

    @Mapping(target = "accessToken", source = "tokenPair.accessToken")
    @Mapping(target = "refreshToken", source = "tokenPair.refreshToken")
    @Mapping(target = "type", source = "tokenType")
    @Mapping(target = "expiresIn", source = "expiry")
    AuthResponse tokenPairToAuthResponse(TokenPair tokenPair, String tokenType, long expiry);
}
