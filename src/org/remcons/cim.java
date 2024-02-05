package org.remcons;


import org.virtdevs.VErrorDialog;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.lang.reflect.Method;

public class cim extends telnet implements MouseSyncListener {
    public static final int MOUSE_BUTTON_LEFT = 4;
    public static final int MOUSE_BUTTON_CENTER = 2;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    static final int CMD_ENCRYPT = 192;
    private static final int CMD_MOUSE_MOVE = 208;
    private static final int CMD_BUTTON_PRESS = 209;
    private static final int CMD_BUTTON_RELEASE = 210;
    private static final int CMD_BUTTON_CLICK = 211;
    private static final int CMD_BYTE = 212;
    private static final int CMD_SET_MODE = 213;
    private static final char MOUSE_USBABS = '\u0001';
    private static final char MOUSE_USBREL = '\u0002';
    private static final int block_width = 16;
    private static final int block_height = 16;
    private static final int RESET = 0;
    private static final int START = 1;
    private static final int PIXELS = 2;
    private static final int PIXLRU1 = 3;
    private static final int PIXLRU0 = 4;
    private static final int PIXCODE1 = 5;
    private static final int PIXCODE2 = 6;
    private static final int PIXCODE3 = 7;
    private static final int PIXGREY = 8;
    private static final int PIXRGBR = 9;
    private static final int PIXRPT = 10;
    private static final int PIXRPT1 = 11;
    private static final int PIXRPTSTD1 = 12;
    private static final int PIXRPTSTD2 = 13;
    private static final int PIXRPTNSTD = 14;
    private static final int CMD = 15;
    private static final int CMD0 = 16;
    private static final int MOVEXY0 = 17;
    private static final int EXTCMD = 18;
    private static final int CMDX = 19;
    private static final int MOVESHORTX = 20;
    private static final int MOVELONGX = 21;
    private static final int BLKRPT = 22;
    private static final int EXTCMD1 = 23;
    private static final int FIRMWARE = 24;
    private static final int EXTCMD2 = 25;
    private static final int MODE0 = 26;
    private static final int TIMEOUT = 27;
    private static final int BLKRPT1 = 28;
    private static final int BLKRPTSTD = 29;
    private static final int BLKRPTNSTD = 30;
    private static final int PIXFAN = 31;
    private static final int PIXCODE4 = 32;
    private static final int PIXDUP = 33;
    private static final int BLKDUP = 34;
    private static final int PIXCODE = 35;
    private static final int PIXSPEC = 36;
    private static final int EXIT = 37;
    private static final int LATCHED = 38;
    private static final int MOVEXY1 = 39;
    private static final int MODE1 = 40;
    private static final int PIXRGBG = 41;
    private static final int PIXRGBB = 42;
    private static final int HUNT = 43;
    private static final int PRINT0 = 44;
    private static final int PRINT1 = 45;
    private static final int CORP = 46;
    private static final int MODE2 = 47;
    private static final int SIZE_OF_ALL = 48;
    private static final int B = -16777216;
    private static final int W = -8355712;
    private static final byte[] cursor_none = new byte[]{0};
    private static final int[] cursor_outline = new int[]{-8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712, -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0};
    private static final int[] bits_to_read = new int[]{0, 1, 1, 1, 1, 1, 2, 3, 5, 5, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1, 1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 5, 5, 1, 8, 8, 1, 4};
    private static final int[] next_0 = new int[]{1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1};
    private static final int[] next_1 = new int[]{1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1};
    private static final int[] dvc_cc_color = new int[17];
    private static final int[] dvc_cc_usage = new int[17];
    private static final int[] dvc_cc_block = new int[17];
    private static final int[] dvc_lru_lengths = new int[]{0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};
    private static final int[] dvc_getmask = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255};
    private static final int[] dvc_reversal = new int[256];
    private static final int[] dvc_left = new int[256];
    private static final int[] dvc_right = new int[256];
    private static final int[] block = new int[256];
    private static final int[] cmd_p_buff = new int[256];
    private static final char last_bits = 0;
    private static final char last_bits2 = 0;
    private static final char last_bits3 = 0;
    private static final char last_bits4 = 0;
    private static final char last_bits5 = 0;
    private static final char last_bits6 = 0;
    private static final char last_bits7 = 0;
    private static final int last_len = 0;
    private static final int last_len1 = 0;
    private static final int last_len2 = 0;
    private static final int last_len3 = 0;
    private static final int last_len4 = 0;
    private static final int last_len5 = 0;
    private static final int last_len6 = 0;
    private static final int last_len7 = 0;
    private static final int last_len8 = 0;
    private static final int last_len9 = 0;
    private static final int last_len10 = 0;
    private static final int last_len11 = 0;
    private static final int last_len12 = 0;
    private static final int last_len13 = 0;
    private static final int last_len14 = 0;
    private static final int last_len15 = 0;
    private static final int last_len16 = 0;
    private static final int last_len17 = 0;
    private static final int last_len18 = 0;
    private static final int last_len19 = 0;
    private static final int last_len20 = 0;
    private static final int last_len21 = 0;
    private static final long dvc_counter_block = 0L;
    private static final boolean show_bitsblk_count = false;
    private static final long show_slices = 0L;
    private static int dvc_cc_active = 0;
    private static int dvc_pixel_count;
    private static int dvc_size_x;
    private static int dvc_size_y;
    private static int dvc_y_clipped;
    private static int dvc_lastx;
    private static int dvc_lasty;
    private static int dvc_newx;
    private static int dvc_newy;
    private static int dvc_color;
    private static int dvc_last_color;
    private static int dvc_ib_acc = 0;
    private static int dvc_ib_bcnt = 0;
    private static int dvc_zero_count = 0;
    private static int dvc_decoder_state = 0;
    private static int dvc_next_state = 0;
    private static int dvc_pixcode = 38;
    private static int dvc_code = 0;
    private static int dvc_red;
    private static int dvc_green;
    private static int dvc_blue;
    private static int fatal_count;
    private static int printchan = 0;
    private static String printstring = "";
    private static long count_bytes = 0L;
    private static int cmd_p_count = 0;
    private static int cmd_last = 0;
    private static int framerate = 30;
    private static boolean debug_msgs = false;
    private static char dvc_new_bits = 0;
    private static int debug_lastx = 0;
    private static int debug_lasty = 0;
    private static int debug_show_block = 0;
    private static long timeout_count = 0L;
    private static long dvc_counter_bits = 0L;
    private static boolean dvc_process_inhibit = false;
    private static boolean video_detected = true;
    private final boolean sending_encrypt_command = false;
    private final int blockWidth = 16;
    public int[] color_remap_table = new int['耀'];
    public boolean UI_dirty = false;
    public byte[] encrypt_key = new byte[16];
    public Point mousePrevPosn = new Point(0, 0);
    protected MouseSync mouse_sync = new MouseSync(this);
    protected Cursor current_cursor;
    private char prev_char = ' ';
    private boolean disable_kbd = false;
    private boolean altlock = false;
    private int scale_x = 1;
    private int scale_y = 1;
    private int screen_x = 1;
    private int screen_y = 1;
    private int mouse_protocol = 0;
    private RC4 RC4encrypter;
    private Aes Aes128encrypter;
    private Aes Aes256encrypter;
    private int key_index = 0;
    private int bitsPerColor = 5;
    private byte mouseBtnState = 0;
    private boolean ignore_next_key = false;
    private int blockHeight = 16;
    private boolean unsupportedVideoModeWarned = false;

    public cim(remcons var1) {
        super(var1);
        dvc_reversal[255] = 0;
        this.current_cursor = Cursor.getDefaultCursor();
        super.screen.addMouseListener(this.mouse_sync);
        super.screen.addMouseMotionListener(this.mouse_sync);
        super.screen.addMouseWheelListener(this.mouse_sync);
        this.mouse_sync.setListener(this);
    }

    public static String byteToHex(byte var0) {
        String var1 = String.valueOf(toHexChar(var0 >>> 4 & 15)) +
                toHexChar(var0 & 15);
        return var1;
    }

    public static String intToHex(int var0) {
        byte var1 = (byte) var0;
        return byteToHex(var1);
    }

    public static String intToHex4(int var0) {
        String var1 = byteToHex((byte) (var0 / 256)) +
                byteToHex((byte) (var0 & 255));
        return var1;
    }

    public static String charToHex(char var0) {
        byte var1 = (byte) var0;
        return byteToHex(var1);
    }

    public static char toHexChar(int var0) {
        return 0 <= var0 && var0 <= 9 ? (char) (48 + var0) : (char) (65 + (var0 - 10));
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = super.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("cim:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public void setup_encryption(byte[] var1, int var2) {
        System.arraycopy(var1, 0, this.encrypt_key, 0, 16);
        this.RC4encrypter = new RC4(var1);
        this.Aes128encrypter = new Aes(0, var1);
        this.Aes256encrypter = new Aes(0, var1);
        this.key_index = var2;
    }

    public void reinit_vars() {
        super.reinit_vars();
        dvc_code = 0;
        dvc_ib_acc = 0;
        dvc_ib_bcnt = 0;
        dvc_counter_bits = 0L;
        this.prev_char = ' ';
        this.disable_kbd = false;
        this.altlock = false;
        dvc_reversal[255] = 0;
        this.scale_x = 1;
        this.scale_y = 1;
        this.mouse_sync.restart();
        dvc_process_inhibit = false;
    }

    public void enable_debug() {
        debug_msgs = true;
        super.enable_debug();
        this.mouse_sync.enableDebug();
    }

    public void disable_debug() {
        debug_msgs = false;
        super.disable_debug();
        this.mouse_sync.disableDebug();
    }

    public void sync_start() {
        this.mouse_sync.sync();
    }

    public void serverMove(int var1, int var2, int var3, int var4) {
        boolean var11;
        if (var1 < -128) {
            var11 = true;
        } else if (var1 > 127) {
            var11 = true;
        }

        boolean var12;
        if (var2 < -128) {
            var12 = true;
        } else if (var2 > 127) {
            var12 = true;
        }

        this.UI_dirty = true;
        if (this.screen_x > 0 && this.screen_y > 0) {
            var3 = 3000 * var3 / this.screen_x;
            var4 = 3000 * var4 / this.screen_y;
        } else {
            var3 = 3000 * var3;
            var4 = 3000 * var4;
        }

        byte[] var9 = new byte[]{2, 0, (byte) (var3 & 255), (byte) (var3 >> 8), (byte) (var4 & 255), (byte) (var4 >> 8), 0, 0, this.mouseBtnState, 0};
        String var10 = new String(var9);
        this.transmit(var10);
    }

    public void mouse_mode_change(boolean var1) {
        boolean var2 = true;
    }

    public synchronized void mouseEntered(MouseEvent var1) {
        this.UI_dirty = true;
        super.mouseEntered(var1);
    }

    public void serverPress(int var1) {
        this.UI_dirty = true;
        this.send_mouse_press(var1);
    }

    public void serverRelease(int var1) {
        this.UI_dirty = true;
        this.send_mouse_release(var1);
    }

    public void serverClick(int var1, int var2) {
        this.UI_dirty = true;
        this.send_mouse_click(var1, var2);
        this.mouseBtnState = this.mouseButtonState(var1);
    }

    public synchronized void mouseExited(MouseEvent var1) {
        super.mouseExited(var1);
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void disable_keyboard() {
        this.disable_kbd = true;
    }

    public void enable_keyboard() {
        this.disable_kbd = false;
    }

    public void disable_altlock() {
        this.altlock = false;
    }

    public void enable_altlock() {
        this.altlock = true;
    }

    public synchronized void connect(String var1, String var2, int var3, int var4, int var5, remcons var6) {
        char[] var10000 = new char[]{'ÿ', 'À'};
        super.connect(var1, var2, var3, var4, var5, var6);
    }

    public synchronized void transmit(String var1) {
        if (super.out != null && var1 != null) {
            if (var1.length() != 0) {
                byte[] var3 = new byte[var1.length()];

                for (int var4 = 0; var4 < var1.length(); ++var4) {
                    var3[var4] = (byte) var1.charAt(var4);
                    if (super.dvc_encryption) {
                        char var2;
                        switch (super.cipher) {
                            case 1:
                                var2 = (char) (this.RC4encrypter.randomValue() & 255);
                                var3[var4] = (byte) (var3[var4] ^ var2);
                                break;
                            case 2:
                                var2 = (char) (this.Aes128encrypter.randomValue() & 255);
                                var3[var4] = (byte) (var3[var4] ^ var2);
                                break;
                            case 3:
                                var2 = (char) (this.Aes256encrypter.randomValue() & 255);
                                var3[var4] = (byte) (var3[var4] ^ var2);
                                break;
                            default:
                                boolean var7 = false;
                                System.out.println("Unknown encryption");
                        }

                        var3[var4] = (byte) (var3[var4] & 255);
                    }
                }

                try {
                    super.out.write(var3, 0, var3.length);
                } catch (IOException var6) {
                    System.out.println("telnet.transmit() IOException: " + var6);
                }
            }

        }
    }

    public synchronized void transmitb(byte[] var1, int var2) {
        byte[] var5 = new byte[var2];
        System.arraycopy(var1, 0, var5, 0, var2);

        for (int var3 = 0; var3 < var2; ++var3) {
            if (super.dvc_encryption) {
                char var4;
                switch (super.cipher) {
                    case 1:
                        var4 = (char) (this.RC4encrypter.randomValue() & 255);
                        var5[var3] = (byte) (var5[var3] ^ var4);
                        break;
                    case 2:
                        var4 = (char) (this.Aes128encrypter.randomValue() & 255);
                        var5[var3] = (byte) (var5[var3] ^ var4);
                        break;
                    case 3:
                        var4 = (char) (this.Aes256encrypter.randomValue() & 255);
                        var5[var3] = (byte) (var5[var3] ^ var4);
                        break;
                    default:
                        boolean var8 = false;
                        System.out.println("Unknown encryption");
                }

                var5[var3] = (byte) (var5[var3] & 255);
            }
        }

        try {
            if (null != super.out) {
                super.out.write(var5, 0, var2);
            }
        } catch (IOException var7) {
            System.out.println("telnet.transmitb() IOException: " + var7);
        }

    }

    protected String translate_key(KeyEvent var1) {
        String var2 = "";
        char var3 = var1.getKeyChar();
        byte var4 = 0;
        boolean var5 = true;
        if (this.disable_kbd) {
            return "";
        } else if (this.ignore_next_key) {
            this.ignore_next_key = false;
            return "";
        } else {
            this.UI_dirty = true;
            if (var1.isShiftDown()) {
                var4 = 1;
            } else if (var1.isControlDown()) {
                var4 = 2;
            } else if (this.altlock || var1.isAltDown()) {
                var4 = 3;
                if (var1.isAltDown()) {
                    var1.consume();
                }
            }

            switch (var3) {
                case '\b':
                    switch (var4) {
                        case 0:
                            var2 = "\b";
                            break;
                        case 1:
                            var2 = "\u001b[3\b";
                            break;
                        case 2:
                            var2 = "\u007f";
                            break;
                        case 3:
                            var2 = "\u001b[1\b";
                    }

                    var5 = false;
                    break;
                case '\n':
                case '\r':
                    switch (var4) {
                        case 0:
                            var2 = "\r";
                            break;
                        case 1:
                            var2 = "\u001b[3\r";
                            break;
                        case 2:
                            var2 = "\n";
                            break;
                        case 3:
                            var2 = "\u001b[1\r";
                    }

                    var5 = false;
                    break;
                case '\u001b':
                    var5 = false;
                    break;
                default:
                    var2 = super.translate_key(var1);
            }

            if (var5 && var2.length() != 0 && var4 == 3) {
                var2 = "\u001b[1" + var2;
            }

            return var2;
        }
    }

    protected String translate_special_key(KeyEvent var1) {
        String var2 = "";
        boolean var3 = true;
        byte var4 = 0;
        if (this.disable_kbd) {
            return "";
        } else {
            this.UI_dirty = true;
            if (var1.isShiftDown()) {
                var4 = 1;
            } else if (var1.isControlDown()) {
                var4 = 2;
            } else if (this.altlock || var1.isAltDown()) {
                var4 = 3;
            }

            switch (var1.getKeyCode()) {
                case 9:
                    var1.consume();
                    var2 = "\t";
                    break;
                case 27:
                    var2 = "\u001b";
                    break;
                case 33:
                    var2 = "\u001b[I";
                    break;
                case 34:
                    var2 = "\u001b[G";
                    break;
                case 35:
                    var2 = "\u001b[F";
                    break;
                case 36:
                    var2 = "\u001b[H";
                    break;
                case 37:
                    var2 = "\u001b[D";
                    break;
                case 38:
                    var2 = "\u001b[A";
                    break;
                case 39:
                    var2 = "\u001b[C";
                    break;
                case 40:
                    var2 = "\u001b[B";
                    break;
                case 112:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[M";
                            break;
                        case 1:
                            var2 = "\u001b[Y";
                            break;
                        case 2:
                            var2 = "\u001b[k";
                            break;
                        case 3:
                            var2 = "\u001b[w";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 113:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[N";
                            break;
                        case 1:
                            var2 = "\u001b[Z";
                            break;
                        case 2:
                            var2 = "\u001b[l";
                            break;
                        case 3:
                            var2 = "\u001b[x";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 114:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[O";
                            break;
                        case 1:
                            var2 = "\u001b[a";
                            break;
                        case 2:
                            var2 = "\u001b[m";
                            break;
                        case 3:
                            var2 = "\u001b[y";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 115:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[P";
                            break;
                        case 1:
                            var2 = "\u001b[b";
                            break;
                        case 2:
                            var2 = "\u001b[n";
                            break;
                        case 3:
                            var2 = "\u001b[z";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 116:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[Q";
                            break;
                        case 1:
                            var2 = "\u001b[c";
                            break;
                        case 2:
                            var2 = "\u001b[o";
                            break;
                        case 3:
                            var2 = "\u001b[@";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 117:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[R";
                            break;
                        case 1:
                            var2 = "\u001b[d";
                            break;
                        case 2:
                            var2 = "\u001b[p";
                            break;
                        case 3:
                            var2 = "\u001b[[";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 118:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[S";
                            break;
                        case 1:
                            var2 = "\u001b[e";
                            break;
                        case 2:
                            var2 = "\u001b[q";
                            break;
                        case 3:
                            var2 = "\u001b[\\";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 119:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[T";
                            break;
                        case 1:
                            var2 = "\u001b[f";
                            break;
                        case 2:
                            var2 = "\u001b[r";
                            break;
                        case 3:
                            var2 = "\u001b[]";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 120:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[U";
                            break;
                        case 1:
                            var2 = "\u001b[g";
                            break;
                        case 2:
                            var2 = "\u001b[s";
                            break;
                        case 3:
                            var2 = "\u001b[^";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 121:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[V";
                            break;
                        case 1:
                            var2 = "\u001b[h";
                            break;
                        case 2:
                            var2 = "\u001b[t";
                            break;
                        case 3:
                            var2 = "\u001b[_";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 122:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[W";
                            break;
                        case 1:
                            var2 = "\u001b[i";
                            break;
                        case 2:
                            var2 = "\u001b[u";
                            break;
                        case 3:
                            var2 = "\u001b[`";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 123:
                    switch (var4) {
                        case 0:
                            var2 = "\u001b[X";
                            break;
                        case 1:
                            var2 = "\u001b[j";
                            break;
                        case 2:
                            var2 = "\u001b[v";
                            break;
                        case 3:
                            var2 = "\u001b['";
                    }

                    var1.consume();
                    var3 = false;
                    break;
                case 127:
                    if (var1.isControlDown() && (this.altlock || var1.isAltDown())) {
                        this.send_ctrl_alt_del();
                        return "";
                    }

                    if (System.getProperty("java.version", "0").compareTo("1.4.2") < 0) {
                        var2 = "\u007f";
                    }
                    break;
                case 155:
                    var2 = "\u001b[L";
                    break;
                default:
                    var3 = false;
                    var2 = super.translate_special_key(var1);
            }

            if (var2.length() != 0 && var3) {
                switch (var4) {
                    case 1:
                        var2 = "\u001b[3" + var2;
                        break;
                    case 2:
                        var2 = "\u001b[2" + var2;
                        break;
                    case 3:
                        var2 = "\u001b[1" + var2;
                }
            }

            return var2;
        }
    }

    protected String translate_special_key_release(KeyEvent var1) {
        String var2 = "";
        int var3 = 0;
        if (var1.isShiftDown()) {
            var3 = 1;
        }

        if (this.altlock || var1.isAltDown()) {
            var3 += 2;
        }

        if (var1.isControlDown()) {
            var3 += 4;
        }

        switch (var1.getKeyCode()) {
            case 28:
            case 256:
            case 257:
                var3 += 144;
                break;
            case 29:
                var3 += 136;
                break;
            case 241:
            case 242:
            case 245:
                var3 += 152;
                break;
            case 243:
            case 244:
            case 263:
                var3 += 128;
        }

        if (var3 > 127) {
            var2 = "" + (char) var3;
        } else {
            var2 = "";
        }

        return var2;
    }

    public void send_ctrl_alt_del() {
        byte[] var1 = new byte[]{1, 0, 5, 0, 76, 0, 0, 0, 0, 0};
        String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var6) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = 0;
        String var3 = new String(var1);
        this.transmit(var3);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var5) {
            System.out.println("Thread interrupted..");
        }

        var1[2] = 0;
        String var4 = new String(var1);
        this.transmit(var4);
        this.requestFocus();
    }

    public void send_num_lock() {
        System.out.println("sending num lock");
        byte[] var1 = new byte[]{1, 0, 0, 0, 83, 0, 0, 0, 0, 0};
        String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var4) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = 0;
        String var3 = new String(var1);
        this.transmit(var3);
    }

    public void send_caps_lock() {
        System.out.println("sending caps lock");
        byte[] var1 = new byte[]{1, 0, 0, 0, 57, 0, 0, 0, 0, 0};
        String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var4) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = 0;
        String var3 = new String(var1);
        this.transmit(var3);
    }

    public void send_ctrl_alt_back() {
        byte[] var1 = new byte[]{1, 0, 5, 0, 42, 0, 0, 0, 0, 0};
        String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var6) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = 0;
        String var3 = new String(var1);
        this.transmit(var3);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var5) {
            System.out.println("Thread interrupted..");
        }

        var1[2] = 0;
        String var4 = new String(var1);
        this.transmit(var4);
        this.requestFocus();
    }

    public void send_ctrl_alt_fn(int var1) {
        byte[] var2 = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean var3 = false;
        byte var9;
        switch (var1 + 1) {
            case 1:
                var9 = 58;
                break;
            case 2:
                var9 = 59;
                break;
            case 3:
                var9 = 60;
                break;
            case 4:
                var9 = 61;
                break;
            case 5:
                var9 = 62;
                break;
            case 6:
                var9 = 63;
                break;
            case 7:
                var9 = 64;
                break;
            case 8:
                var9 = 65;
                break;
            case 9:
                var9 = 66;
                break;
            case 10:
                var9 = 67;
                break;
            case 11:
                var9 = 68;
                break;
            case 12:
                var9 = 69;
                break;
            default:
                var9 = 64;
        }

        var2[2] = 5;
        var2[4] = var9;
        String var4 = new String(var2);
        this.transmit(var4);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var8) {
            System.out.println("Thread interrupted..");
        }

        var2[4] = 0;
        String var5 = new String(var2);
        this.transmit(var5);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var7) {
            System.out.println("Thread interrupted..");
        }

        var2[2] = 0;
        String var6 = new String(var2);
        this.transmit(var6);
        this.requestFocus();
    }

    public void send_alt_fn(int var1) {
        byte[] var2 = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean var3 = false;
        byte var9;
        switch (var1 + 1) {
            case 1:
                var9 = 58;
                break;
            case 2:
                var9 = 59;
                break;
            case 3:
                var9 = 60;
                break;
            case 4:
                var9 = 61;
                break;
            case 5:
                var9 = 62;
                break;
            case 6:
                var9 = 63;
                break;
            case 7:
                var9 = 64;
                break;
            case 8:
                var9 = 65;
                break;
            case 9:
                var9 = 66;
                break;
            case 10:
                var9 = 67;
                break;
            case 11:
                var9 = 68;
                break;
            case 12:
                var9 = 69;
                break;
            default:
                var9 = 64;
        }

        var2[2] = 4;
        var2[4] = var9;
        String var4 = new String(var2);
        this.transmit(var4);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var8) {
            System.out.println("Thread interrupted..");
        }

        var2[4] = 0;
        String var5 = new String(var2);
        this.transmit(var5);

        try {
            Thread.currentThread();
            Thread.sleep(250L);
        } catch (InterruptedException var7) {
            System.out.println("Thread interrupted..");
        }

        var2[2] = 0;
        String var6 = new String(var2);
        this.transmit(var6);
        this.requestFocus();
    }

    public void sendMomPress() {
        super.post_complete = false;
        byte[] var1 = new byte[]{0, 0, 0, 0};
        String var2 = new String(var1);
        this.transmit(var2);
    }

    public void sendPressHold() {
        super.post_complete = false;
        byte[] var1 = new byte[]{0, 0, 1, 0};
        String var2 = new String(var1);
        this.transmit(var2);
    }

    public void sendPowerCycle() {
        super.post_complete = false;
        byte[] var1 = new byte[]{0, 0, 2, 0};
        String var2 = new String(var1);
        this.transmit(var2);
    }

    public void sendSystemReset() {
        super.post_complete = false;
        byte[] var1 = new byte[]{0, 0, 3, 0};
        String var2 = new String(var1);
        this.transmit(var2);
    }

    public void send_mouse_press(int var1) {
    }

    public void send_mouse_release(int var1) {
    }

    public void send_mouse_click(int var1, int var2) {
    }

    public void send_mouse_byte(int var1) {
    }

    public void refresh_screen() {
        byte[] var1 = new byte[]{5, 0};
        String var2 = new String(var1);
        this.transmit(var2);
        this.requestFocus();
    }

    public void send_keep_alive_msg() {
    }

    protected synchronized void set_framerate(int var1) {
        framerate = var1;
        super.screen.set_framerate(var1);
        this.set_status(3, "" + framerate);
    }

    protected void show_error(String var1) {
        System.out.println("dvc:" + var1 + ": state " + dvc_decoder_state + " code " + dvc_code);
        System.out.println("dvc:error at byte count " + count_bytes);
    }

    final void cache_reset() {
        dvc_cc_active = 0;
    }

    final int cache_lru(int var1) {
        int var4 = dvc_cc_active;
        int var3 = 0;
        byte var6 = 0;

        int var2;
        for (var2 = 0; var2 < var4; ++var2) {
            if (var1 == dvc_cc_color[var2]) {
                var3 = var2;
                var6 = 1;
                break;
            }

            if (dvc_cc_usage[var2] == var4 - 1) {
                var3 = var2;
            }
        }

        int var5 = dvc_cc_usage[var3];
        if (var6 == 0) {
            if (var4 < 17) {
                var3 = var4;
                var5 = var4++;
                dvc_cc_active = var4;
                if (dvc_cc_active < 2) {
                    dvc_pixcode = 38;
                } else if (dvc_cc_active == 2) {
                    dvc_pixcode = 4;
                } else if (dvc_cc_active == 3) {
                    dvc_pixcode = 5;
                } else if (dvc_cc_active < 6) {
                    dvc_pixcode = 6;
                } else if (dvc_cc_active < 10) {
                    dvc_pixcode = 7;
                } else {
                    dvc_pixcode = 32;
                }

                next_1[31] = dvc_pixcode;
            }

            dvc_cc_color[var3] = var1;
        }

        dvc_cc_block[var3] = 1;

        for (var2 = 0; var2 < var4; ++var2) {
            if (dvc_cc_usage[var2] < var5) {
                int var10002 = dvc_cc_usage[var2]++;
            }
        }

        dvc_cc_usage[var3] = 0;
        return var6;
    }

    final int cache_find(int var1) {
        int var2 = dvc_cc_active;

        for (int var3 = 0; var3 < var2; ++var3) {
            if (var1 == dvc_cc_usage[var3]) {
                int var5 = dvc_cc_color[var3];
                int var4 = var3;

                for (var3 = 0; var3 < var2; ++var3) {
                    if (dvc_cc_usage[var3] < var1) {
                        int var10002 = dvc_cc_usage[var3]++;
                    }
                }

                dvc_cc_usage[var4] = 0;
                dvc_cc_block[var4] = 1;
                return var5;
            }
        }

        return -1;
    }

    final void cache_prune() {
        int var2 = dvc_cc_active;
        int var1 = 0;

        while (var1 < var2) {
            int var3 = dvc_cc_block[var1];
            if (var3 == 0) {
                --var2;
                dvc_cc_block[var1] = dvc_cc_block[var2];
                dvc_cc_color[var1] = dvc_cc_color[var2];
                dvc_cc_usage[var1] = dvc_cc_usage[var2];
            } else {
                int var10002 = dvc_cc_block[var1]--;
                ++var1;
            }
        }

        dvc_cc_active = var2;
        if (dvc_cc_active < 2) {
            dvc_pixcode = 38;
        } else if (dvc_cc_active == 2) {
            dvc_pixcode = 4;
        } else if (dvc_cc_active == 3) {
            dvc_pixcode = 5;
        } else if (dvc_cc_active < 6) {
            dvc_pixcode = 6;
        } else if (dvc_cc_active < 10) {
            dvc_pixcode = 7;
        } else {
            dvc_pixcode = 32;
        }

        next_1[31] = dvc_pixcode;
    }

    protected void next_block(int var1) {
        boolean var4 = video_detected;

        int var3;
        if (dvc_pixel_count != 0 && dvc_y_clipped > 0 && dvc_lasty == dvc_size_y) {
            int var5 = this.color_remap_table[0];

            for (var3 = dvc_y_clipped; var3 < 256; ++var3) {
                block[var3] = var5;
            }
        }

        dvc_pixel_count = 0;
        dvc_next_state = 1;
        int var2 = dvc_lastx * this.blockWidth;

        for (var3 = dvc_lasty * this.blockHeight; var1 != 0; --var1) {
            if (var4) {
                super.screen.paste_array(block, var2, var3, 16, this.blockHeight);
            }

            ++dvc_lastx;
            var2 += 16;
            if (dvc_lastx >= dvc_size_x) {
                break;
            }
        }

    }

    protected void init_reversal() {
        for (int var1 = 0; var1 < 256; ++var1) {
            int var6 = 8;
            int var5 = 8;
            int var3 = var1;
            int var4 = 0;

            for (int var2 = 0; var2 < 8; ++var2) {
                var4 <<= 1;
                if ((var3 & 1) == 1) {
                    if (var6 > var2) {
                        var6 = var2;
                    }

                    var4 |= 1;
                    var5 = 7 - var2;
                }

                var3 >>= 1;
            }

            dvc_reversal[var1] = var4;
            dvc_right[var1] = var6;
            dvc_left[var1] = var5;
        }

    }

    final int add_bits(char var1) {
        dvc_zero_count += dvc_right[var1];
        dvc_ib_acc |= var1 << dvc_ib_bcnt;
        dvc_ib_bcnt += 8;
        if (dvc_zero_count > 30) {
            if (debug_msgs && dvc_decoder_state == 38 && fatal_count < 40 && fatal_count > 0) {
            }

            dvc_next_state = 43;
            dvc_decoder_state = 43;
            return 4;
        } else {
            if (var1 != 0) {
                dvc_zero_count = dvc_left[var1];
            }

            return 0;
        }
    }

    final int get_bits(int var1) {
        if (var1 == 1) {
            dvc_code = dvc_ib_acc & 1;
            dvc_ib_acc >>= 1;
            --dvc_ib_bcnt;
            return 0;
        } else if (var1 == 0) {
            return 0;
        } else {
            int var2 = dvc_ib_acc & dvc_getmask[var1];
            dvc_ib_bcnt -= var1;
            dvc_ib_acc >>= var1;
            var2 = dvc_reversal[var2];
            var2 >>= 8 - var1;
            dvc_code = var2;
            return 0;
        }
    }

    int process_bits(char var1) {
        boolean var7 = true;
        byte var6 = 0;
        this.add_bits(var1);
        dvc_new_bits = var1;
        ++count_bytes;
        boolean var5 = false;

        while (true) {
            if (var6 == 0) {
                int var8 = bits_to_read[dvc_decoder_state];
                if (var8 <= dvc_ib_bcnt) {
                    this.get_bits(var8);
                    dvc_counter_bits += (long) var8;
                    if (dvc_code == 0) {
                        dvc_next_state = next_0[dvc_decoder_state];
                    } else {
                        dvc_next_state = next_1[dvc_decoder_state];
                    }

                    int var4;
                    label255:
                    switch (dvc_decoder_state) {
                        case 0:
                            this.cache_reset();
                            dvc_pixel_count = 0;
                            dvc_lastx = 0;
                            dvc_lasty = 0;
                            dvc_red = 0;
                            dvc_green = 0;
                            dvc_blue = 0;
                            fatal_count = 0;
                            timeout_count = -1L;
                            cmd_p_count = 0;
                        case 1:
                        case 2:
                        case 10:
                        case 11:
                        case 15:
                        case 16:
                        case 18:
                        case 19:
                        case 22:
                        case 23:
                        case 25:
                        case 28:
                        case 31:
                        case 36:
                        default:
                            break;
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 32:
                            if (dvc_cc_active == 1) {
                                dvc_code = dvc_cc_usage[0];
                            } else if (dvc_decoder_state == 4) {
                                dvc_code = 0;
                            } else if (dvc_decoder_state == 3) {
                                dvc_code = 1;
                            } else if (dvc_code != 0) {
                                ++dvc_code;
                            }

                            dvc_color = this.cache_find(dvc_code);
                            if (dvc_color == -1) {
                                dvc_next_state = 38;
                            } else {
                                dvc_last_color = this.color_remap_table[dvc_color];
                                if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
                                    block[dvc_pixel_count] = dvc_last_color;
                                    ++dvc_pixel_count;
                                } else {
                                    dvc_next_state = 38;
                                }
                            }
                            break;
                        case 8:
                            dvc_red = dvc_code << this.bitsPerColor * 2;
                            dvc_green = dvc_code << this.bitsPerColor;
                        case 42:
                            dvc_blue = dvc_code;
                            dvc_color = dvc_red | dvc_green | dvc_blue;
                            int var2 = this.cache_lru(dvc_color);
                            if (var2 != 0) {
                                if (debug_msgs && count_bytes > 6L) {
                                }

                                dvc_next_state = 38;
                            } else {
                                dvc_last_color = this.color_remap_table[dvc_color];
                                if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
                                    block[dvc_pixel_count] = dvc_last_color;
                                    ++dvc_pixel_count;
                                } else {
                                    dvc_next_state = 38;
                                }
                            }
                            break;
                        case 9:
                            dvc_red = dvc_code << this.bitsPerColor * 2;
                            break;
                        case 12:
                            if (dvc_code == 7) {
                                dvc_next_state = 14;
                            } else if (dvc_code == 6) {
                                dvc_next_state = 13;
                            } else {
                                dvc_code += 2;
                                var4 = 0;

                                while (true) {
                                    if (var4 >= dvc_code) {
                                        break label255;
                                    }

                                    if (dvc_pixel_count >= this.blockHeight * this.blockWidth) {
                                        dvc_next_state = 38;
                                        break label255;
                                    }

                                    block[dvc_pixel_count] = dvc_last_color;
                                    ++dvc_pixel_count;
                                    ++var4;
                                }
                            }
                            break;
                        case 13:
                            dvc_code += 8;
                        case 14:
                            if (debug_msgs && dvc_decoder_state == 14 && dvc_code < 16) {
                            }

                            var4 = 0;

                            while (true) {
                                if (var4 >= dvc_code) {
                                    break label255;
                                }

                                if (dvc_pixel_count >= this.blockHeight * this.blockWidth) {
                                    dvc_next_state = 38;
                                    break label255;
                                }

                                block[dvc_pixel_count] = dvc_last_color;
                                ++dvc_pixel_count;
                                ++var4;
                            }
                        case 17:
                        case 26:
                            dvc_newx = dvc_code;
                            if (dvc_decoder_state == 17 && dvc_newx > dvc_size_x) {
                                if (debug_msgs) {
                                }

                                dvc_newx = 0;
                            }
                            break;
                        case 20:
                            dvc_code = dvc_lastx + dvc_code + 1;
                            if (dvc_code > dvc_size_x && debug_msgs) {
                            }
                        case 21:
                            dvc_lastx = dvc_code;
                            if (this.blockHeight == 16) {
                                dvc_lastx &= 127;
                            }

                            if (dvc_lastx > dvc_size_x && debug_msgs) {
                            }
                            break;
                        case 24:
                            if (cmd_p_count != 0) {
                                cmd_p_buff[cmd_p_count - 1] = cmd_last;
                            }

                            ++cmd_p_count;
                            cmd_last = dvc_code;
                            break;
                        case 27:
                            if (timeout_count == count_bytes - 1L) {
                                dvc_next_state = 38;
                            }

                            if ((dvc_ib_bcnt & 7) != 0) {
                                this.get_bits(dvc_ib_bcnt & 7);
                            }

                            timeout_count = count_bytes;
                            super.screen.repaint_it(1);
                            break;
                        case 29:
                            dvc_code += 2;
                        case 30:
                            this.next_block(dvc_code);
                            break;
                        case 33:
                            if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
                                block[dvc_pixel_count] = dvc_last_color;
                                ++dvc_pixel_count;
                            } else {
                                dvc_next_state = 38;
                            }
                            break;
                        case 34:
                            this.next_block(1);
                            break;
                        case 35:
                            dvc_next_state = dvc_pixcode;
                            break;
                        case 37:
                            return 1;
                        case 38:
                            if (fatal_count == 0) {
                                debug_lastx = dvc_lastx;
                                debug_lasty = dvc_lasty;
                                debug_show_block = 1;
                            }

                            if (fatal_count == 40) {
                                this.refresh_screen();
                            }

                            if (fatal_count == 11680) {
                                this.refresh_screen();
                            }

                            ++fatal_count;
                            if (fatal_count == 120000) {
                                this.refresh_screen();
                            }

                            if (fatal_count == 12000000) {
                                this.refresh_screen();
                                fatal_count = 41;
                            }
                            break;
                        case 39:
                            dvc_newy = dvc_code;
                            if (this.blockHeight == 16) {
                                dvc_newy &= 127;
                            }

                            dvc_lastx = dvc_newx;
                            dvc_lasty = dvc_newy;
                            if (dvc_lasty > dvc_size_y && debug_msgs) {
                            }

                            super.screen.repaint_it(1);
                            break;
                        case 40:
                            dvc_size_x = dvc_newx;
                            dvc_size_y = dvc_code;
                            break;
                        case 41:
                            dvc_green = dvc_code << this.bitsPerColor;
                            break;
                        case 43:
                            if (dvc_next_state != dvc_decoder_state) {
                                dvc_ib_bcnt = 0;
                                dvc_ib_acc = 0;
                                dvc_zero_count = 0;
                                count_bytes = 0L;
                            }
                            break;
                        case 44:
                            printchan = dvc_code;
                            printstring = "";
                            break;
                        case 45:
                            if (dvc_code != 0) {
                                printstring = printstring + (char) dvc_code;
                            } else {
                                switch (printchan) {
                                    case 1:
                                    case 2:
                                        this.set_status(2 + printchan, printstring);
                                        break;
                                    case 3:
                                        System.out.println(printstring);
                                        break;
                                    case 4:
                                        super.screen.show_text(printstring);
                                }

                                dvc_next_state = 1;
                            }
                            break;
                        case 46:
                            if (dvc_code == 0) {
                                switch (cmd_last) {
                                    case 1:
                                        dvc_next_state = 37;
                                        break;
                                    case 2:
                                        dvc_next_state = 44;
                                        break;
                                    case 3:
                                        if (cmd_p_count != 0) {
                                            this.set_framerate(cmd_p_buff[0]);
                                        } else {
                                            this.set_framerate(0);
                                        }
                                        break;
                                    case 4:
                                        super.remconsObj.setPwrStatusPower(1);
                                        break;
                                    case 5:
                                        super.remconsObj.setPwrStatusPower(0);
                                        super.screen.clearScreen();
                                        dvc_newx = 50;
                                        dvc_code = 38;
                                        break;
                                    case 6:
                                        super.screen.clearScreen();
                                        if (!video_detected) {
                                            super.screen.clearScreen();
                                        }

                                        this.set_status(2, this.getLocalString(12290));
                                        this.set_status(1, " ");
                                        this.set_status(3, " ");
                                        this.set_status(4, " ");
                                        super.post_complete = false;
                                        break;
                                    case 7:
                                        super.ts_type = cmd_p_buff[0];
                                    case 8:
                                    default:
                                        break;
                                    case 9:
                                        System.out.println("received keychg and cleared bits\n");
                                        if ((dvc_ib_bcnt & 7) != 0) {
                                            this.get_bits(dvc_ib_bcnt & 7);
                                        }
                                        break;
                                    case 10:
                                        this.seize();
                                        break;
                                    case 11:
                                        System.out.println("Setting bpc to  " + cmd_p_buff[0]);
                                        this.setBitsPerColor(cmd_p_buff[0]);
                                        break;
                                    case 12:
                                        System.out.println("Setting encryption to  " + cmd_p_buff[0]);
                                        this.setVideoDecryption(cmd_p_buff[0]);
                                        break;
                                    case 13:
                                        System.out.println("Header received ");
                                        this.setBitsPerColor(cmd_p_buff[0]);
                                        this.setVideoDecryption(cmd_p_buff[1]);
                                        super.remconsObj.SetLicensed(cmd_p_buff[2]);
                                        super.remconsObj.SetFlags(cmd_p_buff[3]);
                                        break;
                                    case 16:
                                        this.sendAck();
                                        break;
                                    case 128:
                                        super.screen.invalidate();
                                        super.screen.repaint();
                                }

                                cmd_p_count = 0;
                            }
                            break;
                        case 47:
                            dvc_lastx = 0;
                            dvc_lasty = 0;
                            dvc_pixel_count = 0;
                            this.cache_reset();
                            this.scale_x = 1;
                            this.scale_y = 1;
                            this.screen_x = dvc_size_x * this.blockWidth;
                            this.screen_y = dvc_size_y * 16 + dvc_code;
                            video_detected = this.screen_x != 0 && this.screen_y != 0;

                            if (dvc_code > 0) {
                                dvc_y_clipped = 256 - 16 * dvc_code;
                            } else {
                                dvc_y_clipped = 0;
                            }

                            if (!video_detected) {
                                super.screen.clearScreen();
                                this.set_status(2, this.getLocalString(12290));
                                this.set_status(1, " ");
                                this.set_status(3, " ");
                                this.set_status(4, " ");
                                System.out.println("No video. image_source = " + super.screen.image_source);
                                super.post_complete = false;
                            } else {
                                super.screen.set_abs_dimensions(this.screen_x, this.screen_y);
                                this.SetHalfHeight();
                                this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
                                this.set_status(2, this.getLocalString(12291) + this.screen_x + "x" + this.screen_y);
                                this.set_status(1, " ");
                            }
                    }

                    if (dvc_next_state == 2 && dvc_pixel_count == this.blockHeight * this.blockWidth) {
                        this.next_block(1);
                        this.cache_prune();
                    }

                    if (dvc_decoder_state == dvc_next_state && dvc_decoder_state != 45 && dvc_decoder_state != 38 && dvc_decoder_state != 43) {
                        System.out.println("Machine hung in state " + dvc_decoder_state);
                        var6 = 6;
                        continue;
                    }

                    dvc_decoder_state = dvc_next_state;
                    continue;
                }

                var6 = 0;
            }

            return var6;
        }
    }

    boolean process_dvc(char var1) {
        if (dvc_reversal[255] == 0) {
            System.out.println("dvc initializing");
            this.init_reversal();
            this.cache_reset();
            dvc_decoder_state = 0;
            dvc_next_state = 0;
            dvc_zero_count = 0;
            dvc_ib_acc = 0;
            dvc_ib_bcnt = 0;
            this.buildPixelTable(this.bitsPerColor);
            this.SetHalfHeight();
        }

        int var2;
        if (!dvc_process_inhibit) {
            var2 = this.process_bits(var1);
        } else {
            var2 = 0;
        }

        boolean var3;
        if (var2 == 0) {
            var3 = true;
        } else {
            System.out.println("Exit from DVC mode status =" + var2);
            System.out.println("Current block at " + dvc_lastx + " " + dvc_lasty);
            System.out.println("Byte count " + count_bytes);
            var3 = true;
            dvc_decoder_state = 38;
            dvc_next_state = 38;
            fatal_count = 0;
            this.refresh_screen();
        }

        return var3;
    }

    public void set_sig_colors(int[] var1) {
    }

    public void change_key() {
        this.RC4encrypter.update_key();
        super.change_key();
    }

    public void set_mouse_protocol(int var1) {
        this.mouse_protocol = var1;
    }

    Cursor customCursor(Image var1, Point var2, String var3) {
        Cursor var4 = null;

        try {
            Class var5 = class$java$awt$Toolkit == null ? (class$java$awt$Toolkit = class$("java.awt.Toolkit")) : class$java$awt$Toolkit;
            Method var6 = var5.getMethod("createCustomCursor", class$java$awt$Image == null ? (class$java$awt$Image = class$("java.awt.Image")) : class$java$awt$Image, class$java$awt$Point == null ? (class$java$awt$Point = class$("java.awt.Point")) : class$java$awt$Point, class$java$lang$String == null ? (class$java$lang$String = class$("java.lang.String")) : class$java$lang$String);
            Toolkit var7 = Toolkit.getDefaultToolkit();
            if (var6 != null) {
                var4 = (Cursor) var6.invoke(var7, var1, var2, var3);
            }
        } catch (Exception var8) {
            System.out.println("This JVM cannot create custom cursors");
        }

        return var4;
    }

    Cursor createCursor(int var1) {
        String var5 = System.getProperty("java.version", "0");
        Toolkit var7 = Toolkit.getDefaultToolkit();
        MemoryImageSource var2;
        Image var3;
        int[] var6;
        switch (var1) {
            case 0:
                return Cursor.getDefaultCursor();
            case 1:
                return Cursor.getPredefinedCursor(1);
            case 2:
                var3 = var7.createImage(cursor_none);
                break;
            case 3:
                var6 = new int[1024];
                var6[0] = var6[1] = var6[32] = var6[33] = -8355712;
                var2 = new MemoryImageSource(32, 32, var6, 0, 32);
                var3 = this.createImage(var2);
                break;
            case 4:
                var6 = new int[1024];

                for (int var8 = 0; var8 < 21; ++var8) {
                    System.arraycopy(cursor_outline, 0 + var8 * 12, var6, 0 + var8 * 32, 12);
                }

                var2 = new MemoryImageSource(32, 32, var6, 0, 32);
                var3 = this.createImage(var2);
                break;
            default:
                System.out.println("createCursor: unknown cursor " + var1);
                return Cursor.getDefaultCursor();
        }

        Cursor var4 = null;
        if (var5.compareTo("1.2") < 0) {
            System.out.println("This JVM cannot create custom cursors");
        } else {
            var4 = this.customCursor(var3, new Point(), "rcCursor");
        }

        return var4 != null ? var4 : Cursor.getDefaultCursor();
    }

    public void set_cursor(int var1) {
        this.current_cursor = this.createCursor(var1);
        this.setCursor(this.current_cursor);
    }

    private void SetHalfHeight() {
        if (this.screen_x > 1616) {
            if (super.remconsObj.halfHeightCapable) {
                if (8 != this.blockHeight) {
                    System.out.println("Setting halfheight mode on supported system");
                    this.blockHeight = 8;
                    bits_to_read[21] = 8;
                    bits_to_read[17] = 8;
                    bits_to_read[39] = 8;
                    bits_to_read[30] = 8;
                }
            } else if (!this.unsupportedVideoModeWarned) {
                new VErrorDialog(super.remconsObj.ParentApp.dispFrame, this.getLocalString(8225), this.getLocalString(8226), false);
                this.unsupportedVideoModeWarned = true;
            }
        } else if (16 != this.blockHeight) {
            System.out.println("Setting non-halfheight mode");
            this.blockHeight = 16;
            bits_to_read[21] = 7;
            bits_to_read[17] = 7;
            bits_to_read[39] = 7;
            bits_to_read[30] = 7;
        }

    }

    void buildPixelTable(int var1) {
        int var3 = 1 << var1 * 3;
        int[] var10000;
        int var2;
        switch (var1) {
            case 2:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 15) << 6;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 240) << 15;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 3840) << 18;
                }

                return;
            case 3:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 15) << 5;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 240) << 11;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 3840) << 15;
                }

                return;
            case 4:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 15) << 4;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 240) << 8;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 3840) << 12;
                }

                return;
            case 5:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 31) << 3;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 992) << 6;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 31744) << 9;
                }
        }

    }

    void setBitsPerColor(int var1) {
        this.bitsPerColor = 5 - (var1 & 3);
        bits_to_read[8] = this.bitsPerColor;
        bits_to_read[9] = this.bitsPerColor;
        bits_to_read[41] = this.bitsPerColor;
        bits_to_read[42] = this.bitsPerColor;
        this.buildPixelTable(this.bitsPerColor);
    }

    void setVideoDecryption(int var1) {
        switch (var1) {
            case 0:
                super.dvc_encryption = false;
                super.cipher = 0;
                super.remconsObj.setPwrStatusEncLabel(this.getLocalString(12292));
                super.remconsObj.setPwrStatusEnc(0);
                System.out.println("Setting encryption -> None");
                break;
            case 1:
                super.dvc_encryption = true;
                super.remconsObj.setPwrStatusEncLabel(this.getLocalString(12293));
                super.remconsObj.setPwrStatusEnc(1);
                super.dvc_mode = true;
                super.cipher = 1;
                System.out.println("Setting encryption -> RC4 - 128 bit");
                break;
            case 2:
                super.dvc_encryption = true;
                super.remconsObj.setPwrStatusEncLabel(this.getLocalString(12294));
                super.remconsObj.setPwrStatusEnc(1);
                super.dvc_mode = true;
                super.cipher = 2;
                System.out.println("Setting encryption -> AES - 128 bit");
                break;
            case 3:
                super.dvc_encryption = true;
                super.remconsObj.setPwrStatusEncLabel(this.getLocalString(12295));
                super.remconsObj.setPwrStatusEnc(1);
                super.dvc_mode = true;
                super.cipher = 3;
                System.out.println("Setting encryption -> AES - 256 bit");
                break;
            default:
                super.dvc_encryption = false;
                super.remconsObj.setPwrStatusEncLabel(this.getLocalString(12292));
                super.remconsObj.setPwrStatusEnc(0);
                System.out.println("Unsupported encryption");
        }

    }

    public byte mouseButtonState(int var1) {
        byte var2 = 0;
        switch (var1) {
            case 1:
                var2 = (byte) (var2 | 2);
                break;
            case 2:
                var2 = (byte) (var2 | 4);
            case 3:
            default:
                break;
            case 4:
                var2 = (byte) (var2 | 1);
        }

        return var2;
    }

    public byte getMouseButtonState(MouseEvent var1) {
        byte var2 = 0;
        if ((var1.getModifiersEx() & 4096) != 0) {
            var2 = (byte) (var2 | 2);
        }

        if ((var1.getModifiersEx() & 2048) != 0) {
            var2 = (byte) (var2 | 4);
        }

        if ((var1.getModifiersEx() & 1024) != 0) {
            var2 = (byte) (var2 | 1);
        }

        return var2;
    }

    public void sendMouse(MouseEvent var1) {
        new Point(0, 0);
        Point var7 = new Point(0, 0);
        Point var6 = this.getAbsMouseCoordinates(var1);
        char var2 = (char) var6.x;
        char var3 = (char) var6.y;
        if ((var1.getModifiersEx() & 128) > 0) {
            this.mousePrevPosn.x = var2;
            this.mousePrevPosn.y = var3;
        } else if (var2 <= this.screen_x && var3 <= this.screen_y) {
            var7.x = var2 - this.mousePrevPosn.x;
            var7.y = this.mousePrevPosn.y - var3;
            this.mousePrevPosn.x = var2;
            this.mousePrevPosn.y = var3;
            int var4 = var7.x;
            int var5 = var7.y;
            if (var4 < -127) {
                var4 = -127;
            } else if (var4 > 127) {
                var4 = 127;
            }

            if (var5 < -127) {
                var5 = -127;
            } else if (var5 > 127) {
                var5 = 127;
            }

            this.UI_dirty = true;
            if (this.screen_x > 0 && this.screen_y > 0) {
                var2 = (char) (3000 * var2 / this.screen_x);
                var3 = (char) (3000 * var3 / this.screen_y);
            } else {
                var2 = (char) (3000 * var2);
                var3 = (char) (3000 * var3);
            }

            byte[] var8 = new byte[10];
            var8[0] = 2;
            var8[1] = 0;
            var8[2] = (byte) (var2 & 255);
            var8[3] = (byte) (var2 >> 8);
            var8[4] = (byte) (var3 & 255);
            var8[5] = (byte) (var3 >> 8);
            if (var4 < 0) {
                var8[6] = (byte) (var4 & 255);
            } else {
                var8[6] = (byte) (var4 & 255);
            }

            if (var5 < 0) {
                var8[7] = (byte) (var5 & 255);
            } else {
                var8[7] = (byte) (var5 & 255);
            }

            var8[8] = this.getMouseButtonState(var1);
            var8[9] = 0;
            this.transmitb(var8, var8.length);
        }

    }

    private Point getAbsMouseCoordinates(MouseEvent var1) {
        Point var2 = new Point();
        var2.y = var1.getY();
        var2.x = var1.getX();
        return var2;
    }

    public void sendMouseScroll(MouseWheelEvent var1) {
    }

    private void sendAck() {
        byte[] var1 = new byte[]{12, 0};
        String var2 = new String(var1);
        this.transmit(var2);
    }

    public void requestScreenFocus(MouseEvent var1) {
        this.requestFocus();
    }

    public void installKeyboardHook() {
        super.remconsObj.remconsInstallKeyboardHook();
    }

    public void unInstallKeyboardHook() {
        super.remconsObj.remconsUnInstallKeyboardHook();
    }
}
