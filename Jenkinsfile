// ============================================================
// Pipeline Jenkins - RestAssured + Cucumber BDD + JUnit 5 (API ServeRest)
//
// Pré-requisitos no Jenkins (Gerenciar Jenkins):
//   - Tools: Maven com o nome MAVEN-3.9.16 e JDK com o nome JDK-25
//   - Plugins: Pipeline, Git, JUnit, Timestamper, Credentials Binding, Email Extension
//   - Credentials:
//       gmail-smtp        -> Username with password (Gmail + senha de app), usada no SMTP
//       email-recipients  -> Secret text com os destinatários separados por vírgula
//   - System > Extended E-mail Notification: smtp.gmail.com, porta 465, SSL, credencial gmail-smtp
//   - Docker Desktop LIGADO na máquina do Jenkins (o pipeline sobe a ServeRest num container)
//
// A ServeRest sobe na porta 3100 (e não na 3000) para não colidir com o container
// do dia a dia. O teste recebe a URL por -Dbase.uri (o ConfigReader dá prioridade ao -D).
//
// Filtro de tags: usa as tags do JUnit (-Dgroups), SEM o @.
//   regression | smoke | login | usuarios | produtos & !expected-failure | expected-failure
//
// O envio do e-mail é responsabilidade da esteira (plugin Email Extension):
// o framework não tem nada de e-mail.
//
// Este Jenkins roda no Windows, por isso os comandos usam "bat" e "powershell" (no Linux seria "sh").
// ============================================================
pipeline {
    agent any

    tools {
        maven 'MAVEN-3.9.16'
        jdk 'JDK-25'
    }

    // Opções exibidas em "Construir com parâmetros"
    parameters {
        string(name: 'TAGS', defaultValue: 'regression',
               description: 'Tags do JUnit, sem @. Ex.: regression | smoke | login | usuarios | expected-failure')
        booleanParam(name: 'ENVIAR_EMAIL', defaultValue: true,
                     description: 'Enviar o resultado por e-mail ao final (plugin Email Extension)')
    }

    // Verifica o GitHub a cada ~5 minutos e roda se houver commit novo
    triggers {
        pollSCM('H/5 * * * *')
    }

    options {
        timestamps()                                    // horário em cada linha do log
        timeout(time: 20, unit: 'MINUTES')              // evita build travado para sempre
        buildDiscarder(logRotator(numToKeepStr: '20'))  // guarda só os 20 últimos builds
        disableConcurrentBuilds()                       // um build por vez (um container por vez)
    }

    environment {
        SERVEREST_IMAGEM    = 'paulogoncalvesbh/serverest:3.2.2'
        SERVEREST_CONTAINER = 'serverest-jenkins'
        SERVEREST_PORTA     = '3100'
    }

    stages {
        stage('Subir a ServeRest') {
            steps {
                // Remove um container que tenha sobrado de um build interrompido (se não existir, segue)
                bat 'docker rm -f %SERVEREST_CONTAINER% 2>nul || exit /b 0'
                bat 'docker run -d --rm --name %SERVEREST_CONTAINER% -p %SERVEREST_PORTA%:3000 %SERVEREST_IMAGEM%'

                // O container liga em segundos, mas a API demora um pouco para responder.
                // Pergunta a cada 2 segundos (até 30 vezes) antes de rodar os testes.
                powershell '''
                    for ($i = 1; $i -le 30; $i++) {
                        try {
                            Invoke-RestMethod "http://localhost:$env:SERVEREST_PORTA/usuarios" | Out-Null
                            Write-Output "ServeRest pronta (tentativa $i)."
                            exit 0
                        } catch {
                            Write-Output "Aguardando a ServeRest... (tentativa $i)"
                            Start-Sleep -Seconds 2
                        }
                    }
                    Write-Output "A ServeRest nao respondeu em 60 segundos."
                    exit 1
                '''
            }
        }

        stage('Preparar configuração') {
            steps {
                // O config.properties real fica fora do Git; no servidor usamos o modelo
                bat 'if not exist src\\test\\resources\\config.properties copy src\\test\\resources\\config.properties.example src\\test\\resources\\config.properties'
            }
        }

        stage('Executar cenários') {
            steps {
                // -Dmaven.test.failure.ignore=true: um cenário falhando não "quebra" o build;
                // o resultado (instável/sucesso) é decidido pelo plugin JUnit no post.
                // As aspas em volta do -Dgroups protegem os espaços e os símbolos & | ! no cmd do Windows.
                bat "mvn -B clean test \"-Dgroups=${params.TAGS}\" " +
                    "-Dbase.uri=http://localhost:%SERVEREST_PORTA% -Dmaven.test.failure.ignore=true"
            }
        }
    }

    post {
        always {
            // Desliga a ServeRest (o --rm apaga o container). Se ela nem subiu, segue sem erro.
            bat 'docker stop %SERVEREST_CONTAINER% 2>nul || exit /b 0'

            // Gráfico e detalhes dos cenários na página do build (cada cenário = 1 teste)
            junit(testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true)

            // Relatórios (Extent e Cucumber) e logs para download
            archiveArtifacts(artifacts: 'reports/**, logs/**, target/surefire-reports/**',
                             allowEmptyArchive: true)

            script {
                if (params.ENVIAR_EMAIL) {
                    // currentResult já considera o resultado do JUnit: SUCCESS, UNSTABLE (cenário falhou) ou FAILURE
                    def passou = currentBuild.currentResult == 'SUCCESS'
                    def status = passou ? 'PASSOU' : 'FALHOU'
                    def cor = passou ? '#27ae60' : '#c0392b'

                    withCredentials([string(credentialsId: 'email-recipients', variable: 'EMAIL_RECIPIENTS')]) {
                        emailext(
                            to: env.EMAIL_RECIPIENTS,
                            subject: "[${status}] RestAssured + Cucumber (API) - ${params.TAGS} - build #${env.BUILD_NUMBER}",
                            mimeType: 'text/html',
                            attachmentsPattern: 'reports/*.html',
                            // \${TEST_COUNTS...} tem a barra para o Groovy não interpretar:
                            // quem substitui esses valores é o plugin Email Extension
                            body: """
                                <h2 style="color:${cor}">Execução ${status}</h2>
                                <p>
                                  <b>Tags:</b> ${params.TAGS}<br>
                                  <b>API:</b> ServeRest 3.2.2 (container Docker no Jenkins, porta ${env.SERVEREST_PORTA})<br>
                                  <b>Build:</b> #${env.BUILD_NUMBER} (${currentBuild.currentResult})
                                </p>
                                <table border="1" cellpadding="6" style="border-collapse:collapse">
                                  <tr><th>Passou</th><th>Falhou</th><th>Ignorado</th><th>Total</th></tr>
                                  <tr align="center">
                                    <td>\${TEST_COUNTS,var="pass"}</td>
                                    <td>\${TEST_COUNTS,var="fail"}</td>
                                    <td>\${TEST_COUNTS,var="skip"}</td>
                                    <td>\${TEST_COUNTS,var="total"}</td>
                                  </tr>
                                </table>
                                <p>Os relatórios Extent e Cucumber estão em anexo.
                                   Logs: <a href="${env.BUILD_URL}">build no Jenkins</a> (Artefatos).</p>
                                <p>Atenciosamente,<br>Thamires, QA Team.</p>
                            """
                        )
                    }
                }
            }
        }
        unstable {
            echo 'Há cenários com falha: confira a aba "Resultados de testes" e os relatórios em "Artefatos".'
        }
    }
}