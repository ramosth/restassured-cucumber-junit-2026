package com.test.automation.steps;

import com.test.automation.data.Perfil;

import io.cucumber.java.ParameterType;

/**
 * Conversões personalizadas: ensinam o Cucumber a transformar o texto do .feature
 * em objetos Java.
 *
 * {perfil}: "cliente" ou "administrador" no .feature -> Perfil.CLIENTE / Perfil.ADMINISTRADOR.
 * Assim uma frase só ("que existe um usuário {perfil} cadastrado") atende os dois perfis.
 */
public class ParameterTypes {

    @ParameterType("cliente|administrador")
    public Perfil perfil(String texto) {
        return Perfil.valueOf(texto.toUpperCase());
    }
}