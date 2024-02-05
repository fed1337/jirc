package org.virtdevs;


import java.awt.*;

public class VProgressBar extends Canvas {
    private final int progressWidth;
    private final int progressHeight;
    private float percentage;
    private Image offscreenImg;
    private Graphics offscreenG;
    private Color progressColor;
    private Color progressBackground;

    public VProgressBar(int var1, int var2) {
        this.progressColor = Color.red;
        this.progressBackground = Color.white;
        Font var3 = new Font("Dialog", 0, 15);
        this.setFont(var3);
        this.progressWidth = var1;
        this.progressHeight = var2;
        this.setSize(var1, var2);
    }

    public VProgressBar(int var1, int var2, Color var3, Color var4, Color var5) {
        this.progressColor = Color.red;
        this.progressBackground = Color.white;
        Font var6 = new Font("Dialog", 0, 12);
        this.setFont(var6);
        this.progressWidth = var1;
        this.progressHeight = var2;
        this.progressColor = var4;
        this.progressBackground = var5;
        this.setSize(var1, var2);
        this.setBackground(var3);
    }

    public void updateBar(float var1) {
        this.percentage = var1;
        this.repaint();
    }

    public void setCanvasColor(Color var1) {
        this.setBackground(var1);
    }

    public void setProgressColor(Color var1) {
        this.progressColor = var1;
    }

    public void setBackGroundColor(Color var1) {
        this.progressBackground = var1;
    }

    public void paint(Graphics var1) {
        boolean var2 = false;
        boolean var3 = false;
        byte var4 = 4;
        if (this.offscreenImg == null) {
            this.offscreenImg = this.createImage(this.progressWidth - var4, this.progressHeight - var4);
        }

        this.offscreenG = this.offscreenImg.getGraphics();
        int var5 = this.offscreenImg.getWidth(this);
        int var6 = this.offscreenImg.getHeight(this);
        this.offscreenG.setColor(this.progressBackground);
        this.offscreenG.fillRect(0, 0, var5, var6);
        this.offscreenG.setColor(this.progressColor);
        this.offscreenG.fillRect(0, 0, (int) ((float) var5 * this.percentage), var6);
        this.offscreenG.drawString((int) (this.percentage * 100.0F) + "%", var5 / 2 - 8, var6 / 2 + 5);
        this.offscreenG.clipRect(0, 0, (int) ((float) var5 * this.percentage), var6);
        this.offscreenG.setColor(this.progressBackground);
        this.offscreenG.drawString((int) (this.percentage * 100.0F) + "%", var5 / 2 - 8, var6 / 2 + 5);
        var1.setColor(this.progressBackground);
        var1.draw3DRect(this.getSize().width / 2 - this.progressWidth / 2, 0, this.progressWidth - 1, this.progressHeight - 1, false);
        var1.drawImage(this.offscreenImg, var4 / 2, var4 / 2, this);
    }

    public void update(Graphics var1) {
        this.paint(var1);
    }
}
