package com.test.automation.data;

import java.util.Locale;
import java.util.UUID;

import net.datafaker.Faker;

/**
 * Fábrica de dados de teste.
 *
 * Cada chamada devolve dados NOVOS e ÚNICOS. Isso é obrigatório na API porque:
 *   - o e-mail precisa ser único (RN-USU-02);
 *   - os dados ficam salvos entre execuções (requisitos.md, seção 2).
 * Com dados fixos, a 2ª execução já falharia com "Este email já está sendo usado".
 */
public final class DataFactory {

    // Faker em português: gera nomes brasileiros ("Ana Souza", "João Pereira"...)
    private static final Faker FAKER = new Faker(Locale.forLanguageTag("pt-BR"));

    private DataFactory() {
    }

    /** Usuário novo, com nome aleatório e e-mail único. */
    public static Usuario novoUsuario(Perfil perfil) {
        return new Usuario(
                FAKER.name().fullName(),
                emailUnico(),
                FAKER.internet().password(8, 16),
                perfil.getAdministrador());
    }

    /**
     * E-mail único: 8 caracteres de um UUID (identificador aleatório).
     * Exemplo: qa_3f9a1c2b@serverest.com
     * Não usa o nome gerado porque nomes com acento ("joão") não são aceitos em e-mail.
     */
    public static String emailUnico() {
        return "qa_" + UUID.randomUUID().toString().substring(0, 8) + "@serverest.com";
    }
}