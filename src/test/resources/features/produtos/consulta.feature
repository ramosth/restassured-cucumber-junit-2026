# language: pt
@produtos @produtos-consulta
Funcionalidade: Consultar produtos
  Como visitante
  Quero ver o catálogo e os detalhes de um produto
  Para escolher o que comprar

  @smoke
  Cenário: US07-CA01 - Consulta do catálogo sem login
    Dado que não estou logado
    Quando consulto a lista de produtos
    Então o status da resposta deve ser 200
    E a lista de produtos não deve estar vazia

  Cenário: US07-CA02 - Busca por id existente
    Dado que existe um produto cadastrado
    Quando consulto esse produto pelo id
    Então o status da resposta deve ser 200
    E o produto retornado deve ter os mesmos dados do cadastro

  Cenário: US07-CA03 - Busca por id inexistente
    Quando consulto o produto de id "IdInexistente000"
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Produto não encontrado"
