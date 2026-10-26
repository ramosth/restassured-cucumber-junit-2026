package com.test.automation.context;

import com.test.automation.data.Usuario;

import io.restassured.response.Response;

/**
 * "Mochila" compartilhada durante UM cenário.
 *
 * O PicoContainer cria um TestContext novo no início de cada cenário e entrega
 * a MESMA instância para todas as classes que pedirem por ele no construtor.
 *
 *   response -> a última resposta recebida da API (status, corpo, cabeçalhos)
 *   token    -> o "Bearer ..." do usuário logado (null = não logado)
 *   usuario  -> "esse usuário" do .feature: o último usuário criado no cenário (com o id)
 */
public class TestContext {

    private Response response;
    private String token;
    private Usuario usuario;

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}