package com.test.automation.clients;

import static io.restassured.RestAssured.given;

import com.test.automation.config.ApiConfig;

import io.restassured.specification.RequestSpecification;

/**
 * Base de todos os Clients (o "BasePage" da API).
 *
 * Monta o começo de qualquer requisição: o molde do ApiConfig e, se houver,
 * o token no cabeçalho Authorization.
 *
 * Cada Client (ProductClient, UserClient...) herda daqui e só se preocupa
 * com as SUAS rotas.
 */
public abstract class BaseClient {

    /** Requisição sem token (rotas públicas, como GET /produtos). */
    protected RequestSpecification request() {
        return request(null);
    }

    /**
     * Requisição com token. Se o token for null, a requisição vai sem o cabeçalho:
     * é assim que testamos o "sem token" (401) usando o mesmo método.
     */
    protected RequestSpecification request(String token) {
        RequestSpecification request = given().spec(ApiConfig.requestSpec());
        if (token != null) {
            request.header("Authorization", token);
        }
        return request;
    }
}