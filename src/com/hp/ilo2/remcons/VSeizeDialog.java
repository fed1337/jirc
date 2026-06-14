package com.hp.ilo2.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class VSeizeDialog extends JDialog implements ActionListener {
    public static final byte SELCANCEL = (byte) 0;
    public static final byte SELSEIZE = (byte) 2;
    JPanel mainPanel = null;
    JLabel txt = null;
    JButton seize = null;
    JButton cancel = null;
    boolean disp = false;
    byte userInput = (byte) 0;
    final remcons remconsObj;

    public VSeizeDialog(final remcons var1) {
        super(null == var1.ParentApp.dispFrame ? new JFrame() : var1.ParentApp.dispFrame, var1.getLocalString(12562), true);
        this.remconsObj = var1;
        this.ui_init(var1.ParentApp.dispFrame);
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

    protected void ui_init(final JFrame var1) {
        this.txt = new JLabel(this.getLocalString(12563));
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        this.mainPanel.add(this.txt);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        this.seize = new JButton(this.getLocalString(12564));
        this.seize.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(12565));
        this.cancel.addActionListener(this);
        final GridBagLayout var2 = new GridBagLayout();
        final GridBagConstraints var3 = new GridBagConstraints();
        this.setLayout(var2);
        var3.fill = 2;
        var3.anchor = 17;
        var3.gridx = 0;
        var3.gridy = 0;
        this.add(this.mainPanel, var3);
        final JPanel var4 = new JPanel();
        var4.setLayout(new FlowLayout(2));
        var4.add(this.cancel);
        var4.add(this.seize);
        var3.fill = 0;
        var3.anchor = 13;
        var3.gridx = 0;
        var3.gridy = 1;
        var3.gridwidth = 1;
        this.add(var4, var3);
        this.setSize(this.mainPanel.getPreferredSize().width + 40, this.mainPanel.getPreferredSize().height + 100);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(final ActionEvent var1) {
        if (var1.getSource() == this.seize) {
            this.userInput = (byte) 2;
            this.dispose();
            this.disp = true;
        } else if (var1.getSource() == this.cancel) {
            this.userInput = (byte) 0;
            this.dispose();
            this.disp = true;
        }

    }

    public boolean disposed() {
        return this.disp;
    }

    public void append(final String var1) {
        this.txt.repaint();
    }

    public byte getUserInput() {
        return this.userInput;
    }
}
