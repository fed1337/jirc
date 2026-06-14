package org.jirc;


import com.hp.ilo2.remcons.OkCancelDialog;
import com.hp.ilo2.remcons.URLDialog;
import com.hp.ilo2.remcons.remcons;
import com.hp.ilo2.virtdevs.MediaAccess;
import com.hp.ilo2.virtdevs.VErrorDialog;
import com.hp.ilo2.virtdevs.VFileDialog;
import com.hp.ilo2.virtdevs.virtdevs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Consumer;

public class App implements Runnable, ActionListener, ItemListener {
    private static final int REMCONS_MAX_FN_KEYS = 12;
    private static final int REMCONS_MAX_KBD_LAYOUT = 17;
    public final virtdevs virtdevsObj = new virtdevs(this);
    public final remcons remconsObj = new remcons(this);
    public final locinfo locinfoObj = new locinfo(this);
    public final jsonparser jsonObj = new jsonparser(this);
    public String optional_features = null;
    public String moniker = null;
    public boolean moniker_cached = false;
    public JFrame dispFrame = null;
    public JPanel mainPanel = null;
    public JMenuItem vdMenuItemCrImage = null;
    public String enc_key = null;
    public String rc_port = null;
    public String vm_key = null;
    public String vm_port = null;
    public String server_name = null;
    public String ilo_fqdn = null;
    public String enclosure = null;
    public int blade = 0;
    public int bay = 0;
    public final byte[] enc_key_val = new byte[16];
    public int dwidth = 0;
    public int dheight = 0;
    public boolean exit = false;
    public boolean fdSelected = false;
    public boolean cdSelected = false;
    public boolean in_enclosure = false;
    JMenuBar mainMenuBar = null;
    JMenu psMenu = null;
    JMenu vdMenu = null;
    JMenu kbMenu = null;
    JMenu kbCAFMenu = null;
    JMenu kbAFMenu = null;
    JMenu kbLangMenu = null;
    JMenu hlpMenu = null;
    int vdmenuIndx = 0;
    int fdMenuItems = 0;
    int cdMenuItems = 0;
    JCheckBoxMenuItem[] vdMenuItems = null;
    JMenuItem momPress = null;
    JMenuItem pressHold = null;
    JMenuItem powerCycle = null;
    JMenuItem sysReset = null;
    JMenuItem ctlAltDel = null;
    JMenuItem numLock = null;
    JMenuItem capsLock = null;
    JMenuItem ctlAltBack = null;
    JMenuItem hotKeys = null;
    JMenuItem aboutJirc = null;
    JMenuItem[] ctlAltFn = null;
    JMenuItem[] AltFn = null;
    JCheckBoxMenuItem[] localKbdLayout = null;
    JPanel dispStatusBar = null;
    JMenuItem mdebug1 = null;
    JMenuItem mdebug2 = null;
    JMenuItem mdebug3 = null;
    JScrollPane scroller = null;
    String rcErrMessage = null;
    private MediaAccess ma = null;
    private final IrcSessionContext ctx;
    private Runnable onReady = null;
    private Consumer<String> onClosed = null;
    private volatile boolean readySignaled = false;
    private volatile boolean closing = false;

    public App(final IrcSessionContext ctx) {
        super();
        this.ctx = ctx;
    }

