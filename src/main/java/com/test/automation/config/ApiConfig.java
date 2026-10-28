package com.test.automation.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Configuração comum a TODAS as requisições (o "DriverFactory" da API).
 *
 * RequestSpecification = um "molde" de requisição: tudo o que se repete em
 * todas as chamadas fica aqui, num lugar só.
 * - URL base: ambiente escolhido em -Denv (ver baseUri()) ou do -Dbase.uri=...
 * - Content-Type JSON: o corpo que enviamos é JSON
 * - Accept JSON: queremos receber JSON
 */
public final class ApiConfig {

    private ApiConfig() {
    }

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * Descobre a URL base, nesta ordem:
     * 1. -Dbase.uri=... (URL direta: vence tudo; o Jenkins usa para a porta 3100)
     * 2. base.uri.<env> (URL do ambiente escolhido em -Denv=... ou no
     * config.properties)
     */
    public static String baseUri() {
        String urlDireta = ConfigReader.get("base.uri");
        if (urlDireta != null && !urlDireta.isEmpty()) {
            return urlDireta;
        }

        String ambiente = ConfigReader.get("env", "local");
        String url = ConfigReader.get("base.uri." + ambiente);
        if (url == null) {
            // Falha logo no começo, com uma mensagem clara, em vez de 71 cenários com
            // "Connection refused"
            throw new IllegalArgumentException("Ambiente '" + ambiente + "' não configurado: crie a chave base.uri."
                    + ambiente + " no config.properties.");
        }
        return url;
    }
}