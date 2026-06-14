package com.hp.ilo2.remcons;


import com.hp.ilo2.virtdevs.VErrorDialog;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

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
    private static final byte[] cursor_none = {(byte) 0};
    private static final int[] cursor_outline = {-8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712, -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0};
    private static final int[] bits_to_read = {0, 1, 1, 1, 1, 1, 2, 3, 5, 5, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1, 1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 5, 5, 1, 8, 8, 1, 4};
    private static final int[] next_0 = {1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1};
    private static final int[] next_1 = {1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1};
    private static final int[] dvc_cc_color = new int[17];
    private static final int[] dvc_cc_usage = new int[17];
    private static final int[] dvc_cc_block = new int[17];
    private static final int[] dvc_lru_lengths = {0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};
    private static final int[] dvc_getmask = {0, 1, 3, 7, 15, 31, 63, 127, 255};
    private static final int[] dvc_reversal = new int[256];
    private static final int[] dvc_left = new int[256];
    private static final int[] dvc_right = new int[256];
    private static final int[] block = new int[256];
    private static final int[] cmd_p_buff = new int[256];
    private static final char last_bits = (char) 0;
    private static final char last_bits2 = (char) 0;
    private static final char last_bits3 = (char) 0;
    private static final char last_bits4 = (char) 0;
    private static final char last_bits5 = (char) 0;
    private static final char last_bits6 = (char) 0;
    private static final char last_bits7 = (char) 0;
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
    private static int dvc_pixel_count = 0;
    private static int dvc_size_x = 0;
    private static int dvc_size_y = 0;
    private static int dvc_y_clipped = 0;
    private static int dvc_lastx = 0;
    private static int dvc_lasty = 0;
    private static int dvc_newx = 0;
    private static int dvc_newy = 0;
    private static int dvc_color = 0;
    private static int dvc_last_color = 0;
    private static int dvc_ib_acc = 0;
    private static int dvc_ib_bcnt = 0;
    private static int dvc_zero_count = 0;
    private static int dvc_decoder_state = 0;
    private static int dvc_next_state = 0;
    private static int dvc_pixcode = 38;
    private static int dvc_code = 0;
    private static int dvc_red = 0;
    private static int dvc_green = 0;
    private static int dvc_blue = 0;
    private static int fatal_count = 0;
    private static int printchan = 0;
    private static String printstring = "";
    private static long count_bytes = 0L;
    private static int cmd_p_count = 0;
    private static int cmd_last = 0;
    private static int framerate = 30;
    private static boolean debug_msgs = false;
    private static char dvc_new_bits = ' ';
    private static int debug_lastx = 0;
    private static int debug_lasty = 0;
    private static int debug_show_block = 0;
    private static long timeout_count = 0L;
    private static long dvc_counter_bits = 0L;
    private static boolean dvc_process_inhibit = false;
    private static boolean video_detected = true;
    private static final boolean sending_encrypt_command = false;
    private static final int blockWidth = 16;
    public final int[] color_remap_table = new int[(int) '耀'];
    public boolean UI_dirty = false;
    public final byte[] encrypt_key = new byte[16];
    public final Point mousePrevPosn = new Point(0, 0);
    protected final MouseSync mouse_sync = new MouseSync(this);
    protected Cursor current_cursor;
    private char prev_char = ' ';
    private boolean disable_kbd = false;
    private boolean altlock = false;
    private int scale_x = 1;
    private int scale_y = 1;
    private int screen_x = 1;
    private int screen_y = 1;
    private int mouse_protocol = 0;
    private RC4 RC4encrypter = null;
    private Aes Aes128encrypter = null;
    private Aes Aes256encrypter = null;
    private int key_index = 0;
    private int bitsPerColor = 5;
    private byte mouseBtnState = (byte) 0;
    private boolean ignore_next_key = false;
    private int blockHeight = 16;
    private boolean unsupportedVideoModeWarned = false;

    public cim(final remcons var1) {
        super(var1);
        cim.dvc_reversal[255] = 0;
        this.current_cursor = Cursor.getDefaultCursor();
        super.screen.addMouseListener(this.mouse_sync);
        super.screen.addMouseMotionListener(this.mouse_sync);
        super.screen.addMouseWheelListener(this.mouse_sync);
        this.mouse_sync.setListener(this);
    }

    public static String byteToHex(final byte var0) {
        return String.valueOf(cim.toHexChar((int) var0 >>> 4 & 15)) +
                cim.toHexChar((int) var0 & 15);
    }

    public static String intToHex(final int var0) {
        final byte var1 = (byte) var0;
        return cim.byteToHex(var1);
    }

    public static String intToHex4(final int var0) {
        return cim.byteToHex((byte) (var0 / 256)) +
                cim.byteToHex((byte) (var0 & 255));
    }

    public static String charToHex(final char var0) {
        return cim.intToHex((int) var0);
    }

    public static char toHexChar(final int var0) {
        return 0 <= var0 && 9 >= var0 ? (char) (48 + var0) : (char) (65 + (var0 - 10));
    }

    public String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = super.remconsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("cim:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public void setup_encryption(final byte[] var1, final int var2) {
        System.arraycopy(var1, 0, this.encrypt_key, 0, 16);
        this.RC4encrypter = new RC4(var1);
        this.Aes128encrypter = new Aes(0, var1);
        this.Aes256encrypter = new Aes(0, var1);
        this.key_index = var2;
    }

    public void reinit_vars() {
        super.reinit_vars();
        cim.dvc_code = 0;
        cim.dvc_ib_acc = 0;
        cim.dvc_ib_bcnt = 0;
        cim.dvc_counter_bits = 0L;
        this.prev_char = ' ';
        this.disable_kbd = false;
        this.altlock = false;
        cim.dvc_reversal[255] = 0;
        this.scale_x = 1;
        this.scale_y = 1;
        this.mouse_sync.restart();
        cim.dvc_process_inhibit = false;
    }

    public void enable_debug() {
        cim.debug_msgs = true;
        super.enable_debug();
        this.mouse_sync.enableDebug();
    }

    public void disable_debug() {
        cim.debug_msgs = false;
        super.disable_debug();
        this.mouse_sync.disableDebug();
    }

    public void sync_start() {
        this.mouse_sync.sync();
    }

    public void serverMove(final int var1, final int var2, int var3, int var4) {
        this.UI_dirty = true;
        if (0 < this.screen_x && 0 < this.screen_y) {
            var3 = 3000 * var3 / this.screen_x;
            var4 = 3000 * var4 / this.screen_y;
        } else {
            var3 = 3000 * var3;
            var4 = 3000 * var4;
        }

        final byte[] var9 = {(byte) 2, (byte) 0, (byte) (var3 & 255), (byte) (var3 >> 8), (byte) (var4 & 255), (byte) (var4 >> 8), (byte) 0, (byte) 0, this.mouseBtnState, (byte) 0};
        final String var10 = new String(var9);
        this.transmit(var10);
    }

    public void mouse_mode_change(final boolean var1) {
    }

    public synchronized void mouseEntered(final MouseEvent var1) {
        this.UI_dirty = true;
        super.mouseEntered(var1);
    }

    public void serverPress(final int var1) {
        this.UI_dirty = true;
        this.send_mouse_press(var1);
    }

    public void serverRelease(final int var1) {
        this.UI_dirty = true;
        this.send_mouse_release(var1);
    }

    public void serverClick(final int var1, final int var2) {
        this.UI_dirty = true;
        this.send_mouse_click(var1, var2);
        this.mouseBtnState = cim.mouseButtonState(var1);
    }

    public synchronized void mouseExited(final MouseEvent var1) {
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

    public synchronized void connect(final String var1, final String var2, final int var3, final int var4, final int var5, final remcons var6) {
        super.connect(var1, var2, var3, var4, var5, var6);
    }

    public synchronized void transmit(final String var1) {
        if (null != super.out && null != var1) {
            if (!var1.isEmpty()) {
                final byte[] var3 = new byte[var1.length()];

                for (int var4 = 0; var4 < var1.length(); ++var4) {
                    var3[var4] = (byte) var1.charAt(var4);
                    if (super.dvc_encryption) {
                        final char var2;
                        switch (super.cipher) {
                            case 1:
                                var2 = (char) (this.RC4encrypter.randomValue() & 255);
                                var3[var4] = (byte) ((int) var3[var4] ^ (int) var2);
                                break;
                            case 2:
                                var2 = (char) ((int) this.Aes128encrypter.randomValue() & 255);
                                var3[var4] = (byte) ((int) var3[var4] ^ (int) var2);
                                break;
                            case 3:
                                var2 = (char) ((int) this.Aes256encrypter.randomValue() & 255);
                                var3[var4] = (byte) ((int) var3[var4] ^ (int) var2);
                                break;
                            default:
                                System.out.println("Unknown encryption");
                        }

                        var3[var4] = (byte) ((int) var3[var4] & 255);
                    }
                }

                try {
                    super.out.write(var3, 0, var3.length);
                } catch (final IOException var6) {
                    System.out.println("telnet.transmit() IOException: " + var6);
                }
            }

        }
    }

    public synchronized void transmitb(final byte[] var1, final int var2) {
        final byte[] var5 = new byte[var2];
        System.arraycopy(var1, 0, var5, 0, var2);

        for (int var3 = 0; var3 < var2; ++var3) {
            if (super.dvc_encryption) {
                final char var4;
                switch (super.cipher) {
                    case 1:
                        var4 = (char) (this.RC4encrypter.randomValue() & 255);
                        var5[var3] = (byte) ((int) var5[var3] ^ (int) var4);
                        break;
                    case 2:
                        var4 = (char) ((int) this.Aes128encrypter.randomValue() & 255);
                        var5[var3] = (byte) ((int) var5[var3] ^ (int) var4);
                        break;
                    case 3:
                        var4 = (char) ((int) this.Aes256encrypter.randomValue() & 255);
                        var5[var3] = (byte) ((int) var5[var3] ^ (int) var4);
                        break;
                    default:
                        System.out.println("Unknown encryption");
                }

                var5[var3] = (byte) ((int) var5[var3] & 255);
            }
        }

        try {
            if (null != super.out) {
                super.out.write(var5, 0, var2);
            }
        } catch (final IOException var7) {
            System.out.println("telnet.transmitb() IOException: " + var7);
        }

    }

    protected synchronized String translate_key(final KeyEvent var1) {
        String var2 = "";
        final char var3 = var1.getKeyChar();
        byte var4 = (byte) 0;
        boolean var5 = true;
        if (this.disable_kbd) {
            return "";
        } else if (this.ignore_next_key) {
            this.ignore_next_key = false;
            return "";
        } else {
            this.UI_dirty = true;
            if (var1.isShiftDown()) {
                var4 = (byte) 1;
            } else if (var1.isControlDown()) {
                var4 = (byte) 2;
            } else if (this.altlock || var1.isAltDown()) {
                var4 = (byte) 3;
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

            if (var5 && !var2.isEmpty() && 3 == (int) var4) {
                var2 = "\u001b[1" + var2;
            }

            return var2;
        }
    }

    protected synchronized String translate_special_key(final KeyEvent var1) {
        String var2 = "";
        boolean var3 = true;
        byte var4 = (byte) 0;
        if (this.disable_kbd) {
            return "";
        } else {
            this.UI_dirty = true;
            if (var1.isShiftDown()) {
                var4 = (byte) 1;
            } else if (var1.isControlDown()) {
                var4 = (byte) 2;
            } else if (this.altlock || var1.isAltDown()) {
                var4 = (byte) 3;
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
                    break;
                case 155:
                    var2 = "\u001b[L";
                    break;
                default:
                    var3 = false;
                    var2 = super.translate_special_key(var1);
            }

            if (!var2.isEmpty() && var3) {
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

    protected synchronized String translate_special_key_release(final KeyEvent var1) {
        final String var2;
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

        if (127 < var3) {
            var2 = "" + (char) var3;
        } else {
            var2 = "";
        }

        return var2;
    }

    public void send_ctrl_alt_del() {
        final byte[] var1 = {(byte) 1, (byte) 0, (byte) 5, (byte) 0, (byte) 76, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var6) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = (byte) 0;
        final String var3 = new String(var1);
        this.transmit(var3);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var5) {
            System.out.println("Thread interrupted..");
        }

        var1[2] = (byte) 0;
        final String var4 = new String(var1);
        this.transmit(var4);
        this.requestFocus();
    }

    public void send_num_lock() {
        System.out.println("sending num lock");
        final byte[] var1 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 83, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var4) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = (byte) 0;
        final String var3 = new String(var1);
        this.transmit(var3);
    }

    public void send_caps_lock() {
        System.out.println("sending caps lock");
        final byte[] var1 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 57, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var4) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = (byte) 0;
        final String var3 = new String(var1);
        this.transmit(var3);
    }

    public void send_ctrl_alt_back() {
        final byte[] var1 = {(byte) 1, (byte) 0, (byte) 5, (byte) 0, (byte) 42, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var6) {
            System.out.println("Thread interrupted..");
        }

        var1[4] = (byte) 0;
        final String var3 = new String(var1);
        this.transmit(var3);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var5) {
            System.out.println("Thread interrupted..");
        }

        var1[2] = (byte) 0;
        final String var4 = new String(var1);
        this.transmit(var4);
        this.requestFocus();
    }

    public void send_ctrl_alt_fn(final int var1) {
        final byte[] var2 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final byte var9;
        switch (var1 + 1) {
            case 1:
                var9 = (byte) 58;
                break;
            case 2:
                var9 = (byte) 59;
                break;
            case 3:
                var9 = (byte) 60;
                break;
            case 4:
                var9 = (byte) 61;
                break;
            case 5:
                var9 = (byte) 62;
                break;
            case 6:
                var9 = (byte) 63;
                break;
            case 7:
                var9 = (byte) 64;
                break;
            case 8:
                var9 = (byte) 65;
                break;
            case 9:
                var9 = (byte) 66;
                break;
            case 10:
                var9 = (byte) 67;
                break;
            case 11:
                var9 = (byte) 68;
                break;
            case 12:
                var9 = (byte) 69;
                break;
            default:
                var9 = (byte) 64;
        }

        var2[2] = (byte) 5;
        var2[4] = var9;
        final String var4 = new String(var2);
        this.transmit(var4);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var8) {
            System.out.println("Thread interrupted..");
        }

        var2[4] = (byte) 0;
        final String var5 = new String(var2);
        this.transmit(var5);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var7) {
            System.out.println("Thread interrupted..");
        }

        var2[2] = (byte) 0;
        final String var6 = new String(var2);
        this.transmit(var6);
        this.requestFocus();
    }

    public void send_alt_fn(final int var1) {
        final byte[] var2 = {(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final byte var9;
        switch (var1 + 1) {
            case 1:
                var9 = (byte) 58;
                break;
            case 2:
                var9 = (byte) 59;
                break;
            case 3:
                var9 = (byte) 60;
                break;
            case 4:
                var9 = (byte) 61;
                break;
            case 5:
                var9 = (byte) 62;
                break;
            case 6:
                var9 = (byte) 63;
                break;
            case 7:
                var9 = (byte) 64;
                break;
            case 8:
                var9 = (byte) 65;
                break;
            case 9:
                var9 = (byte) 66;
                break;
            case 10:
                var9 = (byte) 67;
                break;
            case 11:
                var9 = (byte) 68;
                break;
            case 12:
                var9 = (byte) 69;
                break;
            default:
                var9 = (byte) 64;
        }

        var2[2] = (byte) 4;
        var2[4] = var9;
        final String var4 = new String(var2);
        this.transmit(var4);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var8) {
            System.out.println("Thread interrupted..");
        }

        var2[4] = (byte) 0;
        final String var5 = new String(var2);
        this.transmit(var5);

        try {
            Thread.sleep(250L);
        } catch (final InterruptedException var7) {
            System.out.println("Thread interrupted..");
        }

        var2[2] = (byte) 0;
        final String var6 = new String(var2);
        this.transmit(var6);
        this.requestFocus();
    }

    public void sendMomPress() {
        super.post_complete = false;
        final byte[] var1 = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);
    }

    public void sendPressHold() {
        super.post_complete = false;
        final byte[] var1 = {(byte) 0, (byte) 0, (byte) 1, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);
    }

    public void sendPowerCycle() {
        super.post_complete = false;
        final byte[] var1 = {(byte) 0, (byte) 0, (byte) 2, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);
    }

    public void sendSystemReset() {
        super.post_complete = false;
        final byte[] var1 = {(byte) 0, (byte) 0, (byte) 3, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);
    }

    public void send_mouse_press(final int var1) {
    }

    public void send_mouse_release(final int var1) {
    }

    public void send_mouse_click(final int var1, final int var2) {
    }

    public void send_mouse_byte(final int var1) {
    }

    public void refresh_screen() {
        final byte[] var1 = {(byte) 5, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);
        this.requestFocus();
    }

    public void send_keep_alive_msg() {
    }

    protected synchronized void set_framerate(final int var1) {
        cim.framerate = var1;
        super.screen.set_framerate(var1);
        this.set_status(3, "" + cim.framerate);
    }

    protected static void show_error(final String var1) {
        System.out.println("dvc:" + var1 + ": state " + cim.dvc_decoder_state + " code " + cim.dvc_code);
        System.out.println("dvc:error at byte count " + cim.count_bytes);
    }

    static void cache_reset() {
        cim.dvc_cc_active = 0;
    }

    static int cache_lru(final int var1) {
        int var4 = cim.dvc_cc_active;
        int var3 = 0;
        byte var6 = (byte) 0;

        int var2;
        for (var2 = 0; var2 < var4; ++var2) {
            if (var1 == cim.dvc_cc_color[var2]) {
                var3 = var2;
                var6 = (byte) 1;
                break;
            }

            if (cim.dvc_cc_usage[var2] == var4 - 1) {
                var3 = var2;
            }
        }

        int var5 = cim.dvc_cc_usage[var3];
        if (0 == (int) var6) {
            if (17 > var4) {
                var3 = var4;
                var5 = var4++;
                cim.dvc_cc_active = var4;
                if (2 > cim.dvc_cc_active) {
                    cim.dvc_pixcode = 38;
                } else if (2 == cim.dvc_cc_active) {
                    cim.dvc_pixcode = 4;
                } else if (3 == cim.dvc_cc_active) {
                    cim.dvc_pixcode = 5;
                } else if (6 > cim.dvc_cc_active) {
                    cim.dvc_pixcode = 6;
                } else if (10 > cim.dvc_cc_active) {
                    cim.dvc_pixcode = 7;
                } else {
                    cim.dvc_pixcode = 32;
                }

                cim.next_1[31] = cim.dvc_pixcode;
            }

            cim.dvc_cc_color[var3] = var1;
        }

        cim.dvc_cc_block[var3] = 1;

        for (var2 = 0; var2 < var4; ++var2) {
            if (cim.dvc_cc_usage[var2] < var5) {
                cim.dvc_cc_usage[var2]++;
            }
        }

        cim.dvc_cc_usage[var3] = 0;
        return (int) var6;
    }

    static int cache_find(final int var1) {
        final int var2 = cim.dvc_cc_active;

        for (int var3 = 0; var3 < var2; ++var3) {
            if (var1 == cim.dvc_cc_usage[var3]) {
                final int var5 = cim.dvc_cc_color[var3];
                final int var4 = var3;

                for (var3 = 0; var3 < var2; ++var3) {
                    if (cim.dvc_cc_usage[var3] < var1) {
                        cim.dvc_cc_usage[var3]++;
                    }
                }

                cim.dvc_cc_usage[var4] = 0;
                cim.dvc_cc_block[var4] = 1;
                return var5;
            }
        }

        return -1;
    }

    static void cache_prune() {
        int var2 = cim.dvc_cc_active;
        int var1 = 0;

        while (var1 < var2) {
            final int var3 = cim.dvc_cc_block[var1];
            if (0 == var3) {
                --var2;
                cim.dvc_cc_block[var1] = cim.dvc_cc_block[var2];
                cim.dvc_cc_color[var1] = cim.dvc_cc_color[var2];
                cim.dvc_cc_usage[var1] = cim.dvc_cc_usage[var2];
            } else {
                cim.dvc_cc_block[var1]--;
                ++var1;
            }
        }

        cim.dvc_cc_active = var2;
        if (2 > cim.dvc_cc_active) {
            cim.dvc_pixcode = 38;
        } else if (2 == cim.dvc_cc_active) {
            cim.dvc_pixcode = 4;
        } else if (3 == cim.dvc_cc_active) {
            cim.dvc_pixcode = 5;
        } else if (6 > cim.dvc_cc_active) {
            cim.dvc_pixcode = 6;
        } else if (10 > cim.dvc_cc_active) {
            cim.dvc_pixcode = 7;
        } else {
            cim.dvc_pixcode = 32;
        }

        cim.next_1[31] = cim.dvc_pixcode;
    }

    protected void next_block(int var1) {
        final boolean var4 = cim.video_detected;

        int var3;
        if (0 != cim.dvc_pixel_count && 0 < cim.dvc_y_clipped && cim.dvc_lasty == cim.dvc_size_y) {
            final int var5 = this.color_remap_table[0];

            for (var3 = cim.dvc_y_clipped; 256 > var3; ++var3) {
                cim.block[var3] = var5;
            }
        }

        cim.dvc_pixel_count = 0;
        cim.dvc_next_state = 1;
        int var2 = cim.dvc_lastx * this.blockWidth;

        for (var3 = cim.dvc_lasty * this.blockHeight; 0 != var1; --var1) {
            if (var4) {
                super.screen.paste_array(cim.block, var2, var3, 16, this.blockHeight);
            }

            ++cim.dvc_lastx;
            var2 += 16;
            if (cim.dvc_lastx >= cim.dvc_size_x) {
                break;
            }
        }

    }

    protected static void init_reversal() {
        for (int var1 = 0; 256 > var1; ++var1) {
            int var6 = 8;
            int var5 = 8;
            int var3 = var1;
            int var4 = 0;

            for (int var2 = 0; 8 > var2; ++var2) {
                var4 <<= 1;
                if (1 == (var3 & 1)) {
                    if (var6 > var2) {
                        var6 = var2;
                    }

                    var4 |= 1;
                    var5 = 7 - var2;
                }

                var3 >>= 1;
            }

            cim.dvc_reversal[var1] = var4;
            cim.dvc_right[var1] = var6;
            cim.dvc_left[var1] = var5;
        }

    }

    static int add_bits(final char var1) {
        cim.dvc_zero_count += cim.dvc_right[(int) var1];
        cim.dvc_ib_acc |= (int) var1 << cim.dvc_ib_bcnt;
        cim.dvc_ib_bcnt += 8;
        if (30 < cim.dvc_zero_count) {
            if (cim.debug_msgs && 38 == cim.dvc_decoder_state && 40 > cim.fatal_count && 0 < cim.fatal_count) {
            }

            cim.dvc_next_state = 43;
            cim.dvc_decoder_state = 43;
            return 4;
        } else {
            if (0 != (int) var1) {
                cim.dvc_zero_count = cim.dvc_left[(int) var1];
            }

            return 0;
        }
    }

    static int get_bits(final int var1) {
        if (1 == var1) {
            cim.dvc_code = cim.dvc_ib_acc & 1;
            cim.dvc_ib_acc >>= 1;
            --cim.dvc_ib_bcnt;
            return 0;
        } else if (0 == var1) {
            return 0;
        } else {
            int var2 = cim.dvc_ib_acc & cim.dvc_getmask[var1];
            cim.dvc_ib_bcnt -= var1;
            cim.dvc_ib_acc >>= var1;
            var2 = cim.dvc_reversal[var2];
            var2 >>= 8 - var1;
            cim.dvc_code = var2;
            return 0;
        }
    }

    int process_bits(final char var1) {
        byte var6 = (byte) 0;
        cim.add_bits(var1);
        cim.dvc_new_bits = var1;
        ++cim.count_bytes;

        while (true) {
            if (0 == (int) var6) {
                final int var8 = cim.bits_to_read[cim.dvc_decoder_state];
                if (var8 <= cim.dvc_ib_bcnt) {
                    cim.get_bits(var8);
                    cim.dvc_counter_bits = cim.dvc_counter_bits + (long) var8;
                    if (0 == cim.dvc_code) {
                        cim.dvc_next_state = cim.next_0[cim.dvc_decoder_state];
                    } else {
                        cim.dvc_next_state = cim.next_1[cim.dvc_decoder_state];
                    }

                    int var4;
                    label255:
                    switch (cim.dvc_decoder_state) {
                        case 0:
                            cim.cache_reset();
                            cim.dvc_pixel_count = 0;
                            cim.dvc_lastx = 0;
                            cim.dvc_lasty = 0;
                            cim.dvc_red = 0;
                            cim.dvc_green = 0;
                            cim.dvc_blue = 0;
                            cim.fatal_count = 0;
                            cim.timeout_count = -1L;
                            cim.cmd_p_count = 0;
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
                            if (1 == cim.dvc_cc_active) {
                                cim.dvc_code = cim.dvc_cc_usage[0];
                            } else if (4 == cim.dvc_decoder_state) {
                                cim.dvc_code = 0;
                            } else if (3 == cim.dvc_decoder_state) {
                                cim.dvc_code = 1;
                            } else if (0 != cim.dvc_code) {
                                ++cim.dvc_code;
                            }

                            cim.dvc_color = cim.cache_find(cim.dvc_code);
                            if (-1 == cim.dvc_color) {
                                cim.dvc_next_state = 38;
                            } else {
                                cim.dvc_last_color = this.color_remap_table[cim.dvc_color];
                                if (cim.dvc_pixel_count < this.blockHeight * this.blockWidth) {
                                    cim.block[cim.dvc_pixel_count] = cim.dvc_last_color;
                                    ++cim.dvc_pixel_count;
                                } else {
                                    cim.dvc_next_state = 38;
                                }
                            }
                            break;
                        case 8:
                            cim.dvc_red = cim.dvc_code << (this.bitsPerColor << 1);
                            cim.dvc_green = cim.dvc_code << this.bitsPerColor;
                        case 42:
                            cim.dvc_blue = cim.dvc_code;
                            cim.dvc_color = cim.dvc_red | cim.dvc_green | cim.dvc_blue;
                            final int var2 = cim.cache_lru(cim.dvc_color);
                            if (0 == var2) {
                                cim.dvc_last_color = this.color_remap_table[cim.dvc_color];
                                if (cim.dvc_pixel_count < this.blockHeight * this.blockWidth) {
                                    cim.block[cim.dvc_pixel_count] = cim.dvc_last_color;
                                    ++cim.dvc_pixel_count;
                                } else {
                                    cim.dvc_next_state = 38;
                                }
                            } else {
                                if (cim.debug_msgs && 6L < cim.count_bytes) {
                                }

                                cim.dvc_next_state = 38;
                            }
                            break;
                        case 9:
                            cim.dvc_red = cim.dvc_code << (this.bitsPerColor << 1);
                            break;
                        case 12:
                            if (7 == cim.dvc_code) {
                                cim.dvc_next_state = 14;
                            } else if (6 == cim.dvc_code) {
                                cim.dvc_next_state = 13;
                            } else {
                                cim.dvc_code += 2;
                                var4 = 0;

                                while (true) {
                                    if (var4 >= cim.dvc_code) {
                                        break label255;
                                    }

                                    if (cim.dvc_pixel_count >= this.blockHeight * this.blockWidth) {
                                        cim.dvc_next_state = 38;
                                        break label255;
                                    }

                                    cim.block[cim.dvc_pixel_count] = cim.dvc_last_color;
                                    ++cim.dvc_pixel_count;
                                    ++var4;
                                }
                            }
                            break;
                        case 13:
                            cim.dvc_code += 8;
                        case 14:
                            if (cim.debug_msgs && 14 == cim.dvc_decoder_state && 16 > cim.dvc_code) {
                            }

                            var4 = 0;

                            while (true) {
                                if (var4 >= cim.dvc_code) {
                                    break label255;
                                }

                                if (cim.dvc_pixel_count >= this.blockHeight * this.blockWidth) {
                                    cim.dvc_next_state = 38;
                                    break label255;
                                }

                                cim.block[cim.dvc_pixel_count] = cim.dvc_last_color;
                                ++cim.dvc_pixel_count;
                                ++var4;
                            }
                        case 17:
                        case 26:
                            cim.dvc_newx = cim.dvc_code;
                            if (17 == cim.dvc_decoder_state && cim.dvc_newx > cim.dvc_size_x) {
                                if (cim.debug_msgs) {
                                }

                                cim.dvc_newx = 0;
                            }
                            break;
                        case 20:
                            cim.dvc_code = cim.dvc_lastx + cim.dvc_code + 1;
                            if (cim.dvc_code > cim.dvc_size_x && cim.debug_msgs) {
                            }
                        case 21:
                            cim.dvc_lastx = cim.dvc_code;
                            if (16 == this.blockHeight) {
                                cim.dvc_lastx &= 127;
                            }

                            if (cim.dvc_lastx > cim.dvc_size_x && cim.debug_msgs) {
                            }
                            break;
                        case 24:
                            if (0 != cim.cmd_p_count) {
                                cim.cmd_p_buff[cim.cmd_p_count - 1] = cim.cmd_last;
                            }

                            ++cim.cmd_p_count;
                            cim.cmd_last = cim.dvc_code;
                            break;
                        case 27:
                            if (cim.timeout_count == cim.count_bytes - 1L) {
                                cim.dvc_next_state = 38;
                            }

                            if (0 != (cim.dvc_ib_bcnt & 7)) {
                                cim.get_bits(cim.dvc_ib_bcnt & 7);
                            }

                            cim.timeout_count = cim.count_bytes;
                            super.screen.repaint_it(1);
                            break;
                        case 29:
                            cim.dvc_code += 2;
                        case 30:
                            this.next_block(cim.dvc_code);
                            break;
                        case 33:
                            if (cim.dvc_pixel_count < this.blockHeight * this.blockWidth) {
                                cim.block[cim.dvc_pixel_count] = cim.dvc_last_color;
                                ++cim.dvc_pixel_count;
                            } else {
                                cim.dvc_next_state = 38;
                            }
                            break;
                        case 34:
                            this.next_block(1);
                            break;
                        case 35:
                            cim.dvc_next_state = cim.dvc_pixcode;
                            break;
                        case 37:
                            return 1;
                        case 38:
                            if (0 == cim.fatal_count) {
                                cim.debug_lastx = cim.dvc_lastx;
                                cim.debug_lasty = cim.dvc_lasty;
                                cim.debug_show_block = 1;
                            }

                            if (40 == cim.fatal_count) {
                                this.refresh_screen();
                            }

                            if (11680 == cim.fatal_count) {
                                this.refresh_screen();
                            }

                            ++cim.fatal_count;
                            if (120000 == cim.fatal_count) {
                                this.refresh_screen();
                            }

                            if (12000000 == cim.fatal_count) {
                                this.refresh_screen();
                                cim.fatal_count = 41;
                            }
                            break;
                        case 39:
                            cim.dvc_newy = cim.dvc_code;
                            if (16 == this.blockHeight) {
                                cim.dvc_newy &= 127;
                            }

                            cim.dvc_lastx = cim.dvc_newx;
                            cim.dvc_lasty = cim.dvc_newy;
                            if (cim.dvc_lasty > cim.dvc_size_y && cim.debug_msgs) {
                            }

                            super.screen.repaint_it(1);
                            break;
                        case 40:
                            cim.dvc_size_x = cim.dvc_newx;
                            cim.dvc_size_y = cim.dvc_code;
                            break;
                        case 41:
                            cim.dvc_green = cim.dvc_code << this.bitsPerColor;
                            break;
                        case 43:
                            if (cim.dvc_next_state != cim.dvc_decoder_state) {
                                cim.dvc_ib_bcnt = 0;
                                cim.dvc_ib_acc = 0;
                                cim.dvc_zero_count = 0;
                                cim.count_bytes = 0L;
                            }
                            break;
                        case 44:
                            cim.printchan = cim.dvc_code;
                            cim.printstring = "";
                            break;
                        case 45:
                            if (0 == cim.dvc_code) {
                                switch (cim.printchan) {
                                    case 1:
                                    case 2:
                                        this.set_status(2 + cim.printchan, cim.printstring);
                                        break;
                                    case 3:
                                        System.out.println(cim.printstring);
                                        break;
                                    case 4:
                                        super.screen.show_text(cim.printstring);
                                }

                                cim.dvc_next_state = 1;
                            } else {
                                cim.printstring = cim.printstring + (char) cim.dvc_code;
                            }
                            break;
                        case 46:
                            if (0 == cim.dvc_code) {
                                switch (cim.cmd_last) {
                                    case 1:
                                        cim.dvc_next_state = 37;
                                        break;
                                    case 2:
                                        cim.dvc_next_state = 44;
                                        break;
                                    case 3:
                                        if (0 == cim.cmd_p_count) {
                                            this.set_framerate(0);
                                        } else {
                                            this.set_framerate(cim.cmd_p_buff[0]);
                                        }
                                        break;
                                    case 4:
                                        super.remconsObj.setPwrStatusPower(1);
                                        break;
                                    case 5:
                                        super.remconsObj.setPwrStatusPower(0);
                                        super.screen.clearScreen();
                                        cim.dvc_newx = 50;
                                        cim.dvc_code = 38;
                                        break;
                                    case 6:
                                        super.screen.clearScreen();
                                        if (!cim.video_detected) {
                                            super.screen.clearScreen();
                                        }

                                        this.set_status(2, this.getLocalString(12290));
                                        this.set_status(1, " ");
                                        this.set_status(3, " ");
                                        this.set_status(4, " ");
                                        super.post_complete = false;
                                        break;
                                    case 7:
                                        super.ts_type = cim.cmd_p_buff[0];
                                    case 8:
                                    default:
                                        break;
                                    case 9:
                                        System.out.println("received keychg and cleared bits\n");
                                        if (0 != (cim.dvc_ib_bcnt & 7)) {
                                            cim.get_bits(cim.dvc_ib_bcnt & 7);
                                        }
                                        break;
                                    case 10:
                                        this.seize();
                                        break;
                                    case 11:
                                        System.out.println("Setting bpc to  " + cim.cmd_p_buff[0]);
                                        this.setBitsPerColor(cim.cmd_p_buff[0]);
                                        break;
                                    case 12:
                                        System.out.println("Setting encryption to  " + cim.cmd_p_buff[0]);
                                        this.setVideoDecryption(cim.cmd_p_buff[0]);
                                        break;
                                    case 13:
                                        System.out.println("Header received ");
                                        this.setBitsPerColor(cim.cmd_p_buff[0]);
                                        this.setVideoDecryption(cim.cmd_p_buff[1]);
                                        super.remconsObj.SetLicensed(cim.cmd_p_buff[2]);
                                        super.remconsObj.SetFlags(cim.cmd_p_buff[3]);
                                        break;
                                    case 16:
                                        this.sendAck();
                                        break;
                                    case 128:
                                        super.screen.invalidate();
                                        super.screen.repaint();
                                }

                                cim.cmd_p_count = 0;
                            }
                            break;
                        case 47:
                            cim.dvc_lastx = 0;
                            cim.dvc_lasty = 0;
                            cim.dvc_pixel_count = 0;
                            cim.cache_reset();
                            this.scale_x = 1;
                            this.scale_y = 1;
                            this.screen_x = cim.dvc_size_x * this.blockWidth;
                            this.screen_y = (cim.dvc_size_y << 4) + cim.dvc_code;
                            cim.video_detected = 0 != this.screen_x && 0 != this.screen_y;

                            if (0 < cim.dvc_code) {
                                cim.dvc_y_clipped = 256 - 16 * cim.dvc_code;
                            } else {
                                cim.dvc_y_clipped = 0;
                            }

                            if (cim.video_detected) {
                                super.screen.set_abs_dimensions(this.screen_x, this.screen_y);
                                this.SetHalfHeight();
                                this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
                                this.set_status(2, this.getLocalString(12291) + this.screen_x + "x" + this.screen_y);
                                this.set_status(1, " ");
                            } else {
                                super.screen.clearScreen();
                                this.set_status(2, this.getLocalString(12290));
                                this.set_status(1, " ");
                                this.set_status(3, " ");
                                this.set_status(4, " ");
                                System.out.println("No video. image_source = " + (null == super.screen.image_source ? "null" : "set"));
                                super.post_complete = false;
                            }
                    }

                    if (2 == cim.dvc_next_state && cim.dvc_pixel_count == this.blockHeight * this.blockWidth) {
                        this.next_block(1);
                        cim.cache_prune();
                    }

                    if (cim.dvc_decoder_state == cim.dvc_next_state && 45 != cim.dvc_decoder_state && 38 != cim.dvc_decoder_state && 43 != cim.dvc_decoder_state) {
                        System.out.println("Machine hung in state " + cim.dvc_decoder_state);
                        var6 = (byte) 6;
                        continue;
                    }

                    cim.dvc_decoder_state = cim.dvc_next_state;
                    continue;
                }

                var6 = (byte) 0;
            }

            return (int) var6;
        }
    }

    boolean process_dvc(final char var1) {
        if (0 == cim.dvc_reversal[255]) {
            System.out.println("dvc initializing");
            cim.init_reversal();
            cim.cache_reset();
            cim.dvc_decoder_state = 0;
            cim.dvc_next_state = 0;
            cim.dvc_zero_count = 0;
            cim.dvc_ib_acc = 0;
            cim.dvc_ib_bcnt = 0;
            this.buildPixelTable(this.bitsPerColor);
            this.SetHalfHeight();
        }

        final int var2;
        if (cim.dvc_process_inhibit) {
            var2 = 0;
        } else {
            var2 = this.process_bits(var1);
        }

        if (0 != var2) {
            System.out.println("Exit from DVC mode status =" + var2);
            System.out.println("Current block at " + cim.dvc_lastx + " " + cim.dvc_lasty);
            System.out.println("Byte count " + cim.count_bytes);
            cim.dvc_decoder_state = 38;
            cim.dvc_next_state = 38;
            cim.fatal_count = 0;
            this.refresh_screen();
        }

        return true;
    }

    public void set_sig_colors(final int[] var1) {
    }

    public void change_key() {
        this.RC4encrypter.update_key();
        super.change_key();
    }

    public void set_mouse_protocol(final int var1) {
        this.mouse_protocol = var1;
    }

    static Cursor customCursor(final Image var1, final Point var2) {
        Cursor var4 = null;

        try {
            final Toolkit var5 = Toolkit.getDefaultToolkit();
            var4 = var5.createCustomCursor(var1, var2, "rcCursor");
        } catch (final Exception var8) {
            System.out.println("This JVM cannot create custom cursors");
        }

        return var4;
    }

    Cursor createCursor(final int var1) {
        final Toolkit var7 = Toolkit.getDefaultToolkit();
        final MemoryImageSource var2;
        final Image var3;
        final int[] var6;
        switch (var1) {
            case 0:
                return Cursor.getDefaultCursor();
            case 1:
                return Cursor.getPredefinedCursor(1);
            case 2:
                var3 = var7.createImage(cim.cursor_none);
                break;
            case 3:
                var6 = new int[1024];
                var6[0] = var6[1] = var6[32] = var6[33] = -8355712;
                var2 = new MemoryImageSource(32, 32, var6, 0, 32);
                var3 = this.createImage(var2);
                break;
            case 4:
                var6 = new int[1024];

                for (int var8 = 0; 21 > var8; ++var8) {
                    System.arraycopy(cim.cursor_outline, var8 * 12, var6, var8 << 5, 12);
                }

                var2 = new MemoryImageSource(32, 32, var6, 0, 32);
                var3 = this.createImage(var2);
                break;
            default:
                System.out.println("createCursor: unknown cursor " + var1);
                return Cursor.getDefaultCursor();
        }

        final Cursor var4 = cim.customCursor(var3, new Point());
        return null != var4 ? var4 : Cursor.getDefaultCursor();
    }

    public void set_cursor(final int var1) {
        this.current_cursor = this.createCursor(var1);
        this.setCursor(this.current_cursor);
    }

    private void SetHalfHeight() {
        if (1616 < this.screen_x) {
            if (super.remconsObj.halfHeightCapable) {
                if (8 != this.blockHeight) {
                    System.out.println("Setting halfheight mode on supported system");
                    this.blockHeight = 8;
                    cim.bits_to_read[21] = 8;
                    cim.bits_to_read[17] = 8;
                    cim.bits_to_read[39] = 8;
                    cim.bits_to_read[30] = 8;
                }
            } else if (!this.unsupportedVideoModeWarned) {
                new VErrorDialog(super.remconsObj.ParentApp.dispFrame, this.getLocalString(8225), this.getLocalString(8226), false);
                this.unsupportedVideoModeWarned = true;
            }
        } else if (16 != this.blockHeight) {
            System.out.println("Setting non-halfheight mode");
            this.blockHeight = 16;
            cim.bits_to_read[21] = 7;
            cim.bits_to_read[17] = 7;
            cim.bits_to_read[39] = 7;
            cim.bits_to_read[30] = 7;
        }

    }

    void buildPixelTable(final int var1) {
        final int var3 = 1 << var1 * 3;
        int[] var10000;
        int var2;
        switch (var1) {
            case 2:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 15) << 6;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 240) << 15;
                    var10000[var2] |= (var2 & 3840) << 18;
                }

                return;
            case 3:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 15) << 5;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 240) << 11;
                    var10000[var2] |= (var2 & 3840) << 15;
                }

                return;
            case 4:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 15) << 4;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 240) << 8;
                    var10000[var2] |= (var2 & 3840) << 12;
                }

                return;
            case 5:
                for (var2 = 0; var2 < var3; ++var2) {
                    this.color_remap_table[var2] = (var2 & 31) << 3;
                    var10000 = this.color_remap_table;
                    var10000[var2] |= (var2 & 992) << 6;
                    var10000[var2] |= (var2 & 31744) << 9;
                }
        }

    }

    void setBitsPerColor(final int var1) {
        this.bitsPerColor = 5 - (var1 & 3);
        cim.bits_to_read[8] = this.bitsPerColor;
        cim.bits_to_read[9] = this.bitsPerColor;
        cim.bits_to_read[41] = this.bitsPerColor;
        cim.bits_to_read[42] = this.bitsPerColor;
        this.buildPixelTable(this.bitsPerColor);
    }

    void setVideoDecryption(final int var1) {
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

    public static byte mouseButtonState(final int var1) {
        byte var2 = (byte) 0;
        switch (var1) {
            case 1:
                var2 = (byte) ((int) var2 | 2);
                break;
            case 2:
                var2 = (byte) ((int) var2 | 4);
            case 3:
            default:
                break;
            case 4:
                var2 = (byte) ((int) var2 | 1);
        }

        return var2;
    }

    public static byte getMouseButtonState(final MouseEvent var1) {
        byte var2 = (byte) 0;
        if (0 != (var1.getModifiersEx() & 4096)) {
            var2 = (byte) ((int) var2 | 2);
        }

        if (0 != (var1.getModifiersEx() & 2048)) {
            var2 = (byte) ((int) var2 | 4);
        }

        if (0 != (var1.getModifiersEx() & 1024)) {
            var2 = (byte) ((int) var2 | 1);
        }

        return var2;
    }

    public void sendMouse(final MouseEvent var1) {
        new Point(0, 0);
        final Point var7 = new Point(0, 0);
        final Point var6 = cim.getAbsMouseCoordinates(var1);
        char var2 = (char) var6.x;
        char var3 = (char) var6.y;
        if (0 < (var1.getModifiersEx() & 128)) {
            this.mousePrevPosn.x = (int) var2;
            this.mousePrevPosn.y = (int) var3;
        } else if ((int) var2 <= this.screen_x && (int) var3 <= this.screen_y) {
            var7.x = (int) var2 - this.mousePrevPosn.x;
            var7.y = this.mousePrevPosn.y - (int) var3;
            this.mousePrevPosn.x = (int) var2;
            this.mousePrevPosn.y = (int) var3;
            int var4 = var7.x;
            int var5 = var7.y;
            if (-127 > var4) {
                var4 = -127;
            } else if (127 < var4) {
                var4 = 127;
            }

            if (-127 > var5) {
                var5 = -127;
            } else if (127 < var5) {
                var5 = 127;
            }

            this.UI_dirty = true;
            if (0 < this.screen_x && 0 < this.screen_y) {
                var2 = (char) (3000 * (int) var2 / this.screen_x);
                var3 = (char) (3000 * (int) var3 / this.screen_y);
            } else {
                var2 = (char) (3000 * (int) var2);
                var3 = (char) (3000 * (int) var3);
            }

            final byte[] var8 = new byte[10];
            var8[0] = (byte) 2;
            var8[1] = (byte) 0;
            var8[2] = (byte) ((int) var2 & 255);
            var8[3] = (byte) ((int) var2 >> 8);
            var8[4] = (byte) ((int) var3 & 255);
            var8[5] = (byte) ((int) var3 >> 8);
            var8[6] = (byte) (var4 & 255);
            var8[7] = (byte) (var5 & 255);
            var8[8] = cim.getMouseButtonState(var1);
            var8[9] = (byte) 0;
            this.transmitb(var8, var8.length);
        }

    }

    private static Point getAbsMouseCoordinates(final MouseEvent var1) {
        final Point var2 = new Point();
        var2.y = var1.getY();
        var2.x = var1.getX();
        return var2;
    }

    public void sendMouseScroll(final MouseWheelEvent var1) {
    }

    private void sendAck() {
        final byte[] var1 = {(byte) 12, (byte) 0};
        final String var2 = new String(var1);
        this.transmit(var2);
    }

    public void requestScreenFocus(final MouseEvent var1) {
        this.requestFocus();
    }

    public void installKeyboardHook() {
        super.remconsObj.remconsInstallKeyboardHook();
    }

    public void unInstallKeyboardHook() {
        super.remconsObj.remconsUnInstallKeyboardHook();
    }
}
