# language: pt
@carrinhos @carrinhos-cancelar
Funcionalidade: Cancelar compra
  Como cliente com carrinho
  Quero cancelar a compra
  Para desistir do pedido e liberar os produtos

  Contexto:
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10

  Cenário: US12-CA01 - Cancelar compra com carrinho
    Dado que eu tenho um carrinho com 3 unidades desse produto
    Quando cancelo a compra
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Registro excluído com sucesso. Estoque dos produtos reabastecido"
    E esse carrinho não deve mais existir

  Cenário: US12-CA02 - O estoque volta ao valor original depois de cancelar
    Dado que eu tenho um carrinho com 3 unidades desse produto
    Quando cancelo a compra
    Então o status da resposta deve ser 200
    E o estoque desse produto deve ser 10

  # OBS-08: sem carrinho, a API responde 200 com uma mensagem.
  Cenário: US12-CA03 - Cancelar compra sem ter carrinho
    Quando cancelo a compra
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Não foi encontrado carrinho para esse usuário"
