package com.jordanrobin.financial_erp.base;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public abstract class BaseControllerTest {

    @Autowired
    protected MockMvcTester mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private MvcTestResult internalPost(String uri, @Nullable Jwt jwt, Object entity) {
        MockMvcTester.MockMvcRequestBuilder request = mvc.post().uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(entity));

        if (jwt != null) {
            request.with(jwt().jwt(jwt));
        }

        return request.exchange();
    }

    protected MvcTestResult post(String uri, Jwt jwt, Object entity) {
        return internalPost(uri, jwt, entity);
    }

    protected MvcTestResult postUnauthenticated(String uri, Object entity) {
        return internalPost(uri, null, entity);
    }

    private MvcTestResult internalGet(String uri, @Nullable Jwt jwt, Object... uriVariables) {
        MockMvcTester.MockMvcRequestBuilder request = mvc.get().uri(uri, uriVariables)
            .accept(MediaType.APPLICATION_JSON);

        if (jwt != null) {
            request.with(jwt().jwt(jwt));
        }

        return request.exchange();
    }

    protected MvcTestResult get(String uri, Jwt jwt, Object... uriVariables) {
        return internalGet(uri, jwt, uriVariables);
    }

    protected MvcTestResult getUnauthenticated(String uri, Object... uriVariables) {
        return internalGet(uri, null, uriVariables);
    }

}
