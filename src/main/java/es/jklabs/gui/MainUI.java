package es.jklabs.gui;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.gui.dialogos.AcercaDe;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesFirebase;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainUI extends JFrame {

    public static final String CAMBIAR_ESTADO_SONAR = "cambiar.estado.sonar";
    private Configuracion configuracion;
    private JPanel panelCentral;
    private JToggleButton btnSonar;

    public MainUI(Configuracion configuracion) {
        this();
        this.configuracion = configuracion;
        cargarPantallaPrincipal();
        super.pack();
    }

    public MainUI() {
        super(Constantes.NOMBRE_APP);
        super.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                ("img/icons/sonarqube.png"))).getImage());
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cargarMenu();
    }

    private void cargarPantallaPrincipal() {
        super.setLayout(new BorderLayout());
        cargarPanelCentral();
        super.add(panelCentral, BorderLayout.CENTER);
    }

    private void cargarPanelCentral() {
        if (obtenerEstadoSonarQube()) {
            btnSonar = new JToggleButton(Mensajes.getMensaje("estado.sonar.running"), new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                    ("img/estados/ok.png"))), true);
        } else {
            btnSonar = new JToggleButton(Mensajes.getMensaje("estado.sonar.stopped"), new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                    ("img/estados/ko.png"))), false);
        }
        btnSonar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnSonar.addActionListener(l -> cambioEstado());
        panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(btnSonar, BorderLayout.CENTER);
    }

    private boolean obtenerEstadoSonarQube() {
        boolean retorno = false;
        if (!StringUtils.isEmpty(configuracion.getPath())) {
            try {
                Process p = Runtime.getRuntime().exec(configuracion.getPath() + " status");
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String linea = stdInput.readLine();
                if (!StringUtils.isEmpty(linea) && linea.contains("SonarQube is running")) {
                    retorno = true;
                }
                if (!retorno) {
                    tratarErrorEjecucion(stdError, "obtener.estado.sonar");
                }
            } catch (IOException e) {
                Growls.mostrarError(Mensajes.getError("obtener.estado.sonar"), e);
            }
        }
        return retorno;
    }

    private void cambioEstado() {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (obtenerEstadoSonarQube()) {
                Process p = Runtime.getRuntime().exec(configuracion.getPath() + " stop");
                cambioEstado(p, "Started SonarQube");
            } else {
                Process p = Runtime.getRuntime().exec(configuracion.getPath() + " start");
                cambioEstado(p, "Stopped SonarQube");
            }
        } catch (IOException e) {
            Growls.mostrarError(Mensajes.getError(CAMBIAR_ESTADO_SONAR), e);
        } finally {
            this.setCursor(null);
        }
        actualizarPanelCentral();
    }

    private void cambioEstado(Process p, String s) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String linea;
        while ((linea = stdInput.readLine()) != null && linea.contains(s)) ;
        tratarErrorEjecucion(stdError, CAMBIAR_ESTADO_SONAR);
    }

    private void tratarErrorEjecucion(BufferedReader stdError, String accion) throws IOException {
        String linea;
        StringBuilder salida = new StringBuilder(StringUtils.EMPTY);
        while ((linea = stdError.readLine()) != null) {
            salida.append(linea);
        }
        if (!StringUtils.isEmpty(salida)) {
            Growls.mostrarInfo(Mensajes.getError(accion), salida.toString());
        }
    }

    private void cargarMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu jmArchivo = new JMenu(Mensajes.getMensaje("archivo"));
        jmArchivo.setMargin(new Insets(5, 5, 5, 5));
        JMenuItem jmiConfiguracion = new JMenuItem(Mensajes.getMensaje("configuracion"), new ImageIcon(Objects
                .requireNonNull(getClass().getClassLoader().getResource("img/icons/settings.png"))));
        jmiConfiguracion.addActionListener(al -> abrirConfiguracion());
        jmArchivo.add(jmiConfiguracion);
        JMenu jmAyuda = new JMenu("Ayuda");
        jmAyuda.setMargin(new Insets(5, 5, 5, 5));
        JMenuItem jmiAcercaDe = new JMenuItem(Mensajes.getMensaje("acerca.de"), new ImageIcon(Objects
                .requireNonNull(getClass().getClassLoader().getResource("img/icons/info.png"))));
        jmiAcercaDe.addActionListener(al -> mostrarAcercaDe());
        jmAyuda.add(jmiAcercaDe);
        menu.add(jmArchivo);
        menu.add(jmAyuda);
        try {
            if (UtilidadesFirebase.existeNuevaVersion()) {
                menu.add(Box.createHorizontalGlue());
                JMenuItem jmActualizacion = new JMenuItem(Mensajes.getMensaje("existe.nueva.version"), new ImageIcon
                        (Objects.requireNonNull(getClass().getClassLoader().getResource("img/icons/update.png"))));
                jmActualizacion.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                jmActualizacion.setHorizontalTextPosition(SwingConstants.RIGHT);
                jmActualizacion.addActionListener(al -> descargarNuevaVersion());
                menu.add(jmActualizacion);
            }
        } catch (IOException e) {
            Logger.error("consultar.nueva.version", e);
        } catch (InterruptedException e) {
            Logger.error("consultar.nueva.version", e);
            Thread.currentThread().interrupt();
        }
        super.setJMenuBar(menu);
    }

    private void descargarNuevaVersion() {
        try {
            UtilidadesFirebase.descargaNuevaVersion(this);
        } catch (InterruptedException e) {
            Growls.mostrarError("descargar.nueva.version", e);
            Thread.currentThread().interrupt();
        }
    }

    private void mostrarAcercaDe() {
        AcercaDe acercaDe = new AcercaDe(this);
        acercaDe.setVisible(true);
    }

    private void abrirConfiguracion() {
        ConfiguracionUI configuracionUI = new ConfiguracionUI(this, configuracion);
        configuracionUI.setVisible(true);
    }

    public void actualizarPanelCentral() {
        if (obtenerEstadoSonarQube()) {
            btnSonar.setText(Mensajes.getMensaje("estado.sonar.running"));
            btnSonar.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                    ("img/estados/ok.png"))));
            btnSonar.setSelected(true);
        } else {
            btnSonar.setText(Mensajes.getMensaje("estado.sonar.stopped"));
            btnSonar.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                    ("img/estados/ko.png"))));
            btnSonar.setSelected(false);
        }
        this.pack();
    }
}
