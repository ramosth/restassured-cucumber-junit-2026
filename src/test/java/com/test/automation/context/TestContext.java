package com.test.automation.context;

import java.util.ArrayList;
import java.util.List;

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
 *
 * Registro para a limpeza: todo registro criado pelo cenário entra numa lista,
 * e o @After dos Hooks apaga tudo no final (mesmo se o cenário falhar).
 */
public class TestContext {

    private Response response;
    private String token;
    private Usuario usuario;

    // ids dos usuários criados neste cenário (para o @After excluir)
    private final List<String> usuariosCriados = new ArrayList<>();

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

    // ---------- Registro para a limpeza ----------

    /** Chame logo depois de qualquer cadastro de usuário que deu certo. */
    public void registrarUsuario(String id) {
        usuariosCriados.add(id);
    }

    public List<String> getUsuariosCriados() {
        return usuariosCriados;
    }
}