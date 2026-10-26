# language: pt
@login
Funcionalidade: Login
  Como usuário cadastrado (cliente ou administrador)
  Quero entrar com meu e-mail e senha
  Para receber um token e usar as funções protegidas da loja

  @login-valido
  Cenário: US01-CA01 - Login com e-mail e senha corretos
    Dado que existe um usuário cliente cadastrado
    Quando faço login com o e-mail e a senha desse usuário
    Então o status da resposta deve ser 200
    E a mensagem deve ser "Login realizado com sucesso"
    E o token deve começar com "Bearer "

  @login-invalido
  Cenário: US01-CA02 - Login com senha errada
    Dado que existe um usuário cliente cadastrado
    Quando faço login com o e-mail desse usuário e a senha "senha_errada"
    Então o status da resposta deve ser 401
    E a mensagem deve ser "Email e/ou senha inválidos"

  @login-invalido
  Cenário: US01-CA03 - Login com e-mail não cadastrado
    Quando faço login com o e-mail "nao.cadastrado@serverest.com" e a senha "teste"
    Então o status da resposta deve ser 401
    E a mensagem deve ser "Email e/ou senha inválidos"

  @login-invalido
  Esquema do Cenário: <id> - <descricao>
    Quando faço login com o e-mail "<email>" e a senha "<senha>"
    Então o status da resposta deve ser 400
    E o erro do campo "<campo>" deve ser "<mensagem>"

    Exemplos:
      | id         | descricao                  | email         | senha | campo    | mensagem                          |
      | US01-CA04  | E-mail em formato inválido | fulano.qa.com | teste | email    | email deve ser um email válido    |
      | US01-CA05a | E-mail em branco           |               | teste | email    | email não pode ficar em branco    |
      | US01-CA05b | Senha em branco            | fulano@qa.com |       | password | password não pode ficar em branco |

  @login-invalido
  Esquema do Cenário: <id> - <descricao>
    Quando faço login sem o campo "<campo>"
    Então o status da resposta deve ser 400
    E o erro do campo "<campo>" deve ser "<mensagem>"

    Exemplos:
      | id         | descricao                | campo    | mensagem               |
      | US01-CA06a | Login sem o campo e-mail | email    | email é obrigatório    |
      | US01-CA06b | Login sem o campo senha  | password | password é obrigatório |

  @login-token
  Cenário: US01-CA07 - Token de usuário excluído não vale mais
    Dado que estou logado como cliente
    E excluo esse usuário
    Quando cancelo a compra
    Então o status da resposta deve ser 401
    E a mensagem deve ser "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"
