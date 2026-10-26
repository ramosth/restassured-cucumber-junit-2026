package com.test.automation.steps;

import com.test.automation.clients.CartClient;
import com.test.automation.context.TestContext;

import io.cucumber.java.pt.Quando;

/**
 * Passos de carrinho. Por enquanto só o cancelar (usado no US01-CA07);
 * o resto entra na etapa 9.
 */
public class CartSteps {

    private final TestContext context;
    private final CartClient cartClient = new CartClient();

    public CartSteps(TestContext context) {
        this.context = context;
    }

    /** Usa o token do contexto: o carrinho cancelado é o do usuário logado. */
    @Quando("cancelo a compra")
    public void canceloACompra() {
        context.setResponse(cartClient.cancelPurchase(context.getToken()));
    }
}