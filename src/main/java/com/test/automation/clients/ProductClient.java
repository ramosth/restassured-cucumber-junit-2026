package com.test.automation.clients;

import com.test.automation.utils.Log;

import io.restassured.response.Response;

/**
 * Rotas de /produtos (o "Page Object" do recurso Produtos).
 *
 * Cada método = uma chamada da API. Ele NÃO valida nada: só envia e devolve a
 * resposta. Quem decide se a resposta está certa é o step (@Entao).
 * Assim o mesmo método serve para o caminho feliz (200) e para os erros (400, 401...).
 */
public class ProductClient extends BaseClient {

    private static final String PRODUTOS = "/produtos";

    /** GET /produtos: lista o catálogo (rota pública). */
    public Response list() {
        Log.info("GET " + PRODUTOS);
        return request()
                .when()
                    .get(PRODUTOS)
                .then()
                    .extract().response();
    }
}