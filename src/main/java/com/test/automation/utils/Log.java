package com.test.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
    
    private static final Logger logger = LogManager.getLogger(Log.class);

    // Métodos de log para diferentes níveis de log

    // Método para log de informações
    public static void info(String message) {
        logger.info(message);
    }

    // Método para log de erros
    public static void error(String message) {
        logger.error(message);
    }

    // Método para log de depuração
    public static void debug(String message) {
        logger.debug(message);
    }

    // Método para log de avisos
    public static void warn(String message) {
        logger.warn(message);
    }

    // Método para log de falhas graves
    public static void fatal(String message) {
        logger.fatal(message);
    }

    // Método para log de traços
    public static void trace(String message) {
        logger.trace(message);
    }

    // Método para log de exceções
    public static void logException(Exception e) {
        logger.error("Exception occurred: ", e);
    }

    // Método para log de erros com mensagem personalizada
    public static void logException(String message, Exception e) {
        logger.error(message, e);
    }

    // Método para log de erros com mensagem personalizada e Throwable
    public static void logException(String message, Throwable t) {
        logger.error(message, t);
    }

    // Método para log de Throwable
    public static void logException(Throwable t) {
        logger.error("Throwable occurred: ", t);
    }
}
