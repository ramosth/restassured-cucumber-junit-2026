# language: pt
@produtos @produtos-cadastro
Funcionalidade: Cadastrar produto
  Como administrador
  Quero cadastrar produtos no catálogo
  Para que os clientes possam comprá-los

  @regression
  Cenário: US06-CA01 - Cadastro por administrador com dados válidos
    Dado que estou logado como administrador
    Quando cadastro um produto com dados válidos
    Então o status da resposta deve ser 201
    E a mensagem deve ser "Cadastro realizado com sucesso"
    E a resposta deve trazer o id do registro cadastrado

  @regression
  Cenário: US06-CA02 - Cadastro sem token
    Dado que não estou logado
    Quando cadastro um produto com dados válidos
    Então o status da resposta deve ser 401
    E a mensagem deve ser "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  @regression
  Cenário: US06-CA03 - Cadastro com token inválido
    Dado que estou com o token "Bearer token_invalido"
    Quando cadastro um produto com dados válidos
    Então o status da resposta deve ser 401
    E a mensagem deve ser "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  @regression
  Cenário: US06-CA04 - Cadastro por cliente
    Dado que estou logado como cliente
    Quando cadastro um produto com dados válidos
    Então o status da resposta deve ser 403
    E a mensagem deve ser "Rota exclusiva para administradores"

  @regression
  Cenário: US06-CA05 - Cadastro com nome já cadastrado
    Dado que estou logado como administrador
    E que existe um produto cadastrado
    Quando cadastro um produto com o nome desse produto
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Já existe produto com esse nome"

  @regression
  Esquema do Cenário: <id> - <descricao>
    Dado que estou logado como administrador
    Quando cadastro um produto com preço <preco> e estoque <estoque>
    Então o status da resposta deve ser 400
    E o erro do campo "<campo>" deve ser "<mensagem>"

    Exemplos:
      | id         | descricao            | preco | estoque | campo      | mensagem                               |
      | US06-CA06a | Preço zero           | 0     | 10      | preco      | preco deve ser um número positivo      |
      | US06-CA06b | Preço negativo       | -1    | 10      | preco      | preco deve ser um número positivo      |
      | US06-CA07a | Preço com decimais   | 10.5  | 10      | preco      | preco deve ser um inteiro              |
      | US06-CA07b | Estoque com decimais | 100   | 1.5     | quantidade | quantidade deve ser um inteiro         |
      | US06-CA08  | Estoque negativo     | 100   | -1      | quantidade | quantidade deve ser maior ou igual a 0 |

  @regression
  Cenário: US06-CA09 - Cadastro com estoque zero é aceito
    Dado que estou logado como administrador
    Quando cadastro um produto com preço 100 e estoque 0
    Então o status da resposta deve ser 201
    E a mensagem deve ser "Cadastro realizado com sucesso"

  # OBS-06: a API só bloqueia nomes iguais com as mesmas maiúsculas. O cenário falha até o defeito ser corrigido.
  @expected-failure
  Cenário: US06-CA10 - Nome já cadastrado escrito em minúsculas deve ser recusado
    Dado que estou logado como administrador
    E que existe um produto cadastrado
    Quando cadastro um produto com o nome desse produto em letras minúsculas
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Já existe produto com esse nome"
