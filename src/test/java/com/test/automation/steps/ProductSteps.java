package com.test.automation.steps;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.test.automation.clients.ProductClient;
import com.test.automation.context.TestContext;
import com.test.automation.data.DataFactory;
import com.test.automation.data.Produto;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

/**
 * Passos de produtos.
 *
 * "esse produto" no .feature = context.getProduto()
 * "o outro produto" no .feature = context.getOutroProduto()
 *
 * Quem cria os produtos dos Dados é o administrador de apoio (AuthSteps): assim
 * o
 * cenário pode estar logado como cliente, ou nem estar logado, e mesmo assim
 * ter um produto.
 * Nas AÇÕES (Quando), o token usado é o do cenário: é ele que está sendo
 * testado.
 */
public class ProductSteps {

    private final TestContext context;
    private final AuthSteps authSteps;
    private final ProductClient productClient = new ProductClient();

    public ProductSteps(TestContext context, AuthSteps authSteps) {
        this.context = context;
        this.authSteps = authSteps;
    }

    // ---------- Pré-condições ----------

    @Dado("que existe um produto cadastrado")
    public void queExisteUmProdutoCadastrado() {
        context.setProduto(criarProduto(DataFactory.novoProduto()));
    }

    @Dado("que existe um produto cadastrado com preço {int} e estoque {int}")
    public void queExisteUmProdutoCadastradoComPrecoEEstoque(int preco, int estoque) {
        context.setProduto(criarProduto(DataFactory.novoProduto(preco, estoque)));
    }

    @Dado("que existe outro produto cadastrado")
    public void queExisteOutroProdutoCadastrado() {
        context.setOutroProduto(criarProduto(DataFactory.novoProduto()));
    }

    @Dado("que existe outro produto cadastrado com preço {int} e estoque {int}")
    public void queExisteOutroProdutoCadastradoComPrecoEEstoque(int preco, int estoque) {
        context.setOutroProduto(criarProduto(DataFactory.novoProduto(preco, estoque)));
    }

    // ---------- Ações: cadastro (com o token do cenário) ----------

    @Quando("cadastro um produto com dados válidos")
    public void cadastroUmProdutoComDadosValidos() {
        cadastrar(DataFactory.novoProduto());
    }

    @Quando("cadastro um produto com o nome desse produto")
    public void cadastroUmProdutoComONomeDesseProduto() {
        Produto novo = DataFactory.novoProduto();
        novo.setNome(context.getProduto().getNome()); // nome repetido de propósito
        cadastrar(novo);
    }

    /** OBS-06: a API só bloqueia nomes iguais com as mesmas maiúsculas. */
    @Quando("cadastro um produto com o nome desse produto em letras minúsculas")
    public void cadastroUmProdutoComONomeDesseProdutoEmLetrasMinusculas() {
        Produto novo = DataFactory.novoProduto();
        novo.setNome(context.getProduto().getNome().toLowerCase());
        cadastrar(novo);
    }

    /**
     * Preço e estoque escritos no .feature, inclusive inválidos (0, -1, 10.5).
     * O corpo é um Map porque o POJO (Integer) não guarda 10.5.
     */
    @Quando("cadastro um produto com preço {numero} e estoque {numero}")
    public void cadastroUmProdutoComPrecoEEstoque(Number preco, Number estoque) {
        Produto base = DataFactory.novoProduto();
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", base.getNome());
        corpo.put("preco", preco);
        corpo.put("descricao", base.getDescricao());
        corpo.put("quantidade", estoque);
        cadastrar(corpo);
    }

    // ---------- Ações: consulta ----------

    @Quando("consulto a lista de produtos")
    public void consultoAListaDeProdutos() {
        context.setResponse(productClient.list());
    }

    @Quando("consulto esse produto pelo id")
    public void consultoEsseProdutoPeloId() {
        context.setResponse(productClient.getById(context.getProduto().getId()));
    }

    @Quando("consulto o produto de id {string}")
    public void consultoOProdutoDeId(String id) {
        context.setResponse(productClient.getById(id));
    }

    // ---------- Ações: alteração (com o token do cenário) ----------

    @Quando("altero o preço desse produto para {int} e o estoque para {int}")
    public void alteroOPrecoDesseProdutoParaEOEstoquePara(int preco, int estoque) {
        Produto produto = context.getProduto();
        Produto corpo = semId(produto);
        corpo.setPreco(preco);
        corpo.setQuantidade(estoque);
        context.setResponse(productClient.update(context.getToken(), produto.getId(), corpo));
    }

    @Quando("altero o nome desse produto para o nome do outro produto")
    public void alteroONomeDesseProdutoParaONomeDoOutroProduto() {
        Produto produto = context.getProduto();
        Produto corpo = semId(produto);
        corpo.setNome(context.getOutroProduto().getNome());
        context.setResponse(productClient.update(context.getToken(), produto.getId(), corpo));
    }

