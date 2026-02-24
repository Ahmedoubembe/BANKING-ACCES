package com.bamis.banking_access.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadDirectoryInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(UploadDirectoryInitializer.class);

    @Value("${app.upload.dir:uploads/justificatifs}")
    private String uploadBaseDir;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path uploadPath = Paths.get(uploadBaseDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("Repertoire d'upload cree : {}", uploadPath.toAbsolutePath());
        } else {
            logger.info("Repertoire d'upload existant : {}", uploadPath.toAbsolutePath());
        }
    }
}