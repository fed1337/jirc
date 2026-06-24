package com.hp.ilo2.remcons;


import com.hp.ilo2.virtdevs.VErrorDialog;
import com.hp.ilo2.virtdevs.virtdevs;
import org.jirc.App;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.Locale;
import java.util.Properties;

public class remcons extends JPanel implements TimerListener, Runnable {
    public static final int RETRY_CONNECTION_MAX = 3;
    static final int ImageDone = 39;
    private static final int SESSION_TIMEOUT_DEFAULT = 900;
    private static final int KEEP_ALIVE_INTERVAL = 30;
    private static final int INFINITE_TIMEOUT = 2147483640;
    private static final int REMCONS_MAX_FN_KEYS = 12;
    private static final int LICENSE_RC = 1;
    private static final char[] base64 = {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '>', '\u0000', '\u0000', '\u0000', '?', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'};
    public static final Properties prop = new Properties();
    private static boolean dialogIsOpen = false;

    static {
        try {
            remcons.prop.load(new FileInputStream(System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + ".java" + FileSystems.getDefault().getSeparator() + "hp.properties"));
        } catch (final Exception var1) {
            System.out.println("Exception: " + var1);
        }

    }

    private static final int keyTimerTick = 20;
    private static final boolean translate = false;
    private static final int ts_param = 0;
    private final LocaleTranslator lt = new LocaleTranslator();
    private volatile boolean running = false;
    public cim session = null;
    private KeyboardHook kHook = null;
    public boolean kbHookInstalled = false;
    private boolean kbHookAvailable = false;
    private int keyData = 0;
    private int prevKeyData = 0;
    public boolean kbHookDataRcvd = false;
    public JPanel pwrStatusPanel = null;
    private int timeout_countdown = 0;
    private final int[] rndm_nums = new int[12];
    private boolean session_encryption_enabled = false;
    private byte[] session_encrypt_key = new byte[16];
    private byte[] session_decrypt_key = new byte[16];
    private int session_key_index = 0;
    private int initialized = 0;
    public boolean retry_connection_flag = false;
    public int retry_connection_count = 0;
    public boolean licensed = false;
    public boolean halfHeightCapable = false;
    public App ParentApp;
    private Thread locale_setter = null;
    boolean fdConnState = false;
    boolean cdConnState = false;
    private boolean fdCachedConnState = false;
    private boolean cdCachedConnState = false;
    private int session_timeout = 900;
    private Image pwrEncImgLock = null;
    private Image pwrEncImgUnlock = null;
    private Image pwrEncImg = null;
    private JPanel pwrEncCanvas = null;
    private Image vmActImgOn = null;
    private Image vmActImgOff = null;
    private Image vmActImg = null;
    private JPanel vmActCanvas = null;
    private Image pwrHealthImgGreen = null;
    private Image pwrHealthImgYellow = null;
    private Image pwrHealthImgRed = null;
    private Image pwrHealthImgOff = null;
    private Image pwrHealthImg = null;
    private JPanel pwrHealthCanvas = null;
    private Image pwrPowerImgOn = null;
    private Image pwrPowerImgOff = null;
    private Image pwrPowerImg = null;
    private JPanel pwrPowerCanvas = null;
    private JLabel pwrEncLabel = null;
    private String login = null;
    private Timer timer = null;
    private Timer keyBoardTimer = null;
    private int port_num = 23;
    private boolean debug_msg = false;
    private String session_ip = null;
    private int mouse_mode = 0;
    private static final String rcErrMessage = null;
    private final JFrame parent_frame = null;
    private int terminalServicesPort = 3389;
    private int localKbdLayoutId = 0;

    public remcons(final App var1) {
        super();
        this.ParentApp = var1;
    }

    public String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("remcons:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    private Image getImg(final String var1) {
        final ClassLoader var2 = this.getClass().getClassLoader();
        return App.getImage(var2.getResource("org/remcons/images/" + var1));
    }

    private void waitImage(final Image var1, final ImageObserver var2) {
        final long var4 = System.currentTimeMillis();

        int var3;
        do {
            var3 = this.checkImage(var1, var2);
            if (0 != (var3 & 192)) {
                break;
            }

            Thread.yield();
        } while (2000L >= System.currentTimeMillis() - var4 && 39 != (var3 & 39));

    }

    public void init() {
        final Image[] img = new Image[22];
        img[0] = this.getImg("blank_cd.png");
        img[1] = this.getImg("blue.png");
        img[2] = this.getImg("CD_Drive.png");
        img[3] = this.getImg("FloppyDisk.png");
        img[4] = this.getImg("Folder.png");
        img[5] = this.getImg("green.png");
        img[6] = this.getImg("hold.png");
        img[7] = null;
        img[8] = null;
        img[9] = null;
        img[10] = null;
        img[11] = this.getImg("irc.png");
        img[12] = this.getImg("Keyboard.png");
        img[13] = this.getImg("off.png");
        img[14] = this.getImg("press.png");
        img[15] = this.getImg("ProtectFormHS.png");
        img[16] = this.getImg("pwr.png");
        img[17] = this.getImg("pwr_off.png");
        img[18] = this.getImg("red.png");
        img[19] = this.getImg("UnProtectFormHS.png");
        img[20] = this.getImg("Warning.png");
        img[21] = this.getImg("yellow.png");
        this.running = true;
        this.locale_setter = new Thread(this);
        this.locale_setter.start();
        this.init_params();
        final boolean var3;
        final String var4 = System.getProperty("os.name").toLowerCase();
        final String var5 = System.getProperty("java.vm.name");
        String var6 = "unknown";
        if (var4.startsWith("windows") || var4.startsWith("linux")) {
            if (var4.startsWith("windows")) {
                if (var5.contains("64")) {
                    System.out.println("kbhookdll Detected win 64bit jvm");
                    var6 = "HpqKbHook-x86-win64";
                } else {
                    System.out.println("kbhookdll Detected win 32bit jvm");
                    var6 = "HpqKbHook-x86-win32";
                }
            } else if (var4.startsWith("linux")) {
                if (var5.contains("64")) {
                    System.out.println("kbhookdll Detected 64bit linux jvm");
                    var6 = "HpqKbHook-x86-linux-64";
                } else {
                    System.out.println("kbhookdll Detected 32bit linux jvm");
                    var6 = "HpqKbHook-x86-linux-32";
                }
            }

            var3 = this.ExtractKeyboardDll(var6);
            if (var3) {
                this.kHook = new KeyboardHook();
                this.kbHookAvailable = true;
                this.kHook.clearKeymap();
            } else {
                System.out.println("ExtractKeyboardDll() returns false");
            }
        }

        this.session = new cim(this);
        final cmd telnetObj = new cmd();
        if (this.session_encryption_enabled) {
            this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
            this.session.setup_decryption(this.session_decrypt_key);
        }

        this.session.set_mouse_protocol(this.mouse_mode);

        for (int var1 = 0; 12 > var1; ++var1) {
            this.rndm_nums[var1] = (int) (Math.random() * 4.0) * 85;
        }

        this.session.set_sig_colors(this.rndm_nums);
        if (this.debug_msg) {
            this.session.enable_debug();
        } else {
            this.session.disable_debug();
        }

        this.pwrStatusPanel = new JPanel(new BorderLayout());
        final JPanel ledStatusPanel = new JPanel(new BorderLayout());
        this.pwrHealthImgGreen = img[5];
        this.prepareImage(this.pwrHealthImgGreen, ledStatusPanel);
        this.pwrHealthImgYellow = img[21];
        this.prepareImage(this.pwrHealthImgYellow, ledStatusPanel);
        this.pwrHealthImgRed = img[18];
        this.prepareImage(this.pwrHealthImgRed, ledStatusPanel);
        this.pwrHealthImgOff = img[13];
        this.prepareImage(this.pwrHealthImgOff, ledStatusPanel);
        this.pwrEncImgLock = img[15];
        this.prepareImage(this.pwrEncImgLock, ledStatusPanel);
        this.pwrEncImgUnlock = img[19];
        this.prepareImage(this.pwrEncImgUnlock, ledStatusPanel);
        this.pwrEncImg = this.pwrEncImgUnlock;
        this.pwrHealthImg = this.pwrHealthImgOff;
        this.vmActImgOn = img[1];
        this.prepareImage(this.vmActImgOn, ledStatusPanel);
        this.vmActImgOff = img[13];
        this.prepareImage(this.vmActImgOff, ledStatusPanel);
        this.pwrPowerImgOn = img[16];
        this.prepareImage(this.pwrPowerImgOn, ledStatusPanel);
        this.pwrPowerImgOff = img[17];
        this.prepareImage(this.pwrPowerImgOff, ledStatusPanel);
        this.vmActImg = this.vmActImgOff;
        this.pwrPowerImg = this.pwrPowerImgOff;
        this.pwrStatusPanel.add(this.pwrEncCanvas = new JPanel() {
            public void paintComponent(final Graphics var1) {
                super.paintComponent(var1);
                if (null != remcons.this.pwrEncImg) {
                    remcons.this.waitImage(remcons.this.pwrEncImg, this);
                    var1.drawImage(remcons.this.pwrEncImg, 1, 4, null);
                } else {
                    System.out.println("pwrEncCanvas Image not found");
                }

            }
        }, "West");
        remcons.setToolTipRecursively(this.pwrEncCanvas, this.getLocalString(16387));
        this.pwrEncCanvas.setPreferredSize(new Dimension(20, 20));
        this.pwrEncCanvas.setVisible(true);
        this.pwrStatusPanel.add(this.pwrEncLabel = new JLabel());
        this.pwrEncLabel.setText("         ");
        ledStatusPanel.add(this.pwrHealthCanvas = new JPanel() {
            public void paintComponent(final Graphics var1) {
                super.paintComponent(var1);
                if (null != remcons.this.pwrHealthImg) {
                    remcons.this.waitImage(remcons.this.pwrHealthImg, this);
                    var1.drawImage(remcons.this.pwrHealthImg, 1, 4, null);
                } else {
                    System.out.println("pwrHealthCanvas Image not found");
                }

            }
        }, "West");
        remcons.setToolTipRecursively(this.pwrHealthCanvas, this.getLocalString(16386));
        this.pwrHealthCanvas.setPreferredSize(new Dimension(18, 25));
        this.pwrHealthCanvas.setVisible(true);
        ledStatusPanel.add(this.vmActCanvas = new JPanel() {
            public void paintComponent(final Graphics var1) {
                super.paintComponent(var1);
                if (null != remcons.this.vmActImg) {
                    remcons.this.waitImage(remcons.this.vmActImg, this);
                    var1.drawImage(remcons.this.vmActImg, 1, 4, null);
                } else {
                    System.out.println("vmActCanvas Image not found");
                }

            }
        }, "Center");
        remcons.setToolTipRecursively(this.vmActCanvas, this.getLocalString(16388));
        this.vmActCanvas.setPreferredSize(new Dimension(18, 25));
        this.vmActCanvas.setVisible(true);
        ledStatusPanel.add(this.pwrPowerCanvas = new JPanel() {
            public void paintComponent(final Graphics var1) {
                super.paintComponent(var1);
                if (null != remcons.this.pwrPowerImg) {
                    remcons.this.waitImage(remcons.this.pwrPowerImg, this);
                    var1.drawImage(remcons.this.pwrPowerImg, 1, 4, null);
                } else {
                    System.out.println("pwrPowerCanvas Image not found");
                }

            }
        }, "East");
        remcons.setToolTipRecursively(this.pwrPowerCanvas, this.getLocalString(16385));
        this.pwrPowerCanvas.setPreferredSize(new Dimension(18, 25));
        this.pwrPowerCanvas.setVisible(true);
        this.pwrStatusPanel.add(ledStatusPanel, "East");
        this.session.enable_keyboard();
        if (this.kbHookAvailable) {
            this.keyBoardTimer = new Timer(remcons.keyTimerTick, false, this.session);
            this.keyBoardTimer.setListener(new keyBoardTimerListener(), null);
            this.keyBoardTimer.start();
            System.out.println("Keyboard Hook available and timer started...");
        }

        this.initialized = 1;
    }

    public void start() {
        this.timeout_countdown = this.session_timeout;
        this.start_session();
        if (2147483640 == this.session_timeout) {
            System.out.println("Remote Console inactivity timeout = infinite.");
        } else {
            System.out.println("Remote Console inactivity timeout = " + this.session_timeout / 60 + " minutes.");
        }

    }

    private boolean ExtractKeyboardDll(final String var1) {
        String var3 = System.getProperty("java.io.tmpdir");
        final String var4 = System.getProperty("os.name").toLowerCase();
        final String var5 = FileSystems.getDefault().getSeparator();
        final String var6;
        final String var7;
        boolean var8;
        final String var9 = "org/remcons/";
        if (!var4.startsWith("windows") && !var4.startsWith("linux")) {
            System.out.println("Cannot load keyboardHook DLL. Non Windows-Linux client system.");
            var8 = false;
        } else {
            if (null == var3) {
                var3 = var4.startsWith("windows") ? "C:\\TEMP" : "/tmp";
            }

            final File var10 = new File(var3);
            if (!var10.exists()) {
                var10.mkdir();
            }

            if (!var3.endsWith(var5)) {
                var3 = var3 + var5;
            }

            var3 = var3 + "HpqKbHook-" +
                    Integer.toHexString(virtdevs.UID) + ".dll";
            System.out.println("checking for kbddll" + var3);
            final File var11 = new File(var3);
            if (var11.exists()) {
                System.out.println(var1 + " already present ..");
                return true;
            }

            System.out.println("Extracting " + var1 + "...");
            final ClassLoader var12 = this.getClass().getClassLoader();
            final byte[] var14 = new byte[4096];
            var6 = var3;
            var7 = var9 + var1;

            try {
                final InputStream var15 = var12.getResourceAsStream(var7);
                if (null == var15) {
                    System.out.println("Keyboard hook library " + var7
                            + " not bundled; continuing without low-level keyboard hook.");
                    return false;
                }

                final FileOutputStream var16 = new FileOutputStream(var6);

                int var13;
                while (-1 != (var13 = var15.read(var14, 0, 4096))) {
                    var16.write(var14, 0, var13);
                }

                System.out.println("Writing dll to " + var6 + "complete");
                var15.close();
                var16.close();
                var8 = true;
            } catch (final IOException var17) {
                System.out.println("dllExtract: " + var17);
                var8 = false;
            }
        }

        return var8;
    }

    public void stop() {
        // Cooperative shutdown: clear the flag and interrupt to break the sleeps
        // in run(), then wait briefly for the loop to exit.
        this.running = false;
        final Thread var1 = this.locale_setter;
        this.locale_setter = null;
        if (null != var1 && var1 != Thread.currentThread()) {
            var1.interrupt();
            try {
                var1.join(2000L);
            } catch (final InterruptedException var2) {
                Thread.currentThread().interrupt();
            }
        }

        this.stop_session();
        System.out.println("Console stopped...");
    }

    public void destroy() {
        System.out.println("Hiding console panel.");
        if (this.isVisible()) {
            this.setVisible(false);
        }

    }

    public void timeout(final Object var1) {
        if (this.session.UI_dirty) {
            this.session.UI_dirty = false;
            this.timeout_countdown = this.session_timeout;
        } else {
            this.timeout_countdown -= 30;
            if (0 >= this.timeout_countdown) {
                this.stop_session();
            }
        }

    }

    private void start_session() {
        this.session.connect(this.session_ip, this.login, this.port_num, remcons.ts_param, this.terminalServicesPort, this);
        this.timer = new Timer(30000, false, this.session);
        this.timer.setListener(this, null);
        this.timer.start();
    }

    private void stop_session() {
        if (null != this.timer) {
            this.timer.stop();
            this.timer = null;
        }

        this.session.disconnect();
    }

    public void setPwrStatusEnc(final int var1) {
        if (0 == var1) {
            this.pwrEncImg = this.pwrEncImgUnlock;
        } else {
            this.pwrEncImg = this.pwrEncImgLock;
        }

        this.pwrEncCanvas.invalidate();
        this.pwrEncCanvas.repaint();
    }

    public void setPwrStatusEncLabel(final String var1) {
        this.pwrEncLabel.setText(var1 + "       ");
    }

    public void setPwrStatusHealth(final int var1) {
        switch (var1) {
            case 0:
                this.pwrHealthImg = this.pwrHealthImgGreen;
                break;
            case 1:
                this.pwrHealthImg = this.pwrHealthImgYellow;
                break;
            case 2:
                this.pwrHealthImg = this.pwrHealthImgRed;
                break;
            default:
                this.pwrHealthImg = this.pwrHealthImgOff;
        }

        this.pwrHealthCanvas.invalidate();
        this.pwrHealthCanvas.repaint();
    }

    public void setPwrStatusPower(final int var1) {
        if (0 == var1 && this.pwrPowerImgOff != this.pwrPowerImg) {
            this.pwrPowerImg = this.pwrPowerImgOff;
            this.ParentApp.updatePsMenu(0);
            this.pwrPowerCanvas.invalidate();
            this.pwrPowerCanvas.repaint();
            System.out.println("Moving Power to Off state");
        } else if (0 != var1 && this.pwrPowerImgOn != this.pwrPowerImg) {
            this.pwrPowerImg = this.pwrPowerImgOn;
            this.ParentApp.updatePsMenu(var1);
            this.pwrPowerCanvas.invalidate();
            this.pwrPowerCanvas.repaint();
            System.out.println("Moving Power to ON state");
        }

        // Secondary readiness marker for the startup loader (idempotent).
        this.ParentApp.signalReady();
    }

    public void setvmAct(final int var1) {
        if (this.vmActImg != this.vmActImgOn && 0 != var1) {
            if (this.vmActImg == this.vmActImgOff) {
                this.vmActImg = this.vmActImgOn;
                this.vmActCanvas.invalidate();
                this.vmActCanvas.repaint();
            }
        } else {
            this.vmActImg = this.vmActImgOff;
            this.vmActCanvas.invalidate();
            this.vmActCanvas.repaint();
        }

    }

    public int seize_dialog(final String var1, final String var2, final int var3) {
        System.out.println("seize dialog invoked" + var3);
        final VSeizeWaitDialog var4 = new VSeizeWaitDialog(this, var1, var2, var3);
        return (int) var4.getUserInput();
    }

    public void seize_confirmed() {
        this.ParentApp.moveUItoInit(false);
        this.session.seize();
        this.ParentApp.requestClose("The remote console session was taken over by another user.");
    }

    public void shared(final String var1, final String var2) {
        System.out.println("shared notification invoked");
        new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(8230), this.getLocalString(8231) + " " + var2 + "@" + var1 + this.getLocalString(8232), false);
    }

