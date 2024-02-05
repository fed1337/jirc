package org.virtdevs;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VErrorDialog extends JDialog implements ActionListener {
    JPanel mainPanel;
    JLabel txt;
    JButton ok;
    boolean disp;
    virtdevs virtdevsObj;

    public VErrorDialog(JFrame var1, String var2) {
        super(var1, "Error", true);
        this.ui_init(var2);
    }

    public VErrorDialog(JFrame var1, String var2, String var3) {
        super(var1, var2, true);
        this.ui_init(var3);
    }

    public VErrorDialog(String var1, boolean var2) {
        super(new JFrame(), "Error", var2);
        this.ui_init(var1);
    }

    public VErrorDialog(JFrame var1, String var2, String var3, boolean var4) {
        super(var1, var2, var4);
        this.ui_init(var3);
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.virtdevsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("VErrorDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    protected void ui_init(String var1) {
        this.txt = new JLabel(var1);
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        this.mainPanel.add(this.txt);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        JPanel var2 = new JPanel();
        var2.setLayout(new FlowLayout(2));
        this.ok = new JButton("    OK    ");
        this.ok.addActionListener(this);
        var2.add(this.ok);
        this.getRootPane().setDefaultButton(this.ok);
        GridBagLayout var3 = new GridBagLayout();
        GridBagConstraints var4 = new GridBagConstraints();
        this.setLayout(var3);
        var4.fill = 0;
        var4.anchor = 10;
        var4.gridx = 0;
        var4.gridy = 0;
        this.add(this.mainPanel, var4);
        var4.fill = 0;
        var4.anchor = 13;
        var4.gridx = 0;
        var4.gridy = 1;
        this.add(var2, var4);
        this.setSize(this.mainPanel.getPreferredSize().width + 40, this.mainPanel.getPreferredSize().height + 100);
        this.setResizable(false);
        this.setLocationRelativeTo((Component) null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent var1) {
        if (var1.getSource() == this.ok) {
            this.disp = true;
            this.dispose();
        }

    }

    public boolean getBoolean() {
        return this.disp;
    }

    public void append(String var1) {
        this.txt.repaint();
    }
}
