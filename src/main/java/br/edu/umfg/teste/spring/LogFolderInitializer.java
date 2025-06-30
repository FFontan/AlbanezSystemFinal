package br.edu.umfg.teste.spring;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LogFolderInitializer {

    @PostConstruct
    public void init() {
        File logDir = new File("logs");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                System.out.println("Pasta de logs criada automaticamente.");
            }
        }
    }
}
