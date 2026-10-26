package com.test.automation.steps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.test.automation.clients.CartClient;
import com.test.automation.clients.ProductClient;
import com.test.automation.context.TestContext;
import com.test.automation.data.Perfil;
import com.test.automation.data.Usuario;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

/**
 * Passos de carrinho e compra.
 *
 * "esse carrinho" no .feature = context.getCarrinhoId()
 *
 * O carrinho é sempre do DONO do token. Por isso a faxina guarda o token de cada dono
 * (context.registrarCarrinho) e, no @After, CANCELA a compra: cancelar devolve o estoque.
 *
 * "unidade(s)" nas frases: o trecho entre parênteses é opcional no Cucumber,
 * então a mesma frase aceita "1 unidade" e "3 unidades".
 */
public class CartSteps {

    private final TestContext context;
    private final UserSteps userSteps;
    private final AuthSteps authSteps;
    private final CartClient cartClient = new CartClient();
    private final ProductClient productClient = new ProductClient();

    public CartSteps(TestContext context, UserSteps userSteps, AuthSteps authSteps) {
        this.context = context;
        this.userSteps = userSteps;
        this.authSteps = authSteps;
    }

    // ---------- Pré-condições ----------

    /** Carrinho do usuário logado no cenário. */
    @Dado("que eu tenho um carrinho com {int} unidade(s) desse produto")
    public void queEuTenhoUmCarrinhoComUnidadesDesseProduto(int quantidade) {
        criarCarrinho(context.getToken(), carrinho(item(context.getProduto().getId(), quantidade)));
    }

    /** Carrinho de OUTRO cliente (bastidores): usado quando o ator do cenário é o administrador. */
    @Dado("que outro cliente tem um carrinho com {int} unidade(s) desse produto")
    public void queOutroClienteTemUmCarrinhoComUnidadesDesseProduto(int quantidade) {
        Usuario outroCliente = userSteps.criarUsuario(Perfil.CLIENTE);
        String tokenDoOutroCliente = authSteps.fazerLogin(outroCliente);
        criarCarrinho(tokenDoOutroCliente, carrinho(item(context.getProduto().getId(), quantidade)));
    }

    // ---------- Ações: montar o carrinho (com o token do cenário) ----------

    @Quando("crio um carrinho com {int} unidade(s) desse produto")
    public void crioUmCarrinhoComUnidadesDesseProduto(int quantidade) {
        criar(carrinho(item(context.getProduto().getId(), quantidade)));
    }

    @Quando("crio um carrinho com {int} unidade(s) do produto de id {string}")
    public void crioUmCarrinhoComUnidadesDoProdutoDeId(int quantidade, String idProduto) {
        criar(carrinho(item(idProduto, quantidade)));
    }

    @Quando("crio um carrinho com o mesmo produto duas vezes")
    public void crioUmCarrinhoComOMesmoProdutoDuasVezes() {
        String id = context.getProduto().getId();
        criar(carrinho(item(id, 1), item(id, 1)));
    }

    @Quando("crio um carrinho com {int} unidade(s) desse produto e {int} unidade(s) do outro produto")
    public void crioUmCarrinhoComDoisProdutos(int quantidade, int quantidadeOutro) {
        criar(carrinho(
                item(context.getProduto().getId(), quantidade),
                item(context.getOutroProduto().getId(), quantidadeOutro)));
    }

    // ---------- Ações: finalizar ----------

    @Quando("concluo a compra")
    public void concluoACompra() {
        context.setResponse(cartClient.concludePurchase(context.getToken()));
    }

    /** Usa o token do contexto: o carrinho cancelado é o do usuário logado. */
    @Quando("cancelo a compra")
    public void canceloACompra() {
        context.setResponse(cartClient.cancelPurchase(context.getToken()));
    }

    @Quando("consulto esse carrinho pelo id")
    public void consultoEsseCarrinhoPeloId() {
        context.setResponse(cartClient.getById(context.getCarrinhoId()));
    }

    // ---------- Validações ----------

