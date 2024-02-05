package org.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class OkCancelDialog extends JDialog implements ActionListener, WindowListener {
    JPanel mainPanel;
    JLabel txt;
    JButton ok;
    JButton cancel;
    boolean rc;
    remcons cmdHandler;

    public OkCancelDialog(remcons var1, JFrame var2, String var3, String var4) {
        super(var2, var4, true);
        this.cmdHandler = var1;
        this.ui_init(var3);
    }

    public OkCancelDialog(remcons var1, String var2, boolean var3, String var4) {
        super(new JFrame(), var4, var3);
        this.cmdHandler = var1;
        this.ui_init(var2);
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.cmdHandler.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("OkCancelDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    protected void ui_init(String var1) {
        this.txt = new JLabel(var1);
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        this.mainPanel.add(this.txt);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        this.ok = new JButton("    " + this.getLocalString(12576) + "    ");
        this.ok.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(12555));
        this.cancel.addActionListener(this);
        GridBagLayout var2 = new GridBagLayout();
        GridBagConstraints var3 = new GridBagConstraints();
        this.setLayout(var2);
        var3.fill = 2;
        var3.anchor = 17;
        var3.gridx = 0;
        var3.gridy = 0;
        this.add(this.mainPanel, var3);
        JPanel var4 = new JPanel();
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
        this.setSize(this.mainPanel.getPreferredSize().width + 40, this.txt.getPreferredSize().height + 100);
        this.setResizable(false);
        this.setLocationRelativeTo((Component) null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent var1) {
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

    public void append(String var1) {
        this.txt.repaint();
    }

    public void windowClosing(WindowEvent var1) {
        this.dispose();
        this.rc = false;
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
