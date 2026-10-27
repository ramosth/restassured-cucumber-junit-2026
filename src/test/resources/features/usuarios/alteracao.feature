# language: pt
@usuarios @usuarios-alteracao @regression
Funcionalidade: Alterar usuário
  Como usuário cadastrado
  Quero alterar os meus dados
  Para mantê-los atualizados

  Cenário: US04-CA01 - Alteração com dados válidos
    Dado que existe um usuário cliente cadastrado
    Quando altero o nome desse usuário para "Nome Alterado"
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Registro alterado com sucesso"
    E a consulta desse usuário deve mostrar o nome "Nome Alterado"

  Cenário: US04-CA02 - Alteração para um e-mail já usado por outro usuário
    Dado que existe um usuário cliente cadastrado
    E que existe outro usuário cadastrado
    Quando altero o e-mail desse usuário para o e-mail do outro usuário
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Este email já está sendo usado"

  # OBS-02: em vez de recusar, a API cria um usuário novo com outro id.
  Cenário: US04-CA03 - Alteração com id inexistente cria um usuário novo
    Quando altero o usuário de id "IdInexistente000" com dados válidos
    Então o status da resposta deve ser 201
    E a mensagem deve ser "Cadastro realizado com sucesso"
    E o id retornado deve ser diferente de "IdInexistente000"
