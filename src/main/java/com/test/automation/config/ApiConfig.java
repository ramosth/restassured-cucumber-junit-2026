package com.test.automation.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Configuração comum a TODAS as requisições (o "DriverFactory" da API).
 *
 * RequestSpecification = um "molde" de requisição: tudo o que se repete em todas as
 * chamadas fica aqui, num lugar só.
 *   - URL base: vem do config.properties (chave base.uri) ou do -Dbase.uri=...
 *   - Content-Type JSON: o corpo que enviamos é JSON
 *   - Accept JSON: queremos receber JSON
 */
public final class ApiConfig {

    private static final String DEFAULT_BASE_URI = "http://localhost:3000";

    private ApiConfig() {
    }

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.uri", DEFAULT_BASE_URI))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}