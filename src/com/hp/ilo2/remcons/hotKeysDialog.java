package com.hp.ilo2.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class hotKeysDialog extends JDialog implements ActionListener, WindowListener {
    private JButton close = null;
    private final String[] ctrl = {"Ctrl-T:", "Ctrl-U:", "Ctrl-V:", "Ctrl-W:", "Ctrl-X:", "Ctrl-Y:"};
    private final Map<String, String> map = hotKeysDialog.buildKeyMap();

    private static Map<String, String> buildKeyMap() {
        final Map<String, String> var0 = new HashMap<>(128);
        var0.put("0", "NONE");
        var0.put("41", "ESC");
        var0.put("226", "L_ALT");
        var0.put("230", "R_ALT");
        var0.put("225", "L_SHIFT");
        var0.put("229", "R_SHIFT");
        var0.put("224", "L_CTRL");
        var0.put("228", "R-CTRL");
        var0.put("227", "L_GUI");
        var0.put("231", "R_GUI");
        var0.put("73", "INS");
        var0.put("76", "DEL");
        var0.put("74", "HOME");
        var0.put("77", "END");
        var0.put("75", "PG_UP");
        var0.put("78", "PG_DN");
        var0.put("88", "ENTER");
        var0.put("43", "TAB");
        var0.put("72", "BREAK");
        var0.put("42", "BACKSPACE");
        var0.put("87", "NUM_PLUS");
        var0.put("86", "NUM_MINUS");
        var0.put("71", "SCRL_LCK");
        var0.put("154", "SYS_RQ");
        var0.put("70", "PRINT_SCRN");
        var0.put("58", "F1");
        var0.put("59", "F2");
        var0.put("60", "F3");
        var0.put("61", "F4");
        var0.put("62", "F5");
        var0.put("63", "F6");
        var0.put("64", "F7");
        var0.put("65", "F8");
        var0.put("66", "F9");
        var0.put("67", "F10");
        var0.put("68", "F11");
        var0.put("69", "F12");
        var0.put("44", "SPACE");
        var0.put("52", "'");
        var0.put("54", ",");
        var0.put("45", "-");
        var0.put("55", ".");
        var0.put("56", "/");
        var0.put("39", "0");
        var0.put("30", "1");
        var0.put("31", "2");
        var0.put("32", "3");
        var0.put("33", "4");
        var0.put("34", "5");
        var0.put("35", "6");
        var0.put("36", "7");
        var0.put("37", "8");
        var0.put("38", "9");
        var0.put("51", ";");
        var0.put("46", "=");
        var0.put("47", "[");
        var0.put("49", "\\");
        var0.put("48", "]");
        var0.put("53", "'");
        var0.put("4", "a");
        var0.put("5", "b");
        var0.put("6", "c");
        var0.put("7", "d");
        var0.put("8", "e");
        var0.put("9", "f");
        var0.put("10", "g");
        var0.put("11", "h");
        var0.put("12", "i");
        var0.put("13", "j");
        var0.put("14", "k");
        var0.put("15", "l");
        var0.put("16", "m");
        var0.put("17", "n");
        var0.put("18", "o");
        var0.put("19", "p");
        var0.put("20", "q");
        var0.put("21", "r");
        var0.put("22", "s");
        var0.put("23", "t");
        var0.put("24", "u");
        var0.put("25", "v");
        var0.put("26", "w");
        var0.put("27", "x");
        var0.put("28", "y");
        var0.put("29", "z");
        return var0;
    }

    public hotKeysDialog(final remcons var1) {
        super(var1.ParentApp.dispFrame, "Programmed Hot Keys", false);
        this.ui_init(var1);
    }

    private void ui_init(final remcons var1) {
        this.setLayout(new GridBagLayout());
        final GridBagConstraints var2 = new GridBagConstraints();
        this.close = new JButton("Close");
        this.close.addActionListener(this);
        final JPanel var3 = new JPanel(new GridLayout(6, 6));
        final GridBagConstraints var4 = new GridBagConstraints();
        var4.fill = 2;

        try {
            String jsonString = var1.ParentApp.jsonObj.getJSONRequest("hot_keys");
            jsonString = jsonString.trim();
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            final Pattern var5 = Pattern.compile("-?\\d+");
            final Matcher var6 = var5.matcher(jsonString);

            for (int var7 = 0; 6 > var7; ++var7) {
                final JLabel var8 = new JLabel("        " + this.ctrl[var7] + "        ");
                var4.gridx = var7;
                var4.gridy = 0;
                var4.fill = 2;
                var3.add(var8, var4);

                for (int var9 = 1; 6 > var9 && var6.find(); ++var9) {
                    final JLabel var10 = new JLabel(this.map.get(var6.group()));
                    var4.gridx = var7;
                    var4.gridy = var9;
                    var4.fill = 2;
                    var3.add(var10, var4);
                }
            }
        } catch (final Exception var11) {
            System.out.println("Error Parsing the JSON Requests");
            var11.printStackTrace();
            this.dispose();
            return;
        }

        var3.setBorder(BorderFactory.createEtchedBorder(0));
        var2.gridx = 0;
        var2.gridy = 0;
        this.add(var3, var2);
        final JPanel var12 = new JPanel();
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
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(final ActionEvent var1) {
        if (var1.getSource() == this.close) {
            this.dispose();
        }

    }

    public void windowClosing(final WindowEvent var1) {
        this.dispose();
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
