package org.remcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class VSeizeWaitDialog extends JDialog implements ActionListener {
    public static final byte SELYES = 0;
    public static final byte SELNO = 2;
    private final int szWaitTimerTick = 1000;
    JPanel mainPanel;
    JLabel txt;
    JButton seize;
    JButton cancel;
    boolean disp;
    byte userInput;
    remcons remconsObj;
    String susr;
    String saddr;
    int sflag;
    private Timer szWaitTimer;

    public VSeizeWaitDialog(remcons var1, String var2, String var3, int var4) {
        super(null == var1.ParentApp.dispFrame ? new JFrame() : var1.ParentApp.dispFrame, var1.getLocalString(12562), true);
        this.remconsObj = var1;
        this.susr = var2;
        this.saddr = var3;
        this.sflag = var4;
        this.ui_init(var1.ParentApp.dispFrame);
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("VSeizeWaitDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    protected void ui_init(JFrame var1) {
        this.txt = new JLabel("<html>" + this.getLocalString(8264) + " " + this.susr + " " + this.getLocalString(8265) + " " + this.saddr + " " + this.getLocalString(8282) + "<br><br>" + this.getLocalString(8283) + this.sflag + this.getLocalString(8284) + "</html>");
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        this.mainPanel.add(this.txt);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        this.seize = new JButton(this.getLocalString(8285));
        this.seize.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(8286));
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
        this.szWaitTimer = new Timer(this.szWaitTimerTick, false, this.remconsObj);
        this.szWaitTimer.setListener(new szWaitTimerListener(), this);
        this.szWaitTimer.start();
        System.out.println("seize wait timer started...");
        this.setSize(this.mainPanel.getPreferredSize().width + 40, this.mainPanel.getPreferredSize().height + 100);
        this.setResizable(false);
        this.setLocationRelativeTo((Component) null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent var1) {
        if (var1.getSource() == this.seize) {
            this.userInput = 0;
            this.dispose();
            this.stop_szWaitTimer();
            this.disp = true;
        } else if (var1.getSource() == this.cancel) {
            this.userInput = 2;
            this.dispose();
            this.stop_szWaitTimer();
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

    private void stop_szWaitTimer() {
        if (this.szWaitTimer != null) {
            this.szWaitTimer.stop();
            this.szWaitTimer = null;
        }

    }

    class szWaitTimerListener implements TimerListener {
        szWaitTimerListener() {
        }

        public synchronized void timeout(Object var1) {
            VSeizeWaitDialog var2 = (VSeizeWaitDialog) var1;
            --var2.sflag;
            if (var2.sflag > 0) {
                VSeizeWaitDialog.this.txt.setText("<html>" + VSeizeWaitDialog.this.getLocalString(8264) + " " + var2.susr + " " + VSeizeWaitDialog.this.getLocalString(8265) + " " + var2.saddr + " " + VSeizeWaitDialog.this.getLocalString(8282) + "<br><br>" + VSeizeWaitDialog.this.getLocalString(8283) + var2.sflag + " " + VSeizeWaitDialog.this.getLocalString(8284) + "</html>");
            } else {
                ActionEvent var3 = new ActionEvent(var2.seize, 1, "vobjyes");
                var2.actionPerformed(var3);
            }

        }
    }
}
