package com.test.automation.clients;

import com.test.automation.utils.Log;

import io.restassured.response.Response;

/**
 * Rotas de /carrinhos. Todas as que alteram o carrinho exigem token:
 * o carrinho é sempre o do DONO do token (não se informa o usuário).
 *
 * Por enquanto só o cancelar (usado no login); o resto entra na etapa 9.
 */
public class CartClient extends BaseClient {

    private static final String CARRINHOS = "/carrinhos";

    /** DELETE /carrinhos/cancelar-compra: exclui o carrinho e devolve o estoque. */
    public Response cancelPurchase(String token) {
        Log.info("DELETE " + CARRINHOS + "/cancelar-compra");
        return request(token)
                .when()
                    .delete(CARRINHOS + "/cancelar-compra")
                .then()
                    .extract().response();
    }
}