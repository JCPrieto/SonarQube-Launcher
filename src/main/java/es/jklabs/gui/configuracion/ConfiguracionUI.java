package es.jklabs.gui.configuracion;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import es.jklabs.utilidades.UtilidadesString;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ConfiguracionUI extends JDialog {

    private final MainUI padre;
    private Configuracion configuracion;
    private JTextField txPath;

    public ConfiguracionUI(MainUI mainUI, Configuracion configuracion) {
        super(mainUI, Mensajes.getMensaje("configuracion"), true);
        this.padre = mainUI;
        this.configuracion = configuracion;
        cargarPantalla();
    }

    private void cargarPantalla() {
        this.setLayout(new BorderLayout());
        this.add(cargarPanelCentral(), BorderLayout.CENTER);
        this.add(cargarBotonera(), BorderLayout.SOUTH);
        this.pack();
    }

    private JPanel cargarBotonera() {
        JPanel panel = new JPanel();
        JButton btnAceptar = new JButton(Mensajes.getMensaje("aceptar"));
        btnAceptar.addActionListener(al -> guardarConfiguracion());
        panel.add(btnAceptar);
        return panel;
    }

    private void guardarConfiguracion() {
        if (validaFormularioConfiguracion()) {
            guardarConfiguracion2();
            this.dispose();
        }
    }

    private void guardarConfiguracion2() {
        if (configuracion == null) {
            configuracion = new Configuracion();
        }
        configuracion.setPath(txPath.getText());
        UtilidadesConfiguracion.guardarConfiguracion(configuracion);
        padre.actualizarPanelCentral();
    }

    private boolean validaFormularioConfiguracion() {
        boolean valido = true;
        if (UtilidadesString.isEmpty(txPath)) {
            valido = false;
            Growls.mostrarAviso("guardar.configuracion", "nombre.bucket.vacio");
        }
        return valido;
    }

    private JPanel cargarPanelCentral() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel jLabel = new JLabel(Mensajes.getMensaje("ruta.instalacion.sonarqube"));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.LINE_START;
        jPanel.add(jLabel, c);
        if (configuracion.getPath() != null) {
            txPath = new JTextField(configuracion.getPath());
        } else {
            txPath = new JTextField();
        }
        txPath.setColumns(30);
        txPath.setEditable(false);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        jPanel.add(txPath, c);
        JButton jButton = new JButton(Mensajes.getMensaje("seleccionar"));
        jButton.addActionListener(l -> seleccionarRuta());
        c.gridx = 1;
        c.gridy = 1;
        jPanel.add(jButton, c);
        return jPanel;
    }

    private void seleccionarRuta() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retorno = fc.showSaveDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directorio = fc.getSelectedFile();
            configuracion.setPath(directorio.getAbsolutePath());
            UtilidadesConfiguracion.guardarConfiguracion(configuracion);
            txPath.setText(directorio.getAbsolutePath());
        }
    }
}
