package com.test.automation.context;

import io.restassured.response.Response;

/**
 * "Mochila" compartilhada durante UM cenário.
 *
 * O PicoContainer cria um TestContext novo no início de cada cenário e entrega
 * a MESMA instância para todas as classes que pedirem por ele no construtor.
 * Assim o @Quando guarda a resposta e o @Entao confere essa mesma resposta,
 * mesmo estando em classes diferentes.
 *
 * Na UI a mochila levava o navegador (WebDriver). Na API ela leva:
 *   response -> a última resposta recebida da API (status, corpo, cabeçalhos)
 *   token    -> o "Bearer ..." do usuário logado (null = não logado)
 *
 * Na etapa 5 ela ganha os dados criados no cenário (usuário, produto, carrinho).
 */
public class TestContext {

    private Response response;
    private String token;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}