package com.test.automation.data;

/**
 * Perfis de usuário da ServeRest.
 *
 * A API guarda o perfil no campo "administrador" como TEXTO ("true"/"false"),
 * e não como booleano (OBS-07). O enum esconde esse detalhe: no código usamos
 * Perfil.CLIENTE, e o valor que vai no JSON fica aqui, num lugar só.
 */
public enum Perfil {

    CLIENTE("false"),
    ADMINISTRADOR("true");

    private final String administrador;

    Perfil(String administrador) {
        this.administrador = administrador;
    }

    /** Valor do campo "administrador" no JSON. */
    public String getAdministrador() {
        return administrador;
    }
}