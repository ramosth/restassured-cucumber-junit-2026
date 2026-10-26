package com.test.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Filtro próprio de log: anota cada requisição e resposta de forma enxuta.
 *
 * Formato:
 *   >>> POST http://localhost:3000/usuarios
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5...
 *   { corpo enviado }
 *   <<< 201 Created (35 ms)
 *   { corpo recebido }
 *
 * Filtro = um elo da corrente por onde toda requisição do REST Assured passa.
 * ctx.next(...) envia a requisição de verdade e devolve a resposta.
 * Este filtro só OBSERVA: não muda nada na requisição nem na resposta.
 */
public class ApiLogFilter implements Filter {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final int TAMANHO_TOKEN = 30;

    private final StringBuilder caderno;

    public ApiLogFilter(StringBuilder caderno) {
        this.caderno = caderno;
    }

    @Override
    public Response filter(FilterableRequestSpecification requisicao,
            FilterableResponseSpecification especificacaoResposta, FilterContext ctx) {

        // Envia a requisição para a API e recebe a resposta
        Response resposta = ctx.next(requisicao, especificacaoResposta);

        // ---------- Requisição ----------
        caderno.append(">>> ").append(requisicao.getMethod()).append(' ').append(requisicao.getURI()).append('\n');

        // O token decide 401 x 403 x 201, por isso aparece (abreviado) quando existe
        String token = requisicao.getHeaders().getValue("Authorization");
        if (token != null) {
            caderno.append("Authorization: ").append(abreviar(token)).append('\n');
        }

        Object corpoEnviado = requisicao.getBody();
        if (corpoEnviado != null) {
            caderno.append(formatar(corpoEnviado.toString())).append('\n');
        }

        // ---------- Resposta ----------
        // "HTTP/1.1 201 Created" -> "201 Created"
        String status = resposta.getStatusLine().replaceFirst("^HTTP/\\S+\\s", "");
        caderno.append("<<< ").append(status).append(" (").append(resposta.getTime()).append(" ms)\n");

        String corpoRecebido = resposta.asString();
        if (corpoRecebido != null && !corpoRecebido.isBlank()) {
            caderno.append(formatar(corpoRecebido)).append('\n');
        }
        caderno.append('\n'); // linha em branco entre uma chamada e outra

        return resposta; // a resposta segue sem alteração para quem chamou
    }

    /** Deixa o JSON indentado (um campo por linha). Se não for JSON, devolve o texto como veio. */
    private static String formatar(String texto) {
        try {
            return JSON.readTree(texto).toPrettyString();
        } catch (Exception e) {
            return texto;
        }
    }

    /** Mostra só o começo do token: o suficiente para saber qual foi enviado. */
    private static String abreviar(String token) {
        return token.length() <= TAMANHO_TOKEN ? token : token.substring(0, TAMANHO_TOKEN) + "...";
    }
}