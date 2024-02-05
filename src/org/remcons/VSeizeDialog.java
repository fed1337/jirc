package org.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VSeizeDialog extends JDialog implements ActionListener {
    public static final byte SELCANCEL = 0;
    public static final byte SELSEIZE = 2;
    JPanel mainPanel;
    JLabel txt;
    JButton seize;
    JButton cancel;
    boolean disp;
    byte userInput;
    remcons remconsObj;

    public VSeizeDialog(remcons var1) {
        super(null == var1.ParentApp.dispFrame ? new JFrame() : var1.ParentApp.dispFrame, var1.getLocalString(12562), true);
        this.remconsObj = var1;
        this.ui_init(var1.ParentApp.dispFrame);
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("VSeizeDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    protected void ui_init(JFrame var1) {
        this.txt = new JLabel(this.getLocalString(12563));
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        this.mainPanel.add(this.txt);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        this.seize = new JButton(this.getLocalString(12564));
        this.seize.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(12565));
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
        this.setLocationRelativeTo((Component) null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent var1) {
        if (var1.getSource() == this.seize) {
            this.userInput = 2;
            this.dispose();
            this.disp = true;
        } else if (var1.getSource() == this.cancel) {
            this.userInput = 0;
            this.dispose();
            this.disp = true;
        }

    }

    public boolean disposed() {
        return this.disp;
    }

    public void append(String var1) {
        this.txt.repaint();
    }

    public byte getUserInput() {
        return this.userInput;
    }
}
