package org.virtdevs;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class VFileDialog extends Frame implements WindowListener {
    FileDialog fd;

    public VFileDialog(String var1, String var2) {
        try {
            this.addWindowListener(this);
            this.fd = new FileDialog(new Frame(), var1);
            if (var2 != null) {
                this.fd.setFile(var2);
            }

            this.fd.setVisible(true);
            this.fd.setFocusable(true);
        } catch (Exception var4) {
            System.out.println("Un able to open virtual drive select");
        }

    }

    public String getString() {
        String var1 = null;
        if (this.fd.getDirectory() != null && this.fd.getFile() != null) {
            var1 = this.fd.getDirectory() + this.fd.getFile();
        }

        return var1;
    }

    public void windowClosing(WindowEvent var1) {
        this.setVisible(false);
    }

    public void windowActivated(WindowEvent var1) {
    }

    public void windowClosed(WindowEvent var1) {
    }

    public void windowDeactivated(WindowEvent var1) {
    }

    public void windowDeiconified(WindowEvent var1) {
    }

    public void windowIconified(WindowEvent var1) {
    }

    public void windowOpened(WindowEvent var1) {
    }
}
