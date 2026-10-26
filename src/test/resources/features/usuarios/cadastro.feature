# language: pt
@usuarios @usuarios-cadastro
Funcionalidade: Cadastrar usuário
  Como visitante
  Quero criar a minha conta como cliente ou administrador
  Para poder fazer login na loja

  Esquema do Cenário: <id> - <descricao>
    Quando cadastro um usuário <perfil> com dados válidos
    Então o status da resposta deve ser 201
    E a mensagem deve ser "Cadastro realizado com sucesso"
    E a resposta deve trazer o id do registro cadastrado

    Exemplos:
      | id        | descricao                                   | perfil        |
      | US02-CA01 | Cadastro de cliente com dados válidos       | cliente       |
      | US02-CA02 | Cadastro de administrador com dados válidos | administrador |

  Cenário: US02-CA03 - Cadastro com e-mail já cadastrado
    Dado que existe um usuário cliente cadastrado
    Quando cadastro um usuário com o e-mail desse usuário
    Então o status da resposta deve ser 400
    E a mensagem deve ser "Este email já está sendo usado"

  Esquema do Cenário: <id> - <descricao>
    Quando cadastro um usuário com o campo "<campo>" igual a "<valor>"
    Então o status da resposta deve ser 400
    E o erro do campo "<campo>" deve ser "<mensagem>"

    Exemplos:
      | id         | descricao                                 | campo         | valor         | mensagem                                 |
      | US02-CA04a | Administrador com valor fora do permitido | administrador | sim           | administrador deve ser 'true' ou 'false' |
      | US02-CA05  | E-mail em formato inválido                | email         | fulano.qa.com | email deve ser um email válido           |
      | US02-CA06a | Nome em branco                            | nome          |               | nome não pode ficar em branco            |
      | US02-CA06b | E-mail em branco                          | email         |               | email não pode ficar em branco           |
      | US02-CA06c | Senha em branco                           | password      |               | password não pode ficar em branco        |

  Cenário: US02-CA04b - Administrador como booleano (sem aspas)
    Quando cadastro um usuário com o campo "administrador" igual ao booleano true
    Então o status da resposta deve ser 400
    E o erro do campo "administrador" deve ser "administrador deve ser 'true' ou 'false'"

  Cenário: US02-CA07 - Cadastro com campo que não existe no contrato
    Quando cadastro um usuário com o campo a mais "telefone"
    Então o status da resposta deve ser 400
    E o erro do campo "telefone" deve ser "telefone não é permitido"
