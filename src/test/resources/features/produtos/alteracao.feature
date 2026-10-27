# language: pt
@produtos @produtos-alteracao @regression
Funcionalidade: Alterar produto
  Como administrador
  Quero alterar preço, descrição e estoque de um produto
  Para manter o catálogo correto

  Cenário: US08-CA01 - Alteração por administrador
    Dado que estou logado como administrador
    E que existe um produto cadastrado com preço 100 e estoque 10
    Quando altero o preço desse produto para 150 e o estoque para 20
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Registro alterado com sucesso"
    E a consulta desse produto deve mostrar preço 150 e estoque 20

  Cenário: US08-CA02 - Alteração por cliente
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    Quando altero o preço desse produto para 150 e o estoque para 20
    Então o status da resposta deve ser 403
    E a mensagem deve ser "Rota exclusiva para administradores"

  Cenário: US08-CA03 - Alteração para um nome já usado por outro produto
    Dado que estou logado como administrador
    E que existe um produto cadastrado
    E que existe outro produto cadastrado
    Quando altero o nome desse produto para o nome do outro produto
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Já existe produto com esse nome"

  # OBS-02: em vez de recusar, a API cria um produto novo com outro id.
  Cenário: US08-CA04 - Alteração com id inexistente cria um produto novo
    Dado que estou logado como administrador
    Quando altero o produto de id "IdInexistente000" com dados válidos
    Então o status da resposta deve ser 201
    E a mensagem deve ser "Cadastro realizado com sucesso"
    E o id retornado deve ser diferente de "IdInexistente000"
