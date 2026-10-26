package com.test.automation.clients;

import com.test.automation.utils.Log;

import io.restassured.response.Response;

/**
 * Rotas de /carrinhos.
 *
 * O carrinho é sempre o do DONO do token: não se informa o usuário no corpo.
 * Por isso criar, concluir e cancelar recebem o token; consultar é público.
 */
public class CartClient extends BaseClient {

    private static final String CARRINHOS = "/carrinhos";
    private static final String CARRINHO_POR_ID = CARRINHOS + "/{id}";

    /** POST /carrinhos. Corpo: {"produtos": [{"idProduto": "...", "quantidade": 1}]} */
    public Response create(String token, Object body) {
        Log.info("POST " + CARRINHOS);
        return request(token)
                .body(body)
                .when()
                    .post(CARRINHOS)
                .then()
                    .extract().response();
    }

    /** GET /carrinhos/{id} (rota pública) */
    public Response getById(String id) {
        Log.info("GET " + CARRINHOS + "/" + id);
        return request()
                .when()
                    .get(CARRINHO_POR_ID, id)
                .then()
                    .extract().response();
    }

    /** DELETE /carrinhos/concluir-compra: exclui o carrinho e o estoque CONTINUA baixado. */
    public Response concludePurchase(String token) {
        Log.info("DELETE " + CARRINHOS + "/concluir-compra");
        return request(token)
                .when()
                    .delete(CARRINHOS + "/concluir-compra")
                .then()
                    .extract().response();
    }

    /** DELETE /carrinhos/cancelar-compra: exclui o carrinho e DEVOLVE o estoque. */
    public Response cancelPurchase(String token) {
        Log.info("DELETE " + CARRINHOS + "/cancelar-compra");
        return request(token)
                .when()
                    .delete(CARRINHOS + "/cancelar-compra")
                .then()
                    .extract().response();
    }
}