package com.test.automation.clients;

import com.test.automation.utils.Log;

import io.restassured.response.Response;

/**
 * Rotas de /produtos (o "Page Object" do recurso Produtos).
 *
 * Consultar é público. Cadastrar, alterar e excluir exigem token de
 * ADMINISTRADOR:
 * sem token -> 401; token de cliente -> 403 (RN-PRO-02 e RN-PRO-03).
 * O token é sempre parâmetro: o mesmo método testa o admin, o cliente e o "sem
 * token" (null).
 *
 * Cada método só envia e devolve a resposta: quem valida é o step (@Entao).
 */
public class ProductClient extends BaseClient {

    private static final String PRODUTOS = "/produtos";
    private static final String PRODUTO_POR_ID = PRODUTOS + "/{id}";

    /** GET /produtos: lista o catálogo (rota pública). */
    public Response list() {
        Log.info("GET " + PRODUTOS);
        return request()
                .when()
                .get(PRODUTOS)
                .then()
                .extract().response();
    }

    /** POST /produtos */
    public Response create(String token, Object body) {
        Log.info("POST " + PRODUTOS);
        return request(token)
                .body(body)
                .when()
                .post(PRODUTOS)
                .then()
                .extract().response();
    }

    /** GET /produtos/{id} (rota pública) */
    public Response getById(String id) {
        Log.info("GET " + PRODUTOS + "/" + id);
        return request()
                .when()
                .get(PRODUTO_POR_ID, id)
                .then()
                .extract().response();
    }

    /**
     * PUT /produtos/{id}. Atenção (OBS-02): id inexistente cria um produto novo,
     * com outro id.
     */
    public Response update(String token, String id, Object body) {
        Log.info("PUT " + PRODUTOS + "/" + id);
        return request(token)
                .body(body)
                .when()
                .put(PRODUTO_POR_ID, id)
                .then()
                .extract().response();
    }

    /** DELETE /produtos/{id} */
    public Response delete(String token, String id) {
        Log.info("DELETE " + PRODUTOS + "/" + id);
        return request(token)
                .when()
                .delete(PRODUTO_POR_ID, id)
                .then()
                .extract().response();
    }
}