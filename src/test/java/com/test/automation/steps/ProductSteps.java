package com.test.automation.steps;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Assertions;

import com.test.automation.config.ConfigReader;
import com.test.automation.context.TestContext;
import com.test.automation.utils.Log;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

/**
 * Passos de produtos.
 *
 * PROVISÓRIO (etapa 4): a requisição é montada aqui mesmo, para provar que tudo funciona.
 * Na etapa 5 ela vai para um "Client" (o equivalente do Page Object na API),
 * e a URL base passa a ser configurada num lugar só.
 */
public class ProductSteps {

    private final TestContext context;

    public ProductSteps(TestContext context) {
        this.context = context;
    }

    @Quando("consulto a lista de produtos")
    public void consultoAListaDeProdutos() {
        String baseUri = ConfigReader.get("base.uri", "http://localhost:3000");
        Log.info("GET " + baseUri + "/produtos");

        // given = o que eu envio | when = a chamada | then/extract = o que eu recebo
        Response response = given()
                .baseUri(baseUri)
            .when()
                .get("/produtos")
            .then()
                .extract().response();

        context.setResponse(response);
    }

    @E("a lista de produtos não deve estar vazia")
    public void aListaDeProdutosNaoDeveEstarVazia() {
        // jsonPath lê o corpo da resposta: { "quantidade": 2, "produtos": [ ... ] }
        int quantidade = context.getResponse().jsonPath().getInt("quantidade");
        Assertions.assertTrue(quantidade > 0, "A lista de produtos veio vazia.");
    }
}