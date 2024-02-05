package org.remcons;


import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolTip extends JPanel implements MouseListener {
    private final int VERTICAL_OFFSET = 10;
    private final int HORIZONTAL_ENLARGE = 10;
    protected String tip;
    protected Component owner;
    private Container mainContainer;
    private LayoutManager mainLayout;
    private boolean shown;

    public ToolTip(String var1, Component var2) {
        this.tip = var1;
        this.owner = var2;
        var2.addMouseListener(this);
        this.setBackground(new Color(255, 255, 220));
    }

    public void paint(Graphics var1) {
        var1.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
        var1.drawString(this.tip, 3, this.getSize().height - 3);
    }

    private void addToolTip() {
        this.mainContainer.setLayout((LayoutManager) null);
        FontMetrics var1 = this.getFontMetrics(this.owner.getFont());
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
        for (var1 = this.owner.getParent(); !(var1 instanceof Applet) && !(var1 instanceof Frame); var1 = var1.getParent()) {
        }

        this.mainContainer = var1;
        this.mainLayout = this.mainContainer.getLayout();
    }

    public void mouseEntered(MouseEvent var1) {
        this.findMainContainer();
        this.addToolTip();
    }

    public void mouseExited(MouseEvent var1) {
        this.removeToolTip();
    }

    public void mousePressed(MouseEvent var1) {
        this.removeToolTip();
    }

    public void mouseClicked(MouseEvent var1) {
    }

    public void mouseReleased(MouseEvent var1) {
    }
}
