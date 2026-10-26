# User stories e critérios de aceite: ServeRest

> Etapa 2 do projeto. Cada história vem dos requisitos em [requisitos.md](requisitos.md) e
> cita os IDs das regras (RG, RN, OBS). Na etapa 3, cada critério de aceite vira um ou mais
> cenários em Gherkin.

## Como ler

| Parte | Significado |
|---|---|
| **Como / Quero / Para** | Quem usa, o que precisa e por quê (o valor de negócio) |
| **Critérios de aceite (CA)** | Condições objetivas para a história ser considerada pronta. Cada CA tem resultado esperado (status + mensagem) |
| **Regras** | IDs do requisitos.md cobertos pela história |
| **Feature / tag** | Onde os cenários vão morar na etapa 3 |

**Personas:**

| Persona | Quem é |
|---|---|
| Visitante | Quem ainda não tem conta ou não fez login |
| Cliente | Usuário com `administrador: "false"` |
| Administrador | Usuário com `administrador: "true"`; mantém o catálogo |

---

## Épico 1: Acesso

### US01 - Login
**Como** usuário cadastrado (cliente ou administrador)
**Quero** entrar com meu e-mail e senha
**Para** receber um token e usar as funções protegidas da loja

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Login com e-mail e senha corretos | 200 `Login realizado com sucesso` e `authorization` começando com `Bearer ` | RN-LOG-01 |
| CA02 | Senha errada | 401 `Email e/ou senha inválidos` | RN-LOG-02 |
| CA03 | E-mail não cadastrado | 401 `Email e/ou senha inválidos` | RN-LOG-02 |
| CA04 | E-mail em formato inválido | 400 `email deve ser um email válido` | RN-LOG-03 |
| CA05 | E-mail e/ou senha em branco | 400 `email não pode ficar em branco` / `password não pode ficar em branco` | RN-LOG-04, RG-02 |
| CA06 | E-mail e/ou senha ausentes | 400 `email é obrigatório` / `password é obrigatório` | RN-LOG-04, RG-01 |
| CA07 | Token de usuário que foi excluído não vale mais | 401 `Token de acesso ausente, inválido, expirado ou usuário do token não existe mais` | RN-LOG-06, RG-06 |

Feature: `login/login.feature` · tag `@login`

---

## Épico 2: Usuários

### US02 - Cadastrar usuário
**Como** visitante
**Quero** criar a minha conta como cliente ou administrador
**Para** poder fazer login na loja

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Cadastro de cliente com dados válidos | 201 `Cadastro realizado com sucesso` + `_id` | RN-USU-01 |
| CA02 | Cadastro de administrador com dados válidos | 201 `Cadastro realizado com sucesso` + `_id` | RN-USU-01 |
| CA03 | E-mail já cadastrado | 400 `Este email já está sendo usado` | RN-USU-02 |
| CA04 | `administrador` diferente de `"true"`/`"false"` (incluindo o booleano `true`) | 400 `administrador deve ser 'true' ou 'false'` | RN-USU-03, OBS-07 |
| CA05 | E-mail em formato inválido | 400 `email deve ser um email válido` | — |
| CA06 | Cada campo obrigatório em branco (nome, email, password) | 400 `<campo> não pode ficar em branco` | RG-02 |
| CA07 | Campo que não existe no contrato | 400 `<campo> não é permitido` | RG-03 |

Feature: `usuarios/cadastro.feature` · tag `@usuarios`

### US03 - Consultar usuários
**Como** consumidor da API
**Quero** listar usuários e buscar um usuário pelo id
**Para** conferir os dados cadastrados

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Busca por id existente | 200 com nome, email e administrador iguais aos cadastrados | — |
| CA02 | Busca por id inexistente (16 caracteres) | 400 `Usuário não encontrado` | RN-USU-04, OBS-01 |
| CA03 | Busca com id em formato inválido | 400 `id deve ter exatamente 16 caracteres alfanuméricos` | RG-05 |
| CA04 | Listagem filtrada por e-mail | 200 com `quantidade: 1` e o usuário cadastrado | RG-10 |
| CA05 | A senha **não** deveria aparecer nas consultas | campo `password` ausente | OBS-04 (falha esperada) |

Feature: `usuarios/consulta.feature` · tag `@usuarios`

### US04 - Alterar usuário
**Como** usuário cadastrado
**Quero** alterar os meus dados
**Para** mantê-los atualizados

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Alteração com dados válidos | 200 `Registro alterado com sucesso` e a consulta mostra os dados novos | RN-USU-05 |
| CA02 | Alterar para um e-mail já usado por outro usuário | 400 `Este email já está sendo usado` | RN-USU-02 |
| CA03 | Alteração com id inexistente **cria** um usuário novo, com outro id | 201 `Cadastro realizado com sucesso` + `_id` diferente | RN-USU-06, OBS-02 |

