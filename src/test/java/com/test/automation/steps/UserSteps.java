package com.test.automation.steps;

import java.util.LinkedHashMap;
import java.util.Map;

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
 * "esse usuário" no .feature = context.getUsuario()
 * "o outro usuário" no .feature = context.getOutroUsuario()
 *
 * Corpos válidos usam o POJO Usuario. Corpos "quebrados" (booleano no lugar de
 * texto,
 * campo a mais) usam um Map, porque o POJO não consegue representar um JSON
 * errado.
 */
public class UserSteps {

    private final TestContext context;
    private final UserClient userClient = new UserClient();

    public UserSteps(TestContext context) {
        this.context = context;
    }

    // ---------- Pré-condições ----------

    /**
     * Cria um usuário pela API e guarda como "esse usuário".
     * Também é usado pelo AuthSteps ("que estou logado como {perfil}").
     */
    @Dado("que existe um usuário {perfil} cadastrado")
    public void queExisteUmUsuarioCadastrado(Perfil perfil) {
        context.setUsuario(criarUsuario(perfil));
    }

    @Dado("que existe outro usuário cadastrado")
    public void queExisteOutroUsuarioCadastrado() {
        context.setOutroUsuario(criarUsuario(Perfil.CLIENTE));
    }

    // ---------- Ações: cadastro ----------

    @Quando("cadastro um usuário {perfil} com dados válidos")
    public void cadastroUmUsuarioComDadosValidos(Perfil perfil) {
        cadastrar(DataFactory.novoUsuario(perfil));
    }

    @Quando("cadastro um usuário com o e-mail desse usuário")
    public void cadastroUmUsuarioComOEmailDesseUsuario() {
        Usuario novo = DataFactory.novoUsuario(Perfil.CLIENTE);
        novo.setEmail(context.getUsuario().getEmail()); // e-mail repetido de propósito
        cadastrar(novo);
    }

    /**
     * Um campo com valor inválido; os outros, válidos. Ex.: campo "email" igual a
     * "fulano.qa.com"
     */
    @Quando("cadastro um usuário com o campo {string} igual a {string}")
    public void cadastroUmUsuarioComOCampoIgualA(String campo, String valor) {
        Map<String, Object> corpo = comoMapa(DataFactory.novoUsuario(Perfil.CLIENTE));
        corpo.put(campo, valor);
        cadastrar(corpo);
    }

    /** Envia true SEM aspas (booleano), que a API não aceita (OBS-07). */
    @Quando("cadastro um usuário com o campo {string} igual ao booleano true")
    public void cadastroUmUsuarioComOCampoIgualAoBooleanoTrue(String campo) {
        Map<String, Object> corpo = comoMapa(DataFactory.novoUsuario(Perfil.CLIENTE));
        corpo.put(campo, true);
        cadastrar(corpo);
    }

    /** Campo que não existe no contrato da API (RG-03). */
    @Quando("cadastro um usuário com o campo a mais {string}")
    public void cadastroUmUsuarioComOCampoAMais(String campo) {
        Map<String, Object> corpo = comoMapa(DataFactory.novoUsuario(Perfil.CLIENTE));
        corpo.put(campo, "valor qualquer");
        cadastrar(corpo);
    }

    // ---------- Ações: consulta ----------

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

    // ---------- Ações: alteração ----------

    @Quando("altero o nome desse usuário para {string}")
    public void alteroONomeDesseUsuarioPara(String novoNome) {
        Usuario usuario = context.getUsuario();
        Usuario corpo = semId(usuario);
        corpo.setNome(novoNome);
        context.setResponse(userClient.update(usuario.getId(), corpo));
    }

    @Quando("altero o e-mail desse usuário para o e-mail do outro usuário")
    public void alteroOEmailDesseUsuarioParaOEmailDoOutroUsuario() {
        Usuario usuario = context.getUsuario();
        Usuario corpo = semId(usuario);
        corpo.setEmail(context.getOutroUsuario().getEmail());
        context.setResponse(userClient.update(usuario.getId(), corpo));
    }

