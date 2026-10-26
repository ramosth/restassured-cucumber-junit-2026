# language: pt
@carrinhos @carrinhos-concluir
Funcionalidade: Concluir compra
  Como cliente com carrinho
  Quero concluir a compra
  Para finalizar o pedido

  Contexto:
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10

  Cenário: US11-CA01 - Concluir compra com carrinho
    Dado que eu tenho um carrinho com 3 unidades desse produto
    Quando concluo a compra
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Registro excluído com sucesso"
    E esse carrinho não deve mais existir

  Cenário: US11-CA02 - O estoque continua baixado depois de concluir
    Dado que eu tenho um carrinho com 3 unidades desse produto
    Quando concluo a compra
    Então o status da resposta deve ser 200
    E o estoque desse produto deve ser 7

  # OBS-08: sem carrinho, a API responde 200 com uma mensagem.
  Cenário: US11-CA03 - Concluir compra sem ter carrinho
    Quando concluo a compra
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Não foi encontrado carrinho para esse usuário"
