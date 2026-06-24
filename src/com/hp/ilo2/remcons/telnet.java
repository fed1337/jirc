package com.hp.ilo2.remcons;


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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public static final int PWR_OPTION_PULSE = 0;
    public static final int PWR_OPTION_HOLD = 1;
    public static final int PWR_OPTION_CYCLE = 2;
    public static final int PWR_OPTION_RESET = 3;
    public static final int CIPHER_NONE = 0;
    public static final int CIPHER_RC4 = 1;
    public static final int CIPHER_AES128 = 2;
    public static final int CIPHER_AES256 = 3;
    public static final int AES_BITSIZE_128 = 0;
    public static final int AES_BITSIZE_192 = 1;
    public static final int AES_BITSIZE_256 = 2;
    public static final int REQ_LOGIN_KEY = 0;
    public static final int REQ_GET_AUTH = 1;
    public static final int REQ_SHARE = 2;
    public static final int REQ_SEIZE = 3;
    public static final int REQ_DONE = 4;
    public static final int CONNECT_CANCEL = 0;
    public static final int CONNECT_SEIZE = 1;
    public static final int CONNECT_SHARE = 2;
    public static final int KEY_STATE_PRESSED = 0;
    public static final int KEY_STATE_TYPED = 1;
    public static final int KEY_STATE_RELEASED = 2;
    private static final boolean crlf_enabled = false;
    private static final boolean tbm_mode = false;
    private static final int total_count = 0;
    private final int[] keyMap = new int[256];
    private final int[] winkey_to_hid = {0, 0, 0, 0, 0, 0, 0, 0, 42, 43, 40, 0, 0, 40, 0, 0, 225, 224, 226, 72, 57, 0, 0, 0, 0, 0, 41, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 54, 45, 55, 56, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 51, 0, 46, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 47, 49, 48, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 159, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 76, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 36, 37, 52, 54, 70, 73, 0, 0, 0, 0, 55, 47, 48, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, 46, 54, 45, 55, 56, 53, 135, 48, 137, 50, 52, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 49, 48, 52, 0, 96, 90, 92, 94, 0, 0, 0, 0, 0, 0, 0, 139, 0, 0, 0, 0, 136, 136, 136, 53, 53, 0, 0, 0, 0, 0, 0, 0, 0, 138, 0, 255};
    public final JLabel status_box = new JLabel();
    public boolean mirror = false;
    private byte[] sessionKey = new byte[32];
    private String st_fld1 = "";
    private String st_fld2 = "";
    private String st_fld3 = "";
    private static final int POST_CODE_PANEL_HEIGHT = 80;
    private final JTextArea postCodeTimeline = new JTextArea();
    private final JScrollPane postCodeScrollPane;
    private boolean postCodeTimelineVisible = false;
    public boolean post_complete = false;
    private int dbg_print = 0;
    private final cmd cmdObj = new cmd();
    remcons remconsObj;
    int cipher = 0;
    final dvcwin screen;
    Thread receiver = null;
    private volatile boolean running = false;
    private Socket s = null;
    private DataInputStream in = null;
    DataOutputStream out = null;
    private String login = "";
    private String host = "";
    private int port = 23;
    private int connected = 0;
    protected int fore = 0;
    protected int back = 0;
    protected int hi_fore = 0;
    protected int hi_back = 0;
    protected String escseq = null;
    protected String curr_num = null;
    protected int[] escseq_val = new int[10];
    protected int escseq_val_count = 0;
    private final byte[] decrypt_key = new byte[16];
    boolean dvc_mode = false;
    boolean dvc_encryption = false;
    int ts_type = 0;
    private final LocaleTranslator translator = new LocaleTranslator();
    private RC4 RC4decrypter = null;
    private Aes aes128decrypter = null;
    private Aes aes256decrypter = null;
    private Process rdpProc = null;
    private int terminalServicesPort = 3389;
    private boolean seized = false;
    private final int japanese_kbd;
    private boolean screenFocusLost = false;

    telnet(final remcons var1) {
        super();
        this.remconsObj = var1;
        this.screen = new dvcwin(1024, 768, this.remconsObj);
        System.out.println("Screen: " + this.screen);
        this.screen.addMouseListener(this);
        this.addFocusListener(this);
        this.screen.addFocusListener(this);
        this.screen.addKeyListener(this);
        telnet.focusTraversalKeysDisable(this.screen);
        telnet.focusTraversalKeysDisable(this);
        this.setBackground(Color.black);
        this.setLayout(new BorderLayout());
        this.add(this.screen, "North");
        this.postCodeTimeline.setEditable(false);
        this.postCodeTimeline.setFocusable(true);
        this.postCodeTimeline.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.postCodeTimeline.setBackground(Color.WHITE);
        this.postCodeTimeline.setForeground(Color.BLACK);
        this.postCodeTimeline.setCaretColor(Color.BLACK);
        this.postCodeTimeline.setRows(4);
        this.postCodeTimeline.setLineWrap(false);
        this.postCodeScrollPane = new JScrollPane(this.postCodeTimeline);
        this.postCodeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.postCodeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.postCodeScrollPane.setBackground(Color.WHITE);
        this.postCodeScrollPane.setOpaque(true);
        this.postCodeScrollPane.getViewport().setBackground(Color.WHITE);
        this.postCodeScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        this.postCodeScrollPane.setPreferredSize(new Dimension(0, telnet.POST_CODE_PANEL_HEIGHT));
        this.add(this.postCodeScrollPane, BorderLayout.SOUTH);
        this.postCodeScrollPane.setVisible(false);
        this.set_status(1, this.getLocalString(12301));
        this.set_status(2, "          ");
        this.set_status(3, "          ");
        if (System.getProperty("os.name").toLowerCase().startsWith("windows") && !this.translator.windows) {
            this.translator.selectLocale("en_US");
        }

        for (int var2 = 0; 256 > var2; ++var2) {
            this.keyMap[var2] = 0;
        }

        final Locale lo = Locale.getDefault();
        final String keyboardLayout = lo.toString();
        System.out.println("telent lang: Keyboard layout is " + keyboardLayout);
        if (keyboardLayout.startsWith("ja")) {
            System.out.println("JAPANESE LANGUAGE \n");
            this.japanese_kbd = 1;
        } else {
            this.japanese_kbd = 0;
        }

    }

    public void setLocale(final String var1) {
        this.translator.selectLocale(var1);
    }

    String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("telnet:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    void enable_debug() {
    }

    void disable_debug() {
    }

    @SuppressWarnings("deprecation")
    public void startRdp() {
        if (null == this.rdpProc) {
            final Runtime var3 = Runtime.getRuntime();
            final String var1;
            if (0 == this.ts_type) {
                var1 = "mstsc";
            } else if (1 == this.ts_type) {
                var1 = "vnc";
            } else {
                var1 = "type" + this.ts_type;
            }

            String var2 = remcons.prop.getProperty(var1 + ".program");
            System.out.println(var1 + " = " + var2);
            if (null != var2) {
                var2 = this.percent_sub(var2);
                System.out.println("exec: " + var2);

                try {
                    this.rdpProc = var3.exec(var2);
                } catch (final SecurityException var8) {
                    System.out.println("SecurityException: " + var8.getMessage() + ":: Attempting to launch " + var2);
                } catch (final IOException var9) {
                    System.out.println("IOException: " + var9.getMessage() + ":: " + var2);
                }

                return;
            }

            boolean var4 = false;

            try {
                System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);
                this.rdpProc = var3.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);
            } catch (final SecurityException var14) {
                System.out.println("SecurityException: " + var14.getMessage() + ":: Attempting to launch mstsc.");
            } catch (final IOException var15) {
                System.out.println("IOException: " + var15.getMessage() + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
                var4 = true;
            }

            String[] var5;
            if (var4) {
                var4 = false;
                var5 = new String[]{"\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort};

                try {
                    this.rdpProc = var3.exec(var5);
                } catch (final SecurityException var12) {
                    System.out.println("SecurityException: " + var12.getMessage() + ":: Attempting to launch mstsc.");
                } catch (final IOException var13) {
                    System.out.println("IOException: " + var13.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
                    var4 = true;
                }
            }

            if (var4) {
                var5 = new String[]{"\\Program Files\\Terminal Services Client\\mstsc"};

                try {
                    this.rdpProc = var3.exec(var5);
                } catch (final SecurityException var10) {
                    System.out.println("SecurityException: " + var10.getMessage() + ":: Attempting to launch mstsc.");
                } catch (final IOException var11) {
                    System.out.println("IOException: " + var11.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
                }
            }
        }

    }

    public void keyTyped(final KeyEvent var1) {
        this.sendKey(var1, 1);
    }

    public void keyPressed(final KeyEvent var1) {
        this.sendKey(var1, 0);
    }

    public void keyReleased(final KeyEvent var1) {
        this.sendKey(var1, 2);
    }

    public void send_auto_alive_msg() {
    }

    public synchronized void focusGained(final FocusEvent var1) {
        if (var1.getComponent() == this.screen) {
            if (this.screenFocusLost) {
                this.remconsObj.remconsInstallKeyboardHook();
                this.screenFocusLost = false;
            }
        } else {
            this.screen.requestFocus();
        }

    }

    public synchronized void focusLost(final FocusEvent var1) {
        if (var1.getComponent() == this.screen && var1.isTemporary()) {
            this.remconsObj.remconsUnInstallKeyboardHook();
            this.screenFocusLost = true;
        }

    }

    public synchronized void mouseClicked(final MouseEvent var1) {
        super.requestFocus();
    }

    public void mousePressed(final MouseEvent var1) {
    }

    public void mouseReleased(final MouseEvent var1) {
    }

    public void mouseEntered(final MouseEvent var1) {
    }

    public void mouseExited(final MouseEvent var1) {
    }

    public synchronized void addNotify() {
        super.addNotify();
    }

    public synchronized void set_status(final int var1, final String var2) {
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
                this.clearPostCodeTimeline();
                break;
        }

        this.status_box.setText(this.st_fld1 + " " + this.st_fld2 + "      " + this.st_fld3);
    }

    public synchronized void appendPostCodeEntry(final String hexCode) {
        final String line = String.format("[%s] %s%n",
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                hexCode);
        this.postCodeTimeline.append(line);
        this.postCodeTimeline.setCaretPosition(this.postCodeTimeline.getDocument().getLength());
    }

    public synchronized void clearPostCodeTimeline() {
        this.postCodeTimeline.setText("");
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension screenSize = this.screen.getPreferredSize();
        int height = screenSize.height;
        if (this.postCodeTimelineVisible) {
            height += telnet.POST_CODE_PANEL_HEIGHT;
        }

        return new Dimension(screenSize.width, height);
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }

    public void setPostCodeTimelineVisible(final boolean visible) {
        if (visible == this.postCodeTimelineVisible) {
            return;
        }

        this.postCodeTimelineVisible = visible;
        this.postCodeScrollPane.setVisible(visible);
        SwingUtilities.invokeLater(() -> {
            this.invalidate();
            this.revalidate();
            this.repaint();

            Container parent = this.getParent();
            while (null != parent) {
                parent.invalidate();
                parent.revalidate();
                parent = parent.getParent();
            }

            if (null != this.remconsObj && null != this.remconsObj.ParentApp && null != this.remconsObj.ParentApp.dispFrame) {
                final JFrame frame = this.remconsObj.ParentApp.dispFrame;
                final int width = frame.getWidth();
                final Point location = frame.getLocation();
                frame.invalidate();
                frame.validate();
                frame.pack();
                frame.setSize(width, frame.getHeight());
                frame.setLocation(location);
            }
        });
    }

    public boolean isPostCodeTimelineVisible() {
        return this.postCodeTimelineVisible;
    }

    void reinit_vars() {
        this.dvc_encryption = false;
    }

    public void setup_decryption(final byte[] var1) {
        System.arraycopy(var1, 0, this.decrypt_key, 0, 16);
        this.RC4decrypter = new RC4(var1);
        final boolean encryption_enabled = true;
        this.aes128decrypter = new Aes(0, var1);
        this.aes256decrypter = new Aes(0, var1);
    }

    synchronized void connect(final String var1, final String var2, final int var3, final int var4, final int var5, final remcons var6) {
        final boolean enable_terminal_services = 1 == (var4 & 1);
        this.ts_type = var4 >> 8;
        if (0 != var5) {
            this.terminalServicesPort = var5;
        }

        if (0 == this.connected) {
            this.screen.start_updates();
            this.connected = 1;
            this.host = var1;
            this.login = var2;
            this.port = var3;
            this.remconsObj = var6;
            this.requestFocus();
            this.sessionKey = var6.ParentApp.getParameter("RCINFO1").getBytes();
            final String var7 = var6.ParentApp.rc_port;
            if (null != var7) {
                try {
                    this.port = Integer.parseInt(var7);
                    System.out.println("RC port number " + this.port);
                } catch (final NumberFormatException var16) {
                    System.out.println("Failed to read rcport from parameters");
                    this.port = 23;
                }
            }

            try {
                this.set_status(1, this.getLocalString(12296));
                System.out.println("updated: connecting to " + this.host + ":" + this.port);

                try {
                    Thread.sleep(1000L);
                } catch (final InterruptedException var12) {
                    System.out.println("connect Thread interrupted..");
                }

                this.s = new Socket(this.host, this.port);

                try {
                    this.s.setSoLinger(true, 0);
                    System.out.println("set TcpNoDelay");
                    this.s.setTcpNoDelay(true);
                } catch (final SocketException var11) {
                    System.out.println("telnet.connect() linger SocketException: " + var11);
                }

                this.in = new DataInputStream(this.s.getInputStream());
                this.out = new DataOutputStream(this.s.getOutputStream());
                final byte var8 = this.in.readByte();
                if (80 == (int) var8) {
                    this.set_status(1, this.getLocalString(12297));
                    final boolean var9;
                    System.out.println("Received hello byte. Requesting remote connection...");
                    final short var10 = (short) 8193;
                    var9 = this.requestRemoteConnection();
                    if (var9) {
                        this.running = true;
                        this.receiver = new Thread(this);
                        this.receiver.setName("telnet_rcvr");
                        this.receiver.start();
                        this.cmdObj.connectCmd(this.remconsObj, this.host, this.port);
                    } else {
                        var6.ParentApp.requestClose("Could not establish the remote console connection.");
                    }
                } else {
                    this.set_status(1, this.getLocalString(12298));
                    System.out.println("Socket connection failure... ");
                }
            } catch (final SocketException var13) {
                System.out.println("telnet.connect() SocketException: " + var13);
                this.set_status(1, var13.toString());
                this.s = null;
                this.in = null;
                this.out = null;
                this.receiver = null;
                this.connected = 0;
            } catch (final UnknownHostException var14) {
                System.out.println("telnet.connect() UnknownHostException: " + var14);
                this.set_status(1, var14.toString());
                this.s = null;
                this.in = null;
                this.out = null;
                this.receiver = null;
                this.connected = 0;
            } catch (final IOException var15) {
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

    private boolean requestRemoteConnection() {
        boolean var2 = false;
        byte var3 = (byte) 0;
        final byte[] var4 = new byte[2];

        while (true) {
            while (4 != (int) var3) {
                final byte var16;
                switch (var3) {
                    case 0:
                        var4[0] = (byte) (8193 & 255);
                        var4[1] = (byte) ((8193 & (int) '\uff00') >>> 8);
                        if (this.remconsObj.ParentApp.optional_features.contains("ENCRYPT_KEY")) {
                            for (int var6 = 0; var6 < this.sessionKey.length; ++var6) {
                                final byte[] var10000 = this.sessionKey;
                                var10000[var6] = (byte) ((int) var10000[var6] ^ (int) (byte) this.remconsObj.ParentApp.enc_key.charAt(var6 % this.remconsObj.ParentApp.enc_key.length()));
                            }

                            if (this.remconsObj.ParentApp.optional_features.contains("ENCRYPT_VMKEY")) {
                                var4[1] = (byte) ((int) var4[1] | 64);
                            } else {
                                var4[1] = (byte) ((int) var4[1] | 128);
                            }
                        }

                        final byte[] var17 = new byte[var4.length + this.sessionKey.length];
                        System.arraycopy(var4, 0, var17, 0, var4.length);
                        System.arraycopy(this.sessionKey, 0, var17, var4.length, this.sessionKey.length);
                        final String var7 = new String(var17);
                        this.transmit(var7);
                        var3 = (byte) 1;
                        break;
                    case 1:
                        try {
                            var16 = this.in.readByte();
                        } catch (final IOException var14) {
                            var3 = (byte) 4;
                            System.out.println("Socket Read failed.");
                            break;
                        }

                        switch (var16) {
                            case 81:
                                System.out.println("Access denied.");
                                this.set_status(1, this.getLocalString(12299));
                                this.remconsObj.ParentApp.requestClose(this.getLocalString(8287));
                                var3 = (byte) 4;
                                continue;
                            case 82:
                                this.set_status(1, this.getLocalString(12300));
                                System.out.println("Authenticated");
                                var2 = true;
                                this.remconsObj.licensed = true;
                                var3 = (byte) 4;
                                continue;
                            case 83:
                            case 89:
                                System.out.println("Authenticated, but busy, negotiating");
                                final int var8;
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
                                        this.remconsObj.ParentApp.requestClose(null);
                                        var3 = (byte) 4;
                                        continue;
                                    case 1:
                                        var4[0] = (byte) 85;
                                        var4[1] = (byte) 0;
                                        final byte[] var9 = new byte[var4.length];
                                        System.arraycopy(var4, 0, var9, 0, var4.length);
                                        System.out.println("Seizing connection, sending command 0x0055");
                                        final String var10 = new String(var9);
                                        this.transmit(var10);
                                        var3 = (byte) 3;
                                        this.set_status(1, this.getLocalString(12568));
                                        continue;
                                    case 2:
                                        var4[0] = (byte) 86;
                                        var4[1] = (byte) 0;
                                        System.out.println("Sharing connection, sending command 0x0056");
                                        final byte[] var11 = new byte[var4.length];
                                        System.arraycopy(var4, 0, var11, 0, var4.length);
                                        final String var12 = new String(var11);
                                        this.transmit(var12);
                                        var3 = (byte) 2;
                                    default:
                                        continue;
                                }
                            case 84:
                            case 85:
                            case 86:
                            default:
                                System.out.println("rqrmtconn default: " + var16);
                                var2 = true;
                                var3 = (byte) 4;
                                continue;
                            case 87:
                                System.out.println("Received No License Notification");
                                this.remconsObj.licensed = false;
                                var3 = (byte) 4;
                                continue;
                            case 88:
                                System.out.println("No free Sessions Notification");
                                this.remconsObj.ParentApp.requestClose(this.getLocalString(8240));
                                var3 = (byte) 4;
                                continue;
                        }
                    case 2:
                        var3 = (byte) 4;
                        break;
                    case 3:
                        var3 = (byte) 4;

                        try {
                            var16 = this.in.readByte();
                        } catch (final IOException var15) {
                            System.out.println("Socket Read failed.");
                            continue;
                        }

                        switch (var16) {
                            case 81:
                                this.remconsObj.ParentApp.requestClose(this.getLocalString(8263));
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

    private int negotiateBusy() {
        byte var1 = (byte) 0;
        this.remconsObj.ParentApp.moveUItoInit(false);
        final VSeizeDialog var2 = new VSeizeDialog(this.remconsObj);
        switch (var2.getUserInput()) {
            case 0:
                break;
            case 2:
                var1 = (byte) 1;
        }

        return (int) var1;
    }

    public void connect(final String var1, final String var2, final int var3, final int var4, final remcons var5) {
        this.connect(var1, var2, this.port, var3, var4, var5);
    }

    public void connect(final String var1, final int var2, final int var3, final remcons var4) {
        this.connect(var1, this.login, this.port, var2, var3, var4);
    }

    public synchronized void disconnect() {
        this.remconsObj.remconsUnInstallKeyboardHook();
        if (1 == this.connected) {
            this.screen.stop_updates();
            this.connected = 0;
            // Cooperative shutdown: stop the loop, close the socket to unblock the
            // read, then wait briefly for the receiver thread to finish.
            this.running = false;
            final Thread var3 = this.receiver;
            this.receiver = null;
            if (null != this.s) {
                try {
                    System.out.println("Closing socket");
                    this.s.close();
                } catch (final IOException var2) {
                    System.out.println("telnet.disconnect() IOException: " + var2);
                    this.set_status(1, var2.toString());
                }
            }

            if (null != var3 && var3 != Thread.currentThread()) {
                try {
                    var3.join(2000L);
                } catch (final InterruptedException var4) {
                    Thread.currentThread().interrupt();
                }
            }

            this.s = null;
            this.in = null;
            this.out = null;
            if (null != this.cmdObj) {
                this.cmdObj.disconnectCmd();
            }

            this.set_status(1, this.getLocalString(12301));
            this.reinit_vars();
            final boolean decryption_active = false;
        }

    }

    synchronized void transmit(final String var1) {
        if (null != this.out) {
            if (!var1.isEmpty()) {
                final byte[] var2 = new byte[var1.length()];

                for (int var3 = 0; var3 < var1.length(); ++var3) {
                    var2[var3] = (byte) var1.charAt(var3);
                }

                try {
                    this.out.write(var2, 0, var2.length);
                } catch (final IOException var5) {
                    System.out.println("telnet.transmit() IOException: " + var5);
                }
            }

        }
    }

    void transmitb(final byte[] var1, final int var2) {
    }

    synchronized String translate_key(final KeyEvent var1) {
        final char var3 = var1.getKeyChar();
        final String var2;
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

    synchronized String translate_special_key(final KeyEvent var1) {
        String var2 = "";
        if (var1.getKeyCode() == 9) {
            var1.consume();
            var2 = "\t";
        }
        return var2;
    }

    protected synchronized String translate_special_key_release(final KeyEvent var1) {
        return "";
    }

    boolean process_dvc(final char var1) {
        return true;
    }

    public void run() {
        byte var3 = (byte) 0;
        final byte[] var6 = new byte[1024];
        int var10 = 0;
        this.dvc_mode = true;
        System.out.println("Starting receiver run");

        try {
            while (this.running) {
                int var7;
                try {
                    if (null == this.s || null == this.in) {
                        System.out.println("telnet.run() s or in is null");
                        break;
                    }

                    this.s.setSoTimeout(1000);
                    var7 = this.in.read(var6);
                } catch (final InterruptedIOException var18) {
                    continue;
                } catch (final Exception var19) {
                    var7 = -1;
                    ++var10;
                }

                if (0 > var7) {
                    if (1 < var10) {
                        System.out.println("Reading from stream failed for  " + var10 + "times");
                        break;
                    }
                } else {
                    for (int var8 = 0; var8 < var7; ++var8) {
                        if (1000 == this.dbg_print) {
                            this.dbg_print = 0;
                        }

                        ++this.dbg_print;
                        this.remconsObj.fdConnState = this.remconsObj.ParentApp.virtdevsObj.fdConnected;
                        this.remconsObj.cdConnState = this.remconsObj.ParentApp.virtdevsObj.cdConnected;
                        char var1 = (char) var6[var8];
                        var1 = (char) ((int) var1 & 255);
                        if (this.dvc_mode) {
                            if (this.dvc_encryption) {
                                final char var9;
                                switch (this.cipher) {
                                    case 1:
                                        var9 = (char) (this.RC4decrypter.randomValue() & 255);
                                        var1 = (char) ((int) var1 ^ (int) var9);
                                        break;
                                    case 2:
                                        var9 = (char) ((int) this.aes128decrypter.randomValue() & 255);
                                        var1 = (char) ((int) var1 ^ (int) var9);
                                        break;
                                    case 3:
                                        var9 = (char) ((int) this.aes256decrypter.randomValue() & 255);
                                        var1 = (char) ((int) var1 ^ (int) var9);
                                        break;
                                    default:
                                        System.out.println("Unknown encryption");
                                }

                                var1 = (char) ((int) var1 & 255);
                            }

                            this.dvc_mode = this.process_dvc(var1);
                            if (!this.dvc_mode) {
                                System.out.println("DVC mode turned off");
                                this.set_status(1, this.getLocalString(12302));
                            }
                        } else if (27 == (int) var1) {
                            var3 = (byte) 1;
                        } else if (1 == (int) var3 && (int) '[' == (int) var1) {
                            var3 = (byte) 2;
                        } else if (2 == (int) var3 && (int) 'R' == (int) var1) {
                            this.dvc_mode = true;
                            this.dvc_encryption = true;
                            this.set_status(1, this.getLocalString(12303));
                        } else if (2 == (int) var3 && (int) 'r' == (int) var1) {
                            this.dvc_mode = true;
                            this.dvc_encryption = false;
                            this.set_status(1, this.getLocalString(12292));
                        } else {
                            var3 = (byte) 0;
                        }
                    }
                }
            }
        } catch (final Exception var20) {
            System.out.println("telnet.run() Exception, class:" + var20.getClass() + "  msg:" + var20.getMessage());
            var20.printStackTrace();
        } finally {
            if (!this.seized) {
                if (3 > this.remconsObj.retry_connection_count) {
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

    void change_key() {
        this.RC4decrypter.update_key();
    }

    private static void focusTraversalKeysDisable(final java.awt.Container var1) {
        var1.setFocusTraversalKeysEnabled(false);
        var1.setFocusCycleRoot(true);
    }

    public void stop_rdp() {
        if (null != this.rdpProc) {
            try {
                this.rdpProc.exitValue();
            } catch (final IllegalThreadStateException var2) {
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

    private String percent_sub(final CharSequence var1) {
        final StringBuilder var4 = new StringBuilder();

        for (int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if ((int) '%' == (int) var3) {
                ++var2;
                var3 = var1.charAt(var2);
                if ((int) 'h' == (int) var3) {
                    var4.append(this.host);
                } else if ((int) 'p' == (int) var3) {
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

    public static byte[] getSessionKey() {
        final String var1 = "0123456789abcdef0123456789abcdef";
        return var1.getBytes();
    }

    public static byte[] getSessionKey(final String var1) {
        final String var2 = telnet.parseParameter(var1);
        if (var2.isEmpty()) {
            System.out.println("Parsing failed.");
        }

        final byte[] var3 = var2.getBytes();
        System.out.println("sessionKey : " + var2);
        return var3;
    }

    public void sendHidKeyCode(final KeyEvent var1) {
        final byte[] var2 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final int var10 = var1.getKeyCode();
        final int var11 = this.keyMap[var10];
        this.keyMap[var10] = 1;
        if (var11 != this.keyMap[var10]) {
            int var5 = 0;

            for (int var6 = 0; 256 > var5; ++var5) {
                if (1 == this.keyMap[var5]) {
                    final byte var9 = (byte) this.winkey_to_hid[var5];
                    var2[4 + var6] = var9;
                    ++var6;
                    if (6 == var6) {
                        var6 = 5;
                    }
                }
            }
        }

        final String var8 = new String(var2);
        this.transmit(var8);
        var1.consume();
    }

    public void sendHidSpecialKeyCode(final KeyEvent var1) {
        final byte[] var2 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final char var10 = var1.getKeyChar();
        final int var11 = this.keyMap[(int) var10];
        this.keyMap[(int) var10] = 1;
        if (var11 != this.keyMap[(int) var10]) {
            int var5 = 0;

            for (int var6 = 0; 256 > var5; ++var5) {
                if (1 == this.keyMap[var5]) {
                    final byte var9 = (byte) this.winkey_to_hid[var5];
                    var2[4 + var6] = var9;
                    ++var6;
                    if (6 == var6) {
                        var6 = 5;
                    }
                }
            }
        }

        final String var8 = new String(var2);
        this.transmit(var8);
        var1.consume();
    }

    public void clearKeyPress(final KeyEvent var1) {
        final byte[] var2 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final int var3 = var1.getKeyCode();
        this.keyMap[var3] = 0;
        final String var4 = new String(var2);
        this.transmit(var4);
        var1.consume();
    }

    public void sendCtrlAltDel() {
        final byte[] var1 = {(byte) 1, (byte) 0, (byte) 5, (byte) 0, (byte) 76, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.sleep(500L);
        } catch (final InterruptedException var4) {
            System.out.println("Thread interrupted..");
        }

        var1[2] = (byte) 0;
        var1[4] = (byte) 0;
        final String var3 = new String(var1);
        this.transmit(var3);
    }

    public void sendPower(final int var1) {
        final byte[] var2 = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        switch (var1) {
            case 0:
                var2[2] = (byte) 0;
                break;
            case 1:
                var2[2] = (byte) 1;
                break;
            case 2:
                var2[2] = (byte) 2;
                break;
            case 3:
                var2[2] = (byte) 3;
        }

        var2[3] = (byte) 0;
        final String var3 = new String(var2);
        this.transmit(var3);
    }

    private synchronized void sendKey(final KeyEvent var1, final int var2) {
        if (!this.remconsObj.kbHookInstalled || !this.remconsObj.kbHookDataRcvd) {
            this.handleKey(var1, var2);
        }

    }

    private void handleKey(final KeyEvent var1, final int var2) {
        final byte[] var3 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        int var10 = 0;
        int var13 = var1.getKeyCode();
        if (1 == this.japanese_kbd && (92 == var13 || 91 == var13 || 93 == var13 || 513 == var13)) {
            switch (var13) {
                case 91:
                    var13 = 194;
                    break;
                case 92:
                    if ((int) '_' == (int) var1.getKeyChar()) {
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
        } else if (255 < var13) {
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

        if (0 != var13) {
            if (1 != var2) {
                if (0 == var2) {
                    this.keyMap[var13] = 1;
                } else {
                    if (9 == var13 && 0 == this.keyMap[var13]) {
                        return;
                    }

                    this.keyMap[var13] = 0;
                    if (var1.isAltDown() && 154 == var13) {
                        this.sendAltSysReq();
                    }
                }

                if (!var1.isAltDown() && 0 != this.keyMap[18]) {
                    this.keyMap[18] = 0;
                }

                if (telnet.isSpecialReleaseKey(var13)) {
                    this.keyMap[var13] = 1;
                }

                int var5 = 0;

                int var6;
                for (var6 = 0; 256 > var5; ++var5) {
                    if (1 == this.keyMap[var5]) {
                        int var11 = (int) (byte) this.winkey_to_hid[var5];

                        if (76 == var11) {
                            var10 |= 4;
                        }

                        if (224 == ((int) (byte) var11 & 224)) {
                            var11 = (int) (byte) var11 ^ 224;
                            var3[2] = (byte) ((int) var3[2] | (int) (byte) (1 << (int) (byte) var11));
                        } else {
                            var3[4 + var6] = (byte) var11;
                            ++var6;
                            if (6 == var6) {
                                var6 = 5;
                            }
                        }
                    }
                }

                this.transmitb(var3, var3.length);
                if (telnet.isSpecialReleaseKey(var13)) {
                    this.keyMap[var13] = 0;
                    var3[4] = var3[5] = var3[6] = var3[7] = var3[8] = var3[9] = (byte) 0;
                    var5 = 0;

                    for (var6 = 0; 256 > var5; ++var5) {
                        if (1 == this.keyMap[var5]) {
                            final byte var12 = (byte) this.winkey_to_hid[var5];
                            var3[4 + var6] = var12;
                            ++var6;
                            if (6 == var6) {
                                var6 = 5;
                            }
                        }
                    }

                    this.transmitb(var3, var3.length);
                }
            }
        }

        var1.consume();
    }

    private static boolean isSpecialReleaseKey(final int var1) {
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

    public static void printByteArray(final byte[] var1, final int var2) {
        if (0 <= var2) {
            for (int var3 = 0; var3 < var2; ++var3) {
                System.out.print("0x" + Integer.toHexString((int) var1[var3]) + " ");
            }

            System.out.println("\n");
        }
    }

    private static String parseParameter(final String var1) {
        final String var3 = "[&]";
        final String var4 = "[=]";
        String var5 = "";
        System.out.println("Invoking url's query: " + var1);
        if (null == var1) {
            return var5;
        }

        final String[] var6 = var1.split(var3);

        for (final String string : var6) {
            final String[] var8 = string.split(var4);
            if ("sessionKey".equals(var8[0])) {
                var5 = var8[1];
                break;
            }
        }

        return var5;
    }

    private void sendAltSysReq() {
        final byte[] var1 = {(byte) 1, (byte) 0, (byte) 4, (byte) 0, (byte) 70, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        this.remconsObj.session.transmitb(var1, var1.length);

        try {
            Thread.sleep(250L);
        } catch (final Exception var3) {
            System.out.println("sendAltSysReq: Failed wait");
        }

        var1[4] = (byte) 0;
        this.remconsObj.session.transmitb(var1, var1.length);
    }

    class statusUpdateTimer implements TimerListener {

        public void timeout(final Object var1) {
            System.out.println("Video data reception timeout occurred. Clearing status.");
            telnet.this.set_status(1, " ");
        }
    }
}
