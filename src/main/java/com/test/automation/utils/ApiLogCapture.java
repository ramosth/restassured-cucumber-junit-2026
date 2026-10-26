package com.test.automation.utils;

import java.util.ArrayList;

import io.restassured.RestAssured;

/**
 * Captura o log de TODAS as requisições e respostas de um cenário
 * (o "print da tela" da API).
 *
 * start() abre um "caderno" novo e liga o ApiLogFilter em todas as requisições.
 * stop()  desliga o filtro e devolve tudo o que foi anotado.
 *
 * Uso (nos Hooks): start() no @Before e stop() no @After.
 */
public final class ApiLogCapture {

    private static StringBuilder caderno = new StringBuilder();

    private ApiLogCapture() {
    }

    /** Abre um caderno novo e liga o filtro. */
    public static void start() {
        caderno = new StringBuilder();
        RestAssured.replaceFiltersWith(new ApiLogFilter(caderno));
    }

    /** Desliga o filtro e devolve tudo o que foi anotado no cenário. */
    public static String stop() {
        RestAssured.replaceFiltersWith(new ArrayList<>());
        return caderno.toString();
    }
}