    public void unAuthorized(final String var1, final boolean var2) {
        new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(8230), this.getLocalString(8233) + var1 + this.getLocalString(8234), false);
        if (var2) {
            this.session.unAuthAccess();
        }

    }

    public void firmwareUpgrade() {
        System.out.println("Firmware upgrade notification invoked");
        this.session.fwUpgrade();
        this.ParentApp.requestClose(this.getLocalString(8235));
    }

    public void ack(final byte var1, final byte var2, final byte var3, final byte var4) {
        if (0 == (int) var1) {
            if (1 == (int) var2) {
                if (1 == (int) var4 && !this.ParentApp.fdSelected) {
                    this.ParentApp.fdSelected = true;
                    this.ParentApp.lockFdMenu(false, this.getLocalString(4131) + this.getLocalString(4106));
                } else if (0 == (int) var4 && this.ParentApp.fdSelected) {
                    this.ParentApp.fdSelected = false;
                    this.ParentApp.lockFdMenu(true, "");
                }
            } else if (2 == (int) var2) {
                if (1 == (int) var4 && !this.ParentApp.cdSelected) {
                    this.ParentApp.cdSelected = true;
                    this.ParentApp.lockCdMenu(false, this.getLocalString(4131) + this.getLocalString(4107));
                } else if (0 == (int) var4 && this.ParentApp.cdSelected) {
                    this.ParentApp.cdSelected = false;
                    this.ParentApp.lockCdMenu(true, "");
                }
            }
        }

    }

    private void init_params() {
        this.login = null;
        this.port_num = 23;
        this.mouse_mode = 0;
        this.session_timeout = 900;
        this.session_encryption_enabled = true;
        this.session_key_index = 0;
        final boolean launchTerminalServices = false;
        this.terminalServicesPort = 0;
        this.debug_msg = true;
        this.session_ip = this.ParentApp.getCodeBase().getHost();
        final int num_cursors = 0;
        if (this.session_encryption_enabled) {
            if (null != this.ParentApp.enc_key) {
                System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_decrypt_key, 0, this.session_decrypt_key.length);
                System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_encrypt_key, 0, this.session_encrypt_key.length);
            }
        } else {
            this.session_decrypt_key = null;
            this.session_encrypt_key = null;
        }

    }

    private static String parse_login(final String var1) {
        if (var1.startsWith("Compaq-RIB-Login=")) {
            String var2 = "\u001b[!";

            try {
                var2 = var2 + var1.substring(17, 73);
                var2 = var2 + '\r';
                var2 = var2 + var1.substring(74, 106);
                var2 = var2 + '\r';
                return var2;
            } catch (final StringIndexOutOfBoundsException var4) {
                return null;
            }
        } else {
            return remcons.base64_decode(var1);
        }
    }

    private static String base64_decode(final String var1) {
        int var9 = 0;
        int var10 = 0;

        String var11;
        for (var11 = ""; var9 + 3 < var1.length() && 0 == var10; var9 += 4) {
            final char var2 = remcons.base64[(int) var1.charAt(var9) & 127];
            final char var3 = remcons.base64[(int) var1.charAt(var9 + 1) & 127];
            final char var4 = remcons.base64[(int) var1.charAt(var9 + 2) & 127];
            final char var5 = remcons.base64[(int) var1.charAt(var9 + 3) & 127];
            char var6 = (char) (((int) var2 << 2) + ((int) var3 >> 4));
            char var7 = (char) (((int) var3 << 4) + ((int) var4 >> 2));
            char var8 = (char) (((int) var4 << 6) + (int) var5);
            var6 = (char) ((int) var6 & 255);
            var7 = (char) ((int) var7 & 255);
            var8 = (char) ((int) var8 & 255);
            if ((int) ':' == (int) var6) {
                var6 = '\r';
            }

            if ((int) ':' == (int) var7) {
                var7 = '\r';
            }

            if ((int) ':' == (int) var8) {
                var8 = '\r';
            }

            var11 = var11 + var6;
            if ((int) '=' == (int) var1.charAt(var9 + 2)) {
                ++var10;
            } else {
                var11 = var11 + var7;
            }

            if ((int) '=' == (int) var1.charAt(var9 + 3)) {
                ++var10;
            } else {
                var11 = var11 + var8;
            }
        }

        if (!var11.isEmpty()) {
            var11 = var11 + '\r';
        }

        return var11;
    }

    public void paint(final Graphics var1) {
    }

    public int getTimeoutValue() {
        return this.timeout_countdown;
    }

    public void run() {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows") && !this.lt.windows) {
            Locale.setDefault(Locale.US);
        }

        while (this.running) {
            while (this.running) {
                while (this.running && (!this.retry_connection_flag || 3 < this.retry_connection_count)) {
                    if (this.retry_connection_flag) {
                        System.out.println("Retry connection  - video maximum attempts exhausted");
                        this.stop_session();
                        this.retry_connection_flag = false;
                    } else {
                        try {
                            remcons.sleepAtLeast(2500L);
                        } catch (final InterruptedException var2) {
                            System.out.println("Thread interrupted..");
                        }
                    }
                }

                if (!this.running) {
                    break;
                }

                System.out.println("Retrying connection" + this.retry_connection_count);
                this.retry_connection_flag = false;
                ++this.retry_connection_count;
                if (!this.fdCachedConnState) {
                    this.fdCachedConnState = this.fdConnState;
                }

                if (!this.cdCachedConnState) {
                    this.cdCachedConnState = this.cdConnState;
                }

                System.out.println("fd conn:" + this.fdConnState + " cd conn:" + this.cdConnState);
                System.out.println("fdcache:" + this.fdCachedConnState + " cdcache:" + this.cdCachedConnState);
                this.stop_session();

                try {
                    remcons.sleepAtLeast(5000L);
                } catch (final InterruptedException var4) {
                    System.out.println("Thread interrupted..");
                }

                if (this.session_encryption_enabled && null != this.ParentApp.enc_key) {
                    System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_decrypt_key, 0, this.session_decrypt_key.length);
                    System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_encrypt_key, 0, this.session_encrypt_key.length);
                }

                this.session.setup_decryption(this.session_decrypt_key);
                this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
                this.start_session();

                try {
                    remcons.sleepAtLeast(2500L);
                } catch (final InterruptedException var3) {
                    System.out.println("Thread interrupted..");
                }

                if (null != this.session.receiver && !this.retry_connection_flag) {
                    this.retry_connection_count = 0;
                } else {
                    this.retry_connection_flag = true;
                }
            }
        }
    }

    public static void sleepAtLeast(final long var1) throws InterruptedException {
        final long var3 = System.currentTimeMillis();

        long var7;
        for (long var5 = var1; 0L < var5; var5 = var1 - (var7 - var3)) {
            Thread.sleep(var5);
            var7 = System.currentTimeMillis();
        }

    }

    public static void setDialogIsOpen(final boolean var1) {
        remcons.dialogIsOpen = var1;
    }

    public void SetLicensed(final int var1) {
        this.licensed = 0 != (var1 & 1);

        System.out.println("SetLicensed: " + this.licensed);
    }

    void SetFlags(final int var1) {
        if (0 == (var1 & 8)) {
            this.halfHeightCapable = false;
            System.out.println("halfHeightCapable false");
        } else {
            this.halfHeightCapable = true;
            System.out.println("halfHeightCapable true");
        }

    }

    public void UnlicensedShutdown() {
        final String var1 = "<html>" + this.getLocalString(8213) + " " + this.getLocalString(8215) + " " + this.getLocalString(8237) + "<br><br>" + this.getLocalString(8238) + "</html>";
        System.out.println("Unlicensed notification invoked");
        this.ParentApp.requestClose(var1);
    }

    public void resetShutdown() {
        this.ParentApp.requestClose(this.getLocalString(8289));
    }

    public int getInitialized() {
        return this.initialized;
    }

    private static void get_terminal_svcs_label(final int var1) {
        final String var2;
        if (0 == var1) {
            var2 = "mstsc";
        } else if (1 == var1) {
            var2 = "vnc";
        } else {
            var2 = "type" + var1;
        }

        final String term_svcs_label = remcons.prop.getProperty(var2 + ".label", "Terminal Svcs");
    }

    public void remconsInstallKeyboardHook() {
        final String var1 = System.getProperty("os.name").toLowerCase();
        if (null == this.kHook) {
            System.out.println("remconsInstallKeyboardHook:KB Hook dll not loaded");
        } else if (!this.kbHookInstalled && this.kbHookAvailable && !remcons.dialogIsOpen) {
            try {
                this.kHook.clearKeymap();
                final int var3 = this.kHook.InstallKeyboardHook();
                if (!var1.startsWith("windows") && -1412584499 == var3) {
                    this.kbHookInstalled = false;
                    this.keyBoardTimer.stop();
                    System.out.println("remconsInstallKeyboardHook: KB Hook install failed");
                } else {
                    this.kHook.setKeyboardLayoutId(var3);
                    this.kbHookInstalled = true;
                    this.prevKeyData = this.keyData = 0;
                    if (!var1.startsWith("windows")) {
                        this.keyBoardTimer.start();
                        this.kHook.setLocalKbdLayout(this.localKbdLayoutId);
                    }
                }
            } catch (final Throwable var4) {
                this.kbHookInstalled = false;
                this.kbHookAvailable = false;
                this.kHook = null;
                if (null != this.keyBoardTimer) {
                    this.keyBoardTimer.stop();
                }

                System.out.println("remconsInstallKeyboardHook: native keyboard hook unavailable (" + var4 + "); continuing without low-level keyboard hook.");
            }
        }

    }

    public void remconsUnInstallKeyboardHook() {
        if (null != this.kHook && this.kbHookInstalled && this.kbHookAvailable) {
            final int var2 = this.kHook.UnInstallKeyboardHook();
            if (0 == var2) {
                this.kbHookInstalled = false;
                this.prevKeyData = this.keyData = 0;
                this.kHook.clearKeymap();
            } else {
                System.out.println("remconsUnInstallKeyboardHook: uninstall failed:" + var2);
            }
        }

    }

    public void setLocalKbdLayout(final int var1) {
        if (null != this.kHook && this.kbHookInstalled) {
            System.out.println("setKbdLayoutHandler: set Layout - " + var1);
            this.kHook.setLocalKbdLayout(var1);
        } else {
            System.out.println("setKbdLayoutHandler: kHook not available. dbg caching..");
            this.localKbdLayoutId = var1;
        }

    }

    private static void setToolTipRecursively(final JComponent var1, final String var2) {
        var1.setToolTipText(var2);
    }

    public void viewHotKeys() {
        new hotKeysDialog(this);
    }

    public void viewAboutJirc() {
        new aboutJircDialog(this);
    }

    class keyBoardTimerListener implements TimerListener {

        public synchronized void timeout(final Object var1) {
            boolean var6 = false;
            byte[] var7;
            boolean var8;
            int var9 = 995;
            if (null != remcons.this.kHook && remcons.this.kbHookInstalled) {
                do {
                    remcons.this.prevKeyData = remcons.this.keyData;
                    remcons.this.keyData = remcons.this.kHook.GetKeyData();
                    if (remcons.this.keyData != remcons.this.prevKeyData && 0 != remcons.this.keyData) {
                        final int var3 = (remcons.this.keyData & 16711680) >> 16;
                        final int var5 = (remcons.this.keyData & (int) '\uff00') >> 8;
                        final int var4 = remcons.this.keyData & 255;
                        if (144 == (var3 & 144)) {
                            var8 = true;
                        } else if (128 == (var3 & 128)) {
                            var6 = false;
                            var8 = false;
                        } else {
                            var6 = true;
                            var8 = false;
                        }

                        var7 = remcons.this.kHook.HandleHookKey(var4, var5, var6, var8);
                        if (remcons.this.kHook.kcmdValid) {
                            if (!remcons.this.kbHookDataRcvd) {
                                remcons.this.kbHookDataRcvd = true;
                            }

                            remcons.this.session.transmitb(var7, var7.length);
                        }

                        var9 = 0;
                    }
                } while (1000 > var9++);
            }

        }
    }
}
