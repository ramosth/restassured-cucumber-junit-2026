package com.test.automation.hooks;

import com.test.automation.clients.UserClient;
import com.test.automation.context.TestContext;
import com.test.automation.utils.Log;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

/**
 * Ciclo de vida de cada cenário.
 *
 *   @Before -> registra no log o início do cenário
 *   @After  -> apaga os dados criados pelo cenário e registra o resultado
 *
 * O @After roda SEMPRE, mesmo quando o cenário falha. É justamente nesses casos
 * que sobraria "lixo" no ServeRest.
 *
 * Na etapa 5.4 os Hooks ganham o Extent Report (como nos projetos de UI).
 */
public class Hooks {

    private final TestContext context;
    private final UserClient userClient = new UserClient();

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        Log.info("Iniciando o cenário: " + scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        limparDados();
        Log.info("Cenário finalizado: " + scenario.getName() + " | Status: " + scenario.getStatus());
    }

    /**
     * Exclui o que o cenário criou, NESTA ordem (a API bloqueia a ordem contrária):
     *   1. carrinhos  -> entra na etapa 9 (cancelar a compra devolve o estoque)
     *   2. produtos   -> entra na etapa 8
     *   3. usuários
     *
     * Falha na limpeza NÃO derruba o cenário: só gera um aviso no log.
     * O resultado do cenário precisa refletir a regra testada, não a faxina.
     */
    private void limparDados() {
        for (String id : context.getUsuariosCriados()) {
            try {
                Response response = userClient.delete(id);
                // 200 com "Registro excluído com sucesso" ou "Nenhum registro excluído"
                // (o próprio cenário pode ter excluído o usuário, como no US05-CA01)
                if (response.statusCode() != 200) {
                    Log.warn("Limpeza: não foi possível excluir o usuário " + id + ". Resposta: " + response.asString());
                }
            } catch (Exception e) {
                Log.warn("Limpeza: erro ao excluir o usuário " + id + ": " + e.getMessage());
            }
        }
    }
}