    /**
     * OBS-02: com id inexistente, a API cria um produto novo. Ele entra na faxina.
     */
    @Quando("altero o produto de id {string} com dados válidos")
    public void alteroOProdutoDeIdComDadosValidos(String id) {
        Response response = productClient.update(context.getToken(), id, DataFactory.novoProduto());
        registrarSeCriou(response);
        context.setResponse(response);
    }

    // ---------- Ações: exclusão (com o token do cenário) ----------

    @Quando("excluo esse produto")
    public void excluoEsseProduto() {
        context.setResponse(productClient.delete(context.getToken(), context.getProduto().getId()));
    }

    // ---------- Validações ----------

    @E("a lista de produtos não deve estar vazia")
    public void aListaDeProdutosNaoDeveEstarVazia() {
        // jsonPath lê o corpo da resposta: { "quantidade": 2, "produtos": [ ... ] }
        int quantidade = context.getResponse().jsonPath().getInt("quantidade");
        Assertions.assertTrue(quantidade > 0, "A lista de produtos veio vazia.");
    }

    @Entao("o produto retornado deve ter os mesmos dados do cadastro")
    public void oProdutoRetornadoDeveTerOsMesmosDadosDoCadastro() {
        Produto esperado = context.getProduto();
        Produto atual = context.getResponse().as(Produto.class);

        Assertions.assertAll("Dados do produto",
                () -> Assertions.assertEquals(esperado.getId(), atual.getId(), "id"),
                () -> Assertions.assertEquals(esperado.getNome(), atual.getNome(), "nome"),
                () -> Assertions.assertEquals(esperado.getPreco(), atual.getPreco(), "preco"),
                () -> Assertions.assertEquals(esperado.getDescricao(), atual.getDescricao(), "descricao"),
                () -> Assertions.assertEquals(esperado.getQuantidade(), atual.getQuantidade(), "quantidade"));
    }

    /**
     * Confere a alteração com uma consulta NOVA (não confia só na mensagem do PUT).
     */
    @Entao("a consulta desse produto deve mostrar preço {int} e estoque {int}")
    public void aConsultaDesseProdutoDeveMostrarPrecoEEstoque(int precoEsperado, int estoqueEsperado) {
        Response consulta = productClient.getById(context.getProduto().getId());
        Assertions.assertEquals(200, consulta.statusCode(), "Falha ao consultar o produto: " + consulta.asString());
        Assertions.assertAll("Produto depois da alteração",
                () -> Assertions.assertEquals(precoEsperado, consulta.jsonPath().getInt("preco"), "preco"),
                () -> Assertions.assertEquals(estoqueEsperado, consulta.jsonPath().getInt("quantidade"), "quantidade"));
    }

    /**
     * Confere a exclusão com uma consulta NOVA: o produto não pode mais ser
     * encontrado.
     */
    @Entao("esse produto não deve mais existir")
    public void esseProdutoNaoDeveMaisExistir() {
        Response consulta = productClient.getById(context.getProduto().getId());
        Assertions.assertEquals(400, consulta.statusCode(),
                "O produto ainda existe depois da exclusão: " + consulta.asString());
        Assertions.assertEquals("Produto não encontrado", consulta.jsonPath().getString("message"));
    }

    // ---------- Apoio ----------

    /**
     * Cria o produto com o ADMINISTRADOR DE APOIO e confere o 201 como
     * pré-condição.
     * Registra o id para a faxina do @After.
     */
    private Produto criarProduto(Produto produto) {
        Response response = productClient.create(authSteps.tokenDeAdministradorDeApoio(), produto);
        Assertions.assertEquals(201, response.statusCode(),
                "Pré-condição: não foi possível cadastrar o produto. Resposta: " + response.asString());

        produto.setId(response.jsonPath().getString("_id"));
        context.registrarProduto(produto.getId());
        return produto;
    }

    /**
     * Cadastro como AÇÃO do cenário, com o token do cenário (o resultado é o que o
     * Então testa).
     */
    private void cadastrar(Object corpo) {
        Response response = productClient.create(context.getToken(), corpo);
        registrarSeCriou(response);
        context.setResponse(response);
    }

    /**
     * Qualquer 201 cria um registro: ele entra na faxina, mesmo num cenário de
     * erro.
     */
    private void registrarSeCriou(Response response) {
        if (response.statusCode() == 201) {
            context.registrarProduto(response.jsonPath().getString("_id"));
        }
    }

    /**
     * Cópia do produto SEM o id: no PUT o id vai na URL, e no corpo a API recusaria
     * (RG-03).
     */
    private Produto semId(Produto produto) {
        return new Produto(produto.getNome(), produto.getPreco(), produto.getDescricao(), produto.getQuantidade());
    }
}