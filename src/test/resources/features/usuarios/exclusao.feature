# language: pt
@usuarios @usuarios-exclusao
Funcionalidade: Excluir usuário
  Como usuário cadastrado
  Quero excluir a minha conta
  Para deixar de ter dados na loja

  Cenário: US05-CA01 - Exclusão de usuário sem carrinho
    Dado que existe um usuário cliente cadastrado
    Quando excluo esse usuário
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Registro excluído com sucesso"
    E esse usuário não deve mais existir

  Cenário: US05-CA02 - Exclusão de usuário com carrinho
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    E que eu tenho um carrinho com 1 unidade desse produto
    Quando excluo esse usuário
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Não é permitido excluir usuário com carrinho cadastrado"
    E a resposta deve informar o id desse carrinho

  # OBS-03: id inexistente responde 200 em vez de 404.
  Cenário: US05-CA03 - Exclusão de id inexistente
    Quando excluo o usuário de id "IdInexistente000"
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Nenhum registro excluído"