    /**
     * OBS-02: com id inexistente, a API cria um usuário novo. Ele entra na faxina.
     */
    @Quando("altero o usuário de id {string} com dados válidos")
    public void alteroOUsuarioDeIdComDadosValidos(String id) {
        Response response = userClient.update(id, DataFactory.novoUsuario(Perfil.CLIENTE));
        registrarSeCriou(response);
        context.setResponse(response);
    }

    // ---------- Ações: exclusão ----------

    @Quando("excluo esse usuário")
    public void excluoEsseUsuario() {
        context.setResponse(userClient.delete(context.getUsuario().getId()));
    }

    @Quando("excluo o usuário de id {string}")
    public void excluoOUsuarioDeId(String id) {
        context.setResponse(userClient.delete(id));
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

    /**
     * Confere a alteração com uma consulta NOVA (não confia só na mensagem do PUT).
     */
    @Entao("a consulta desse usuário deve mostrar o nome {string}")
    public void aConsultaDesseUsuarioDeveMostrarONome(String nomeEsperado) {
        Response consulta = userClient.getById(context.getUsuario().getId());
        Assertions.assertEquals(200, consulta.statusCode(), "Falha ao consultar o usuário: " + consulta.asString());
        Assertions.assertEquals(nomeEsperado, consulta.jsonPath().getString("nome"),
                "O nome não foi alterado.");
    }

    /**
     * Confere a exclusão com uma consulta NOVA: o usuário não pode mais ser
     * encontrado.
     */
    @Entao("esse usuário não deve mais existir")
    public void esseUsuarioNaoDeveMaisExistir() {
        Response consulta = userClient.getById(context.getUsuario().getId());
        Assertions.assertEquals(400, consulta.statusCode(),
                "O usuário ainda existe depois da exclusão: " + consulta.asString());
        Assertions.assertEquals("Usuário não encontrado", consulta.jsonPath().getString("message"));
    }

    // ---------- Apoio ----------

    /**
     * Cria o usuário e confere o 201 como PRÉ-CONDIÇÃO: se falhar, o cenário para
     * no Dado
     * com uma mensagem clara. Registra o id para a faxina do @After.
     */
    public Usuario criarUsuario(Perfil perfil) {
        Usuario usuario = DataFactory.novoUsuario(perfil);
        Response response = userClient.create(usuario);
        Assertions.assertEquals(201, response.statusCode(),
                "Pré-condição: não foi possível cadastrar o usuário. Resposta: " + response.asString());

        usuario.setId(response.jsonPath().getString("_id"));
        context.registrarUsuario(usuario.getId()); // o @After vai excluir no final
        return usuario;
    }

    /**
     * Cadastro como AÇÃO do cenário (o resultado pode ser 201 ou erro: quem confere
     * é o Então).
     */
    private void cadastrar(Object corpo) {
        Response response = userClient.create(corpo);
        registrarSeCriou(response);
        context.setResponse(response);
    }

    /**
     * Qualquer 201 cria um registro: ele entra na faxina, mesmo num cenário de
     * erro.
     */
    private void registrarSeCriou(Response response) {
        if (response.statusCode() == 201) {
            context.registrarUsuario(response.jsonPath().getString("_id"));
        }
    }

    /**
     * Cópia do usuário SEM o id: no PUT o id vai na URL, e no corpo a API recusaria
     * (RG-03).
     */
    private Usuario semId(Usuario usuario) {
        return new Usuario(usuario.getNome(), usuario.getEmail(), usuario.getPassword(), usuario.getAdministrador());
    }

    /** Usuário como Map, para os cenários que precisam "quebrar" o JSON. */
    private Map<String, Object> comoMapa(Usuario usuario) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", usuario.getNome());
        corpo.put("email", usuario.getEmail());
        corpo.put("password", usuario.getPassword());
        corpo.put("administrador", usuario.getAdministrador());
        return corpo;
    }
}