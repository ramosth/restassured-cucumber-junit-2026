package com.test.automation.steps;

import org.junit.jupiter.api.Assertions;

import com.test.automation.clients.ProductClient;
import com.test.automation.context.TestContext;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;

/**
 * Passos de produtos.
 *
 * O step só traduz a frase do .feature em uma chamada do Client e guarda a
 * resposta no TestContext. URL, rota e cabeçalhos ficam no ProductClient.
 */
public class ProductSteps {

    private final TestContext context;
    private final ProductClient productClient = new ProductClient();

    public ProductSteps(TestContext context) {
        this.context = context;
    }

    @Quando("consulto a lista de produtos")
    public void consultoAListaDeProdutos() {
        context.setResponse(productClient.list());
    }

    @E("a lista de produtos não deve estar vazia")
    public void aListaDeProdutosNaoDeveEstarVazia() {
        // jsonPath lê o corpo da resposta: { "quantidade": 2, "produtos": [ ... ] }
        int quantidade = context.getResponse().jsonPath().getInt("quantidade");
        Assertions.assertTrue(quantidade > 0, "A lista de produtos veio vazia.");
    }
}