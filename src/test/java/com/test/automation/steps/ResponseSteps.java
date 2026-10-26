package com.test.automation.steps;

import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.test.automation.context.TestContext;

import io.cucumber.java.pt.Entao;
import io.restassured.response.Response;

/**
 * Validações que valem para qualquer resposta da API (status, mensagem, erros
 * de campo).
 * Ficam aqui, num lugar só, porque todas as features usam.
 */
public class ResponseSteps {

    private final TestContext context;

    public ResponseSteps(TestContext context) {
        this.context = context;
    }

    @Entao("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int statusEsperado) {
        Response response = context.getResponse();
        // JUnit: (esperado, atual, mensagem). O corpo vai na mensagem para facilitar a
        // análise da falha.
        Assertions.assertEquals(statusEsperado, response.statusCode(),
                "Status diferente do esperado. Corpo da resposta: " + response.asString());
    }

    /**
     * Campo "message" do corpo: {"message": "Cadastro realizado com sucesso", ...}
     */
    @Entao("a mensagem deve ser {string}")
    public void aMensagemDeveSer(String mensagemEsperada) {
        Assertions.assertEquals(mensagemEsperada, context.getResponse().jsonPath().getString("message"),
                "Mensagem diferente da esperada.");
    }

    /**
     * Erros de validação: a API devolve um campo por erro.
     * Ex.: {"email": "email deve ser um email válido", "password": "password não
     * pode ficar em branco"}
     *
     * O corpo é lido como Map (chave -> valor) e não com jsonPath, porque alguns
     * nomes de campo
     * têm ponto e colchete ("produtos[0].quantidade") e o jsonPath os entenderia
     * como um caminho.
     */
    @Entao("o erro do campo {string} deve ser {string}")
    public void oErroDoCampoDeveSer(String campo, String mensagemEsperada) {
        Map<?, ?> corpo = context.getResponse().as(Map.class);
        Assertions.assertEquals(mensagemEsperada, corpo.get(campo),
                "Erro do campo '" + campo + "' diferente do esperado. Corpo: " + corpo);
    }

    /** Listagens: {"quantidade": 1, "usuarios": [...]} */
    @Entao("a quantidade de registros retornados deve ser {int}")
    public void aQuantidadeDeRegistrosRetornadosDeveSer(int quantidadeEsperada) {
        Assertions.assertEquals(quantidadeEsperada, context.getResponse().jsonPath().getInt("quantidade"),
                "Quantidade de registros diferente da esperada.");
    }

    /**
     * {"message": "Cadastro realizado com sucesso", "_id": "Xk2..."}: o id tem 16
     * caracteres (RG-05).
     */
    @Entao("a resposta deve trazer o id do registro cadastrado")
    public void aRespostaDeveTrazerOIdDoRegistroCadastrado() {
        String id = context.getResponse().jsonPath().getString("_id");
        Assertions.assertNotNull(id, "A resposta não trouxe o _id. Corpo: " + context.getResponse().asString());
        Assertions.assertEquals(16, id.length(), "O _id deveria ter 16 caracteres: " + id);
    }

    /** OBS-02: o PUT com id inexistente cria o registro com OUTRO id. */
    @Entao("o id retornado deve ser diferente de {string}")
    public void oIdRetornadoDeveSerDiferenteDe(String idInformado) {
        String id = context.getResponse().jsonPath().getString("_id");
        Assertions.assertNotNull(id, "A resposta não trouxe o _id. Corpo: " + context.getResponse().asString());
        Assertions.assertNotEquals(idInformado, id, "O registro foi criado com o mesmo id da URL.");
    }
}