Feature: `usuarios/alteracao.feature` · tag `@usuarios`

### US05 - Excluir usuário
**Como** usuário cadastrado
**Quero** excluir a minha conta
**Para** deixar de ter dados na loja

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Exclusão de usuário sem carrinho | 200 `Registro excluído com sucesso` e a consulta não encontra mais o usuário | RN-USU-08 |
| CA02 | Exclusão de usuário com carrinho | 400 `Não é permitido excluir usuário com carrinho cadastrado` + `idCarrinho` | RN-USU-07 |
| CA03 | Exclusão de id inexistente | 200 `Nenhum registro excluído` | RG-08, OBS-03 |

Feature: `usuarios/exclusao.feature` · tag `@usuarios`

---

## Épico 3: Produtos

### US06 - Cadastrar produto
**Como** administrador
**Quero** cadastrar produtos no catálogo
**Para** que os clientes possam comprá-los

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Cadastro por administrador com dados válidos | 201 `Cadastro realizado com sucesso` + `_id` | RN-PRO-01 |
| CA02 | Cadastro sem token | 401 `Token de acesso ausente, inválido, expirado ou usuário do token não existe mais` | RN-PRO-02 |
| CA03 | Cadastro com token inválido | 401 (mesma mensagem) | RN-PRO-02 |
| CA04 | Cadastro por cliente | 403 `Rota exclusiva para administradores` | RN-PRO-03 |
| CA05 | Nome já cadastrado | 400 `Já existe produto com esse nome` | RN-PRO-04 |
| CA06 | `preco` 0 ou negativo | 400 `preco deve ser um número positivo` | RN-PRO-05 |
| CA07 | `preco` ou `quantidade` com decimais | 400 `<campo> deve ser um inteiro` | RN-PRO-06 |
| CA08 | `quantidade` negativa | 400 `quantidade deve ser maior ou igual a 0` | RN-PRO-07 |
| CA09 | `quantidade` 0 é aceita (produto sem estoque) | 201 | RN-PRO-07 |
| CA10 | Nome igual a um existente mudando só maiúsculas/minúsculas **deveria** ser recusado | 400 `Já existe produto com esse nome` | OBS-06 (falha esperada) |

Feature: `produtos/cadastro.feature` · tag `@produtos`

### US07 - Consultar produtos
**Como** visitante
**Quero** ver o catálogo e os detalhes de um produto
**Para** escolher o que comprar

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Consulta sem login | 200 com a lista de produtos | — |
| CA02 | Busca por id existente | 200 com nome, preco, descricao e quantidade iguais aos cadastrados | — |
| CA03 | Busca por id inexistente | 400 `Produto não encontrado` | RN-PRO-08, OBS-01 |

Feature: `produtos/consulta.feature` · tag `@produtos`

### US08 - Alterar produto
**Como** administrador
**Quero** alterar preço, descrição e estoque de um produto
**Para** manter o catálogo correto

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Alteração por administrador | 200 `Registro alterado com sucesso` e a consulta mostra os dados novos | — |
| CA02 | Alteração por cliente | 403 `Rota exclusiva para administradores` | RN-PRO-03 |
| CA03 | Alterar para um nome já usado por outro produto | 400 `Já existe produto com esse nome` | RN-PRO-04 |
| CA04 | Alteração com id inexistente **cria** um produto novo, com outro id | 201 + `_id` diferente | RN-PRO-10, OBS-02 |

Feature: `produtos/alteracao.feature` · tag `@produtos`

### US09 - Excluir produto
**Como** administrador
**Quero** retirar produtos do catálogo
**Para** não vender o que não existe mais

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Exclusão por administrador de produto fora de carrinhos | 200 `Registro excluído com sucesso` | — |
| CA02 | Exclusão por cliente | 403 `Rota exclusiva para administradores` | RN-PRO-03 |
| CA03 | Exclusão de produto que está em um carrinho | 400 `Não é permitido excluir produto que faz parte de carrinho` + `idCarrinhos` | RN-PRO-09 |

Feature: `produtos/exclusao.feature` · tag `@produtos`

---

## Épico 4: Carrinho e compra

