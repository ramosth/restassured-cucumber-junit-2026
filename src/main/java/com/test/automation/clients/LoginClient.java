package com.test.automation.clients;

import com.test.automation.utils.Log;

import io.restassured.response.Response;

/**
 * Rota de /login.
 *
 * O corpo é um Object (normalmente um Map) porque os cenários de erro mandam
 * corpos incompletos: sem o e-mail, sem a senha, com campos em branco.
 */
public class LoginClient extends BaseClient {

    private static final String LOGIN = "/login";

    /** POST /login */
    public Response login(Object body) {
        Log.info("POST " + LOGIN);
        return request()
                .body(body)
                .when()
                    .post(LOGIN)
                .then()
                    .extract().response();
    }
}