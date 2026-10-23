# Requisitos e regras de negócio: ServeRest

> Etapa 1 do projeto. Levantamento feito em 23/10/2026 na **ServeRest 3.2.2** rodando local,
> com base no Swagger, no código-fonte do pacote e em chamadas reais (cada regra abaixo foi
> executada e conferida: status code + mensagem).

## 1. Contexto

A ServeRest simula uma **loja virtual**: pessoas se cadastram (como cliente ou administrador),
fazem login, administradores mantêm o catálogo de produtos e clientes montam um carrinho e
concluem ou cancelam a compra.

| Recurso | O que representa |
|---|---|
| `/login` | Autenticação; devolve um token JWT |
| `/usuarios` | Cadastro de pessoas (cliente ou administrador) |
| `/produtos` | Catálogo da loja |
| `/carrinhos` | Carrinho de compras (um por usuário) |

## 2. Ambiente

| Item | Valor |
|---|---|
| Subir local (npm) | `npx serverest@3.2.2` (precisa de Node >= 22) |
| Subir local (Docker) | `docker run -d --rm --name serverest -p 3000:3000 paulogoncalvesbh/serverest:3.2.2` |
| URL base | `http://localhost:3000` |
| Documentação (Swagger) | `http://localhost:3000` (abre sozinha ao subir) |
| Opções úteis | `--nodoc` (não abre o navegador), `-t 3600` (token de 1 h), `-p 3500` (outra porta) |

**Persistência dos dados (conferido na prática):**

| Forma | Os dados somem ao reiniciar? |
|---|---|
| `npx` | **Não.** Os arquivos `.db` ficam na pasta do pacote; o que um teste cria continua lá na próxima execução |
| Docker com `--rm` | **Sim.** Cada `docker run` começa com os dados de fábrica |
| serverest.dev | Apagados uma vez por dia, mas compartilhados com todo mundo |

➡️ Consequência para a automação: **cada cenário cria os próprios dados com valores únicos e
apaga o que criou no final**. Nunca depender de dados de outro cenário nem dos dados de fábrica
(exceto o administrador semente para casos pontuais).

**Dados de fábrica:**

| Tipo | Dado |
|---|---|
| Usuário administrador | `fulano@qa.com` / `teste` (já possui um carrinho) |
| Produtos | `Logitech MX Vertical` (R$ 470) e `Samsung 60 polegadas` (R$ 5240) |

## 3. Regras gerais (valem para todos os recursos)

| ID | Regra | Resposta |
|---|---|---|
| RG-01 | Corpo com campo obrigatório ausente | 400 `{"campo": "campo é obrigatório"}` |
| RG-02 | Campo texto vazio | 400 `{"campo": "campo não pode ficar em branco"}` |
| RG-03 | Campo que não existe no contrato | 400 `{"campo": "campo não é permitido"}` |
| RG-04 | Vários erros de validação | 400 com **todos** os erros no mesmo JSON (um por campo) |
| RG-05 | `id` na URL sem 16 caracteres alfanuméricos | 400 `{"id": "id deve ter exatamente 16 caracteres alfanuméricos"}` |
| RG-06 | Rota protegida sem token, token inválido, sem a palavra `Bearer`, expirado ou de usuário que não existe mais | 401 `Token de acesso ausente, inválido, expirado ou usuário do token não existe mais` |
| RG-07 | Rota exclusiva de administrador chamada por cliente | 403 `Rota exclusiva para administradores` |
| RG-08 | Exclusão de `id` inexistente | **200** `Nenhum registro excluído` |
| RG-09 | Busca por `id` inexistente | **400** `<Recurso> não encontrado` |
| RG-10 | Filtros por query string (`?nome=`) em `nome`, `password` e `descricao` são parciais e sem diferenciar maiúsculas | `?nome=fulano` encontra "Fulano da Silva" |

## 4. Login (`/login`)

| Método | Rota | Token | Sucesso |
|---|---|---|---|
| POST | `/login` | não | 200 `Login realizado com sucesso` + `authorization: "Bearer <jwt>"` |

| ID | Regra | Resposta |
|---|---|---|
| RN-LOG-01 | E-mail e senha de usuário cadastrado | 200 + token |
| RN-LOG-02 | Senha errada ou e-mail não cadastrado | 401 `Email e/ou senha inválidos` |
| RN-LOG-03 | E-mail em formato inválido | 400 `email deve ser um email válido` |
| RN-LOG-04 | E-mail e/ou senha ausentes ou em branco | 400 (RG-01 / RG-02) |
| RN-LOG-05 | O token vale **600 s (10 min)** por padrão; depois disso, 401 nas rotas protegidas | 401 (RG-06) |
| RN-LOG-06 | O token deixa de valer se o usuário for excluído ou trocar de e-mail/senha | 401 (RG-06) |

