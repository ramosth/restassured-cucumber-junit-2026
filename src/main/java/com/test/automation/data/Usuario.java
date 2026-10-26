package com.test.automation.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO do usuário: os mesmos campos do JSON de /usuarios.
 *
 * O Jackson usa os getters e setters para converter:
 *   envio:   body(usuario)               -> {"nome": "...", "email": "...", ...}
 *   retorno: response.as(Usuario.class)  -> objeto Usuario preenchido
 *
 * @JsonInclude(NON_NULL): campos null NÃO vão no JSON. Assim o id (que só existe
 *   depois do cadastro) não é enviado no POST.
 * @JsonProperty("_id"): no JSON o campo se chama "_id"; no Java, "id".
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario {

    private String nome;
    private String email;
    private String password;
    private String administrador;

    @JsonProperty("_id")
    private String id;

    /** Construtor vazio: obrigatório para o Jackson montar o objeto a partir do JSON. */
    public Usuario() {
    }

    public Usuario(String nome, String email, String password, String administrador) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.administrador = administrador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdministrador() {
        return administrador;
    }

    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}