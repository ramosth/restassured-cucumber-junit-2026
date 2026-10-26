package com.test.automation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.test.automation.utils.Log;

/**
 * Lê as configurações do projeto a partir de
 * src/test/resources/config.properties.
 *
 * Ordem de prioridade para cada chave (a primeira encontrada vence):
 * 1. Propriedade passada no Maven: mvn test -Dbase.uri=https://serverest.dev
 * 2. Arquivo config.properties
 *
 * O config.properties fica fora do Git (.gitignore).
 * O modelo sem dados reais é o config.properties.example.
 */
public class ConfigReader {

    private static final String CONFIG_FILE = System.getProperty("user.dir") + "/src/test/resources/config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE),
                StandardCharsets.UTF_8)) {
            properties.load(reader);
            Log.info("Configurações carregadas de " + CONFIG_FILE);
        } catch (IOException e) {
            Log.warn("Arquivo config.properties não encontrado (" + CONFIG_FILE
                    + "). Copie o config.properties.example e preencha os dados.");
        }
        return properties;
    }

    /** Retorna o valor da chave, ou null se ela não existir. */
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = PROPERTIES.getProperty(key);
        }
        return value == null ? null : value.trim();
    }

    /** Retorna o valor da chave, ou o valor padrão se ela não existir. */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }
}