package org.remcons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

public class dvcwin extends JPanel implements Runnable {
    protected static final int REFRESH_RATE = 15;
    private final int refresh_count = 0;
    private final int need_to_refresh = 1;
    private final int paint_count = 0;
    public int[] pixel_buffer;
    public remcons remconsObj;
    public boolean mirror = false;
    protected Image offscreen_image = null;
    protected Image first_image = null;
    protected Graphics offscreen_gc = null;
    protected MemoryImageSource image_source;
    protected int screen_x;
    protected int screen_y;
    protected int block_y;
    protected int block_x;
    protected ColorModel cm;
    protected Image clearScreenImage = null;
    protected Graphics clearScreenGc;
    protected Thread screen_updater = null;
    protected boolean updater_running = false;
    private int need_to_refresh_r = 1;
    private int need_to_refresh_w = 1;
    private int frametime = 0;
    private boolean abs_dimen_initialized = false;
    private boolean clear_screen = false;
    private boolean firstResize = true;

    public dvcwin(int var1, int var2, remcons var3) {
        this.screen_x = var1;
        this.screen_y = var2;
        this.cm = new DirectColorModel(32, 16711680, 65280, 255, 0);
        this.set_framerate(0);
        this.remconsObj = var3;
    }

    public boolean isFocusTraversable() {
        return true;
    }

    public void addNotify() {
        super.addNotify();
        if (this.offscreen_image == null) {
            if (this.screen_x == 0 && this.screen_y == 0) {
                this.screen_x = 1;
                this.screen_y = 1;
            }

            this.offscreen_image = this.createImage(this.screen_x, this.screen_y);
        }

    }

    public boolean repaint_it(int var1) {
        boolean var2 = false;
        if (var1 == 1) {
            ++this.need_to_refresh_w;
        } else {
            int var3 = this.need_to_refresh_w;
            if (this.need_to_refresh_r != var3) {
                this.need_to_refresh_r = var3;
                var2 = true;
            }
        }

        return var2;
    }

    public void paintComponent(Graphics var1) {
        super.paintComponents(var1);
        if (var1 == null) {
            System.out.println("dvcwin.paint() g is null");
        } else if (this.first_image != null) {
            var1.drawImage(this.first_image, 0, 0, this);
        } else if (this.clearScreenImage != null) {
            var1.drawImage(this.clearScreenImage, 0, 0, this);
        } else if (this.offscreen_image != null) {
            var1.drawImage(this.offscreen_image, 0, 0, this);
        }
    }

    public void update(Graphics var1) {
        if (this.offscreen_image == null || null == this.offscreen_gc) {
            this.offscreen_image = this.createImage(this.getSize().width, this.getSize().height);
            this.offscreen_gc = this.offscreen_image.getGraphics();
        }

        if (this.first_image != null) {
            if (null == this.offscreen_gc) {
                System.out.println("Message from offscreen_gc null detection");
            }

            this.offscreen_gc.drawImage(this.first_image, 0, 0, this);
        }

        var1.drawImage(this.offscreen_image, 0, 0, this);
    }

    public void paste_array(int[] var1, int var2, int var3, int var4, int var5) {
        int var7;
        if (8 == var5) {
            var7 = 8;
        } else if (var3 + 16 > this.screen_y) {
            var7 = this.screen_y - var3;
        } else {
            var7 = 16;
        }

        for (int var6 = 0; var6 < var7; ++var6) {
            try {
                System.arraycopy(var1, var6 * 16, this.pixel_buffer, (var3 + var6) * this.screen_x + var2, var4);
            } catch (Exception var9) {
                return;
            }
        }

        this.image_source.newPixels(var2, var3, var4, 16, false);
    }

