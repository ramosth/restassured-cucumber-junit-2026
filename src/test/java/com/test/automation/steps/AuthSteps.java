package com.test.automation.steps;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.test.automation.clients.LoginClient;
import com.test.automation.context.TestContext;
import com.test.automation.data.Perfil;
import com.test.automation.data.Usuario;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

/**
 * Passos de autenticação: login, token, "logado como" e o administrador de
 * apoio.
 *
 * Recebe o UserSteps no construtor para REAPROVEITAR a criação de usuário
 * (criação + registro para a faxina), em vez de copiar o código.
 * O PicoContainer entrega a mesma instância do cenário, como faz com o
 * TestContext.
 */
public class AuthSteps {

    private final TestContext context;
    private final UserSteps userSteps;
    private final LoginClient loginClient = new LoginClient();

    public AuthSteps(TestContext context, UserSteps userSteps) {
        this.context = context;
        this.userSteps = userSteps;
    }

    // ---------- Pré-condições ----------

    /**
     * Deixa claro no .feature que a falta de token é intencional.
     * Na prática só garante que nenhum token será enviado.
     */
    @Dado("que não estou logado")
    public void queNaoEstouLogado() {
        context.setToken(null);
    }

    /** Cria um usuário do perfil pedido, faz login e guarda o token no contexto. */
    @Dado("que estou logado como {perfil}")
    public void queEstouLogadoComo(Perfil perfil) {
        userSteps.queExisteUmUsuarioCadastrado(perfil); // cria, guarda como "esse usuário" e registra para a faxina
        context.setToken(fazerLogin(context.getUsuario()));
    }

    /** Token escrito no .feature. Ex.: "Bearer token_invalido" (RN-PRO-02). */
    @Dado("que estou com o token {string}")
    public void queEstouComOToken(String token) {
        context.setToken(token);
    }

    // ---------- Ações ----------

    @Quando("faço login com o e-mail e a senha desse usuário")
    public void facoLoginComOEmailEASenhaDesseUsuario() {
        Usuario usuario = context.getUsuario();
        context.setResponse(loginClient.login(credenciais(usuario.getEmail(), usuario.getPassword())));
    }

    @Quando("faço login com o e-mail desse usuário e a senha {string}")
    public void facoLoginComOEmailDesseUsuarioEASenha(String senha) {
        context.setResponse(loginClient.login(credenciais(context.getUsuario().getEmail(), senha)));
    }

    /**
     * E-mail e senha escritos no .feature (inclusive em branco: "" vira texto
     * vazio).
     */
    @Quando("faço login com o e-mail {string} e a senha {string}")
    public void facoLoginComOEmailEASenha(String email, String senha) {
        context.setResponse(loginClient.login(credenciais(email, senha)));
    }

    /**
     * Envia o corpo SEM um dos campos (diferente de enviar em branco):
     * sem "email" -> {"password": "teste"}
     * sem "password" -> {"email": "fulano@qa.com"}
     */
    @Quando("faço login sem o campo {string}")
    public void facoLoginSemOCampo(String campo) {
        Map<String, Object> corpo = credenciais("fulano@qa.com", "teste");
        corpo.remove(campo);
        context.setResponse(loginClient.login(corpo));
    }

    // ---------- Validações ----------

    @Entao("o token deve começar com {string}")
    public void oTokenDeveComecarCom(String inicioEsperado) {
        String token = context.getResponse().jsonPath().getString("authorization");
        Assertions.assertNotNull(token, "A resposta não trouxe o campo 'authorization'.");
        Assertions.assertTrue(token.startsWith(inicioEsperado),
                "O token deveria começar com '" + inicioEsperado + "', mas veio: " + token);
    }

    // ---------- Apoio (usado por outras classes) ----------

    /**
     * Token de um administrador "de apoio": cria produtos nos Dados e faz a faxina.
     * É criado só na primeira vez que alguém pede, e reaproveitado no resto do
     * cenário.
     * Ele também é um usuário: entra na lista da faxina e é excluído no final.
     */
    public String tokenDeAdministradorDeApoio() {
        if (context.getTokenAdminApoio() == null) {
            Usuario admin = userSteps.criarUsuario(Perfil.ADMINISTRADOR);
            context.setTokenAdminApoio(fazerLogin(admin));
        }
        return context.getTokenAdminApoio();
    }

    /** Faz login como pré-condição (confere o 200) e devolve o token. */
    public String fazerLogin(Usuario usuario) {
        Response response = loginClient.login(credenciais(usuario.getEmail(), usuario.getPassword()));
        Assertions.assertEquals(200, response.statusCode(),
                "Pré-condição: não foi possível fazer login. Resposta: " + response.asString());
        return response.jsonPath().getString("authorization");
    }

    /**
     * Corpo do login como Map: {"email": "...", "password": "..."}.
     * LinkedHashMap mantém a ordem dos campos no JSON (fica mais fácil de ler no
     * log).
     */
    private Map<String, Object> credenciais(String email, String senha) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("email", email);
        corpo.put("password", senha);
        return corpo;
    }
}