# language: pt
@usuarios @usuarios-consulta
Funcionalidade: Consultar usuários
  Como consumidor da API
  Quero listar usuários e buscar um usuário pelo id
  Para conferir os dados cadastrados

  Cenário: US03-CA01 - Busca por id existente
    Dado que existe um usuário cliente cadastrado
    Quando consulto esse usuário pelo id
    Então o status da resposta deve ser 200
    E o usuário retornado deve ter os mesmos dados do cadastro

  Cenário: US03-CA02 - Busca por id inexistente
    Quando consulto o usuário de id "IdInexistente000"
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Usuário não encontrado"

  Cenário: US03-CA03 - Busca com id em formato inválido
    Quando consulto o usuário de id "123"
    Então o status da resposta deve ser 400
    E o erro do campo "id" deve ser "id deve ter exatamente 16 caracteres alfanuméricos"

  Cenário: US03-CA04 - Listagem filtrada por e-mail
    Dado que existe um usuário cliente cadastrado
    Quando listo os usuários filtrando pelo e-mail desse usuário
    Então o status da resposta deve ser 200
    E a quantidade de registros retornados deve ser 1

  # OBS-04: a API devolve a senha em texto puro. O cenário falha até o defeito ser corrigido.
  @expected-failure
  Cenário: US03-CA05 - A senha não deve aparecer na consulta
    Dado que existe um usuário cliente cadastrado
    Quando consulto esse usuário pelo id
    Então o status da resposta deve ser 200
    E a resposta não deve exibir a senha
