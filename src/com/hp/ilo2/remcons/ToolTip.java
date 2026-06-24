package com.hp.ilo2.remcons;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

final class ToolTip extends JPanel implements MouseListener {
    private static final int VERTICAL_OFFSET = 10;
    private static final int HORIZONTAL_ENLARGE = 10;
    private final String tip;
    private final Component owner;
    private Container mainContainer = null;
    private LayoutManager mainLayout = null;
    private boolean shown = false;

    public ToolTip(final String var1, final Component var2) {
        super();
        this.tip = var1;
        this.owner = var2;
        var2.addMouseListener(this);
        this.setBackground(new Color(255, 255, 220));
    }

    public void paint(final Graphics var1) {
        var1.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
        var1.drawString(this.tip, 3, this.getSize().height - 3);
    }

    private void addToolTip() {
        this.mainContainer.setLayout(null);
        final FontMetrics var1 = this.getFontMetrics(this.owner.getFont());
        this.setSize(var1.stringWidth(this.tip) + 10, var1.getHeight());
        this.setLocation(this.owner.getLocationOnScreen().x - this.mainContainer.getLocationOnScreen().x, this.owner.getLocationOnScreen().y - this.mainContainer.getLocationOnScreen().y - 10);
        if (this.mainContainer.getSize().width < this.getLocation().x + this.getSize().width) {
            this.setLocation(this.mainContainer.getSize().width - (this.getSize().width + 10), this.getLocation().y - 10);
        }

        this.mainContainer.add(this, 0);
        this.mainContainer.validate();
        this.repaint();
        this.shown = true;
    }

    private void removeToolTip() {
        if (this.shown) {
            this.mainContainer.remove(0);
            this.mainContainer.setLayout(this.mainLayout);
            this.mainContainer.validate();
        }

        this.shown = false;
    }

    private void findMainContainer() {
        Container var1;
        for (var1 = this.owner.getParent(); null != var1 && !(var1 instanceof Window); var1 = var1.getParent()) {
        }

        this.mainContainer = var1;
        this.mainLayout = Objects.requireNonNull(this.mainContainer).getLayout();
    }

    public void mouseEntered(final MouseEvent var1) {
        this.findMainContainer();
        this.addToolTip();
    }

    public void mouseExited(final MouseEvent var1) {
        this.removeToolTip();
    }

    public void mousePressed(final MouseEvent var1) {
        this.removeToolTip();
    }

    public void mouseClicked(final MouseEvent var1) {
    }

    public void mouseReleased(final MouseEvent var1) {
    }
}
