package com.hp.ilo2.remcons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

public final class dvcwin extends JPanel implements Runnable {
    protected static final int REFRESH_RATE = 15;
    private static final int refresh_count = 0;
    private static final int need_to_refresh = 1;
    private static final int paint_count = 0;
    public int[] pixel_buffer = null;
    public final remcons remconsObj;
    public boolean mirror = false;
    protected Image offscreen_image = null;
    protected Image first_image = null;
    protected Graphics offscreen_gc = null;
    protected MemoryImageSource image_source = null;
    protected int screen_x;
    protected int screen_y;
    protected int block_y = 0;
    protected int block_x = 0;
    protected final ColorModel cm;
    protected Image clearScreenImage = null;
    protected Graphics clearScreenGc = null;
    protected Thread screen_updater = null;
    protected boolean updater_running = false;
    private int need_to_refresh_r = 1;
    private int need_to_refresh_w = 1;
    private int frametime = 0;
    private boolean abs_dimen_initialized = false;
    private boolean clear_screen = false;
    private boolean firstResize = true;

    public dvcwin(final int var1, final int var2, final remcons var3) {
        super();
        this.screen_x = var1;
        this.screen_y = var2;
        this.cm = new DirectColorModel(32, 16711680, 65280, 255, 0);
        this.set_framerate(0);
        this.remconsObj = var3;
    }

    public boolean isFocusable() {
        return true;
    }

    public void addNotify() {
        super.addNotify();
        if (null == this.offscreen_image) {
            if (0 == this.screen_x && 0 == this.screen_y) {
                this.screen_x = 1;
                this.screen_y = 1;
            }

            this.offscreen_image = this.createImage(this.screen_x, this.screen_y);
        }

    }

    public boolean repaint_it(final int var1) {
        boolean var2 = false;
        if (1 == var1) {
            ++this.need_to_refresh_w;
        } else {
            final int var3 = this.need_to_refresh_w;
            if (this.need_to_refresh_r != var3) {
                this.need_to_refresh_r = var3;
                var2 = true;
            }
        }

        return var2;
    }

    public void paintComponent(final Graphics var1) {
        super.paintComponents(var1);
        if (null == var1) {
            System.out.println("dvcwin.paint() g is null");
        } else if (null != this.first_image) {
            var1.drawImage(this.first_image, 0, 0, this);
        } else if (null != this.clearScreenImage) {
            var1.drawImage(this.clearScreenImage, 0, 0, this);
        } else if (null != this.offscreen_image) {
            var1.drawImage(this.offscreen_image, 0, 0, this);
        }
    }

    public void update(final Graphics var1) {
        if (null == this.offscreen_image || null == this.offscreen_gc) {
            this.offscreen_image = this.createImage(this.getSize().width, this.getSize().height);
            this.offscreen_gc = this.offscreen_image.getGraphics();
        }

        if (null != this.first_image) {
            if (null == this.offscreen_gc) {
                System.out.println("Message from offscreen_gc null detection");
            }

            this.offscreen_gc.drawImage(this.first_image, 0, 0, this);
        }

        var1.drawImage(this.offscreen_image, 0, 0, this);
    }

    public void paste_array(final int[] var1, final int var2, final int var3, final int var4, final int var5) {
        final int var7;
        if (8 == var5) {
            var7 = 8;
        } else if (var3 + 16 > this.screen_y) {
            var7 = this.screen_y - var3;
        } else {
            var7 = 16;
        }

        for (int var6 = 0; var6 < var7; ++var6) {
            try {
                System.arraycopy(var1, var6 << 4, this.pixel_buffer, (var3 + var6) * this.screen_x + var2, var4);
            } catch (final Exception var9) {
                return;
            }
        }

        this.image_source.newPixels(var2, var3, var4, 16, false);
    }

    public void set_abs_dimensions(final int var1, final int var2) {
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

            this.image_source.setAnimated(true);
            this.image_source.setFullBufferUpdates(false);
            this.first_image = this.createImage(this.image_source);

            // Invoked from the socket receiver thread. AWT layout
            // (invalidate/validate/pack) must run on the EDT, otherwise it
            // deadlocks against the EDT over the AWT tree lock vs. this monitor
            // (getPreferredSize is synchronized on this), freezing the console.
            javax.swing.SwingUtilities.invokeLater(() -> {
                this.invalidate();
                this.validate();
                Container var4 = this.getParent();
                if (null != var4) {
                    while (null != var4.getParent()) {
                        var4.invalidate();
                        var4 = var4.getParent();
                    }

                    var4.invalidate();
                    var4.validate();
                }

                if (this.firstResize) {
                    this.firstResize = false;
                    this.remconsObj.ParentApp.dispFrame.pack();
                    this.remconsObj.ParentApp.dispFrame.setLocationRelativeTo(null);
                }

                // Screen is now sized and on-screen: the console is responsive.
                this.remconsObj.ParentApp.signalReady();
            });
        }

    }

    public synchronized Dimension getPreferredSize() {
        return new Dimension(this.screen_x, this.screen_y);
    }

    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    public void show_text(final String var1) {
        if (null == this.screen_updater) {
            System.out.println("Screen is no longer updating");
        } else {
            System.out.println("dvcwin:show_text " + var1);
            if (640 != this.screen_x || 100 != this.screen_y) {
                this.set_abs_dimensions(640, 100);
                this.image_source = null;
                this.first_image = null;
                this.offscreen_image = null;
                this.offscreen_image = this.createImage(this.screen_x, this.screen_y);
            }

            if (null != this.offscreen_image) {
                final Graphics var2 = this.offscreen_image.getGraphics();
                new Color(0);
                var2.setColor(Color.black);
                var2.fillRect(0, 0, this.screen_x, this.screen_y);
                final Font var3 = new Font("Courier", 0, 20);
                new Color(0);
                var2.setColor(Color.white);
                var2.setFont(var3);
                var2.drawString(var1, 10, 20);
                var2.drawImage(this.offscreen_image, 0, 0, this);
                var2.dispose();
                super.repaint();
            }

        }
    }

    public void set_framerate(final int var1) {
        if (0 < var1) {
            this.frametime = 1000 / var1;
        } else {
            this.frametime = 66;
        }

    }

    public void run() {
        while (this.updater_running) {
            try {
                Thread.sleep((long) this.frametime);
            } catch (final InterruptedException var2) {
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
        if (null != this.screen_updater && this.screen_updater.isAlive()) {
            this.updater_running = false;
        }

        this.screen_x = 0;
        this.screen_y = 0;
        this.screen_updater = null;
    }

    public void clearScreen() {
        if (null == this.screen_updater) {
            System.out.println("Screen is no longer updating");
        } else {
            this.clear_screen = true;
            if (0 == this.screen_x && 0 == this.screen_y) {
                System.out.println("clearScreen() EXCEPTION Screen_x = 0 Screen_y = 0");
                this.screen_x = 1;
                this.screen_y = 1;
            }

            this.set_abs_dimensions(this.screen_x, this.screen_y);
            this.offscreen_image = null;
            this.offscreen_image = this.createImage(this.screen_x, this.screen_y);
            final Graphics var1 = this.offscreen_image.getGraphics();
            new Color(0);
            var1.setColor(Color.black);
            var1.fillRect(0, 0, this.screen_x, this.screen_y);
            var1.drawImage(this.offscreen_image, 0, 0, this);
            var1.dispose();
            super.repaint();
        }
    }
}
