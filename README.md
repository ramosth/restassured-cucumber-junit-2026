# RestAssured + Cucumber + JUnit 5 (API ServeRest)

[![CI - RestAssured + Cucumber (JUnit 5)](https://github.com/ramosth/restassured-cucumber-junit-2026/actions/workflows/ci.yml/badge.svg)](https://github.com/ramosth/restassured-cucumber-junit-2026/actions/workflows/ci.yml)

Automação de testes de **API REST** com **Java 17, REST Assured, Cucumber (BDD, Gherkin em português) e JUnit 5**, usando a API de estudos [ServeRest](https://serverest.dev): uma loja virtual com login por token, usuários, produtos e carrinhos.

O projeto segue o fluxo completo de QA: **requisito → user story → cenário Gherkin → automação → esteira**, com rastreabilidade do critério de aceite até o resultado no relatório.

## Destaques

| | |
|---|---|
| **13** user stories | **64** critérios de aceite |
| **71** cenários automatizados | **69** na regressão (`@regression`) |
| **2** defeitos reais comprovados por teste (`@expected-failure`) | **4** contratos validados com JSON Schema |
| API sobe **dentro da esteira** (Docker) | Cada cenário cria e **apaga os próprios dados** |

## Stack

| Ferramenta | Versão | Papel |
|---|---|---|
| Java | 17 | Linguagem |
| Maven + Surefire | 3.9 / 3.5.4 | Build e execução |
| REST Assured | 6.0.1 | Requisições HTTP (`given / when / then`) |
| json-schema-validator | 6.0.1 | Testes de contrato |
| Cucumber | 7.34.8 | BDD (Gherkin em português) |
| JUnit 5 (Platform Suite) | 5.14.4 | Runner (`@Suite`) e asserções |
| PicoContainer | 7.34.8 | Injeção de dependência entre Steps e Hooks |
| Jackson | 2.22.4 | POJO ↔ JSON |
| Datafaker | 2.7.0 | Dados únicos a cada execução |
| Extent Reports / Log4j2 | 5.1.2 / 2.26.1 | Relatório HTML e log |
| ServeRest | 3.2.2 (Docker) | API testada |
| GitHub Actions / Jenkins | — | Esteiras com envio do resultado por e-mail |

## Do requisito ao teste

| Etapa | Onde está |
|---|---|
| 1. Requisitos e regras de negócio (RG, RN, OBS) levantados explorando a API | [docs/requisitos.md](docs/requisitos.md) |
| 2. User stories e critérios de aceite, com o ID das regras | [docs/user-stories.md](docs/user-stories.md) |
| 3. Cenários Gherkin: o nome do cenário é o ID do critério | [src/test/resources/features](src/test/resources/features) |
| 4. Automação (Steps → Clients → API) | [src/test/java](src/test/java) e [src/main/java](src/main/java) |

Exemplo da rastreabilidade: a regra **RN-PRO-03** (só administrador cadastra produto) vira o critério **US06-CA04**, que vira o cenário `US06-CA04 - Cadastro por cliente`. Se ele falhar, o nome no relatório já diz qual critério quebrou.

```gherkin
Cenário: US06-CA04 - Cadastro por cliente
  Dado que estou logado como cliente
  Quando cadastro um produto com dados válidos
  Então o status da resposta deve ser 403
  E a mensagem deve ser "Rota exclusiva para administradores"
```

## Cobertura

| Recurso | Feature | Cenários | Destaques |
|---|---|---|---|
| Login (US01) | `login/` | 9 | Token `Bearer`, campos ausentes × em branco, token de usuário excluído |
| Usuários (US02–US05) | `usuarios/` | 21 | CRUD, e-mail único, contrato do corpo, exclusão bloqueada por carrinho |
| Produtos (US06–US09) | `produtos/` | 22 | Permissões (401 × 403 × 201), validações numéricas, nome único |
| Carrinhos (US10–US12) | `carrinhos/` | 15 | Totais calculados, **estoque antes e depois** de concluir e de cancelar |
| Contrato (US13) | `contrato/` | 4 | JSON Schema de usuário, produto, carrinho e login |
| **Total** | | **71** | |

## Arquitetura

```
.feature  →  Steps  →  Client  →  BaseClient  →  ApiConfig  →  ServeRest
 (frase)    (regra)   (rota)     (requisição)    (URL, JSON)
               │                      └─ ApiLogFilter: anota cada requisição e resposta
               └─ TestContext: resposta, token, "esse usuário/produto/carrinho", lista da faxina

Hooks: Extent Report + log da API anexado aos relatórios + limpeza automática dos dados
```

| Camada | Na UI (projetos Selenium) | Aqui (API) |
|---|---|---|
| Configuração comum | `DriverFactory` | `ApiConfig` (RequestSpecification) |
| Objeto de página / recurso | Page Object | **Client** (`UserClient`, `ProductClient`...) |
| Evidência da falha | Print da tela | **Log da requisição e da resposta** |
| Dados | Usuários fixos do Sauce Demo | **Criados pelo cenário** (Datafaker) e apagados no final |

```
src
├── main/java/com/test/automation
│   ├── clients/      BaseClient, LoginClient, UserClient, ProductClient, CartClient
│   ├── config/       ApiConfig, ConfigReader
│   ├── data/         Usuario, Produto, Perfil, DataFactory
│   ├── reports/      ExtentReportManager
│   └── utils/        ApiLogFilter, ApiLogCapture, Log
└── test
    ├── java/com/test/automation
    │   ├── context/  TestContext
    │   ├── hooks/    Hooks
    │   ├── runners/  TestRunner
    │   └── steps/    Auth, User, Product, Cart, Response, ParameterTypes
    └── resources
        ├── features/ login, usuarios, produtos, carrinhos, contrato
        ├── schemas/  usuario, produto, carrinho, login (JSON Schema)
        ├── config.properties.example
        └── junit-platform.properties
```

## Decisões de projeto

| Decisão | Por quê |
|---|---|
| **Cada cenário cria os próprios dados** (e-mail e nome de produto únicos) | Os dados da ServeRest persistem entre execuções; dados fixos gerariam "e-mail já usado" e dependência entre cenários |
| **Faxina no `@After`**: cancela carrinhos → exclui produtos → exclui usuários | A API bloqueia excluir usuário com carrinho e produto em carrinho. Roda mesmo quando o cenário falha, e nunca derruba o resultado |
| **Administrador de apoio** | Cenários com cliente logado ainda precisam de um produto existente; um admin "de bastidores" prepara os dados sem aparecer no `.feature` |
| **Pré-condição confere o status** (`Dado`) | Se o preparo falhar, o cenário para no `Dado` com mensagem clara, em vez de falhar adiante sem explicação |
| **Validar com uma consulta nova** | Depois de alterar, excluir ou comprar, um GET confere o estado real (não basta a mensagem de sucesso) |
| **Filtro de log próprio** (`ApiLogFilter`) | Relatório enxuto: método, URL, token abreviado, corpo enviado, status, tempo e corpo recebido |
| **POJO para corpos válidos, `Map` para corpos "quebrados"** | Campo a mais, booleano no lugar de texto e preço decimal não cabem num POJO |
| **REST Assured 6.0.1** (e não 6.3) | Linha 6 estável; as versões 6.1–6.3 saíram semanalmente em outubro/2026, e a 6.2 tem quebras de compatibilidade |

## Como executar

**Pré-requisitos:** Java 17+, Maven e Docker.

```powershell
# 1. Subir a API (dados de fábrica a cada execução)
docker run -d --rm --name serverest -p 3000:3000 paulogoncalvesbh/serverest:3.2.2

# 2. Criar a configuração local
copy src\test\resources\config.properties.example src\test\resources\config.properties

# 3. Rodar
mvn clean test "-Dgroups=regression"

# 4. Desligar a API
docker stop serverest
```

O filtro usa as **tags do JUnit** (`-Dgroups`, sem o `@`):

| Comando | Roda |
|---|---|
| `mvn clean test` | Os 71 cenários (as 2 falhas esperadas aparecem em vermelho) |
| `"-Dgroups=regression"` | Os 69 da regressão |
| `"-Dgroups=smoke"` | 1 cenário: a API está no ar? |
| `"-Dgroups=carrinhos"` | Só um recurso (`login`, `usuarios`, `produtos`, `carrinhos`, `contrato`) |
| `"-Dgroups=produtos & !expected-failure"` | Combinações com `&`, `\|` e `!` |
| `"-Dgroups=expected-failure"` | Só os 2 defeitos conhecidos |
| `-Dbase.uri=http://localhost:3100` | Outra URL da API, sem mexer no `config.properties` |

## Relatórios

| Relatório | Onde | Conteúdo |
|---|---|---|
| Extent | `reports/ExtentReport_<data-hora>.html` | Cenários por tag (categorias) e o log das requisições de cada um |
| Cucumber | `reports/cucumber-report.html` | Passo a passo do Gherkin, com o anexo "Requisições e respostas" |
| Surefire | `target/surefire-reports/TEST-*.xml` | Formato JUnit, lido pelas esteiras |

Trecho do log anexado (o "print da tela" da API):

```
>>> POST http://localhost:3000/produtos
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
{
  "nome" : "Gorgeous Rubber Pants fbf31c1a",
  "preco" : 754,
  "descricao" : "Iron",
  "quantidade" : 68
}
<<< 201 Created (33 ms)
{
  "message" : "Cadastro realizado com sucesso",
  "_id" : "eMnC3U4XWw0Ls6rT"
}
```

## Esteiras (CI)

| | GitHub Actions | Jenkins |
|---|---|---|
| Arquivo | [.github/workflows/ci.yml](.github/workflows/ci.yml) | [Jenkinsfile](Jenkinsfile) |
| Quando roda | Push e PR na `master`; manual com tags | Polling do GitHub a cada ~5 min; manual com parâmetros |
| Como sobe a API | *Service container* `serverest:3.2.2` | `docker run` no stage "Subir a ServeRest" (porta 3100) e `docker stop` no `post` |
| Espera a API | `curl` até 30 × 2 s | PowerShell `Invoke-RestMethod` até 30 × 2 s |
| Resultado | Resumo pelos `TEST-*.xml` + artifacts | Plugin JUnit + artefatos |
| E-mail | `dawidd6/action-send-mail` (segredos `EMAIL_*`) | Email Extension (credenciais `gmail-smtp` e `email-recipients`) |

O envio de e-mail é responsabilidade das esteiras: o framework não tem nada de e-mail. O assunto já traz o resultado, por exemplo `[PASSOU] RestAssured + Cucumber (API) - regression - 69 passou, 0 falhou, 0 ignorado(s)`.

## Defeitos e observações encontrados

Os dois defeitos abaixo têm **cenário automatizado que falha de propósito** (`@expected-failure`): o teste documenta o comportamento esperado e comprova o defeito no relatório.

| ID | Defeito | Esperado | Obtido | Cenário |
|---|---|---|---|---|
| OBS-04 | **A senha aparece em texto puro** no `GET /usuarios` | Nunca devolver a senha | `"password": "h7ur7plpvs"` | US03-CA05 |
| OBS-06 | Nome de produto só é único com as mesmas maiúsculas | `teclado x` recusado se existe `Teclado X` | `201 Created` | US06-CA10 |

Comportamentos fora do padrão REST, automatizados **como estão hoje** (o teste documenta o comportamento atual):

| ID | Observação | Boa prática | ServeRest |
|---|---|---|---|
| OBS-01 | Recurso inexistente | `404 Not Found` | `400` |
| OBS-02 | `PUT` com id inexistente | `404` ou criar com o id da URL | Cria com **outro** id (`201`) |
| OBS-03 | `DELETE` de id inexistente | `404` | `200 Nenhum registro excluído` |
| OBS-05 | **O token JWT carrega a senha** no payload (Base64) | Só um identificador | `{"email": "...", "password": "..."}` |
| OBS-07 | `administrador` como texto | Booleano `true` | `"true"` |
| OBS-08 | Concluir ou cancelar sem carrinho | `404` ou `400` | `200` com mensagem |

Detalhes e evidências em [docs/requisitos.md](docs/requisitos.md#8-observações-e-possíveis-defeitos).

## Do Selenium para a API: o que aprendi

| Tema | Lição |
|---|---|
| Dados | Na UI os usuários eram fixos; na API cada cenário cria e apaga os seus. Isolamento é o que deixa a suíte confiável |
| Evidência | Sem tela, o "print" é o log da requisição e da resposta. Um log enxuto vale mais que um completo |
| Asserções | O Client não valida nada; o Step decide. O mesmo método testa o 201, o 401 e o 403 |
| Ambiente | Subir a API dentro da esteira (Docker) dá dados de fábrica a cada execução e elimina a dependência da internet |
| Esteira | O container liga antes de a API responder: sem o passo de espera, os primeiros cenários seriam *flaky* |
| Contrato | Teste funcional confere valores; contrato confere a estrutura. Um `minLength` num campo numérico é ignorado em silêncio, e o teste para de testar sem quebrar |
| Versões | Escolher a versão estável (e não a da semana) e tratar avisos de *deprecated* enquanto é barato |

## Portfólio

| Projeto | Foco |
|---|---|
| [selenium-automation-framework-2026](https://github.com/ramosth/selenium-automation-framework-2026) | Selenium + TestNG (UI) |
| [selenium-cucumber-testng-2026](https://github.com/ramosth/selenium-cucumber-testng-2026) | Selenium + Cucumber + TestNG (UI) |
| [selenium-cucumber-junit-2026](https://github.com/ramosth/selenium-cucumber-junit-2026) | Selenium + Cucumber + JUnit 5 (UI) |
| **restassured-cucumber-junit-2026** | **REST Assured + Cucumber + JUnit 5 (API)** |

---

Thamires Ramos · QA Analyst
