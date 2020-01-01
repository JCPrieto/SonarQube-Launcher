package es.jklabs.json.firebase;

import java.io.Serializable;

public class Aplicacion implements Serializable {

    private static final long serialVersionUID = -3298697109090563568L;

    private String ultimaVersion;
    private int numDescargas;

    public String getUltimaVersion() {
        return ultimaVersion;
    }

    public void setUltimaVersion(String ultimaVersion) {
        this.ultimaVersion = ultimaVersion;
    }

    public int getNumDescargas() {
        return numDescargas;
    }

    public void setNumDescargas(int numDescargas) {
        this.numDescargas = numDescargas;
    }

}
