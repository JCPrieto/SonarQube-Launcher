package es.jklabs.utilidades;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class UtilidadesString extends StringUtils {

    public static boolean isEmpty(JTextField jTextField) {
        return isEmpty(jTextField.getText().trim());
    }
}
