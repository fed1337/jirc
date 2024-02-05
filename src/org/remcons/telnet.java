package org.remcons;


import org.virtdevs.VErrorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

public class telnet extends JPanel implements Runnable, MouseListener, FocusListener, KeyListener {
    public static final int TELNET_PORT = 23;
    public static final int TELNET_ENCRYPT = 192;
    public static final int TELNET_CHG_ENCRYPT_KEYS = 193;
    public static final int TELNET_SE = 240;
    public static final int TELNET_NOP = 241;
    public static final int TELNET_DM = 242;
    public static final int TELNET_BRK = 243;
    public static final int TELNET_IP = 244;
    public static final int TELNET_AO = 245;
    public static final int TELNET_AYT = 246;
    public static final int TELNET_EC = 247;
    public static final int TELNET_EL = 248;
    public static final int TELNET_GA = 249;
    public static final int TELNET_SB = 250;
    public static final int TELNET_WILL = 251;
    public static final int TELNET_WONT = 252;
    public static final int TELNET_DO = 253;
    public static final int TELNET_DONT = 254;
    public static final int TELNET_IAC = 255;
    public static final int JAP_VK_OPEN_BRACKET = 194;
    public static final int JAP_VK_BACK_SLASH = 195;
    public static final int JAP_VK_CLOSE_BRACKET = 196;
    public static final int JAP_VK_COLON = 197;
    public static final int JAP_VK_RO = 198;
    private static final int CMD_TS_AVAIL = 194;
    private static final int CMD_TS_NOT_AVAIL = 195;
    private static final int CMD_TS_STARTED = 196;
    private static final int CMD_TS_STOPPED = 197;
    public final int PWR_OPTION_PULSE = 0;
    public final int PWR_OPTION_HOLD = 1;
    public final int PWR_OPTION_CYCLE = 2;
    public final int PWR_OPTION_RESET = 3;
    public final int CIPHER_NONE = 0;
    public final int CIPHER_RC4 = 1;
    public final int CIPHER_AES128 = 2;
    public final int CIPHER_AES256 = 3;
    public final int AES_BITSIZE_128 = 0;
    public final int AES_BITSIZE_192 = 1;
    public final int AES_BITSIZE_256 = 2;
    public final int REQ_LOGIN_KEY = 0;
    public final int REQ_GET_AUTH = 1;
    public final int REQ_SHARE = 2;
    public final int REQ_SEIZE = 3;
    public final int REQ_DONE = 4;
    public final int CONNECT_CANCEL = 0;
    public final int CONNECT_SEIZE = 1;
    public final int CONNECT_SHARE = 2;
    public final int KEY_STATE_PRESSED = 0;
    public final int KEY_STATE_TYPED = 1;
    public final int KEY_STATE_RELEASED = 2;
    private final boolean crlf_enabled = false;
    private final boolean tbm_mode = false;
    private final int total_count = 0;
    private final int[] keyMap = new int[256];
    private final int[] winkey_to_hid = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 42, 43, 40, 0, 0, 40, 0, 0, 225, 224, 226, 72, 57, 0, 0, 0, 0, 0, 41, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 54, 45, 55, 56, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 51, 0, 46, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 47, 49, 48, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 159, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 76, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 36, 37, 52, 54, 70, 73, 0, 0, 0, 0, 55, 47, 48, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, 46, 54, 45, 55, 56, 53, 135, 48, 137, 50, 52, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 49, 48, 52, 0, 96, 90, 92, 94, 0, 0, 0, 0, 0, 0, 0, 139, 0, 0, 0, 0, 136, 136, 136, 53, 53, 0, 0, 0, 0, 0, 0, 0, 0, 138, 0, 255};
    private final Locale lo;
    private final String keyboardLayout;
    public JLabel status_box = new JLabel();
    public boolean mirror = false;
    public byte[] sessionKey = new byte[32];
    public String st_fld1 = "";
    public String st_fld2 = "";
    public String st_fld3 = "";
    public String st_fld4 = "";
    public boolean post_complete = false;
    public int dbg_print = 0;
    public cmd cmdObj = new cmd();
    public remcons remconsObj;
    public int cipher = 0;
    protected dvcwin screen;
    protected Thread receiver;
    protected Socket s;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected String login = "";
    protected String host = "";
    protected int port = 23;
    protected int connected = 0;
    protected int fore;
    protected int back;
    protected int hi_fore;
    protected int hi_back;
    protected String escseq;
    protected String curr_num;
    protected int[] escseq_val = new int[10];
    protected int escseq_val_count = 0;
    protected byte[] decrypt_key = new byte[16];
    protected boolean encryption_enabled = false;
    protected boolean dvc_mode = false;
    protected boolean dvc_encryption = false;
    int ts_type;
    LocaleTranslator translator = new LocaleTranslator();
    private RC4 RC4decrypter;
    private Aes aes128decrypter;
    private Aes aes256decrypter;
    private boolean decryption_active = false;
    private Process rdpProc = null;
    private boolean enable_terminal_services = false;
    private int terminalServicesPort = 3389;
    private boolean seized = false;
    private int japanese_kbd = 0;
    private boolean screenFocusLost = false;

    public telnet(remcons var1) {
        this.remconsObj = var1;
        this.screen = new dvcwin(1024, 768, this.remconsObj);
        System.out.println("Screen: " + this.screen);
        this.screen.addMouseListener(this);
        this.addFocusListener(this);
        this.screen.addFocusListener(this);
        this.screen.addKeyListener(this);
        this.focusTraversalKeysDisable(this.screen);
        this.focusTraversalKeysDisable(this);
        this.setBackground(Color.black);
        this.setLayout(new BorderLayout());
        this.add(this.screen, "North");
        this.set_status(1, this.getLocalString(12301));
        this.set_status(2, "          ");
        this.set_status(3, "          ");
        this.set_status(4, "          ");
        if (System.getProperty("os.name").toLowerCase().startsWith("windows") && !this.translator.windows) {
            this.translator.selectLocale("en_US");
        }

        for (int var2 = 0; var2 < 256; ++var2) {
            this.keyMap[var2] = 0;
        }

        this.lo = Locale.getDefault();
        this.keyboardLayout = this.lo.toString();
        System.out.println("telent lang: Keyboard layout is " + this.keyboardLayout);
        if (this.keyboardLayout.startsWith("ja")) {
            System.out.println("JAPANESE LANGUAGE \n");
            this.japanese_kbd = 1;
        } else {
            this.japanese_kbd = 0;
        }

    }

    public void setLocale(String var1) {
        this.translator.selectLocale(var1);
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("telnet:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public void enable_debug() {
    }

    public void disable_debug() {
    }

    public void startRdp() {
        if (this.rdpProc == null) {
            Runtime var3 = Runtime.getRuntime();
            String var1;
            if (this.ts_type == 0) {
                var1 = "mstsc";
            } else if (this.ts_type == 1) {
                var1 = "vnc";
            } else {
                var1 = "type" + this.ts_type;
            }

            String var2 = remcons.prop.getProperty(var1 + ".program");
            System.out.println(var1 + " = " + var2);
            if (var2 != null) {
                var2 = this.percent_sub(var2);
                System.out.println("exec: " + var2);

                try {
                    this.rdpProc = var3.exec(var2);
                } catch (SecurityException var8) {
                    System.out.println("SecurityException: " + var8.getMessage() + ":: Attempting to launch " + var2);
                } catch (IOException var9) {
                    System.out.println("IOException: " + var9.getMessage() + ":: " + var2);
                }

                return;
            }

            boolean var4 = false;

            try {
                System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);
                this.rdpProc = var3.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);
            } catch (SecurityException var14) {
                System.out.println("SecurityException: " + var14.getMessage() + ":: Attempting to launch mstsc.");
            } catch (IOException var15) {
                System.out.println("IOException: " + var15.getMessage() + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
                var4 = true;
            }

            String[] var5;
            if (var4) {
                var4 = false;
                var5 = new String[]{"\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort};

                try {
                    this.rdpProc = var3.exec(var5);
                } catch (SecurityException var12) {
                    System.out.println("SecurityException: " + var12.getMessage() + ":: Attempting to launch mstsc.");
                } catch (IOException var13) {
                    System.out.println("IOException: " + var13.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
                    var4 = true;
                }
            }

            if (var4) {
                var5 = new String[]{"\\Program Files\\Terminal Services Client\\mstsc"};

                try {
                    this.rdpProc = var3.exec(var5);
                } catch (SecurityException var10) {
                    System.out.println("SecurityException: " + var10.getMessage() + ":: Attempting to launch mstsc.");
                } catch (IOException var11) {
                    System.out.println("IOException: " + var11.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
                }
            }
        }

    }

    public void keyTyped(KeyEvent var1) {
        String var2 = "";
        this.sendKey(var1, 1);
    }

    public void keyPressed(KeyEvent var1) {
        String var2 = "";
        this.sendKey(var1, 0);
    }

    public void keyReleased(KeyEvent var1) {
        String var2 = "";
        this.sendKey(var1, 2);
    }

    public void send_auto_alive_msg() {
    }

    public synchronized void focusGained(FocusEvent var1) {
        if (var1.getComponent() == this.screen) {
            if (this.screenFocusLost) {
                this.remconsObj.remconsInstallKeyboardHook();
                this.screenFocusLost = false;
            }
        } else {
            this.screen.requestFocus();
        }

    }

    public synchronized void focusLost(FocusEvent var1) {
        if (var1.getComponent() == this.screen && var1.isTemporary()) {
            this.remconsObj.remconsUnInstallKeyboardHook();
            this.screenFocusLost = true;
        }

    }

    public synchronized void mouseClicked(MouseEvent var1) {
        super.requestFocus();
    }

    public synchronized void mousePressed(MouseEvent var1) {
    }

    public synchronized void mouseReleased(MouseEvent var1) {
    }

    public synchronized void mouseEntered(MouseEvent var1) {
    }

    public synchronized void mouseExited(MouseEvent var1) {
    }

    public synchronized void addNotify() {
        super.addNotify();
    }

    public synchronized void set_status(int var1, String var2) {
        switch (var1) {
            case 1:
                this.st_fld1 = var2;
                break;
            case 2:
                this.st_fld2 = var2;
                break;
            case 3:
                this.st_fld3 = var2;
                break;
            case 4:
                this.st_fld4 = var2;
        }

        this.status_box.setText(this.st_fld1 + " " + this.st_fld2 + "      " + this.st_fld3 + "      " + this.st_fld4);
    }

    public void reinit_vars() {
        this.dvc_encryption = false;
    }

    public void setup_decryption(byte[] var1) {
        System.arraycopy(var1, 0, this.decrypt_key, 0, 16);
        this.RC4decrypter = new RC4(var1);
        this.encryption_enabled = true;
        this.aes128decrypter = new Aes(0, var1);
        this.aes256decrypter = new Aes(0, var1);
    }

    public synchronized void connect(String var1, String var2, int var3, int var4, int var5, remcons var6) {
        this.enable_terminal_services = (var4 & 1) == 1;
        this.ts_type = var4 >> 8;
        if (var5 != 0) {
            this.terminalServicesPort = var5;
        }

        if (this.connected == 0) {
            this.screen.start_updates();
            this.connected = 1;
            this.host = var1;
            this.login = var2;
            this.port = var3;
            this.remconsObj = var6;
            this.requestFocus();
            this.sessionKey = var6.ParentApp.getParameter("RCINFO1").getBytes();
            String var7 = var6.ParentApp.rc_port;
            if (var7 != null) {
                try {
                    this.port = Integer.parseInt(var7);
                    System.out.println("RC port number " + this.port);
                } catch (NumberFormatException var16) {
                    System.out.println("Failed to read rcport from parameters");
                    this.port = 23;
                }
            }

            try {
                this.set_status(1, this.getLocalString(12296));
                System.out.println("updated: connecting to " + this.host + ":" + this.port);

                try {
                    Thread.currentThread();
                    Thread.sleep(1000L);
                } catch (InterruptedException var12) {
                    System.out.println("connect Thread interrupted..");
                }

                this.s = new Socket(this.host, this.port);

                try {
                    this.s.setSoLinger(true, 0);
                    System.out.println("set TcpNoDelay");
                    this.s.setTcpNoDelay(true);
                } catch (SocketException var11) {
                    System.out.println("telnet.connect() linger SocketException: " + var11);
                }

                this.in = new DataInputStream(this.s.getInputStream());
                this.out = new DataOutputStream(this.s.getOutputStream());
                byte var8 = this.in.readByte();
                if (var8 == 80) {
                    this.set_status(1, this.getLocalString(12297));
                    boolean var9 = false;
                    System.out.println("Received hello byte. Requesting remote connection...");
                    short var10 = 8193;
                    var9 = this.requestRemoteConnection(var10);
                    if (var9) {
                        this.receiver = new Thread(this);
                        this.receiver.setName("telnet_rcvr");
                        this.receiver.start();
                        this.cmdObj.connectCmd(this.remconsObj, this.host, this.port);
                    } else {
                        var6.ParentApp.stop();
                    }
                } else {
                    this.set_status(1, this.getLocalString(12298));
                    System.out.println("Socket connection failure... ");
                }
            } catch (SocketException var13) {
                System.out.println("telnet.connect() SocketException: " + var13);
                this.set_status(1, var13.toString());
                this.s = null;
                this.in = null;
                this.out = null;
                this.receiver = null;
                this.connected = 0;
            } catch (UnknownHostException var14) {
                System.out.println("telnet.connect() UnknownHostException: " + var14);
                this.set_status(1, var14.toString());
                this.s = null;
                this.in = null;
                this.out = null;
                this.receiver = null;
                this.connected = 0;
            } catch (IOException var15) {
                System.out.println("telnet.connect() IOException: " + var15);
                this.set_status(1, var15.toString());
                this.s = null;
                this.in = null;
                this.out = null;
                this.receiver = null;
                this.connected = 0;
            }
        } else {
            this.requestFocus();
        }

    }

    public boolean requestRemoteConnection(int var1) {
        boolean var2 = false;
        byte var3 = 0;
        byte[] var4 = new byte[2];
        boolean var5 = false;

        while (true) {
            while (var3 != 4) {
                byte var16;
                switch (var3) {
                    case 0:
                        var4[0] = (byte) (var1 & 255);
                        var4[1] = (byte) ((var1 & '\uff00') >>> 8);
                        if (this.remconsObj.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {
                            for (int var6 = 0; var6 < this.sessionKey.length; ++var6) {
                                byte[] var10000 = this.sessionKey;
                                var10000[var6] ^= (byte) this.remconsObj.ParentApp.enc_key.charAt(var6 % this.remconsObj.ParentApp.enc_key.length());
                            }

                            if (this.remconsObj.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
                                var4[1] = (byte) (var4[1] | 64);
                            } else {
                                var4[1] = (byte) (var4[1] | 128);
                            }
                        }

                        byte[] var17 = new byte[var4.length + this.sessionKey.length];
                        System.arraycopy(var4, 0, var17, 0, var4.length);
                        System.arraycopy(this.sessionKey, 0, var17, var4.length, this.sessionKey.length);
                        String var7 = new String(var17);
                        this.transmit(var7);
                        var3 = 1;
                        break;
                    case 1:
                        try {
                            var16 = this.in.readByte();
                        } catch (IOException var14) {
                            var2 = false;
                            var3 = 4;
                            System.out.println("Socket Read failed.");
                            break;
                        }

                        switch (var16) {
                            case 81:
                                System.out.println("Access denied.");
                                this.set_status(1, this.getLocalString(12299));
                                if (null != this.remconsObj.ParentApp.dispFrame) {
                                    new VErrorDialog(this.remconsObj.ParentApp.dispFrame, this.getLocalString(8239), this.getLocalString(8287), true);
                                    this.remconsObj.ParentApp.dispFrame.setVisible(false);
                                } else {
                                    new VErrorDialog(this.getLocalString(8287), true);
                                }

                                var2 = false;
                                var3 = 4;
                                this.remconsObj.ParentApp.stop();
                                continue;
                            case 82:
                                this.set_status(1, this.getLocalString(12300));
                                System.out.println("Authenticated");
                                var2 = true;
                                this.remconsObj.licensed = true;
                                var3 = 4;
                                continue;
                            case 83:
                            case 89:
                                System.out.println("Authenticated, but busy, negotiating");
                                int var8;
                                if (0 == this.remconsObj.retry_connection_count) {
                                    var8 = this.negotiateBusy();
                                    System.out.println("negotiateResult:" + var8);
                                } else {
                                    System.out.println("Overriding seize option for internal retry");
                                    var8 = 1;
                                }

                                switch (var8) {
                                    case 0:
                                        System.out.println("Connection cancelled by user");
                                        if (null != this.remconsObj.ParentApp.dispFrame) {
                                            this.remconsObj.ParentApp.dispFrame.setVisible(false);
                                        }

                                        var2 = false;
                                        var3 = 4;
                                        continue;
                                    case 1:
                                        var4[0] = 85;
                                        var4[1] = 0;
                                        byte[] var9 = new byte[var4.length];
                                        System.arraycopy(var4, 0, var9, 0, var4.length);
                                        System.out.println("Seizing connection, sending command 0x0055");
                                        String var10 = new String(var9);
                                        this.transmit(var10);
                                        var3 = 3;
                                        this.set_status(1, this.getLocalString(12568));
                                        continue;
                                    case 2:
                                        var4[0] = 86;
                                        var4[1] = 0;
                                        System.out.println("Sharing connection, sending command 0x0056");
                                        byte[] var11 = new byte[var4.length];
                                        System.arraycopy(var4, 0, var11, 0, var4.length);
                                        String var12 = new String(var11);
                                        this.transmit(var12);
                                        var3 = 2;
                                    default:
                                        continue;
                                }
                            case 84:
                            case 85:
                            case 86:
                            default:
                                System.out.println("rqrmtconn default: " + var16);
                                var2 = true;
                                var3 = 4;
                                continue;
                            case 87:
                                System.out.println("Received No License Notification");
                                this.remconsObj.licensed = false;
                                var2 = false;
                                var3 = 4;
                                continue;
                            case 88:
                                System.out.println("No free Sessions Notification");
                                if (null != this.remconsObj.ParentApp.dispFrame) {
                                    new VErrorDialog(this.remconsObj.ParentApp.dispFrame, this.getLocalString(8239), this.getLocalString(8240), true);
                                    this.remconsObj.ParentApp.dispFrame.setVisible(false);
                                } else {
                                    new VErrorDialog(this.getLocalString(8240), true);
                                }

                                var2 = false;
                                var3 = 4;
                                this.remconsObj.ParentApp.stop();
                                continue;
                        }
                    case 2:
                        var2 = false;
                        var3 = 4;
                        break;
                    case 3:
                        var3 = 4;

                        try {
                            var16 = this.in.readByte();
                        } catch (IOException var15) {
                            var2 = false;
                            var3 = 4;
                            System.out.println("Socket Read failed.");
                            continue;
                        }

                        switch (var16) {
                            case 81:
                                if (null != this.remconsObj.ParentApp.dispFrame) {
                                    new VErrorDialog(this.remconsObj.ParentApp.dispFrame, this.getLocalString(8239), this.getLocalString(8263), true);
                                    this.remconsObj.ParentApp.dispFrame.setVisible(false);
                                } else {
                                    new VErrorDialog(this.getLocalString(8263), true);
                                }

                                var2 = false;
                                break;
                            case 82:
                                this.remconsObj.ParentApp.moveUItoInit(true);
                                var2 = true;
                        }
                }
            }

            return var2;
        }
    }

    public int negotiateBusy() {
        byte var1 = 0;
        this.remconsObj.ParentApp.moveUItoInit(false);
        VSeizeDialog var2 = new VSeizeDialog(this.remconsObj);
        switch (var2.getUserInput()) {
            case 0:
                var1 = 0;
                break;
            case 2:
                var1 = 1;
        }

        return var1;
    }

    public void connect(String var1, String var2, int var3, int var4, remcons var5) {
        this.connect(var1, var2, this.port, var3, var4, var5);
    }

    public void connect(String var1, int var2, int var3, remcons var4) {
        this.connect(var1, this.login, this.port, var2, var3, var4);
    }

    public synchronized void disconnect() {
        this.remconsObj.remconsUnInstallKeyboardHook();
        if (this.connected == 1) {
            this.screen.stop_updates();
            this.connected = 0;
            if (this.receiver != null && this.receiver.isAlive()) {
                this.receiver.stop();
            }

            this.receiver = null;
            if (this.s != null) {
                try {
                    System.out.println("Closing socket");
                    this.s.close();
                } catch (IOException var2) {
                    System.out.println("telnet.disconnect() IOException: " + var2);
                    this.set_status(1, var2.toString());
                }
            }

            this.s = null;
            this.in = null;
            this.out = null;
            if (this.cmdObj != null) {
                this.cmdObj.disconnectCmd();
            }

            this.set_status(1, this.getLocalString(12301));
            this.reinit_vars();
            this.decryption_active = false;
        }

    }

    public synchronized void transmit(String var1) {
        if (this.out != null) {
            if (var1.length() != 0) {
                byte[] var2 = new byte[var1.length()];

                for (int var3 = 0; var3 < var1.length(); ++var3) {
                    var2[var3] = (byte) var1.charAt(var3);
                }

                try {
                    this.out.write(var2, 0, var2.length);
                } catch (IOException var5) {
                    System.out.println("telnet.transmit() IOException: " + var5);
                }
            }

        }
    }

    public synchronized void transmitb(byte[] var1, int var2) {
    }

    protected synchronized String translate_key(KeyEvent var1) {
        char var3 = var1.getKeyChar();
        String var2;
        switch (var3) {
            case '\t':
                var2 = "";
                break;
            case '\n':
            case '\r':
                if (var1.isShiftDown()) {
                    var2 = "\n";
                } else {
                    var2 = "\r";
                }
                break;
            case '\u000b':
            case '\f':
            default:
                var2 = this.translator.translate(var3);
        }

        return var2;
    }

    protected synchronized String translate_special_key(KeyEvent var1) {
        String var2 = "";
        switch (var1.getKeyCode()) {
            case 9:
                var1.consume();
                var2 = "\t";
            default:
                return var2;
        }
    }

    protected synchronized String translate_special_key_release(KeyEvent var1) {
        String var2 = "";
        return var2;
    }

    boolean process_dvc(char var1) {
        return true;
    }

    public void run() {
        boolean var2 = false;
        byte var3 = 0;
        boolean var4 = false;
        boolean var5 = false;
        byte[] var6 = new byte[1024];
        int var10 = 0;
        this.dvc_mode = true;
        System.out.println("Starting receiver run");

        try {
            while (true) {
                int var7;
                try {
                    if (this.s == null || this.in == null) {
                        System.out.println("telnet.run() s or in is null");
                        break;
                    }

                    this.s.setSoTimeout(1000);
                    var7 = this.in.read(var6);
                } catch (InterruptedIOException var18) {
                    continue;
                } catch (Exception var19) {
                    var7 = -1;
                    ++var10;
                }

                if (var7 < 0) {
                    if (var10 > 1) {
                        System.out.println("Reading from stream failed for  " + var10 + "times");
                        boolean var23 = false;
                        break;
                    }
                } else {
                    for (int var8 = 0; var8 < var7; ++var8) {
                        if (this.dbg_print == 1000) {
                            this.dbg_print = 0;
                        }

                        ++this.dbg_print;
                        this.remconsObj.fdConnState = this.remconsObj.ParentApp.virtdevsObj.fdConnected;
                        this.remconsObj.cdConnState = this.remconsObj.ParentApp.virtdevsObj.cdConnected;
                        char var1 = (char) var6[var8];
                        var1 = (char) (var1 & 255);
                        if (this.dvc_mode) {
                            if (this.dvc_encryption) {
                                char var9;
                                switch (this.cipher) {
                                    case 1:
                                        var9 = (char) (this.RC4decrypter.randomValue() & 255);
                                        var1 ^= var9;
                                        break;
                                    case 2:
                                        var9 = (char) (this.aes128decrypter.randomValue() & 255);
                                        var1 ^= var9;
                                        break;
                                    case 3:
                                        var9 = (char) (this.aes256decrypter.randomValue() & 255);
                                        var1 ^= var9;
                                        break;
                                    default:
                                        boolean var22 = false;
                                        System.out.println("Unknown encryption");
                                }

                                var1 = (char) (var1 & 255);
                            }

                            this.dvc_mode = this.process_dvc(var1);
                            if (!this.dvc_mode) {
                                System.out.println("DVC mode turned off");
                                this.set_status(1, this.getLocalString(12302));
                            }
                        } else if (var1 == 27) {
                            var3 = 1;
                        } else if (var3 == 1 && var1 == '[') {
                            var3 = 2;
                        } else if (var3 == 2 && var1 == 'R') {
                            this.dvc_mode = true;
                            this.dvc_encryption = true;
                            this.set_status(1, this.getLocalString(12303));
                        } else if (var3 == 2 && var1 == 'r') {
                            this.dvc_mode = true;
                            this.dvc_encryption = false;
                            this.set_status(1, this.getLocalString(12292));
                        } else {
                            var3 = 0;
                        }
                    }
                }
            }
        } catch (Exception var20) {
            System.out.println("telnet.run() Exception, class:" + var20.getClass() + "  msg:" + var20.getMessage());
            var20.printStackTrace();
        } finally {
            if (!this.seized) {
                remcons var10001 = this.remconsObj;
                if (this.remconsObj.retry_connection_count < 3) {
                    this.screen.clearScreen();
                    System.out.println("Retrying connection");
                    this.set_status(1, this.getLocalString(12305));
                } else {
                    this.screen.clearScreen();
                    System.out.println("offline");
                    this.set_status(1, this.getLocalString(12301));
                }

                this.set_status(2, "");
                this.set_status(3, "");
                this.set_status(4, "");
                System.out.println("Actually Retrying connection");
                this.remconsObj.retry_connection_flag = true;
            }

        }

        System.out.println("Completed receiver run");
    }

    public void change_key() {
        this.RC4decrypter.update_key();
    }

    void focusTraversalKeysDisable(Object var1) {
        Class[] var2 = new Class[]{Boolean.TYPE};
        Object[] var3 = new Object[]{Boolean.TRUE};
        Object[] var4 = new Object[]{Boolean.FALSE};

        try {
            var1.getClass().getMethod("setFocusTraversalKeysEnabled", var2).invoke(var1, var4);
        } catch (Throwable var7) {
        }

        try {
            var1.getClass().getMethod("setFocusCycleRoot", var2).invoke(var1, var3);
        } catch (Throwable var6) {
        }

    }

    public void stop_rdp() {
        if (this.rdpProc != null) {
            try {
                this.rdpProc.exitValue();
            } catch (IllegalThreadStateException var2) {
                System.out.println("IllegalThreadStateException thrown. Destroying TS.");
                this.rdpProc.destroy();
            }

            this.rdpProc = null;
        }

        System.out.println("TS stop.");
    }

    public void seize() {
        System.out.println("Received seize command. halting RC.");
        this.seized = true;
        this.screen.clearScreen();
        this.set_status(1, this.getLocalString(12306));
        this.set_status(2, "");
        this.set_status(3, "");
        this.set_status(4, "");
    }

    public void fwUpgrade() {
        System.out.println("Received FW Upgrade notification. Halting RC.");
        this.seized = true;
        this.screen.clearScreen();
        this.set_status(1, this.getLocalString(12307));
        this.set_status(2, "");
        this.set_status(3, "");
        this.set_status(4, "");
    }

    public void UnlicensedAccess() {
        System.out.println("Received UnlicensedAccess. Halting RC.");
        this.seized = true;
        this.screen.clearScreen();
        this.set_status(1, this.getLocalString(8236));
        this.set_status(2, "");
        this.set_status(3, "");
        this.set_status(4, "");
    }

    public void unAuthAccess() {
        System.out.println("Received unAuthAccess notification. Halting RC.");
        this.seized = true;
        this.screen.clearScreen();
        this.set_status(1, this.getLocalString(12308));
        this.set_status(2, "");
        this.set_status(3, "");
        this.set_status(4, "");
    }

    public String percent_sub(String var1) {
        StringBuffer var4 = new StringBuffer();

        for (int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (var3 == '%') {
                ++var2;
                var3 = var1.charAt(var2);
                if (var3 == 'h') {
                    var4.append(this.host);
                } else if (var3 == 'p') {
                    var4.append(this.terminalServicesPort);
                } else {
                    var4.append(var3);
                }
            } else {
                var4.append(var3);
            }
        }

        return var4.toString();
    }

    public byte[] getSessionKey() {
        String var1 = "0123456789abcdef0123456789abcdef";
        byte[] var2 = var1.getBytes();
        return var2;
    }

    public byte[] getSessionKey(String var1) {
        String var2 = this.parseParameter(var1, "sessionKey");
        if (var2 == "") {
            System.out.println("Parsing failed.");
        }

        byte[] var3 = var2.getBytes();
        System.out.println("sessionKey : " + var2);
        return var3;
    }

    public void sendHidKeyCode(KeyEvent var1) {
        byte[] var2 = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean var3 = false;
        boolean var4 = false;
        boolean var7 = false;
        int var10 = var1.getKeyCode();
        int var11 = this.keyMap[var10];
        this.keyMap[var10] = 1;
        if (var11 != this.keyMap[var10]) {
            int var5 = 0;

            for (int var6 = 0; var5 < 256; ++var5) {
                if (this.keyMap[var5] == 1) {
                    byte var9 = (byte) this.winkey_to_hid[var5];
                    var2[4 + var6] = var9;
                    ++var6;
                    if (var6 == 6) {
                        var6 = 5;
                    }
                }
            }
        }

        String var8 = new String(var2);
        this.transmit(var8);
        var1.consume();
    }

    public void sendHidSpecialKeyCode(KeyEvent var1) {
        byte[] var2 = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean var3 = false;
        boolean var4 = false;
        boolean var7 = false;
        char var10 = var1.getKeyChar();
        int var11 = this.keyMap[var10];
        this.keyMap[var10] = 1;
        if (var11 != this.keyMap[var10]) {
            int var5 = 0;

            for (int var6 = 0; var5 < 256; ++var5) {
                if (this.keyMap[var5] == 1) {
                    byte var9 = (byte) this.winkey_to_hid[var5];
                    var2[4 + var6] = var9;
                    ++var6;
                    if (var6 == 6) {
                        var6 = 5;
                    }
                }
            }
        }

        String var8 = new String(var2);
        this.transmit(var8);
        var1.consume();
    }

    public void clearKeyPress(KeyEvent var1) {
        byte[] var2 = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int var3 = var1.getKeyCode();
        this.keyMap[var3] = 0;
        String var4 = new String(var2);
        this.transmit(var4);
        var1.consume();
    }

    public void sendCtrlAltDel() {
        byte[] var1 = new byte[]{1, 0, 5, 0, 76, 0, 0, 0, 0, 0};
        String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.currentThread();
            Thread.sleep(500L);
        } catch (InterruptedException var4) {
            System.out.println("Thread interrupted..");
        }

        var1[2] = 0;
        var1[4] = 0;
        String var3 = new String(var1);
        this.transmit(var3);
    }

    public void sendPower(int var1) {
        byte[] var2 = new byte[]{0, 0, 0, 0};
        switch (var1) {
            case 0:
                var2[2] = 0;
                break;
            case 1:
                var2[2] = 1;
                break;
            case 2:
                var2[2] = 2;
                break;
            case 3:
                var2[2] = 3;
        }

        var2[3] = 0;
        String var3 = new String(var2);
        this.transmit(var3);
    }

    public synchronized void sendKey(KeyEvent var1, int var2) {
        if (!this.remconsObj.kbHookInstalled || !this.remconsObj.kbHookDataRcvd) {
            this.handleKey(var1, var2);
        }

    }

    public void handleKey(KeyEvent var1, int var2) {
        byte[] var3 = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean var4 = false;
        boolean var7 = false;
        boolean var8 = false;
        boolean var9 = false;
        int var10 = 0;
        int var13 = var1.getKeyCode();
        if (this.japanese_kbd == 1 && (var13 == 92 || var13 == 91 || var13 == 93 || var13 == 513)) {
            switch (var13) {
                case 91:
                    var13 = 194;
                    break;
                case 92:
                    if ('_' == var1.getKeyChar()) {
                        var13 = 198;
                    } else {
                        var13 = 195;
                    }
                    break;
                case 93:
                    var13 = 196;
                    break;
                case 513:
                    var13 = 197;
            }
        } else if (var13 > 255) {
            switch (var13) {
                case 259:
                    var13 = 241;
                    break;
                case 260:
                    var13 = 242;
                    break;
                case 512:
                    var13 = 91;
                    break;
                case 513:
                    var13 = 93;
                    break;
                case 514:
                    var13 = 61;
                    break;
                case 515:
                    var13 = 52;
                    break;
                case 517:
                    var13 = 49;
                    break;
                case 519:
                    var13 = 57;
                    break;
                case 520:
                    var13 = 51;
                    break;
                case 521:
                    var13 = 61;
                    break;
                case 522:
                    var13 = 48;
                    break;
                case 523:
                    var13 = 45;
                    break;
                default:
                    System.out.println("Unknown key " + var13);
                    var13 = 0;
            }
        }

        if (var13 != 0) {
            if (var2 == 1) {
                var9 = false;
            } else {
                if (var2 == 0) {
                    this.keyMap[var13] = 1;
                } else {
                    if (9 == var13 && 0 == this.keyMap[var13]) {
                        return;
                    }

                    this.keyMap[var13] = 0;
                    if (var1.isAltDown() && var13 == 154) {
                        this.sendAltSysReq();
                    }
                }

                if (!var1.isAltDown() && 0 != this.keyMap[18]) {
                    this.keyMap[18] = 0;
                }

                if (this.isSpecialReleaseKey(var13)) {
                    this.keyMap[var13] = 1;
                }

                int var5 = 0;

                int var6;
                for (var6 = 0; var5 < 256; ++var5) {
                    if (this.keyMap[var5] == 1) {
                        int var11 = (byte) this.winkey_to_hid[var5];
                        if (var11 == 224) {
                            var10 |= 1;
                        }

                        if (var11 == 226) {
                            var10 |= 2;
                        }

                        if (var11 == 76) {
                            var10 |= 4;
                        }

                        if (((byte) var11 & 224) == 224) {
                            var11 = (byte) var11 ^ 224;
                            var3[2] |= (byte) (1 << (byte) var11);
                        } else {
                            var3[4 + var6] = (byte) var11;
                            ++var6;
                            if (var6 == 6) {
                                var6 = 5;
                            }
                        }
                    }
                }

                if (var10 == 7) {
                    for (var5 = 0; var5 < 256; ++var5) {
                        this.keyMap[var5] = 0;
                    }
                } else {
                    this.transmitb(var3, var3.length);
                    if (this.isSpecialReleaseKey(var13)) {
                        this.keyMap[var13] = 0;
                        var3[4] = var3[5] = var3[6] = var3[7] = var3[8] = var3[9] = 0;
                        var5 = 0;

                        for (var6 = 0; var5 < 256; ++var5) {
                            if (this.keyMap[var5] == 1) {
                                byte var12 = (byte) this.winkey_to_hid[var5];
                                var3[4 + var6] = (byte) var12;
                                ++var6;
                                if (var6 == 6) {
                                    var6 = 5;
                                }
                            }
                        }

                        this.transmitb(var3, var3.length);
                    }
                }
            }
        }

        var1.consume();
    }

    public boolean isSpecialReleaseKey(int var1) {
        boolean var2 = false;
        switch (var1) {
            case 28:
            case 29:
            case 240:
            case 243:
            case 244:
                var2 = true;
            default:
                return var2;
        }
    }

    public void printByteArray(byte[] var1, int var2) {
        if (var2 >= 0) {
            for (int var3 = 0; var3 < var2; ++var3) {
                System.out.print("0x" + Integer.toHexString(var1[var3]) + " ");
            }

            System.out.println("\n");
        }
    }

    public String parseParameter(String var1, String var2) {
        String var3 = "[&]";
        String var4 = "[=]";
        String var5 = "";
        System.out.println("Invoking url's query: " + var1);
        if (var1 == null) {
            return var5;
        } else {
            String[] var6 = var1.split(var3);

            for (int var7 = 0; var7 < var6.length; ++var7) {
                String[] var8 = var6[var7].split(var4);
                if (var8[0] == var2) {
                    var5 = var8[1];
                    break;
                }
            }

            return var5;
        }
    }

    public void sendAltSysReq() {
        byte[] var1 = new byte[]{1, 0, 4, 0, 70, 0, 0, 0, 0, 0};
        this.remconsObj.session.transmitb(var1, var1.length);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (Exception var3) {
            System.out.println("sendAltSysReq: Failed wait");
        }

        var1[4] = 0;
        this.remconsObj.session.transmitb(var1, var1.length);
    }

    class statusUpdateTimer implements TimerListener {
        statusUpdateTimer() {
        }

        public void timeout(Object var1) {
            System.out.println("Video data reception timeout occurred. Clearing status.");
            telnet.this.set_status(1, " ");
        }
    }
}
