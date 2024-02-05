package org.remcons;


import org.jirc.intgapp;
import org.virtdevs.VErrorDialog;
import org.virtdevs.virtdevs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.*;
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
    private static final char[] base64 = new char[]{'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '>', '\u0000', '\u0000', '\u0000', '?', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'};
    public static Properties prop = new Properties();
    private static boolean dialogIsOpen = false;

    static {
        try {
            prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));
        } catch (Exception var1) {
            System.out.println("Exception: " + var1);
        }

    }

    private final int keyTimerTick = 20;
    private final boolean translate = false;
    private final int ts_param = 0;
    private final LocaleTranslator lt = new LocaleTranslator();
    public cim session;
    public cmd telnetObj;
    public KeyboardHook kHook = null;
    public boolean kbHookInstalled = false;
    public boolean kbHookAvailable = false;
    public int keyData = 0;
    public int prevKeyData = 0;
    public boolean kbHookDataRcvd = false;
    public JPanel pwrStatusPanel;
    public JPanel ledStatusPanel;
    public int timeout_countdown;
    public int[] rndm_nums = new int[12];
    public boolean session_encryption_enabled = false;
    public byte[] session_encrypt_key = new byte[16];
    public byte[] session_decrypt_key = new byte[16];
    public int session_key_index = 0;
    public int initialized = 0;
    public boolean retry_connection_flag = false;
    public int retry_connection_count = 0;
    public boolean licensed = false;
    public boolean halfHeightCapable = false;
    public intgapp ParentApp;
    Image[] img;
    Thread locale_setter;
    boolean fdConnState = false;
    boolean cdConnState = false;
    boolean fdCachedConnState = false;
    boolean cdCachedConnState = false;
    private int session_timeout = 900;
    private String term_svcs_label = "Terminal Svcs";
    private Image pwrEncImgLock;
    private Image pwrEncImgUnlock;
    private Image pwrEncImg;
    private JPanel pwrEncCanvas;
    private Image vmActImgOn;
    private Image vmActImgOff;
    private Image vmActImg;
    private JPanel vmActCanvas;
    private Image pwrHealthImgGreen;
    private Image pwrHealthImgYellow;
    private Image pwrHealthImgRed;
    private Image pwrHealthImgOff;
    private Image pwrHealthImg;
    private JPanel pwrHealthCanvas;
    private Image pwrPowerImgOn;
    private Image pwrPowerImgOff;
    private Image pwrPowerImg;
    private JPanel pwrPowerCanvas;
    private JLabel pwrEncLabel;
    private String login;
    private Timer timer;
    private Timer keyBoardTimer;
    private int port_num = 23;
    private boolean debug_msg = false;
    private String session_ip = null;
    private int num_cursors = 0;
    private int mouse_mode = 0;
    private String rcErrMessage;
    private JFrame parent_frame;
    private int terminalServicesPort = 3389;
    private boolean launchTerminalServices = false;
    private int localKbdLayoutId = 0;

    public remcons(intgapp var1) {
        this.ParentApp = var1;
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("remcons:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public Image getImg(String var1) {
        ClassLoader var2 = this.getClass().getClassLoader();
        return this.ParentApp.getImage(var2.getResource("com/hp/ilo2/remcons/images/" + var1));
    }

    void waitImage(Image var1, ImageObserver var2) {
        long var4 = System.currentTimeMillis();

        int var3;
        do {
            var3 = this.checkImage(var1, var2);
            if ((var3 & 192) != 0) {
                break;
            }

            Thread.yield();
        } while (System.currentTimeMillis() - var4 <= 2000L && (var3 & 39) != 39);

    }

    public void init() {
        this.img = new Image[22];
        this.img[0] = this.getImg("blank_cd.png");
        this.img[1] = this.getImg("blue.png");
        this.img[2] = this.getImg("CD_Drive.png");
        this.img[3] = this.getImg("FloppyDisk.png");
        this.img[4] = this.getImg("Folder.png");
        this.img[5] = this.getImg("green.png");
        this.img[6] = this.getImg("hold.png");
        this.img[7] = null;
        this.img[8] = null;
        this.img[9] = null;
        this.img[10] = null;
        this.img[11] = this.getImg("irc.png");
        this.img[12] = this.getImg("Keyboard.png");
        this.img[13] = this.getImg("off.png");
        this.img[14] = this.getImg("press.png");
        this.img[15] = this.getImg("ProtectFormHS.png");
        this.img[16] = this.getImg("pwr.png");
        this.img[17] = this.getImg("pwr_off.png");
        this.img[18] = this.getImg("red.png");
        this.img[19] = this.getImg("UnProtectFormHS.png");
        this.img[20] = this.getImg("Warning.png");
        this.img[21] = this.getImg("yellow.png");
        this.locale_setter = new Thread(this);
        this.locale_setter.start();
        this.init_params();
        boolean var3 = false;
        String var4 = System.getProperty("os.name").toLowerCase();
        String var5 = System.getProperty("java.vm.name");
        String var6 = "unknown";
        if (var4.startsWith("windows") || var4.startsWith("linux")) {
            if (var4.startsWith("windows")) {
                if (var5.indexOf("64") != -1) {
                    System.out.println("kbhookdll Detected win 64bit jvm");
                    var6 = "HpqKbHook-x86-win64";
                } else {
                    System.out.println("kbhookdll Detected win 32bit jvm");
                    var6 = "HpqKbHook-x86-win32";
                }
            } else if (var4.startsWith("linux")) {
                if (var5.indexOf("64") != -1) {
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
                if (this.kHook == null) {
                    System.out.println("remcons: kHook = null, Failed to initialize and load kHook");
                } else {
                    this.kbHookAvailable = true;
                    this.kHook.clearKeymap();
                }
            } else {
                System.out.println("ExtractKeyboardDll() returns false");
            }
        }

        this.session = new cim(this);
        this.telnetObj = new cmd();
        if (this.session_encryption_enabled) {
            this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
            this.session.setup_decryption(this.session_decrypt_key);
        }

        this.session.set_mouse_protocol(this.mouse_mode);

        for (int var1 = 0; var1 < 12; ++var1) {
            this.rndm_nums[var1] = (int) (Math.random() * 4.0) * 85;
        }

        this.session.set_sig_colors(this.rndm_nums);
        if (this.debug_msg) {
            this.session.enable_debug();
        } else {
            this.session.disable_debug();
        }

        this.pwrStatusPanel = new JPanel(new BorderLayout());
        this.ledStatusPanel = new JPanel(new BorderLayout());
        this.pwrHealthImgGreen = this.img[5];
        this.prepareImage(this.pwrHealthImgGreen, this.ledStatusPanel);
        this.pwrHealthImgYellow = this.img[21];
        this.prepareImage(this.pwrHealthImgYellow, this.ledStatusPanel);
        this.pwrHealthImgRed = this.img[18];
        this.prepareImage(this.pwrHealthImgRed, this.ledStatusPanel);
        this.pwrHealthImgOff = this.img[13];
        this.prepareImage(this.pwrHealthImgOff, this.ledStatusPanel);
        this.pwrEncImgLock = this.img[15];
        this.prepareImage(this.pwrEncImgLock, this.ledStatusPanel);
        this.pwrEncImgUnlock = this.img[19];
        this.prepareImage(this.pwrEncImgUnlock, this.ledStatusPanel);
        this.pwrEncImg = this.pwrEncImgUnlock;
        this.pwrHealthImg = this.pwrHealthImgOff;
        this.vmActImgOn = this.img[1];
        this.prepareImage(this.vmActImgOn, this.ledStatusPanel);
        this.vmActImgOff = this.img[13];
        this.prepareImage(this.vmActImgOff, this.ledStatusPanel);
        this.pwrPowerImgOn = this.img[16];
        this.prepareImage(this.pwrPowerImgOn, this.ledStatusPanel);
        this.pwrPowerImgOff = this.img[17];
        this.prepareImage(this.pwrPowerImgOff, this.ledStatusPanel);
        this.vmActImg = this.vmActImgOff;
        this.pwrPowerImg = this.pwrPowerImgOff;
        this.pwrStatusPanel.add(this.pwrEncCanvas = new JPanel() {
            public void paintComponent(Graphics var1) {
                super.paintComponent(var1);
                if (remcons.this.pwrEncImg != null) {
                    remcons.this.waitImage(remcons.this.pwrEncImg, this);
                    var1.drawImage(remcons.this.pwrEncImg, 1, 4, (ImageObserver) null);
                } else {
                    System.out.println("pwrEncCanvas Image not found");
                }

            }
        }, "West");
        this.setToolTipRecursively(this.pwrEncCanvas, this.getLocalString(16387));
        this.pwrEncCanvas.setPreferredSize(new Dimension(20, 20));
        this.pwrEncCanvas.setVisible(true);
        this.pwrStatusPanel.add(this.pwrEncLabel = new JLabel());
        this.pwrEncLabel.setText("         ");
        this.ledStatusPanel.add(this.pwrHealthCanvas = new JPanel() {
            public void paintComponent(Graphics var1) {
                super.paintComponent(var1);
                if (remcons.this.pwrHealthImg != null) {
                    remcons.this.waitImage(remcons.this.pwrHealthImg, this);
                    var1.drawImage(remcons.this.pwrHealthImg, 1, 4, (ImageObserver) null);
                } else {
                    System.out.println("pwrHealthCanvas Image not found");
                }

            }
        }, "West");
        this.setToolTipRecursively(this.pwrHealthCanvas, this.getLocalString(16386));
        this.pwrHealthCanvas.setPreferredSize(new Dimension(18, 25));
        this.pwrHealthCanvas.setVisible(true);
        this.ledStatusPanel.add(this.vmActCanvas = new JPanel() {
            public void paintComponent(Graphics var1) {
                super.paintComponent(var1);
                if (remcons.this.vmActImg != null) {
                    remcons.this.waitImage(remcons.this.vmActImg, this);
                    var1.drawImage(remcons.this.vmActImg, 1, 4, (ImageObserver) null);
                } else {
                    System.out.println("vmActCanvas Image not found");
                }

            }
        }, "Center");
        this.setToolTipRecursively(this.vmActCanvas, this.getLocalString(16388));
        this.vmActCanvas.setPreferredSize(new Dimension(18, 25));
        this.vmActCanvas.setVisible(true);
        this.ledStatusPanel.add(this.pwrPowerCanvas = new JPanel() {
            public void paintComponent(Graphics var1) {
                super.paintComponent(var1);
                if (remcons.this.pwrPowerImg != null) {
                    remcons.this.waitImage(remcons.this.pwrPowerImg, this);
                    var1.drawImage(remcons.this.pwrPowerImg, 1, 4, (ImageObserver) null);
                } else {
                    System.out.println("pwrPowerCanvas Image not found");
                }

            }
        }, "East");
        this.setToolTipRecursively(this.pwrPowerCanvas, this.getLocalString(16385));
        this.pwrPowerCanvas.setPreferredSize(new Dimension(18, 25));
        this.pwrPowerCanvas.setVisible(true);
        this.pwrStatusPanel.add(this.ledStatusPanel, "East");
        this.session.enable_keyboard();
        if (this.kbHookAvailable) {
            this.keyBoardTimer = new Timer(this.keyTimerTick, false, this.session);
            this.keyBoardTimer.setListener(new keyBoardTimerListener(), (Object) null);
            this.keyBoardTimer.start();
            System.out.println("Keyboard Hook available and timer started...");
        }

        this.initialized = 1;
    }

    public void start() {
        this.timeout_countdown = this.session_timeout;
        this.start_session();
        if (this.session_timeout == 2147483640) {
            System.out.println("Remote Console inactivity timeout = infinite.");
        } else {
            System.out.println("Remote Console inactivity timeout = " + this.session_timeout / 60 + " minutes.");
        }

    }

    public boolean ExtractKeyboardDll(String var1) {
        String var3 = System.getProperty("java.io.tmpdir");
        String var4 = System.getProperty("os.name").toLowerCase();
        String var5 = System.getProperty("file.separator");
        String var6 = " ";
        String var7 = " ";
        boolean var8 = false;
        String var9 = "com/hp/ilo2/remcons/";
        if (!var4.startsWith("windows") && !var4.startsWith("linux")) {
            System.out.println("Cannot load keyboardHook DLL. Non Windows-Linux client system.");
            var8 = false;
        } else {
            if (var3 == null) {
                var3 = var4.startsWith("windows") ? "C:\\TEMP" : "/tmp";
            }

            File var10 = new File(var3);
            if (!var10.exists()) {
                var10.mkdir();
            }

            if (!var3.endsWith(var5)) {
                var3 = var3 + var5;
            }

            String var10000 = var3 + "HpqKbHook-" +
                    Integer.toHexString(virtdevs.UID) + ".dll";
            virtdevs var10001 = this.ParentApp.virtdevsObj;
            var3 = var10000;
            System.out.println("checking for kbddll" + var3);
            File var11 = new File(var3);
            if (var11.exists()) {
                System.out.println(var1 + " already present ..");
                var8 = true;
                return var8;
            }

            System.out.println("Extracting " + var1 + "...");
            ClassLoader var12 = this.getClass().getClassLoader();
            byte[] var14 = new byte[4096];
            var6 = var3;
            var7 = var9 + var1;

            try {
                InputStream var15 = var12.getResourceAsStream(var7);
                FileOutputStream var16 = new FileOutputStream(var6);

                int var13;
                while ((var13 = var15.read(var14, 0, 4096)) != -1) {
                    var16.write(var14, 0, var13);
                }

                System.out.println("Writing dll to " + var6 + "complete");
                var15.close();
                var16.close();
                var8 = true;
            } catch (IOException var17) {
                System.out.println("dllExtract: " + var17);
                var8 = false;
            }
        }

        return var8;
    }

    public void stop() {
        if (this.locale_setter != null && this.locale_setter.isAlive()) {
            this.locale_setter.stop();
        }

        this.locale_setter = null;
        this.stop_session();
        System.out.println("Applet stopped...");
    }

    public void destroy() {
        System.out.println("Hiding applet.");
        if (this.isVisible()) {
            this.setVisible(false);
        }

    }

    public void timeout(Object var1) {
        if (this.session.UI_dirty) {
            this.session.UI_dirty = false;
            this.timeout_countdown = this.session_timeout;
        } else {
            this.timeout_countdown -= 30;
            if (this.timeout_countdown <= 0 && System.getProperty("java.version", "0").compareTo("1.2") < 0) {
                this.stop_session();
            }
        }

    }

    private void start_session() {
        this.session.connect(this.session_ip, this.login, this.port_num, this.ts_param, this.terminalServicesPort, this);
        this.timer = new Timer(30000, false, this.session);
        this.timer.setListener(this, (Object) null);
        this.timer.start();
    }

    private void stop_session() {
        if (this.timer != null) {
            this.timer.stop();
            this.timer = null;
        }

        this.session.disconnect();
    }

    public void setPwrStatusEnc(int var1) {
        if (var1 == 0) {
            this.pwrEncImg = this.pwrEncImgUnlock;
        } else {
            this.pwrEncImg = this.pwrEncImgLock;
        }

        this.pwrEncCanvas.invalidate();
        this.pwrEncCanvas.repaint();
    }

    public void setPwrStatusEncLabel(String var1) {
        this.pwrEncLabel.setText(var1 + "       ");
    }

    public void setPwrStatusHealth(int var1) {
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

    public void setPwrStatusPower(int var1) {
        if (var1 == 0 && this.pwrPowerImgOff != this.pwrPowerImg) {
            this.pwrPowerImg = this.pwrPowerImgOff;
            this.ParentApp.updatePsMenu(var1);
            this.pwrPowerCanvas.invalidate();
            this.pwrPowerCanvas.repaint();
            System.out.println("Moving Power to Off state");
        } else if (var1 != 0 && this.pwrPowerImgOn != this.pwrPowerImg) {
            this.pwrPowerImg = this.pwrPowerImgOn;
            this.ParentApp.updatePsMenu(var1);
            this.pwrPowerCanvas.invalidate();
            this.pwrPowerCanvas.repaint();
            System.out.println("Moving Power to ON state");
        }

    }

    public void setvmAct(int var1) {
        if (this.vmActImg != this.vmActImgOn && var1 != 0) {
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

    public int seize_dialog(String var1, String var2, int var3) {
        System.out.println("seize dialog invoked" + var3);
        VSeizeWaitDialog var4 = new VSeizeWaitDialog(this, var1, var2, var3);
        return var4.getUserInput();
    }

    public void seize_confirmed() {
        this.ParentApp.moveUItoInit(false);
        this.ParentApp.virtdevsObj.stop();
        this.remconsUnInstallKeyboardHook();
        this.ParentApp.dispFrame.setVisible(false);
        this.session.seize();
        this.ParentApp.stop();
    }

    public void shared(String var1, String var2) {
        System.out.println("shared notification invoked");
        new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(8230), this.getLocalString(8231) + " " + var2 + "@" + var1 + this.getLocalString(8232), false);
    }

    public void unAuthorized(String var1, boolean var2) {
        new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(8230), this.getLocalString(8233) + var1 + this.getLocalString(8234), false);
        if (var2) {
            this.session.unAuthAccess();
        }

    }

    public void firmwareUpgrade() {
        System.out.println("Firmware upgrade notification invoked");
        VErrorDialog var1 = new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(8230), this.getLocalString(8235), false);
        this.ParentApp.moveUItoInit(false);
        this.ParentApp.virtdevsObj.stop();
        this.session.fwUpgrade();
        this.ParentApp.stop();
        if (var1.getBoolean()) {
            System.exit(0);
        }

    }

    public void ack(byte var1, byte var2, byte var3, byte var4) {
        if (var1 == 0) {
            if (var2 == 1) {
                if (var4 == 1 && !this.ParentApp.fdSelected) {
                    this.ParentApp.fdSelected = true;
                    this.ParentApp.lockFdMenu(false, this.getLocalString(4131) + this.getLocalString(4106));
                } else if (var4 == 0 && this.ParentApp.fdSelected) {
                    this.ParentApp.fdSelected = false;
                    this.ParentApp.lockFdMenu(true, "");
                }
            } else if (var2 == 2) {
                if (var4 == 1 && !this.ParentApp.cdSelected) {
                    this.ParentApp.cdSelected = true;
                    this.ParentApp.lockCdMenu(false, this.getLocalString(4131) + this.getLocalString(4107));
                } else if (var4 == 0 && this.ParentApp.cdSelected) {
                    this.ParentApp.cdSelected = false;
                    this.ParentApp.lockCdMenu(true, "");
                }
            }
        }

    }

    protected void init_params() {
        this.login = null;
        this.port_num = 23;
        this.mouse_mode = 0;
        this.session_timeout = 900;
        this.session_encryption_enabled = true;
        this.session_key_index = 0;
        this.launchTerminalServices = false;
        this.terminalServicesPort = 0;
        this.debug_msg = true;
        this.session_ip = this.ParentApp.getCodeBase().getHost();
        this.num_cursors = 0;
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

    private String parse_login(String var1) {
        if (var1.startsWith("Compaq-RIB-Login=")) {
            String var2 = "\u001b[!";

            try {
                var2 = var2 + var1.substring(17, 73);
                var2 = var2 + '\r';
                var2 = var2 + var1.substring(74, 106);
                var2 = var2 + '\r';
                return var2;
            } catch (StringIndexOutOfBoundsException var4) {
                return null;
            }
        } else {
            return this.base64_decode(var1);
        }
    }

    private String base64_decode(String var1) {
        int var9 = 0;
        int var10 = 0;

        String var11;
        for (var11 = ""; var9 + 3 < var1.length() && var10 == 0; var9 += 4) {
            char var2 = base64[var1.charAt(var9) & 127];
            char var3 = base64[var1.charAt(var9 + 1) & 127];
            char var4 = base64[var1.charAt(var9 + 2) & 127];
            char var5 = base64[var1.charAt(var9 + 3) & 127];
            char var6 = (char) ((var2 << 2) + (var3 >> 4));
            char var7 = (char) ((var3 << 4) + (var4 >> 2));
            char var8 = (char) ((var4 << 6) + var5);
            var6 = (char) (var6 & 255);
            var7 = (char) (var7 & 255);
            var8 = (char) (var8 & 255);
            if (var6 == ':') {
                var6 = '\r';
            }

            if (var7 == ':') {
                var7 = '\r';
            }

            if (var8 == ':') {
                var8 = '\r';
            }

            var11 = var11 + var6;
            if (var1.charAt(var9 + 2) == '=') {
                ++var10;
            } else {
                var11 = var11 + var7;
            }

            if (var1.charAt(var9 + 3) == '=') {
                ++var10;
            } else {
                var11 = var11 + var8;
            }
        }

        if (var11.length() != 0) {
            var11 = var11 + '\r';
        }

        return var11;
    }

    public void paint(Graphics var1) {
    }

    public int getTimeoutValue() {
        return this.timeout_countdown;
    }

    public void run() {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows") && !this.lt.windows) {
            Locale.setDefault(Locale.US);
        }

        while (true) {
            while (true) {
                while (!this.retry_connection_flag || 3 < this.retry_connection_count) {
                    if (this.retry_connection_flag) {
                        System.out.println("Retry connection  - video maximum attempts exhausted");
                        this.stop_session();
                        this.retry_connection_flag = false;
                    } else {
                        try {
                            this.sleepAtLeast(2500L);
                        } catch (InterruptedException var2) {
                            System.out.println("Thread interrupted..");
                        }
                    }
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
                    this.sleepAtLeast(5000L);
                } catch (InterruptedException var4) {
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
                    this.sleepAtLeast(2500L);
                } catch (InterruptedException var3) {
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

    public void sleepAtLeast(long var1) throws InterruptedException {
        long var3 = System.currentTimeMillis();

        long var7;
        for (long var5 = var1; var5 > 0L; var5 = var1 - (var7 - var3)) {
            Thread.sleep(var5);
            var7 = System.currentTimeMillis();
        }

    }

    public void setDialogIsOpen(boolean var1) {
        dialogIsOpen = var1;
    }

    public void SetLicensed(int var1) {
        this.licensed = (var1 & 1) != 0;

        System.out.println("SetLicensed: " + this.licensed);
    }

    void SetFlags(int var1) {
        if ((var1 & 8) == 0) {
            this.halfHeightCapable = false;
            System.out.println("halfHeightCapable false");
        } else {
            this.halfHeightCapable = true;
            System.out.println("halfHeightCapable true");
        }

    }

    public void UnlicensedShutdown() {
        String var1 = "<html>" + this.getLocalString(8213) + " " + this.getLocalString(8215) + " " + this.getLocalString(8237) + "<br><br>" + this.getLocalString(8238) + "</html>";
        System.out.println("Unlicensed notification invoked");
        VErrorDialog var2 = new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(8236), var1, true);
        var2.getBoolean();
        this.ParentApp.moveUItoInit(false);
        this.ParentApp.stop();
    }

    public void resetShutdown() {
        VErrorDialog var1 = new VErrorDialog(this.ParentApp.dispFrame, this.getLocalString(4103), this.getLocalString(8289), true);
        var1.getBoolean();
        this.ParentApp.moveUItoInit(false);
        this.ParentApp.stop();
    }

    public int getInitialized() {
        return this.initialized;
    }

    private void get_terminal_svcs_label(int var1) {
        String var2;
        if (var1 == 0) {
            var2 = "mstsc";
        } else if (var1 == 1) {
            var2 = "vnc";
        } else {
            var2 = "type" + var1;
        }

        this.term_svcs_label = prop.getProperty(var2 + ".label", "Terminal Svcs");
    }

    public void remconsInstallKeyboardHook() {
        String var1 = System.getProperty("os.name").toLowerCase();
        boolean var2 = true;
        if (this.kHook == null) {
            System.out.println("remconsInstallKeyboardHook:KB Hook dll not loaded");
        } else if (!this.kbHookInstalled && this.kbHookAvailable && !dialogIsOpen) {
            this.kHook.clearKeymap();
            int var3 = this.kHook.InstallKeyboardHook();
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
        }

    }

    public void remconsUnInstallKeyboardHook() {
        boolean var1 = true;
        if (this.kHook != null && this.kbHookInstalled && this.kbHookAvailable) {
            int var2 = this.kHook.UnInstallKeyboardHook();
            if (var2 == 0) {
                this.kbHookInstalled = false;
                this.prevKeyData = this.keyData = 0;
                this.kHook.clearKeymap();
            } else {
                System.out.println("remconsUnInstallKeyboardHook: uninstall failed:" + var2);
            }
        }

    }

    public void setLocalKbdLayout(int var1) {
        if (this.kHook != null && this.kbHookInstalled) {
            System.out.println("setKbdLayoutHandler: set Layout - " + var1);
            this.kHook.setLocalKbdLayout(var1);
        } else {
            System.out.println("setKbdLayoutHandler: kHook not available. dbg caching..");
            this.localKbdLayoutId = var1;
        }

    }

    public void setToolTipRecursively(JComponent var1, String var2) {
        var1.setToolTipText(var2);
    }

    public void viewHotKeys() {
        new hotKeysDialog(this);
    }

    public void viewAboutJirc() {
        new aboutJircDialog(this);
    }

    class keyBoardTimerListener implements TimerListener {
        keyBoardTimerListener() {
        }

        public synchronized void timeout(Object var1) {
            boolean var6 = false;
            byte[] var7 = new byte[10];
            boolean var8 = false;
            int var9 = 995;
            if (remcons.this.kHook != null && remcons.this.kbHookInstalled) {
                do {
                    remcons.this.prevKeyData = remcons.this.keyData;
                    remcons.this.keyData = remcons.this.kHook.GetKeyData();
                    if (remcons.this.keyData != remcons.this.prevKeyData && 0 != remcons.this.keyData) {
                        int var3 = (remcons.this.keyData & 16711680) >> 16;
                        int var5 = (remcons.this.keyData & '\uff00') >> 8;
                        int var4 = remcons.this.keyData & 255;
                        if ((var3 & 144) == 144) {
                            var8 = true;
                        } else if ((var3 & 128) == 128) {
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
                } while (var9++ < 1000);
            }

        }
    }
}