## 5. Usuários (`/usuarios`)

| Método | Rota | Token | Sucesso |
|---|---|---|---|
| GET | `/usuarios` | não | 200 `{quantidade, usuarios[]}` (aceita filtros `_id`, `nome`, `email`, `password`, `administrador`) |
| GET | `/usuarios/{id}` | não | 200 com o usuário |
| POST | `/usuarios` | não | 201 `Cadastro realizado com sucesso` + `_id` |
| PUT | `/usuarios/{id}` | não | 200 `Registro alterado com sucesso` |
| DELETE | `/usuarios/{id}` | não | 200 `Registro excluído com sucesso` |

Contrato do corpo (POST/PUT), **todos obrigatórios**:

| Campo | Tipo | Regra |
|---|---|---|
| `nome` | texto | não pode ser vazio |
| `email` | texto | formato de e-mail; **único** |
| `password` | texto | não pode ser vazio |
| `administrador` | **texto** | só `"true"` ou `"false"` (com aspas; o booleano `true` é recusado) |

| ID | Regra | Resposta |
|---|---|---|
| RN-USU-01 | Cadastro válido | 201 + `_id` |
| RN-USU-02 | E-mail já usado por outro usuário (no POST e no PUT) | 400 `Este email já está sendo usado` |
| RN-USU-03 | `administrador` diferente de `"true"`/`"false"` | 400 `administrador deve ser 'true' ou 'false'` |
| RN-USU-04 | Busca por id inexistente | 400 `Usuário não encontrado` |
| RN-USU-05 | Alteração de usuário existente | 200 `Registro alterado com sucesso` |
| RN-USU-06 | **PUT com id inexistente cria um usuário novo** | 201 `Cadastro realizado com sucesso` + `_id` **diferente** do id da URL |
| RN-USU-07 | Exclusão de usuário que tem carrinho | 400 `Não é permitido excluir usuário com carrinho cadastrado` + `idCarrinho` |
| RN-USU-08 | Exclusão de usuário sem carrinho | 200 `Registro excluído com sucesso` |

## 6. Produtos (`/produtos`)

| Método | Rota | Token | Sucesso |
|---|---|---|---|
| GET | `/produtos` | não | 200 `{quantidade, produtos[]}` |
| GET | `/produtos/{id}` | não | 200 com o produto |
| POST | `/produtos` | **admin** | 201 `Cadastro realizado com sucesso` + `_id` |
| PUT | `/produtos/{id}` | **admin** | 200 `Registro alterado com sucesso` |
| DELETE | `/produtos/{id}` | **admin** | 200 `Registro excluído com sucesso` |

Contrato do corpo (POST/PUT):

| Campo | Tipo | Regra |
|---|---|---|
| `nome` | texto | obrigatório; **único**; espaços nas pontas são removidos |
| `preco` | inteiro | obrigatório; **maior que 0**; sem casas decimais |
| `descricao` | texto | obrigatório; não pode ser vazio |
| `quantidade` | inteiro | obrigatório; **maior ou igual a 0** |
| `imagem` | texto | opcional |

| ID | Regra | Resposta |
|---|---|---|
| RN-PRO-01 | Cadastro por administrador | 201 + `_id` |
| RN-PRO-02 | Cadastro/alteração/exclusão sem token ou com token inválido | 401 (RG-06) |
| RN-PRO-03 | Cadastro/alteração/exclusão por cliente | 403 `Rota exclusiva para administradores` |
| RN-PRO-04 | Nome já usado por outro produto (POST e PUT) | 400 `Já existe produto com esse nome` |
| RN-PRO-05 | `preco` 0 ou negativo | 400 `preco deve ser um número positivo` |
| RN-PRO-06 | `preco` ou `quantidade` com decimais | 400 `... deve ser um inteiro` |
| RN-PRO-07 | `quantidade` negativa | 400 `quantidade deve ser maior ou igual a 0` |
| RN-PRO-08 | Busca por id inexistente | 400 `Produto não encontrado` |
| RN-PRO-09 | Exclusão de produto que está em algum carrinho | 400 `Não é permitido excluir produto que faz parte de carrinho` + `idCarrinhos` |
| RN-PRO-10 | PUT com id inexistente cria um produto novo | 201 + `_id` novo |

## 7. Carrinhos (`/carrinhos`)

