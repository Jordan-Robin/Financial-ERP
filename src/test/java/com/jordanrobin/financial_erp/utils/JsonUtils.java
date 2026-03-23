package com.jordanrobin.financial_erp.utils;

import org.springframework.test.json.JsonContent;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtils {

    public static Function<JsonContent, Object> fromPath(String path) {
        return jsonContent -> assertThat(jsonContent).extractingPath(path).actual();
    }

}