    public void set_abs_dimensions(int var1, int var2) {
        if (var1 != this.screen_x || var2 != this.screen_y || !this.abs_dimen_initialized || this.clear_screen) {
            synchronized (this) {
                this.screen_x = var1;
                this.screen_y = var2;
            }

            this.clear_screen = false;
            this.abs_dimen_initialized = true;
            this.offscreen_image = null;
            this.pixel_buffer = new int[this.screen_x * this.screen_y];
            this.image_source = new MemoryImageSource(this.screen_x, this.screen_y, this.cm, this.pixel_buffer, 0, this.screen_x);
            if (this.image_source != null) {
            }

            this.image_source.setAnimated(true);
            this.image_source.setFullBufferUpdates(false);
            this.first_image = this.createImage(this.image_source);
            this.invalidate();
            this.validate();
            Container var4 = this.getParent();
            if (var4 != null) {
                while (var4.getParent() != null) {
                    var4.invalidate();
                    var4 = var4.getParent();
                }

                var4.invalidate();
                var4.validate();
            }

            System.gc();
            if (this.firstResize) {
                this.firstResize = false;
                this.remconsObj.ParentApp.dispFrame.pack();
                this.remconsObj.ParentApp.dispFrame.setLocationRelativeTo((Component) null);
            }
        }

    }

    public Dimension getPreferredSize() {
        synchronized (this) {
            Dimension var1 = new Dimension(this.screen_x, this.screen_y);
            return var1;
        }
    }

    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    public void show_text(String var1) {
        if (this.screen_updater == null) {
            System.out.println("Screen is no longer updating");
        } else {
            System.out.println("dvcwin:show_text " + var1);
            if (this.screen_x != 640 || this.screen_y != 100) {
                this.set_abs_dimensions(640, 100);
                this.image_source = null;
                this.first_image = null;
                this.offscreen_image = null;
                this.offscreen_image = this.createImage(this.screen_x, this.screen_y);
            }

            if (this.offscreen_image != null) {
                Graphics var2 = this.offscreen_image.getGraphics();
                new Color(0);
                var2.setColor(Color.black);
                var2.fillRect(0, 0, this.screen_x, this.screen_y);
                Font var3 = new Font("Courier", 0, 20);
                new Color(0);
                var2.setColor(Color.white);
                var2.setFont(var3);
                var2.drawString(var1, 10, 20);
                var2.drawImage(this.offscreen_image, 0, 0, this);
                var2.dispose();
                System.gc();
                super.repaint();
            }

        }
    }

    public void set_framerate(int var1) {
        if (var1 > 0) {
            this.frametime = 1000 / var1;
        } else {
            this.frametime = 66;
        }

    }

    public void run() {
        while (this.updater_running) {
            try {
                Thread.sleep((long) this.frametime);
            } catch (InterruptedException var2) {
            }

            if (this.repaint_it(0)) {
                super.repaint();
            }
        }

        System.out.println("Updater finished running");
    }

    public synchronized void start_updates() {
        this.screen_updater = new Thread(this, "dvcwin");
        this.updater_running = true;
        this.screen_updater.start();
        System.out.println("..screen update thread started..");
    }

    public synchronized void stop_updates() {
        System.out.println("dvcwin.stop_update");
        if (this.screen_updater != null && this.screen_updater.isAlive()) {
            this.updater_running = false;
        }

        this.screen_x = 0;
        this.screen_y = 0;
        this.screen_updater = null;
    }

    public void clearScreen() {
        if (this.screen_updater == null) {
            System.out.println("Screen is no longer updating");
        } else {
            this.clear_screen = true;
            if (this.screen_x == 0 && this.screen_y == 0) {
                System.out.println("clearScreen() EXCEPTION Screen_x = 0 Screen_y = 0");
                this.screen_x = 1;
                this.screen_y = 1;
            }

            this.set_abs_dimensions(this.screen_x, this.screen_y);
            this.offscreen_image = null;
            this.offscreen_image = this.createImage(this.screen_x, this.screen_y);
            Graphics var1 = this.offscreen_image.getGraphics();
            new Color(0);
            var1.setColor(Color.black);
            var1.fillRect(0, 0, this.screen_x, this.screen_y);
            var1.drawImage(this.offscreen_image, 0, 0, this);
            var1.dispose();
            System.gc();
            super.repaint();
        }
    }
}