| Método | Rota | Token | Sucesso |
|---|---|---|---|
| GET | `/carrinhos` | não | 200 `{quantidade, carrinhos[]}` (filtros `_id`, `idUsuario`, `precoTotal`, `quantidadeTotal`) |
| GET | `/carrinhos/{id}` | não | 200 com o carrinho |
| POST | `/carrinhos` | **qualquer usuário** | 201 `Cadastro realizado com sucesso` + `_id` |
| DELETE | `/carrinhos/concluir-compra` | **qualquer usuário** | 200 `Registro excluído com sucesso` |
| DELETE | `/carrinhos/cancelar-compra` | **qualquer usuário** | 200 `Registro excluído com sucesso. Estoque dos produtos reabastecido` |

Corpo do POST: `{"produtos": [{"idProduto": "...", "quantidade": 1}]}` (quantidade inteira > 0).
O carrinho é sempre **do usuário dono do token** (não se informa o usuário no corpo).

| ID | Regra | Resposta |
|---|---|---|
| RN-CAR-01 | Cadastro válido | 201 + `_id` |
| RN-CAR-02 | **Cada usuário só pode ter 1 carrinho** | 400 `Não é permitido ter mais de 1 carrinho` |
| RN-CAR-03 | Mesmo produto repetido na lista | 400 `Não é permitido possuir produto duplicado` + `idProdutosDuplicados` |
| RN-CAR-04 | Produto inexistente | 400 `Produto não encontrado` + `item` |
| RN-CAR-05 | Quantidade maior que o estoque | 400 `Produto não possui quantidade suficiente` + `item.quantidadeEstoque` |
| RN-CAR-06 | Quantidade 0 ou negativa | 400 `produtos[0].quantidade deve ser um número positivo` |
| RN-CAR-07 | **Ao criar o carrinho, o estoque do produto diminui** na quantidade reservada | estoque 5 → carrinho com 2 → estoque 3 |
| RN-CAR-08 | `precoTotal` = soma de `precoUnitario × quantidade`; `quantidadeTotal` = soma das quantidades | 2 × 100 + 1 × 470 = **670**; 2 + 1 = **3** |
| RN-CAR-09 | `precoUnitario` é copiado do produto no momento da criação | — |
| RN-CAR-10 | **Concluir compra** exclui o carrinho e o estoque **continua baixado** | 200 `Registro excluído com sucesso` |
| RN-CAR-11 | **Cancelar compra** exclui o carrinho e **devolve o estoque** | 200 `... Estoque dos produtos reabastecido` |
| RN-CAR-12 | Concluir/cancelar sem ter carrinho | **200** `Não foi encontrado carrinho para esse usuário` |
| RN-CAR-13 | Busca por id inexistente | 400 `Carrinho não encontrado` |

## 8. Observações e possíveis defeitos

Pontos que fogem do que se espera de uma API REST. Ficam registrados (como os defeitos do
Sauce Demo nos outros repositórios) e podem virar cenários `@expected-failure`.

| ID | Observação | Esperado (boa prática) | Obtido |
|---|---|---|---|
| OBS-01 | Recurso inexistente responde 400 | 404 Not Found | 400 |
| OBS-02 | `PUT` com id inexistente cria registro com **outro** id | 404, ou criar com o id da URL | 201 com `_id` diferente |
| OBS-03 | `DELETE` de id inexistente responde sucesso | 404 | 200 `Nenhum registro excluído` |
| OBS-04 | **A senha aparece em texto puro** no `GET /usuarios` | nunca devolver a senha | `"password": "teste"` |
| OBS-05 | **O token JWT carrega a senha** (o payload em Base64 tem `email` e `password`) | só um identificador | `{"email":"...","password":"..."}` |
| OBS-06 | Nome de produto é único só com as mesmas maiúsculas: `teclado x` é aceito se já existe `Teclado X` | tratar como duplicado | 201 |
| OBS-07 | `administrador` é texto (`"true"`) e não booleano | `true` | `"true"` |
| OBS-08 | Concluir/cancelar sem carrinho responde 200 | 404 ou 400 | 200 |

## 9. Decisões para a automação

| Decisão | Motivo |
|---|---|
| Cada cenário cria usuário/produto com nome e e-mail únicos (Datafaker) | Dados persistem no `npx`; evita "e-mail já usado" entre execuções |
| Limpeza no `@After`: cancelar carrinho → excluir produto → excluir usuário (nessa ordem) | RN-USU-07 e RN-PRO-09 bloqueiam a exclusão fora de ordem |
| Não usar o `fulano@qa.com` para carrinho | Ele já tem um carrinho de fábrica (RN-CAR-02) |
| Validar sempre **status code + mensagem** | São os dois "resultados esperados" de cada regra |
| Token de 600 s é suficiente | A suíte inteira roda em bem menos tempo; cada cenário faz o próprio login |
| Versão fixa `serverest@3.2.2` | Resultado reproduzível; atualizar de propósito, não por acaso |
