package com.test.automation.context;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.ExtentTest;
import com.test.automation.data.Produto;
import com.test.automation.data.Usuario;

import io.restassured.response.Response;

/**
 * "Mochila" compartilhada durante UM cenário.
 *
 * O PicoContainer cria um TestContext novo no início de cada cenário e entrega
 * a MESMA instância para todas as classes que pedirem por ele no construtor.
 *
 * response -> a última resposta recebida da API
 * token -> o "Bearer ..." do usuário do cenário (null = não logado)
 * tokenAdminApoio -> token do administrador "de apoio" (bastidores: cria
 * produtos e faz a faxina)
 * usuario / outroUsuario -> "esse usuário" / "o outro usuário" do .feature
 * produto / outroProduto -> "esse produto" / "o outro produto" do .feature
 * carrinhoId -> "esse carrinho" do .feature
 *
 * Registro para a limpeza: todo registro criado entra numa lista, e o @After
 * dos Hooks apaga tudo no final (mesmo se o cenário falhar).
 */
public class TestContext {

    private Response response;
    private String token;
    private String tokenAdminApoio;
    private Usuario usuario;
    private Usuario outroUsuario;
    private Produto produto;
    private Produto outroProduto;
    private ExtentTest extentTest;
    private String carrinhoId;

    // ids criados neste cenário (para o @After excluir)
    private final List<String> usuariosCriados = new ArrayList<>();
    private final List<String> produtosCriados = new ArrayList<>();
    // tokens dos DONOS dos carrinhos criados: cancelar a compra precisa do token,
    // não do id
    private final List<String> carrinhosCriados = new ArrayList<>();

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenAdminApoio() {
        return tokenAdminApoio;
    }

    public void setTokenAdminApoio(String tokenAdminApoio) {
        this.tokenAdminApoio = tokenAdminApoio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getOutroUsuario() {
        return outroUsuario;
    }

    public void setOutroUsuario(Usuario outroUsuario) {
        this.outroUsuario = outroUsuario;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Produto getOutroProduto() {
        return outroProduto;
    }

    public void setOutroProduto(Produto outroProduto) {
        this.outroProduto = outroProduto;
    }

    /** Registro do cenário atual no Extent Report (criado no @Before dos Hooks). */
    public ExtentTest getExtentTest() {
        return extentTest;
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }

    /** "esse carrinho" do .feature: o id do último carrinho criado no cenário. */
    public String getCarrinhoId() {
        return carrinhoId;
    }

    public void setCarrinhoId(String carrinhoId) {
        this.carrinhoId = carrinhoId;
    }

    // ---------- Registro para a limpeza ----------

    /** Chame logo depois de qualquer cadastro de usuário que deu certo. */
    public void registrarUsuario(String id) {
        usuariosCriados.add(id);
    }

    public List<String> getUsuariosCriados() {
        return usuariosCriados;
    }

    /** Chame logo depois de qualquer cadastro de produto que deu certo. */
    public void registrarProduto(String id) {
        produtosCriados.add(id);
    }

    public List<String> getProdutosCriados() {
        return produtosCriados;
    }

    /**
     * Chame logo depois de qualquer carrinho criado, com o token do DONO do
     * carrinho.
     */
    public void registrarCarrinho(String tokenDoDono) {
        carrinhosCriados.add(tokenDoDono);
    }

    public List<String> getCarrinhosCriados() {
        return carrinhosCriados;
    }
}