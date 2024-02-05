package org.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class hotKeysDialog extends JDialog implements ActionListener, WindowListener {
    JButton close;
    String jsonString;
    String[] ctrl = new String[]{"Ctrl-T:", "Ctrl-U:", "Ctrl-V:", "Ctrl-W:", "Ctrl-X:", "Ctrl-Y:"};
    Hashtable map = new Hashtable() {
        {
            this.put("0", "NONE");
            this.put("41", "ESC");
            this.put("226", "L_ALT");
            this.put("230", "R_ALT");
            this.put("225", "L_SHIFT");
            this.put("229", "R_SHIFT");
            this.put("224", "L_CTRL");
            this.put("228", "R-CTRL");
            this.put("227", "L_GUI");
            this.put("231", "R_GUI");
            this.put("73", "INS");
            this.put("76", "DEL");
            this.put("74", "HOME");
            this.put("77", "END");
            this.put("75", "PG_UP");
            this.put("78", "PG_DN");
            this.put("88", "ENTER");
            this.put("43", "TAB");
            this.put("72", "BREAK");
            this.put("42", "BACKSPACE");
            this.put("87", "NUM_PLUS");
            this.put("86", "NUM_MINUS");
            this.put("71", "SCRL_LCK");
            this.put("154", "SYS_RQ");
            this.put("70", "PRINT_SCRN");
            this.put("58", "F1");
            this.put("59", "F2");
            this.put("60", "F3");
            this.put("61", "F4");
            this.put("62", "F5");
            this.put("63", "F6");
            this.put("64", "F7");
            this.put("65", "F8");
            this.put("66", "F9");
            this.put("67", "F10");
            this.put("68", "F11");
            this.put("69", "F12");
            this.put("44", "SPACE");
            this.put("52", "'");
            this.put("54", ",");
            this.put("45", "-");
            this.put("55", ".");
            this.put("56", "/");
            this.put("39", "0");
            this.put("30", "1");
            this.put("31", "2");
            this.put("32", "3");
            this.put("33", "4");
            this.put("34", "5");
            this.put("35", "6");
            this.put("36", "7");
            this.put("37", "8");
            this.put("38", "9");
            this.put("51", ";");
            this.put("46", "=");
            this.put("47", "[");
            this.put("49", "\\");
            this.put("48", "]");
            this.put("53", "'");
            this.put("4", "a");
            this.put("5", "b");
            this.put("6", "c");
            this.put("7", "d");
            this.put("8", "e");
            this.put("9", "f");
            this.put("10", "g");
            this.put("11", "h");
            this.put("12", "i");
            this.put("13", "j");
            this.put("14", "k");
            this.put("15", "l");
            this.put("16", "m");
            this.put("17", "n");
            this.put("18", "o");
            this.put("19", "p");
            this.put("20", "q");
            this.put("21", "r");
            this.put("22", "s");
            this.put("23", "t");
            this.put("24", "u");
            this.put("25", "v");
            this.put("26", "w");
            this.put("27", "x");
            this.put("28", "y");
            this.put("29", "z");
        }
    };

    public hotKeysDialog(remcons var1) {
        super(var1.ParentApp.dispFrame, "Programmed Hot Keys", false);
        this.ui_init(var1);
    }

    protected void ui_init(remcons var1) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints var2 = new GridBagConstraints();
        this.close = new JButton("Close");
        this.close.addActionListener(this);
        JPanel var3 = new JPanel(new GridLayout(6, 6));
        GridBagConstraints var4 = new GridBagConstraints();
        var4.fill = 2;

        try {
            this.jsonString = var1.ParentApp.jsonObj.getJSONRequest("hot_keys");
            this.jsonString = this.jsonString.trim();
            this.jsonString = this.jsonString.substring(1, this.jsonString.length() - 1);
            Pattern var5 = Pattern.compile("-?\\d+");
            Matcher var6 = var5.matcher(this.jsonString);

            for (int var7 = 0; var7 < 6; ++var7) {
                JLabel var8 = new JLabel("        " + this.ctrl[var7] + "        ");
                var4.gridx = var7;
                var4.gridy = 0;
                var4.fill = 2;
                var3.add(var8, var4);

                for (int var9 = 1; var9 < 6 && var6.find(); ++var9) {
                    JLabel var10 = new JLabel((String) this.map.get(var6.group()));
                    var4.gridx = var7;
                    var4.gridy = var9;
                    var4.fill = 2;
                    var3.add(var10, var4);
                }
            }
        } catch (Exception var11) {
            System.out.println("Error Parsing the JSON Requets");
            var11.printStackTrace();
            this.dispose();
            return;
        }

        var3.setBorder(BorderFactory.createEtchedBorder(0));
        var2.gridx = 0;
        var2.gridy = 0;
        this.add(var3, var2);
        JPanel var12 = new JPanel();
        var12.setLayout(new FlowLayout(2));
        var12.add(this.close);
        var2.fill = 2;
        var2.anchor = 13;
        var2.gridx = 0;
        var2.gridy = 1;
        this.add(var12, var2);
        this.pack();
        this.setSize(this.getPreferredSize().width + 20, this.getPreferredSize().height + 10);
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
