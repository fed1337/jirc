package org.jirc;

import javax.swing.*;

final class Main {
    private static final String APP_NAME = "Java Integrated Remote Console";

    public static void main(final String[] args) {
        // shall add support for plain HTTP
        IloSsl.install();
        SwingUtilities.invokeLater(() -> new ConnectionDialog(Main.APP_NAME).setVisible(true));
    }
}
