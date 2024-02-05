package org.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class aboutJircDialog extends JDialog implements ActionListener, WindowListener {
    JPanel mainPanel;
    JLabel txt1;
    JLabel txt2;
    JLabel txt3;
    JButton close;
    remcons remconsObj;

    public aboutJircDialog(remcons var1) {
        super(var1.ParentApp.dispFrame, var1.ParentApp.getLocalString(4137), false);
        this.remconsObj = var1;
        this.ui_init();
    }

    protected void ui_init() {
        this.txt1 = new JLabel(this.remconsObj.ParentApp.getLocalString(8296));
        this.txt2 = new JLabel("Version 231");
        this.txt3 = new JLabel("Copyright 2009-2019 Hewlett Packard Enterprise Development LP");
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridBagLayout());
        this.close = new JButton("Close");
        this.close.addActionListener(this);
        this.setBackground(Color.lightGray);
        GridBagConstraints var1 = new GridBagConstraints();
        var1.fill = 2;
        var1.anchor = 17;
        var1.gridx = 0;
        var1.gridy = 0;
        this.mainPanel.add(this.txt1, var1);
        var1.gridy = 1;
        this.mainPanel.add(this.txt2, var1);
        var1.gridy = 2;
        this.mainPanel.add(this.txt3, var1);
        JPanel var2 = new JPanel();
        var2.setLayout(new FlowLayout(2));
        var2.add(this.close);
        this.setLayout(new GridBagLayout());
        GridBagConstraints var3 = new GridBagConstraints();
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
        this.setLocationRelativeTo((Component) null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent var1) {
        if (var1.getSource() == this.close) {
            this.dispose();
        }

    }

    public void windowClosing(WindowEvent var1) {
        this.dispose();
    }

    public void windowOpened(WindowEvent var1) {
    }

    public void windowDeiconified(WindowEvent var1) {
    }

    public void windowIconified(WindowEvent var1) {
    }

    public void windowActivated(WindowEvent var1) {
    }

    public void windowClosed(WindowEvent var1) {
    }

    public void windowDeactivated(WindowEvent var1) {
    }
}
