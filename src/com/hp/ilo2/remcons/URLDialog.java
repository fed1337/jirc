package com.hp.ilo2.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class URLDialog extends JDialog implements ActionListener, WindowListener {
    JPanel mainPanel = null;
    JLabel txt1 = null;
    JLabel txt2 = null;
    JTextArea txt3 = null;
    JScrollPane scroller = null;
    JButton ok = null;
    JButton cancel = null;
    String url = null;
    boolean rc = false;
    final remcons remconsObj;

    public URLDialog(final remcons var1) {
        super(null == var1.ParentApp.dispFrame ? new JFrame() : var1.ParentApp.dispFrame, var1.getLocalString(8290), true);
        this.remconsObj = var1;
        this.ui_init();
    }

    public String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("VSeizeDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    protected void ui_init() {
        this.txt1 = new JLabel(this.getLocalString(8291) + "\n\n\n");
        this.txt3 = new JTextArea(1, 40);
        this.txt3.setEditable(true);
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridBagLayout());
        this.ok = new JButton(this.getLocalString(12577));
        this.ok.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(12565));
        this.cancel.addActionListener(this);
        this.setBackground(Color.lightGray);
        final GridBagConstraints var1 = new GridBagConstraints();
        var1.fill = 2;
        var1.anchor = 17;
        var1.gridx = 0;
        var1.gridy = 0;
        this.mainPanel.add(this.txt1, var1);
        this.scroller = new JScrollPane(this.txt3, 21, 31);
        var1.gridx = 0;
        var1.gridy = 1;
        this.mainPanel.add(this.scroller, var1);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        final JPanel var2 = new JPanel();
        var2.setLayout(new FlowLayout(2));
        var2.add(this.cancel);
        var2.add(this.ok);
        this.setLayout(new GridBagLayout());
        final GridBagConstraints var3 = new GridBagConstraints();
        var3.fill = 2;
        var3.anchor = 17;
        var3.gridx = 0;
        var3.gridy = 0;
        this.add(this.mainPanel, var3);
        var3.gridx = 0;
        var3.gridy = 1;
        this.add(var2, var3);
        this.setSize(this.mainPanel.getPreferredSize().width + 40, this.mainPanel.getPreferredSize().height + 100);
        this.addWindowListener(this);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(final ActionEvent var1) {
        if (var1.getSource() == this.ok) {
            this.url = this.txt3.getText();
            this.dispose();
        } else if (var1.getSource() == this.cancel) {
            this.url = "userhitcancel";
            this.dispose();
        }

    }

    public String getUserInput() {
        return this.url;
    }

    public void windowClosing(final WindowEvent var1) {
        this.url = "userhitclose";
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
