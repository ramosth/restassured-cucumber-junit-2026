# language: pt
@produtos @produtos-exclusao
Funcionalidade: Excluir produto
  Como administrador
  Quero retirar produtos do catálogo
  Para não vender o que não existe mais

  Cenário: US09-CA01 - Exclusão por administrador de produto fora de carrinhos
    Dado que estou logado como administrador
    E que existe um produto cadastrado
    Quando excluo esse produto
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Registro excluído com sucesso"
    E esse produto não deve mais existir

  Cenário: US09-CA02 - Exclusão por cliente
    Dado que estou logado como cliente
    E que existe um produto cadastrado
    Quando excluo esse produto
    Então o status da resposta deve ser 403
    E a mensagem deve ser "Rota exclusiva para administradores"

  # @wip: precisa de carrinho (etapa 9). A tag sai quando ele estiver automatizado.
  @wip
  Cenário: US09-CA03 - Exclusão de produto que está em um carrinho
    Dado que existe um produto cadastrado com preço 100 e estoque 10
    E que outro cliente tem um carrinho com 1 unidade desse produto
    E que estou logado como administrador
    Quando excluo esse produto
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Não é permitido excluir produto que faz parte de carrinho"
    E a resposta deve informar o id desse carrinho
