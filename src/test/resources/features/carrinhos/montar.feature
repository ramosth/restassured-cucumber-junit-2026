# language: pt
@carrinhos @carrinhos-montar @regression
Funcionalidade: Montar carrinho
  Como cliente logado
  Quero colocar produtos no meu carrinho
  Para reservar o que vou comprar

  Cenário: US10-CA01 - Carrinho com produto válido
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    Quando crio um carrinho com 2 unidades desse produto
    Então o status da resposta deve ser 201
    E a mensagem deve ser "Cadastro realizado com sucesso"
    E a resposta deve trazer o id do registro cadastrado

  Cenário: US10-CA02 - Carrinho sem token
    Dado que existe um produto cadastrado com preço 100 e estoque 10
    E que não estou logado
    Quando crio um carrinho com 1 unidade desse produto
    Então o status da resposta deve ser 401
    E a mensagem deve ser "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  Cenário: US10-CA03 - Segundo carrinho para o mesmo usuário
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    E que eu tenho um carrinho com 1 unidade desse produto
    Quando crio um carrinho com 1 unidade desse produto
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Não é permitido ter mais de 1 carrinho"

  Cenário: US10-CA04 - Mesmo produto duas vezes no carrinho
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    Quando crio um carrinho com o mesmo produto duas vezes
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Não é permitido possuir produto duplicado"

  Cenário: US10-CA05 - Produto inexistente
    Dado que estou logado como cliente
    Quando crio um carrinho com 1 unidade do produto de id "IdInexistente000"
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Produto não encontrado"

  Cenário: US10-CA06 - Quantidade maior que o estoque
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 5
    Quando crio um carrinho com 6 unidades desse produto
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Produto não possui quantidade suficiente"

  Cenário: US10-CA07 - Quantidade zero
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    Quando crio um carrinho com 0 unidades desse produto
    Então o status da resposta deve ser 400
    E o erro do campo "produtos[0].quantidade" deve ser "produtos[0].quantidade deve ser um número positivo"

  Cenário: US10-CA08 - Totais do carrinho
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    E que existe outro produto cadastrado com preço 50 e estoque 10
    Quando crio um carrinho com 2 unidades desse produto e 1 unidade do outro produto
    Então o status da resposta deve ser 201
    E o carrinho deve ter preço total 250 e quantidade total 3

  Cenário: US10-CA09 - O estoque diminui ao criar o carrinho
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    Quando crio um carrinho com 3 unidades desse produto
    Então o status da resposta deve ser 201
    E o estoque desse produto deve ser 7