    /**
     * Invoked once when the remote console is up and responsive (first video
     * frame or first power-status update, whichever lands first). Used to
     * dismiss the startup loader. Idempotent and always fired on the EDT.
     */
    public void signalReady() {
        if (this.readySignaled) {
            return;
        }
        this.readySignaled = true;
        final Runnable r = this.onReady;
        if (null != r) {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Single teardown path for the console: stops the engine, disposes the
     * window, and hands control back to the connection dialog with an optional
     * reason. Runs on the EDT (so it is safe to call from socket threads) and is
     * idempotent. Replaces the old scattered {@code dispFrame.setVisible(false)}
     * calls that left the app with no visible window.
     *
     * @param reason user-readable cause, or {@code null} for a plain user close
     */
    public void requestClose(final String reason) {
        SwingUtilities.invokeLater(() -> {
            if (this.closing) {
                return;
            }
            this.closing = true;
            try {
                this.stop();
            } catch (final Exception e) {
                System.out.println("Exception during console stop: " + e);
            }
            if (null != this.dispFrame) {
                this.dispFrame.dispose();
            }
            if (null != this.onClosed) {
                this.onClosed.accept(reason);
            }
        });
    }

    /**
     * Parameter lookup now comes from the session context instead of the JNLP
     * {@code <param>} entries.
     */
    public String getParameter(final String name) {
        return this.ctx.getParameter(name);
    }

    /**
     * Base URL of the iLO: {@code https://host:port/}.
     */
    public URL getCodeBase() {
        return this.ctx.getCodeBase();
    }

    /**
     * URL of the page that used to host the engine.
     */
    public URL getDocumentBase() {
        return this.ctx.getDocumentBase();
    }

    /**
     * Loads the image off the given URL (typically a classpath resource) and
     * blocks until it is ready, matching the synchronous behavior the menu/icon
     * code expects.
     */
    public static Image getImage(final URL url) {
        if (null == url) {
            return null;
        }

        final Image img = Toolkit.getDefaultToolkit().createImage(url);
        if (null != img) {
            final MediaTracker tracker = new MediaTracker(new Container());
            tracker.addImage(img, 0);
            try {
                tracker.waitForID(0);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return img;
    }

    /**
     * Standalone entry point for the engine. Runs {@code init()} then
     * {@code start()} on a dedicated thread, keeping the calling UI responsive
     * while the engine performs its blocking iLO handshake.
     */
    public void launch() {
        this.launch(null, null, null);
    }

    /**
     * @param onReady  run on the EDT once the console is responsive (see {@link #signalReady()})
     * @param onError  run on the EDT if the engine throws during init/start
     * @param onClosed run on the EDT when the console is torn down; the argument is
     *                 a user-readable reason (e.g. "session ended") or {@code null}
     *                 when the user simply closed the window
     */
    public void launch(final Runnable onReady, final Consumer<Throwable> onError, final Consumer<String> onClosed) {
        this.onReady = onReady;
        this.onClosed = onClosed;
        final Thread t = new Thread(() -> {
            try {
                if (this.init()) {
                    this.start();
                }
            } catch (final Exception e) {
                System.out.println("FAILURE: exception launching IRC engine");
                e.printStackTrace();
                if (null != onError) {
                    SwingUtilities.invokeLater(() -> onError.accept(e));
                }
            }
        }, "irc-engine");
        t.start();
    }


    public String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("remcons:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    /**
     * @return {@code true} if the console initialized successfully and
     * {@link #start()} should run; {@code false} if init failed (in which
     * case teardown has already been requested via {@link #requestClose}).
     */
    public boolean init() {
        final boolean var1;
        System.out.println("Started Retrieving parameters from ILO..");
        final String var2 = this.jsonObj.getJSONRequest("rc_info");
        if (null != var2) {
            this.ApplyRcInfoParameters(var2);
            System.out.println("Completed Retrieving parameters from ILO");
        }

        var1 = this.locinfoObj.initLocStrings();
        this.virtdevsObj.init();
        this.remconsObj.init();
        this.ui_init();
        if (null == var2) {
            System.out.println("Failed to retrieve parameters from ILO");
            this.requestClose(null != this.rcErrMessage ? this.rcErrMessage : this.getLocalString(8212));
            return false;
        } else if (!var1) {
            this.requestClose(null != this.locinfoObj.rcErrMessage ? this.locinfoObj.rcErrMessage : this.getLocalString(8212));
            return false;
        }

        return true;
    }

    public void start() {
        try {
            // Assemble the content pane BEFORE the network/video threads start.
            // Otherwise the first video frame can fire dvcwin's dispFrame.pack()
            // while the screen is not yet in the content pane, packing the
            // window down to just the menu-bar height.
            this.dispFrame.getContentPane().add(this.scroller, "Center");
            this.dispFrame.getContentPane().add(this.dispStatusBar, "South");
            this.scroller.validate();
            this.dispStatusBar.validate();
            this.dispFrame.validate();
            this.virtdevsObj.start();
            this.remconsObj.start();
            System.out.println("Set Inital focus for session..");
            this.remconsObj.session.requestFocus();
            this.run();
        } catch (final Exception var2) {
            System.out.println("FAILURE: exception starting console");
            var2.printStackTrace();
        }

    }

    public void stop() {
        this.exit = true;
        this.virtdevsObj.stop();
        this.remconsObj.remconsUnInstallKeyboardHook();
        this.remconsObj.stop();
    }

    public void destroy() {
        System.out.println("Destroying subsystems");
        this.exit = true;
        this.remconsObj.remconsUnInstallKeyboardHook();
        this.virtdevsObj.destroy();
        this.remconsObj.destroy();
        this.dispFrame.dispose();
    }

    public synchronized void run() {
        boolean var6;

        while (true) {
            try {
                int var11 = 0;
                int var1 = 0;
                this.ma = new MediaAccess();
                final String[] var7 = this.ma.devices();

                int var2;
                for (int var8 = 0; null != var7 && var8 < var7.length; ++var8) {
                    var2 = this.ma.devtype(var7[var8]);
                    if (2 == var2 || 5 == var2) {
                        ++var1;
                    }
                }

                int var3;
                int var4;
                if (var1 > this.vdmenuIndx - 4) {
                    final ClassLoader var9 = this.getClass().getClassLoader();

                    for (var3 = 0; null != var7 && var3 < var7.length; ++var3) {
                        var6 = false;
                        var2 = this.ma.devtype(var7[var3]);

                        for (var4 = 0; var4 < this.vdmenuIndx - 4; ++var4) {
                            if (var7[var3].equals(this.vdMenu.getItem(var4).getText())) {
                                var6 = true;
                                ++var11;
                            }
                        }

                        if (!var6) {
                            if (2 == var2) {
                                System.out.println("Device attached: " + var7[var3]);
                                this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(var7[var3]);
                                this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + var7[var3]);
                                this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                                if (!"A:".equals(var7[var3]) && !"B:".equals(var7[var3])) {
                                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var9.getResource("org/remcons/images/usb.png"))));
                                } else {
                                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var9.getResource("org/remcons/images/FloppyDisk.png"))));
                                }

                                this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx], var11);
                                this.vdMenu.updateUI();
                                ++this.vdmenuIndx;
                                break;
                            }

                            if (5 == var2) {
                                System.out.println("CDROM Hot plug device auto-update no supported at this time");
                            }
                        }
                    }
                } else if (var1 < this.vdmenuIndx - 4) {
                    for (var4 = 0; var4 < this.vdmenuIndx - 4; ++var4) {
                        var6 = false;

                        for (var3 = 0; null != var7 && var3 < var7.length; ++var3) {
                            var2 = this.ma.devtype(var7[var3]);
                            if ((2 == var2 || 5 == var2) && this.vdMenu.getItem(var4).getText().equals(var7[var3])) {
                                var6 = true;
                            }
                        }

                        if (!var6) {
                            System.out.println("Device removed: " + this.vdMenu.getItem(var4).getText());
                            this.vdMenu.remove(var4);
                            this.vdMenu.updateUI();
                            --this.vdmenuIndx;
                            break;
                        }
                    }
                }

                this.ma = null;
                this.remconsObj.session.set_status(3, "");
                remcons.sleepAtLeast(5000L);
                if (this.exit) {
                    break;
                }
            } catch (final InterruptedException var10) {
                System.out.println("Exception on App");
            }
        }

        System.out.println("Intgapp stopped running");
    }

    public void ui_init() {
        String var1 = "";
        System.out.println("Message from ui_init55");
        this.dispFrame = new JFrame("Java Integrated Remote Console");
        this.dispFrame.getContentPane().setLayout(new BorderLayout());
        this.dispFrame.addWindowListener(new WindowCloser());
        this.mainMenuBar = new JMenuBar();
        this.dispStatusBar = new JPanel(new BorderLayout());
        this.dispStatusBar.add(this.remconsObj.session.status_box, "West");
        this.dispStatusBar.add(this.remconsObj.pwrStatusPanel, "East");
        final String var3 = this.jsonObj.getJSONRequest("session_info");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        this.dispFrame.setJMenuBar(this.mainMenuBar);
        if (null != var3) {
            this.makePsMenu(this.mainMenuBar, jsonparser.getJSONNumber(var3, "reset_priv"));
            this.makeVdMenu(this.mainMenuBar, jsonparser.getJSONNumber(var3, "virtual_media_priv"));
        }

        this.makeKbMenu(this.mainMenuBar);
        final String var2 = this.jsonObj.getJSONRequest("login_session");
        if (null != var2) {
            var1 = jsonparser.getJSONObject(var2, "alt");
            if (null == var1 || 0 == jsonparser.getJSONNumber(var1, "mode")) {
                this.makeHlpMenu(this.mainMenuBar);
            }
        }

        this.scroller = new JScrollPane(this.remconsObj.session, 20, 30);
        this.scroller.setVisible(true);

        try {
            String var4 = this.getLocalString(4132) + " " + this.server_name + " " + this.getLocalString(4133) + " " + this.ilo_fqdn;
            if (1 == this.blade && this.in_enclosure) {
                var4 = var4 + " " + this.getLocalString(4134) + " " + this.enclosure + " " + this.getLocalString(4135) + " " + this.bay;
            }

            this.dispFrame.setTitle(var4);
        } catch (final Exception var13) {
            this.dispFrame.setTitle(this.getLocalString(4132) + " " + this.getCodeBase().getHost());
            System.out.println("IRC title not available");
        }

        final int var15 = Toolkit.getDefaultToolkit().getScreenSize().width;
        final int var5 = Toolkit.getDefaultToolkit().getScreenSize().height;
        final int var6 = Math.min(1054, var15);
        final int var7 = 874 > var5 ? var5 - 30 : 874;
        final int var8 = 1054 < var15 ? (var15 - 1054) / 2 : 0;
        final int var9 = 874 < var5 ? (var5 - 874) / 2 : 0;
        this.dispFrame.setSize(var6, var7);
        this.dispFrame.setLocation(var8, var9);
        System.out.println("check dimensions " + var6 + " " + var7 + " " + var8 + " " + var9);
        this.dispFrame.setVisible(true);

        try {
            final ClassLoader var11 = this.getClass().getClassLoader();
            if (null == var1 || 0 == jsonparser.getJSONNumber(var1, "mode")) {
                this.dispFrame.setIconImage(App.getImage(var11.getResource("org/remcons/images/ilo_logo.png")));
            }

            final Image var12 = this.dispFrame.getIconImage();
            if (null == var12) {
                System.out.println("Dimage is null");
            }
        } catch (final Exception var14) {
            System.out.println("JIRC icon not available");
        }

    }

    protected void makeHlpMenu(final JMenuBar var1) {
        this.hlpMenu = new JMenu(this.getLocalString(4136));
        this.aboutJirc = new JMenuItem(this.getLocalString(4137));
        this.aboutJirc.addActionListener(this);
        this.hlpMenu.add(this.aboutJirc);
        var1.add(this.hlpMenu);
    }

    protected void makeVdMenu(final JMenuBar var1, final int var2) {
        this.vdMenu = new JMenu(this.getLocalString(4098));
        if (1 == var2) {
            var1.add(this.vdMenu);
        }

    }

    public void updateVdMenu() {
        this.ma = new MediaAccess();
        final ClassLoader var2 = this.getClass().getClassLoader();
        final String var3 = this.jsonObj.getJSONRequest("vm_status");
        final String var4 = jsonparser.getJSONArray(var3, "options", 0);
        final String var5 = jsonparser.getJSONArray(var3, "options", 1);
        final String[] var6 = this.ma.devices();
        this.vdmenuIndx = 0;
        if (null != var6) {
            this.vdMenuItems = new JCheckBoxMenuItem[var6.length + 5];

            for (final String s : var6) {
                final int var7 = this.ma.devtype(s);
                if (5 == var7) {
                    this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(s);
                    this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + s);
                    this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/CD_Drive.png"))));
                    this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
                    ++this.vdmenuIndx;
                } else if (2 == var7) {
                    this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(s);
                    this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + s);
                    this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                    if (!"A:".equals(s) && !"B:".equals(s)) {
                        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/usb.png"))));
                    } else {
                        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/FloppyDisk.png"))));
                    }

                    this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
                    ++this.vdmenuIndx;
                }
            }
        } else {
            this.vdMenuItems = new JCheckBoxMenuItem[5];
            System.out.println("Media Access not available...");
        }

        this.ma = null;
        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4130) + " " + this.getLocalString(4106));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + this.getLocalString(12567));
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/Image_File.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        ++this.vdmenuIndx;
        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4131) + this.getLocalString(4106));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("FLOPPY");
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/Network.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        ++this.vdmenuIndx;
        if (1 == jsonparser.getJSONNumber(var4, "vm_url_connected") && 1 == jsonparser.getJSONNumber(var4, "vm_connected")) {
            this.fdSelected = true;
            this.lockFdMenu(false, "URL Removable Media");
        }

        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4130) + " " + this.getLocalString(4107));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + this.getLocalString(12567));
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/Image_File.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        ++this.vdmenuIndx;
        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4131) + this.getLocalString(4107));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("CDROM");
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(App.getImage(var2.getResource("org/remcons/images/Network.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        ++this.vdmenuIndx;
        if (1 == jsonparser.getJSONNumber(var5, "vm_url_connected") && 1 == jsonparser.getJSONNumber(var5, "vm_connected")) {
            this.cdSelected = true;
            this.lockCdMenu(false, "URL CD/DVD-ROM");
        }

        this.vdMenu.addSeparator();
        this.vdMenuItemCrImage = new JMenuItem(this.getLocalString(4109));
        this.vdMenuItemCrImage.setActionCommand("CreateDiskImage");
        this.vdMenuItemCrImage.addActionListener(this);
        this.vdMenu.add(this.vdMenuItemCrImage);
    }

    public void lockCdMenu(final boolean var1, final String var2) {
        for (int var5 = 0; var5 < this.vdmenuIndx; ++var5) {
            this.vdMenu.getItem(var5).removeItemListener(this);
            if (this.vdMenu.getItem(var5).getActionCommand().startsWith("cd") || "CDROM".equals(this.vdMenu.getItem(var5).getActionCommand())) {
                if (var2.equals(this.vdMenu.getItem(var5).getText())) {
                    this.vdMenu.getItem(var5).setSelected(!var1);
                } else {
                    this.vdMenu.getItem(var5).setSelected(false);
                    this.vdMenu.getItem(var5).setEnabled(var1);
                }
            }

            this.vdMenu.getItem(var5).addItemListener(this);
        }

    }

    public void lockFdMenu(final boolean var1, final String var2) {
        for (int var5 = 0; var5 < this.vdmenuIndx; ++var5) {
            this.vdMenu.getItem(var5).removeItemListener(this);
            if (this.vdMenu.getItem(var5).getActionCommand().startsWith("fd") || "FLOPPY".equals(this.vdMenu.getItem(var5).getActionCommand())) {
                if (var2.equals(this.vdMenu.getItem(var5).getText())) {
                    this.vdMenu.getItem(var5).setSelected(!var1);
                } else {
                    this.vdMenu.getItem(var5).setSelected(false);
                    this.vdMenu.getItem(var5).setEnabled(var1);
                }
            }

            this.vdMenu.getItem(var5).addItemListener(this);
        }

    }

    protected void makePsMenu(final JMenuBar var1, final int var2) {
        final ClassLoader var3 = this.getClass().getClassLoader();
        this.psMenu = new JMenu(this.getLocalString(4097));
        this.momPress = new JMenuItem(this.getLocalString(4100));
        this.momPress.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/press.png"))));
        this.momPress.setActionCommand("psMomPress");
        this.momPress.addActionListener(this);
        this.psMenu.add(this.momPress);
        this.pressHold = new JMenuItem(this.getLocalString(4101));
        this.pressHold.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/hold.png"))));
        this.pressHold.setActionCommand("psPressHold");
        this.pressHold.addActionListener(this);
        this.powerCycle = new JMenuItem(this.getLocalString(4102));
        this.powerCycle.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/coldboot.png"))));
        this.powerCycle.setActionCommand("psPowerCycle");
        this.powerCycle.addActionListener(this);
        this.sysReset = new JMenuItem(this.getLocalString(4103));
        this.sysReset.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/reset.png"))));
        this.sysReset.setActionCommand("psSysReset");
        this.sysReset.addActionListener(this);
        if (1 == var2) {
            var1.add(this.psMenu);
        }

    }

    public void updatePsMenu(final int var1) {
        if (0 == var1) {
            this.psMenu.remove(this.pressHold);
            this.psMenu.remove(this.powerCycle);
            this.psMenu.remove(this.sysReset);
        } else {
            this.psMenu.remove(this.pressHold);
            this.psMenu.remove(this.powerCycle);
            this.psMenu.remove(this.sysReset);
            this.psMenu.add(this.pressHold);
            this.psMenu.add(this.powerCycle);
            this.psMenu.add(this.sysReset);
        }

    }

    protected void makeKbMenu(final JMenuBar var1) {
        final ClassLoader var3 = this.getClass().getClassLoader();
        this.kbMenu = new JMenu(this.getLocalString(4099));
        this.kbCAFMenu = new JMenu("CTRL-ALT-Fn");
        this.kbAFMenu = new JMenu("ALT-Fn");
        this.kbLangMenu = new JMenu(this.getLocalString(4110));
        this.ctlAltDel = new JMenuItem(this.getLocalString(4104));
        this.ctlAltDel.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/Keyboard.png"))));
        this.ctlAltDel.setActionCommand("kbCtlAltDel");
        this.ctlAltDel.addActionListener(this);
        this.kbMenu.add(this.ctlAltDel);
        this.numLock = new JMenuItem(this.getLocalString(4105));
        this.numLock.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/Keyboard.png"))));
        this.numLock.setActionCommand("kbNumLock");
        this.numLock.addActionListener(this);
        this.kbMenu.add(this.numLock);
        this.capsLock = new JMenuItem(this.getLocalString(4128));
        this.capsLock.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/Keyboard.png"))));
        this.capsLock.setActionCommand("kbCapsLock");
        this.capsLock.addActionListener(this);
        this.kbMenu.add(this.capsLock);
        this.ctlAltBack = new JMenuItem("CTRL-ALT-BACKSPACE");
        this.ctlAltBack.setIcon(new ImageIcon(App.getImage(var3.getResource("org/remcons/images/Keyboard.png"))));
        this.ctlAltBack.setActionCommand("kbCtlAltBack");
        this.ctlAltBack.addActionListener(this);
        this.ctlAltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];

        int var2;
        for (var2 = 0; var2 < this.REMCONS_MAX_FN_KEYS; ++var2) {
            this.ctlAltFn[var2] = new JMenuItem("CTRL-ALT-F" + (var2 + 1));
            this.ctlAltFn[var2].setActionCommand("kbCtrlAltFn" + var2);
            this.ctlAltFn[var2].addActionListener(this);
            this.kbCAFMenu.add(this.ctlAltFn[var2]);
        }

        this.AltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];

        for (var2 = 0; var2 < this.REMCONS_MAX_FN_KEYS; ++var2) {
            this.AltFn[var2] = new JMenuItem("ALT-F" + (var2 + 1));
            this.AltFn[var2].setActionCommand("kbAltFn" + var2);
            this.AltFn[var2].addActionListener(this);
            this.kbAFMenu.add(this.AltFn[var2]);
        }

        this.localKbdLayout = new JCheckBoxMenuItem[this.REMCONS_MAX_KBD_LAYOUT];

        for (var2 = 0; var2 < this.REMCONS_MAX_KBD_LAYOUT; ++var2) {
            this.localKbdLayout[var2] = new JCheckBoxMenuItem(this.getLocalString(4111 + var2));
            this.localKbdLayout[var2].setActionCommand("localKbdLayout" + var2);
            this.localKbdLayout[var2].addItemListener(this);
            this.kbLangMenu.add(this.localKbdLayout[var2]);
        }

        this.localKbdLayout[0].setSelected(true);
        final String var4 = System.getProperty("os.name").toLowerCase();
        if (!var4.startsWith("windows")) {
            this.kbMenu.add(this.ctlAltBack);
            this.kbMenu.add(this.kbCAFMenu);
            this.kbMenu.add(this.kbAFMenu);
            this.kbMenu.add(this.kbLangMenu);
        }

        this.kbMenu.addSeparator();
        this.hotKeys = new JMenuItem(this.getLocalString(4129));
        this.hotKeys.addActionListener(this);
        this.kbMenu.add(this.hotKeys);
        var1.add(this.kbMenu);
    }

    public void actionPerformed(final ActionEvent var1) {
        final OkCancelDialog var3;
        if (var1.getSource() == this.momPress) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8297), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendMomPress();
            }
        } else if (var1.getSource() == this.pressHold) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8298), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendPressHold();
            }
        } else if (var1.getSource() == this.powerCycle) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8299), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendPowerCycle();
            }
        } else if (var1.getSource() == this.sysReset) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8300), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendSystemReset();
            }
        } else if (var1.getSource() == this.ctlAltDel) {
            this.remconsObj.session.send_ctrl_alt_del();
        } else if (var1.getSource() == this.numLock) {
            this.remconsObj.session.send_num_lock();
        } else if (var1.getSource() == this.capsLock) {
            this.remconsObj.session.send_caps_lock();
        } else if (var1.getSource() == this.ctlAltBack) {
            this.remconsObj.session.send_ctrl_alt_back();
        } else if (var1.getSource() == this.hotKeys) {
            this.remconsObj.viewHotKeys();
        } else if (var1.getSource() == this.vdMenuItemCrImage) {
            this.virtdevsObj.createImage();
        } else if (var1.getSource() == this.aboutJirc) {
            this.remconsObj.viewAboutJirc();
        } else {
            int var2;
            for (var2 = 0; var2 < this.REMCONS_MAX_FN_KEYS; ++var2) {
                if (var1.getSource() == this.ctlAltFn[var2]) {
                    this.remconsObj.session.send_ctrl_alt_fn(var2);
                    break;
                }

                if (var1.getSource() == this.AltFn[var2]) {
                    this.remconsObj.session.send_alt_fn(var2);
                    break;
                }
            }

            if (var2 >= this.REMCONS_MAX_FN_KEYS) {
                System.out.println("Unhandled ActionItem" + var1.getActionCommand());
            }
        }

    }

    public void itemStateChanged(final ItemEvent var1) {
        final boolean var3;
        JCheckBoxMenuItem var4 = null;
        String var5 = null;
        String var6 = null;
        final int var7 = var1.getStateChange();

        int var2;
        for (var2 = 0; var2 < this.REMCONS_MAX_KBD_LAYOUT; ++var2) {
            if (this.localKbdLayout[var2] == var1.getSource() && 1 == var7) {
                System.out.println(var2);
                this.localKbdLayout[var2].setSelected(true);
                this.kbdLayoutMenuHandler(var2);
                return;
            }
        }

        for (var2 = 0; var2 < this.vdmenuIndx; ++var2) {
            if (this.vdMenuItems[var2] == var1.getSource()) {
                var4 = this.vdMenuItems[var2];
                var5 = var4.getActionCommand();
                var6 = var4.getText();
                break;
            }
        }

        if (null != var4 && null != var5) {
            String var8;
            final VFileDialog var14;
            if (var5.equals("fd" + this.getLocalString(12567))) {
                if (2 == var7) {
                    this.virtdevsObj.do_floppy(var6);
                    this.lockFdMenu(true, var6);
                } else if (1 == var7) {
                    this.dispFrame.setVisible(false);
                    var14 = new VFileDialog(this.getLocalString(8261), "*.img");
                    var8 = var14.getString();
                    this.dispFrame.setVisible(true);
                    if (null != var8) {
                        if (null != this.virtdevsObj.fdThread) {
                            this.virtdevsObj.change_disk(this.virtdevsObj.fdConnection, var8);
                        }

                        System.out.println("Image file: " + var8);
                        var3 = this.virtdevsObj.do_floppy(var8);
                        this.lockFdMenu(!var3, var6);
                    } else {
                        this.lockFdMenu(true, var6);
                    }
                }

            } else if (var5.equals("cd" + this.getLocalString(12567))) {
                if (2 == var7) {
                    this.virtdevsObj.do_cdrom(var6);
                    this.lockCdMenu(true, var6);
                } else if (1 == var7) {
                    this.dispFrame.setVisible(false);
                    var14 = new VFileDialog(this.getLocalString(8261), "*.iso");
                    var8 = var14.getString();
                    this.dispFrame.setVisible(true);
                    if (null != var8) {
                        if (null != this.virtdevsObj.cdThread) {
                            this.virtdevsObj.change_disk(this.virtdevsObj.cdConnection, var8);
                        }

                        System.out.println("Image file: " + var8);
                        var3 = this.virtdevsObj.do_cdrom(var8);
                        this.lockCdMenu(!var3, var6);
                    } else {
                        this.lockCdMenu(true, var6);
                    }
                }

            } else if (var5.startsWith("cd")) {
                var3 = this.virtdevsObj.do_cdrom(var6);
                if (var3) {
                    this.lockCdMenu(1 != var7, var6);
                }

            } else if (var5.startsWith("fd")) {
                var3 = this.virtdevsObj.do_floppy(var6);
                if (var3) {
                    this.lockFdMenu(1 != var7, var6);
                }

            } else if ("FLOPPY".equals(var5) || "CDROM".equals(var5)) {
                String var10;
                boolean var11 = false;
                String var9;
                if (2 == var7) {
                    var9 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + var5 + "\", \"command\":\"EJECT\", \"session_key\":\"" + this.getParameter("RCINFO1") + "\"}";
                    this.jsonObj.postJSONRequest("vm_status", var9);
                    this.remconsObj.session.set_status(3, "Unmounted URL");
                } else if (1 == var7) {
                    remcons.setDialogIsOpen(true);
                    final URLDialog var12 = new URLDialog(this.remconsObj);
                    var8 = var12.getUserInput();
                    if (0 == var8.compareTo("userhitcancel") || 0 == var8.compareTo("userhitclose")) {
                        var8 = null;
                    }

                    if (null != var8) {
                        var8 = var8.replaceAll("[\u0000-\u001f]", "");
                        System.out.println("url:  " + var8);
                    }

                    remcons.setDialogIsOpen(false);
                    if (null != var8) {
                        var9 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + var5 + "\", \"command\":\"INSERT\", \"url\":\"" + var8 + "\", \"session_key\":\"" + this.getParameter("RCINFO1") + "\"}";
                        var10 = this.jsonObj.postJSONRequest("vm_status", var9);
                        if ("Success".equals(var10)) {
                            var9 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + var5 + "\", \"boot_option\":\"CONNECT\", \"command\":\"SET\", \"url\":\"" + var8 + "\", \"session_key\":\"" + this.getParameter("RCINFO1") + "\"}";
                            var10 = this.jsonObj.postJSONRequest("vm_status", var9);
                        }

                        if ("SCSI_ERR_NO_LICENSE".equals(var10)) {
                            final String var13 = "<html>" + this.getLocalString(8213) + " " + this.getLocalString(8214) + " " + this.getLocalString(8237) + "<br><br>" + this.getLocalString(8238) + "</html>";
                            new VErrorDialog(this.dispFrame, this.getLocalString(8236), var13, true);
                        } else if (!"Success".equals(var10)) {
                            new VErrorDialog(this.dispFrame, this.getLocalString(8212), this.getLocalString(8292), true);
                        } else {
                            var11 = true;
                            this.remconsObj.session.set_status(3, this.getLocalString(12581));
                        }
                    }
                }

                if ("FLOPPY".equals(var5)) {
                    this.lockFdMenu(!var11, var6);
                } else {
                    this.lockCdMenu(!var11, var6);
                }

            }
        } else {
            System.out.println("Unhandled item event");
        }
    }

    public void kbdLayoutMenuHandler(final int var1) {
        for (int var2 = 0; var2 < this.REMCONS_MAX_KBD_LAYOUT; ++var2) {
            if (var2 != var1) {
                this.localKbdLayout[var2].setSelected(false);
            }
        }

        this.remconsObj.setLocalKbdLayout(var1);
    }

    private void ApplyRcInfoParameters(String var1) {
        this.enc_key = this.rc_port = this.vm_key = this.vm_port = null;
        Arrays.fill(this.enc_key_val, (byte) 0);
        var1 = var1.trim();
        var1 = var1.substring(1, var1.length() - 1);
        final String[] var2 = var1.split(",");

        for (final String s : var2) {
            final String[] var4 = s.split(":");
            if (2 != var4.length) {
                System.out.println("Error in ApplyRcInfoParameters");
                return;
            }

            String var5 = var4[0].trim();
            var5 = var5.substring(1, var5.length() - 1);
            String var6 = var4[1].trim();
            if ((int) '"' == (int) var6.charAt(0)) {
                var6 = var6.substring(1, var6.length() - 1);
            }

            if (0 == var5.compareToIgnoreCase("enc_key")) {
                this.enc_key = var6;

                for (int var7 = 0; var7 < this.enc_key_val.length; ++var7) {
                    final String var8 = this.enc_key.substring(var7 << 1, (var7 << 1) + 2);

                    try {
                        this.enc_key_val[var7] = (byte) Integer.parseInt(var8, 16);
                    } catch (final NumberFormatException var10) {
                        System.out.println("Failed to Parse enc_key");
                    }
                }
            } else if (0 == var5.compareToIgnoreCase("rc_port")) {
                System.out.println("rc_port:" + var6);
                this.rc_port = var6;
            } else if (0 == var5.compareToIgnoreCase("vm_key")) {
                this.vm_key = var6;
            } else if (0 == var5.compareToIgnoreCase("vm_port")) {
                System.out.println("vm_port:" + var6);
                this.vm_port = var6;
            } else if ("optional_features".equalsIgnoreCase(var5)) {
                System.out.println("optional_features:" + var6);
                this.optional_features = var6;
            } else if (0 == var5.compareToIgnoreCase("server_name")) {
                System.out.println("server_name:" + var6);
                this.server_name = var6;
            } else if (0 == var5.compareToIgnoreCase("ilo_fqdn")) {
                System.out.println("ilo_fqdn:" + var6);
                this.ilo_fqdn = var6;
            } else if (0 == var5.compareToIgnoreCase("blade")) {
                this.blade = Integer.parseInt(var6);
                System.out.println("blade:" + this.blade);
            } else if (1 == this.blade && 0 == var5.compareToIgnoreCase("enclosure")) {
                if (!"null".equals(var6)) {
                    this.in_enclosure = true;
                    System.out.println("enclosure:" + var6);
                    this.enclosure = var6;
                }
            } else if (1 == this.blade && 0 == var5.compareToIgnoreCase("bay")) {
                this.bay = Integer.parseInt(var6);
                System.out.println("bay:" + this.bay);
            }
        }

    }

    public void moveUItoInit(final boolean var1) {
        System.out.println("Disable Menus\n");
        this.psMenu.setEnabled(var1);
        this.vdMenu.setEnabled(var1);
        this.kbMenu.setEnabled(var1);
    }

    public String rebrandToken(final String var1) {
        if (!this.moniker_cached) {
            final String var4 = this.jsonObj.getJSONRequest("login_session");
            if (null == var4) {
                return var1;
            }

            this.moniker = jsonparser.getJSONObject(var4, "moniker");
            if (null == this.moniker) {
                return var1;
            }

            this.moniker_cached = true;
        }

        final String var3 = jsonparser.getJSONString(this.moniker, var1);
        return var3.isEmpty() ? var1 : var3;
    }

    class WindowCloser extends WindowAdapter {

        public void windowClosing(final WindowEvent var1) {
            App.this.exit = true;
            App.this.requestClose(null);
        }
    }
}
