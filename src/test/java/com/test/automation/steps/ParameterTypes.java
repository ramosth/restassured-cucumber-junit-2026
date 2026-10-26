package com.test.automation.steps;

import com.test.automation.data.Perfil;

import io.cucumber.java.ParameterType;

/**
 * Conversões personalizadas: ensinam o Cucumber a transformar o texto do
 * .feature
 * em objetos Java.
 *
 * {perfil}: "cliente" ou "administrador" no .feature -> Perfil.CLIENTE /
 * Perfil.ADMINISTRADOR.
 * Assim uma frase só ("que existe um usuário {perfil} cadastrado") atende os
 * dois perfis.
 */
public class ParameterTypes {

    @ParameterType("cliente|administrador")
    public Perfil perfil(String texto) {
        return Perfil.valueOf(texto.toUpperCase());
    }

    /**
     * {numero}: número com PONTO, inteiro ou decimal. Ex.: 100, -1, 10.5
     * Devolve Integer quando não tem ponto e Double quando tem, para o JSON sair
     * igual ao .feature
     * (100 -> 100; 10.5 -> 10.5).
     *
     * Existe porque o {double} do Cucumber segue o idioma do .feature e, em
     * português,
     * esperaria vírgula ("10,5").
     */
    @ParameterType("-?\\d+(?:\\.\\d+)?")
    public Number numero(String texto) {
        return texto.contains(".") ? Double.valueOf(texto) : Integer.valueOf(texto);
    }
}