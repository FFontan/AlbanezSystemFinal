package br.edu.umfg.teste.spring.util;

import org.slf4j.Logger;

public class LogUtils {

    public static void logInicio(Logger logger, String acao) {
        logger.info("INICIANDO: {}", acao);
    }

    public static void logSucesso(Logger logger, String acao) {
        logger.info("SUCESSO: {}", acao);
    }

    public static void logErro(Logger logger, String acao, Exception e) {
        logger.error("ERRO ao {}: {}", acao, e.getMessage());
    }

    public static void logAviso(Logger logger, String aviso) {
        logger.warn("AVISO: {}", aviso);
    }

    public static void logDebug(Logger logger, String mensagem) {
        logger.debug("DEBUG: {}", mensagem);
    }
}
