package com.hp.ilo2.remcons;


import org.jirc.Version;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public final class aboutJircDialog extends JDialog {

    private static final String INFO_URL = "https://github.com/fed1337/jirc";

    public aboutJircDialog(final remcons var1) {
        this(var1.ParentApp.dispFrame, var1.ParentApp.getLocalString(4137));
    }

    public aboutJircDialog(final Window owner, final String title) {
        super(owner, title, ModalityType.MODELESS);

        final JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        final JLabel line0 = new JLabel("JIRC v" + Version.get());
        line0.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JLabel line1 = new JLabel("Based on intgapp4_231.jar - Java IRC Version 231");
        line1.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);
        line2.add(new JLabel("Visit "));
        line2.add(aboutJircDialog.createLink());
        line2.add(new JLabel(" for more info"));

        content.add(line0);
        content.add(line1);
        content.add(Box.createVerticalStrut(6));
        content.add(line2);

        final JButton close = new JButton("Close");
        close.addActionListener(e -> this.dispose());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(close);

        this.setLayout(new BorderLayout());
        this.add(content, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.SOUTH);
        this.getRootPane().setDefaultButton(close);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    private static JLabel createLink() {
        final JLabel link = new JLabel("<html><a href=\"\">" + aboutJircDialog.INFO_URL + "</a></html>");
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                aboutJircDialog.openUrl();
            }
        });
        return link;
    }

    private static void openUrl() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(aboutJircDialog.INFO_URL));
            } else {
                System.out.println("Desktop browse not supported; link: " + aboutJircDialog.INFO_URL);
            }
        } catch (final Exception ex) {
            System.out.println("Could not open link " + aboutJircDialog.INFO_URL + ": " + ex);
        }
    }
}