    /** Confere os totais com uma consulta NOVA do carrinho criado (RN-CAR-08). */
    @Entao("o carrinho deve ter preço total {int} e quantidade total {int}")
    public void oCarrinhoDeveTerPrecoTotalEQuantidadeTotal(int precoTotal, int quantidadeTotal) {
        Response consulta = cartClient.getById(context.getCarrinhoId());
        Assertions.assertEquals(200, consulta.statusCode(), "Falha ao consultar o carrinho: " + consulta.asString());
        Assertions.assertAll("Totais do carrinho",
                () -> Assertions.assertEquals(precoTotal, consulta.jsonPath().getInt("precoTotal"), "precoTotal"),
                () -> Assertions.assertEquals(quantidadeTotal, consulta.jsonPath().getInt("quantidadeTotal"),
                        "quantidadeTotal"));
    }

    /** Confere o estoque com uma consulta NOVA do produto (RN-CAR-07, 10 e 11). */
    @Entao("o estoque desse produto deve ser {int}")
    public void oEstoqueDesseProdutoDeveSer(int estoqueEsperado) {
        Response consulta = productClient.getById(context.getProduto().getId());
        Assertions.assertEquals(200, consulta.statusCode(), "Falha ao consultar o produto: " + consulta.asString());
        Assertions.assertEquals(estoqueEsperado, consulta.jsonPath().getInt("quantidade"),
                "Estoque do produto diferente do esperado.");
    }

    /** Confere com uma consulta NOVA: o carrinho não pode mais ser encontrado. */
    @Entao("esse carrinho não deve mais existir")
    public void esseCarrinhoNaoDeveMaisExistir() {
        Response consulta = cartClient.getById(context.getCarrinhoId());
        Assertions.assertEquals(400, consulta.statusCode(),
                "O carrinho ainda existe: " + consulta.asString());
        Assertions.assertEquals("Carrinho não encontrado", consulta.jsonPath().getString("message"));
    }

    /**
     * A API informa o carrinho que impediu a exclusão:
     *   exclusão de usuário -> {"idCarrinho": "..."}        (RN-USU-07)
     *   exclusão de produto -> {"idCarrinhos": ["..."]}     (RN-PRO-09)
     */
    @Entao("a resposta deve informar o id desse carrinho")
    public void aRespostaDeveInformarOIdDesseCarrinho() {
        Response response = context.getResponse();
        String idCarrinho = response.jsonPath().getString("idCarrinho");
        List<String> idCarrinhos = response.jsonPath().getList("idCarrinhos");

        boolean informou = context.getCarrinhoId().equals(idCarrinho)
                || (idCarrinhos != null && idCarrinhos.contains(context.getCarrinhoId()));
        Assertions.assertTrue(informou,
                "A resposta não informou o carrinho " + context.getCarrinhoId() + ". Corpo: " + response.asString());
    }

    // ---------- Apoio ----------

    /** Carrinho como PRÉ-CONDIÇÃO: confere o 201 e registra o dono para a faxina. */
    private void criarCarrinho(String tokenDoDono, Map<String, Object> corpo) {
        Response response = cartClient.create(tokenDoDono, corpo);
        Assertions.assertEquals(201, response.statusCode(),
                "Pré-condição: não foi possível criar o carrinho. Resposta: " + response.asString());
        context.setCarrinhoId(response.jsonPath().getString("_id"));
        context.registrarCarrinho(tokenDoDono);
    }

    /** Carrinho como AÇÃO do cenário (o resultado é o que o Então testa). Qualquer 201 entra na faxina. */
    private void criar(Map<String, Object> corpo) {
        Response response = cartClient.create(context.getToken(), corpo);
        if (response.statusCode() == 201) {
            context.setCarrinhoId(response.jsonPath().getString("_id"));
            context.registrarCarrinho(context.getToken());
        }
        context.setResponse(response);
    }

    /** Um item do carrinho: {"idProduto": "...", "quantidade": 1} */
    private Map<String, Object> item(String idProduto, int quantidade) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("idProduto", idProduto);
        item.put("quantidade", quantidade);
        return item;
    }

    /** O corpo inteiro: {"produtos": [ item, item... ]} */
    @SafeVarargs
    private Map<String, Object> carrinho(Map<String, Object>... itens) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("produtos", List.of(itens));
        return corpo;
    }
}