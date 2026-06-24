package com.hp.ilo2.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class OkCancelDialog extends JDialog implements ActionListener, WindowListener {
    private JLabel txt = null;
    private JButton ok = null;
    private JButton cancel = null;
    private boolean rc = false;
    private final remcons cmdHandler;

    public OkCancelDialog(final remcons var1, final JFrame var2, final String var3, final String var4) {
        super(var2, var4, true);
        this.cmdHandler = var1;
        this.ui_init(var3);
    }

    public OkCancelDialog(final remcons var1, final String var2, final boolean var3, final String var4) {
        super(new JFrame(), var4, var3);
        this.cmdHandler = var1;
        this.ui_init(var2);
    }

    private String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.cmdHandler.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("OkCancelDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    private void ui_init(final String var1) {
        this.txt = new JLabel(var1);
        final JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        mainPanel.add(this.txt);
        mainPanel.setPreferredSize(mainPanel.getPreferredSize());
        this.ok = new JButton("    " + this.getLocalString(12576) + "    ");
        this.ok.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(12555));
        this.cancel.addActionListener(this);
        final LayoutManager var2 = new GridBagLayout();
        final GridBagConstraints var3 = new GridBagConstraints();
        this.setLayout(var2);
        var3.fill = 2;
        var3.anchor = 17;
        var3.gridx = 0;
        var3.gridy = 0;
        this.add(mainPanel, var3);
        final JPanel var4 = new JPanel();
        var4.setLayout(new FlowLayout(2));
        var4.add(this.ok);
        var4.add(this.cancel);
        var3.fill = 0;
        var3.anchor = 13;
        var3.gridx = 0;
        var3.gridy = 1;
        var3.gridwidth = 1;
        this.add(var4, var3);
        this.addWindowListener(this);
        this.setSize(mainPanel.getPreferredSize().width + 40, this.txt.getPreferredSize().height + 100);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(final ActionEvent var1) {
        if (var1.getSource() == this.ok) {
            this.dispose();
            this.rc = true;
        } else if (var1.getSource() == this.cancel) {
            this.dispose();
            this.rc = false;
        }

    }

    public boolean result() {
        return this.rc;
    }

    public void append(final String var1) {
        this.txt.repaint();
    }

    public void windowClosing(final WindowEvent var1) {
        this.dispose();
        this.rc = false;
    }

    public void windowOpened(final WindowEvent var1) {
    }

    public void windowDeiconified(final WindowEvent var1) {
    }

    public void windowIconified(final WindowEvent var1) {
    }

    public void windowActivated(final WindowEvent var1) {
    }

    public void windowClosed(final WindowEvent var1) {
    }

    public void windowDeactivated(final WindowEvent var1) {
    }
}
