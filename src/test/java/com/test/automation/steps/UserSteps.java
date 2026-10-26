package com.test.automation.steps;

import org.junit.jupiter.api.Assertions;

import com.test.automation.clients.UserClient;
import com.test.automation.context.TestContext;
import com.test.automation.data.DataFactory;
import com.test.automation.data.Perfil;
import com.test.automation.data.Usuario;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

/**
 * Passos de usuários.
 *
 * "esse usuário" no .feature = context.getUsuario(): o último usuário criado no
 * cenário.
 */
public class UserSteps {

    private final TestContext context;
    private final UserClient userClient = new UserClient();

    public UserSteps(TestContext context) {
        this.context = context;
    }

    // ---------- Pré-condições ----------

    /**
     * Cria um usuário pela API e guarda no contexto.
     * A verificação do 201 aqui é de PRÉ-CONDIÇÃO: se o cadastro falhar, o cenário
     * para logo no Dado, com uma mensagem clara, em vez de falhar lá na frente sem
     * explicação.
     */
    @Dado("que existe um usuário {perfil} cadastrado")
    public void queExisteUmUsuarioCadastrado(Perfil perfil) {
        Usuario usuario = DataFactory.novoUsuario(perfil);
        Response response = userClient.create(usuario);
        Assertions.assertEquals(201, response.statusCode(),
                "Pré-condição: não foi possível cadastrar o usuário. Resposta: " + response.asString());

        usuario.setId(response.jsonPath().getString("_id"));
        context.setUsuario(usuario);
        context.registrarUsuario(usuario.getId()); // o @After vai excluir no final
    }

    // ---------- Ações ----------

    @Quando("consulto esse usuário pelo id")
    public void consultoEsseUsuarioPeloId() {
        context.setResponse(userClient.getById(context.getUsuario().getId()));
    }

    @Quando("consulto o usuário de id {string}")
    public void consultoOUsuarioDeId(String id) {
        context.setResponse(userClient.getById(id));
    }

    @Quando("listo os usuários filtrando pelo e-mail desse usuário")
    public void listoOsUsuariosFiltrandoPeloEmailDesseUsuario() {
        context.setResponse(userClient.listByEmail(context.getUsuario().getEmail()));
    }

    // ---------- Validações ----------

    /** Compara o que voltou da API com o que foi cadastrado no Dado. */
    @Entao("o usuário retornado deve ter os mesmos dados do cadastro")
    public void oUsuarioRetornadoDeveTerOsMesmosDadosDoCadastro() {
        Usuario esperado = context.getUsuario();
        Usuario atual = context.getResponse().as(Usuario.class); // JSON -> POJO (Jackson)

        // assertAll confere TODOS os campos e mostra todas as diferenças de uma vez
        Assertions.assertAll("Dados do usuário",
                () -> Assertions.assertEquals(esperado.getId(), atual.getId(), "id"),
                () -> Assertions.assertEquals(esperado.getNome(), atual.getNome(), "nome"),
                () -> Assertions.assertEquals(esperado.getEmail(), atual.getEmail(), "email"),
                () -> Assertions.assertEquals(esperado.getAdministrador(), atual.getAdministrador(), "administrador"));
    }

    /**
     * OBS-04: hoje a API devolve a senha, então este passo FALHA
     * (cenário @expected-failure).
     */
    @Entao("a resposta não deve exibir a senha")
    public void aRespostaNaoDeveExibirASenha() {
        String senha = context.getResponse().jsonPath().getString("password");
        Assertions.assertNull(senha, "A API devolveu a senha do usuário em texto puro (OBS-04).");
    }
}