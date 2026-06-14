package com.hp.ilo2.remcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public final class VSeizeWaitDialog extends JDialog implements ActionListener {
    public static final byte SELYES = (byte) 0;
    public static final byte SELNO = (byte) 2;
    private static final int szWaitTimerTick = 1000;
    JPanel mainPanel = null;
    JLabel txt = null;
    JButton seize = null;
    JButton cancel = null;
    boolean disp = false;
    byte userInput = (byte) 0;
    final remcons remconsObj;
    final String susr;
    final String saddr;
    int sflag;
    private Timer szWaitTimer = null;

    public VSeizeWaitDialog(final remcons var1, final String var2, final String var3, final int var4) {
        super(null == var1.ParentApp.dispFrame ? new JFrame() : var1.ParentApp.dispFrame, var1.getLocalString(12562), true);
        this.remconsObj = var1;
        this.susr = var2;
        this.saddr = var3;
        this.sflag = var4;
        this.ui_init(var1.ParentApp.dispFrame);
    }

    public String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("VSeizeWaitDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    protected void ui_init(final JFrame var1) {
        this.txt = new JLabel("<html>" + this.getLocalString(8264) + " " + this.susr + " " + this.getLocalString(8265) + " " + this.saddr + " " + this.getLocalString(8282) + "<br><br>" + this.getLocalString(8283) + this.sflag + this.getLocalString(8284) + "</html>");
        this.mainPanel = new JPanel();
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
        this.mainPanel.add(this.txt);
        this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
        this.seize = new JButton(this.getLocalString(8285));
        this.seize.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(8286));
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
        this.szWaitTimer = new Timer(this.szWaitTimerTick, false, this.remconsObj);
        this.szWaitTimer.setListener(new szWaitTimerListener(), this);
        this.szWaitTimer.start();
        System.out.println("seize wait timer started...");
        this.setSize(this.mainPanel.getPreferredSize().width + 40, this.mainPanel.getPreferredSize().height + 100);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(final ActionEvent var1) {
        if (var1.getSource() == this.seize) {
            this.userInput = (byte) 0;
            this.dispose();
            this.stop_szWaitTimer();
            this.disp = true;
        } else if (var1.getSource() == this.cancel) {
            this.userInput = (byte) 2;
            this.dispose();
            this.stop_szWaitTimer();
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

    private void stop_szWaitTimer() {
        if (null != this.szWaitTimer) {
            this.szWaitTimer.stop();
            this.szWaitTimer = null;
        }

    }

    class szWaitTimerListener implements TimerListener {

        public synchronized void timeout(final Object var1) {
            final VSeizeWaitDialog var2 = (VSeizeWaitDialog) var1;
            --var2.sflag;
            if (0 < var2.sflag) {
                VSeizeWaitDialog.this.txt.setText("<html>" + VSeizeWaitDialog.this.getLocalString(8264) + " " + var2.susr + " " + VSeizeWaitDialog.this.getLocalString(8265) + " " + var2.saddr + " " + VSeizeWaitDialog.this.getLocalString(8282) + "<br><br>" + VSeizeWaitDialog.this.getLocalString(8283) + var2.sflag + " " + VSeizeWaitDialog.this.getLocalString(8284) + "</html>");
            } else {
                final ActionEvent var3 = new ActionEvent(var2.seize, 1, "vobjyes");
                var2.actionPerformed(var3);
            }

        }
    }
}
