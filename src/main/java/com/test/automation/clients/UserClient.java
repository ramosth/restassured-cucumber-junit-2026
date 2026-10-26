package com.test.automation.clients;

import com.test.automation.utils.Log;

import io.restassured.response.Response;

/**
 * Rotas de /usuarios. Nenhuma rota de usuários exige token.
 *
 * create recebe Object (e não só Usuario) de propósito: nos cenários de erro
 * vamos enviar corpos "quebrados" (campo a mais, booleano no lugar de texto),
 * que um POJO não consegue representar. Um Map serve para isso.
 */
public class UserClient extends BaseClient {

    private static final String USUARIOS = "/usuarios";
    private static final String USUARIO_POR_ID = USUARIOS + "/{id}";

    /** POST /usuarios */
    public Response create(Object body) {
        Log.info("POST " + USUARIOS);
        return request()
                .body(body)
                .when()
                .post(USUARIOS)
                .then()
                .extract().response();
    }

    /** GET /usuarios/{id} */
    public Response getById(String id) {
        Log.info("GET " + USUARIOS + "/" + id);
        return request()
                .when()
                .get(USUARIO_POR_ID, id)
                .then()
                .extract().response();
    }

    /** GET /usuarios?email=... */
    public Response listByEmail(String email) {
        Log.info("GET " + USUARIOS + "?email=" + email);
        return request()
                .queryParam("email", email)
                .when()
                .get(USUARIOS)
                .then()
                .extract().response();
    }

    /**
     * PUT /usuarios/{id}: o id vai na URL e o corpo leva TODOS os campos (sem o
     * _id).
     * Atenção (OBS-02): se o id não existir, a API cria um usuário novo, com outro
     * id.
     */
    public Response update(String id, Object body) {
        Log.info("PUT " + USUARIOS + "/" + id);
        return request()
                .body(body)
                .when()
                .put(USUARIO_POR_ID, id)
                .then()
                .extract().response();
    }

    /** DELETE /usuarios/{id} */
    public Response delete(String id) {
        Log.info("DELETE " + USUARIOS + "/" + id);
        return request()
                .when()
                .delete(USUARIO_POR_ID, id)
                .then()
                .extract().response();
    }
}