package com.hp.ilo2.virtdevs;


import java.awt.*;

final class VProgressBar extends Canvas {
    private final int progressWidth;
    private final int progressHeight;
    private float percentage = 0.0F;
    private Image offscreenImg = null;
    private Color progressColor;
    private Color progressBackground;

    public VProgressBar(final int var1, final int var2) {
        super();
        this.progressColor = Color.red;
        this.progressBackground = Color.white;
        final Font var3 = new Font("Dialog", 0, 15);
        this.setFont(var3);
        this.progressWidth = var1;
        this.progressHeight = var2;
        this.setSize(var1, var2);
    }

    public VProgressBar(final int var1, final int var2, final Color var3, final Color var4, final Color var5) {
        super();
        this.progressColor = Color.red;
        this.progressBackground = Color.white;
        final Font var6 = new Font("Dialog", 0, 12);
        this.setFont(var6);
        this.progressWidth = var1;
        this.progressHeight = var2;
        this.progressColor = var4;
        this.progressBackground = var5;
        this.setSize(var1, var2);
        this.setBackground(var3);
    }

    public void updateBar(final float var1) {
        this.percentage = var1;
        this.repaint();
    }

    public void setCanvasColor(final Color var1) {
        this.setBackground(var1);
    }

    public void setProgressColor(final Color var1) {
        this.progressColor = var1;
    }

    public void setBackGroundColor(final Color var1) {
        this.progressBackground = var1;
    }

    public void paint(final Graphics var1) {
        final byte var4 = (byte) 4;
        if (null == this.offscreenImg) {
            this.offscreenImg = this.createImage(this.progressWidth - (int) var4, this.progressHeight - (int) var4);
        }

        final Graphics offscreenG = this.offscreenImg.getGraphics();
        final int var5 = this.offscreenImg.getWidth(this);
        final int var6 = this.offscreenImg.getHeight(this);
        offscreenG.setColor(this.progressBackground);
        offscreenG.fillRect(0, 0, var5, var6);
        offscreenG.setColor(this.progressColor);
        offscreenG.fillRect(0, 0, (int) ((float) var5 * this.percentage), var6);
        offscreenG.drawString((int) (this.percentage * 100.0F) + "%", var5 / 2 - 8, var6 / 2 + 5);
        offscreenG.clipRect(0, 0, (int) ((float) var5 * this.percentage), var6);
        offscreenG.setColor(this.progressBackground);
        offscreenG.drawString((int) (this.percentage * 100.0F) + "%", var5 / 2 - 8, var6 / 2 + 5);
        var1.setColor(this.progressBackground);
        var1.draw3DRect(this.getSize().width / 2 - this.progressWidth / 2, 0, this.progressWidth - 1, this.progressHeight - 1, false);
        var1.drawImage(this.offscreenImg, (int) var4 / 2, (int) var4 / 2, this);
    }

    public void update(final Graphics var1) {
        this.paint(var1);
    }
}