### US10 - Montar carrinho
**Como** cliente logado
**Quero** colocar produtos no meu carrinho
**Para** reservar o que vou comprar

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Carrinho com produtos válidos | 201 `Cadastro realizado com sucesso` + `_id` | RN-CAR-01 |
| CA02 | Carrinho sem token | 401 `Token de acesso ausente, ...` | RG-06 |
| CA03 | Segundo carrinho para o mesmo usuário | 400 `Não é permitido ter mais de 1 carrinho` | RN-CAR-02 |
| CA04 | Mesmo produto duas vezes | 400 `Não é permitido possuir produto duplicado` | RN-CAR-03 |
| CA05 | Produto inexistente | 400 `Produto não encontrado` | RN-CAR-04 |
| CA06 | Quantidade maior que o estoque | 400 `Produto não possui quantidade suficiente` | RN-CAR-05 |
| CA07 | Quantidade 0 | 400 `produtos[0].quantidade deve ser um número positivo` | RN-CAR-06 |
| CA08 | Totais calculados: `precoTotal` = Σ preço × quantidade; `quantidadeTotal` = Σ quantidades | ex.: 2 × 100 + 1 × 50 = **250**; 2 + 1 = **3** | RN-CAR-08, RN-CAR-09 |
| CA09 | O estoque do produto diminui na quantidade reservada | estoque 10 → carrinho com 3 → estoque 7 | RN-CAR-07 |

Feature: `carrinhos/montar.feature` · tag `@carrinhos`

### US11 - Concluir compra
**Como** cliente com carrinho
**Quero** concluir a compra
**Para** finalizar o pedido

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Concluir com carrinho | 200 `Registro excluído com sucesso` e o carrinho não existe mais | RN-CAR-10 |
| CA02 | O estoque continua baixado depois de concluir | estoque 10 → carrinho com 3 → concluir → estoque **7** | RN-CAR-10 |
| CA03 | Concluir sem ter carrinho | 200 `Não foi encontrado carrinho para esse usuário` | RN-CAR-12, OBS-08 |

Feature: `carrinhos/concluir.feature` · tag `@carrinhos`

### US12 - Cancelar compra
**Como** cliente com carrinho
**Quero** cancelar a compra
**Para** desistir do pedido e liberar os produtos

| CA | Critério | Resultado esperado | Regras |
|---|---|---|---|
| CA01 | Cancelar com carrinho | 200 `Registro excluído com sucesso. Estoque dos produtos reabastecido` | RN-CAR-11 |
| CA02 | O estoque volta ao valor original | estoque 10 → carrinho com 3 → cancelar → estoque **10** | RN-CAR-11 |
| CA03 | Cancelar sem ter carrinho | 200 `Não foi encontrado carrinho para esse usuário` | RN-CAR-12, OBS-08 |

Feature: `carrinhos/cancelar.feature` · tag `@carrinhos`

---

## Épico 5: Contrato

### US13 - Contrato das respostas
**Como** time que consome a API
**Quero** que o formato das respostas (campos e tipos) não mude sem aviso
**Para** que as integrações não quebrem

| CA | Critério | Resultado esperado |
|---|---|---|
| CA01 | `GET /usuarios/{id}` segue o schema de usuário | campos `nome`, `email`, `password`, `administrador`, `_id`, todos texto |
| CA02 | `GET /produtos/{id}` segue o schema de produto | `nome`, `descricao`, `_id` texto; `preco`, `quantidade` inteiros |
| CA03 | `GET /carrinhos/{id}` segue o schema de carrinho | `produtos[]` com `idProduto`, `quantidade`, `precoUnitario`; `precoTotal`, `quantidadeTotal`, `idUsuario`, `_id` |
| CA04 | `POST /login` segue o schema de login | `message` e `authorization` texto |

Feature: `contrato/contrato.feature` · tag `@contrato`

---

## Fora do escopo

| Item | Motivo |
|---|---|
| Teste de carga | A própria ServeRest pede para não fazer; não é o foco do projeto |
| Expiração do token (10 min) | Deixaria a suíte lenta; a regra está documentada (RN-LOG-05) |
| Filtros combinados da listagem | Baixo valor; um filtro (e-mail) já demonstra a regra |

## Resumo

| Épico | Histórias | Critérios | Falhas esperadas |
|---|---|---|---|
| Acesso | US01 | 7 | — |
| Usuários | US02–US05 | 18 | US03-CA05 |
| Produtos | US06–US09 | 20 | US06-CA10 |
| Carrinho e compra | US10–US12 | 15 | — |
| Contrato | US13 | 4 | — |
| **Total** | **13** | **64** | **2** |

**Comportamentos registrados como observação, mas automatizados como estão hoje** (o teste
documenta o comportamento atual): OBS-01, OBS-02, OBS-03, OBS-07, OBS-08. Só OBS-04 e OBS-06
viram `@expected-failure`, porque são as que um time trataria como defeito (segurança e
regra de unicidade).
