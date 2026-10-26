package com.test.automation.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO do produto: os mesmos campos do JSON de /produtos.
 *
 * preco e quantidade são Integer: a API só aceita inteiros (RN-PRO-06).
 * Os cenários que testam decimais (10.5) usam um Map, porque o POJO não guarda 10.5.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Produto {

    private String nome;
    private Integer preco;
    private String descricao;
    private Integer quantidade;

    @JsonProperty("_id")
    private String id;

    /** Construtor vazio: obrigatório para o Jackson montar o objeto a partir do JSON. */
    public Produto() {
    }

    public Produto(String nome, Integer preco, String descricao, Integer quantidade) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getPreco() {
        return preco;
    }

    public void setPreco(Integer preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}