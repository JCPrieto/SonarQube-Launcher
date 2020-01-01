package es.jklabs.json.configuracion;

import java.io.Serializable;

public class Configuracion implements Serializable {

    private static final long serialVersionUID = 7573571562293629291L;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
