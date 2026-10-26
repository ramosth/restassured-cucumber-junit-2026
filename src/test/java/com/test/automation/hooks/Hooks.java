package com.test.automation.hooks;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.test.automation.clients.UserClient;
import com.test.automation.context.TestContext;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.ApiLogCapture;
import com.test.automation.utils.Log;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

/**
 * Ciclo de vida da execução.
 *
 *   @BeforeAll -> cria o Extent Report (uma vez por execução)
 *   @Before    -> registra o cenário no Extent e começa a capturar o log da API
 *   @After     -> anexa o log da API aos relatórios, registra o resultado e
 *                 apaga os dados criados pelo cenário
 *   @AfterAll  -> grava o Extent Report em disco
 *
 * O log da API é o "print da tela" deste projeto: a requisição enviada e a
 * resposta recebida, anexadas ao Extent e ao relatório do Cucumber.
 */
public class Hooks {

    private final TestContext context;
    private final UserClient userClient = new UserClient();

    public Hooks(TestContext context) {
        this.context = context;
    }

    @BeforeAll
    public static void setupReport() {
        Log.info("Configurando o Extent Report antes da execução dos cenários.");
        ExtentReportManager.getReportInstance();
    }

    @AfterAll
    public static void tearDownReport() {
        ExtentReportManager.getReportInstance().flush();
        Log.info("Relatório Extent gerado em: " + ExtentReportManager.reportFilePath);
    }

    @Before
    public void setUp(Scenario scenario) {
        Log.info("Iniciando o cenário: " + scenario.getName());

        // Registro do cenário no Extent; as tags (@usuarios...) viram categorias no relatório
        ExtentTest test = ExtentReportManager.createTest(scenario.getName());
        for (String tag : scenario.getSourceTagNames()) {
            test.assignCategory(tag.replace("@", ""));
        }
        context.setExtentTest(test);

        ApiLogCapture.start();
    }

    @After
    public void tearDown(Scenario scenario) {
        // 1. Fecha o "caderno" ANTES da limpeza: as exclusões da faxina não fazem parte do teste
        String logDaApi = ApiLogCapture.stop();

        // 2. Anexa o log aos dois relatórios
        scenario.attach(logDaApi, "text/plain", "Requisições e respostas");
        ExtentTest test = context.getExtentTest();
        test.info(MarkupHelper.createCodeBlock(logDaApi));

        // 3. Resultado do cenário no Extent
        switch (scenario.getStatus()) {
            case PASSED -> test.pass("Cenário concluído com sucesso.");
            case FAILED -> {
                Log.error("O cenário falhou: " + scenario.getName());
                test.fail("Falha no cenário. A mensagem do erro está no relatório do Cucumber e no console; "
                        + "as requisições e respostas estão acima.");
            }
            // SKIPPED, UNDEFINED (frase sem step), PENDING...
            default -> test.skip("Cenário não executado por completo (status: " + scenario.getStatus() + ").");
        }

        // 4. Faxina
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