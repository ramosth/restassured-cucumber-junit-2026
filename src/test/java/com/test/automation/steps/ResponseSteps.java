package com.test.automation.steps;

import org.junit.jupiter.api.Assertions;

import com.test.automation.context.TestContext;

import io.cucumber.java.pt.Entao;

/**
 * Validações que valem para qualquer resposta da API (status, mensagem, erros de campo).
 * Ficam aqui, num lugar só, porque todas as features usam.
 */
public class ResponseSteps {

    private final TestContext context;

    public ResponseSteps(TestContext context) {
        this.context = context;
    }

    @Entao("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int statusEsperado) {
        // JUnit: (esperado, atual, mensagem). O corpo vai na mensagem para facilitar a análise da falha.
        Assertions.assertEquals(statusEsperado, context.getResponse().statusCode(),
                "Status diferente do esperado. Corpo da resposta: " + context.getResponse().asString());
    }
}