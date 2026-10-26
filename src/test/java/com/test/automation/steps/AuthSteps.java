package com.test.automation.steps;

import com.test.automation.context.TestContext;

import io.cucumber.java.pt.Dado;

/**
 * Passos de autenticação (login e token).
 * Por enquanto só o "não logado"; o login entra na etapa 6.
 */
public class AuthSteps {

    private final TestContext context;

    public AuthSteps(TestContext context) {
        this.context = context;
    }

    /**
     * Deixa claro no .feature que a falta de token é intencional.
     * Na prática só garante que nenhum token será enviado.
     */
    @Dado("que não estou logado")
    public void queNaoEstouLogado() {
        context.setToken(null);
    }
}