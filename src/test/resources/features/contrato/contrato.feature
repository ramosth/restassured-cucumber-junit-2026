# language: pt
@contrato
Funcionalidade: Contrato das respostas
  Como time que consome a API
  Quero que o formato das respostas (campos e tipos) não mude sem aviso
  Para que as integrações não quebrem

  Cenário: US13-CA01 - Contrato da consulta de usuário
    Dado que existe um usuário cliente cadastrado
    Quando consulto esse usuário pelo id
    Então o status da resposta deve ser 200
    E a resposta deve seguir o contrato "usuario"

  Cenário: US13-CA02 - Contrato da consulta de produto
    Dado que existe um produto cadastrado
    Quando consulto esse produto pelo id
    Então o status da resposta deve ser 200
    E a resposta deve seguir o contrato "produto"

  Cenário: US13-CA03 - Contrato da consulta de carrinho
    Dado que estou logado como cliente
    E que existe um produto cadastrado com preço 100 e estoque 10
    E que eu tenho um carrinho com 1 unidade desse produto
    Quando consulto esse carrinho pelo id
    Então o status da resposta deve ser 200
    E a resposta deve seguir o contrato "carrinho"

  Cenário: US13-CA04 - Contrato do login
    Dado que existe um usuário cliente cadastrado
    Quando faço login com o e-mail e a senha desse usuário
    Então o status da resposta deve ser 200
    E a resposta deve seguir o contrato "login"
