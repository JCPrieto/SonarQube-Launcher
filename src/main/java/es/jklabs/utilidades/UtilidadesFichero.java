package es.jklabs.utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class UtilidadesFichero {

    static final String HOME = System.getProperty("user.home");
    static final String SEPARADOR = System.getProperty("file.separator");
    static final String SONARQUBE_LAUNCHER_FOLDER = ".SonarQubeLauncher";

    private UtilidadesFichero() {

    }

    public static void createBaseFolder() {
        File base = new File(HOME + SEPARADOR + SONARQUBE_LAUNCHER_FOLDER);
        if (!base.exists()) {
            try {
                Files.createDirectory(FileSystems.getDefault().getPath(HOME + SEPARADOR + SONARQUBE_LAUNCHER_FOLDER));
            } catch (IOException e) {
                Logger.error("Crear carpeta base", e);
            }
        }
    }
}
