package com.hp.ilo2.virtdevs;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class VFileDialog extends Frame implements WindowListener {
    private FileDialog fd = null;

    public VFileDialog(final String var1, final String var2) {
        super();
        try {
            this.addWindowListener(this);
            this.fd = new FileDialog(new Frame(), var1);
            if (null != var2) {
                this.fd.setFile(var2);
            }

            this.fd.setVisible(true);
            this.fd.setFocusable(true);
        } catch (final Exception var4) {
            System.out.println("Un able to open virtual drive select");
        }

    }

    public String getString() {
        String var1 = null;
        if (null != this.fd.getDirectory() && null != this.fd.getFile()) {
            var1 = this.fd.getDirectory() + this.fd.getFile();
        }

        return var1;
    }

    public void windowClosing(final WindowEvent var1) {
        this.setVisible(false);
    }

    public void windowActivated(final WindowEvent var1) {
    }

    public void windowClosed(final WindowEvent var1) {
    }

    public void windowDeactivated(final WindowEvent var1) {
    }

    public void windowDeiconified(final WindowEvent var1) {
    }

    public void windowIconified(final WindowEvent var1) {
    }

    public void windowOpened(final WindowEvent var1) {
    }
}
