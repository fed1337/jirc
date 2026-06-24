package com.hp.ilo2.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.Socket;

abstract class SCSI {
    public static final int SCSI_FORMAT_UNIT = 4;
    public static final int SCSI_INQUIRY = 18;
    public static final int SCSI_MODE_SELECT_6 = 21;
    public static final int SCSI_MODE_SELECT = 85;
    public static final int SCSI_MODE_SENSE_6 = 26;
    public static final int SCSI_MODE_SENSE = 90;
    public static final int SCSI_PA_MEDIA_REMOVAL = 30;
    public static final int SCSI_READ_10 = 40;
    public static final int SCSI_READ_12 = 168;
    public static final int SCSI_READ_CAPACITY = 37;
    public static final int SCSI_READ_CAPACITIES = 35;
    public static final int SCSI_REQUEST_SENSE = 3;
    public static final int SCSI_REZERO_UNIT = 1;
    public static final int SCSI_SEEK = 43;
    public static final int SCSI_SEND_DIAGNOSTIC = 29;
    public static final int SCSI_START_STOP_UNIT = 27;
    public static final int SCSI_TEST_UNIT_READY = 0;
    public static final int SCSI_VERIFY = 47;
    public static final int SCSI_WRITE_10 = 42;
    public static final int SCSI_WRITE_12 = 170;
    public static final int SCSI_WRITE_VERIFY = 46;
    public static final int SCSI_READ_CD = 190;
    public static final int SCSI_READ_CD_MSF = 185;
    public static final int SCSI_READ_HEADER = 68;
    public static final int SCSI_READ_SUBCHANNEL = 66;
    public static final int SCSI_READ_TOC = 67;
    public static final int SCSI_STOP_PLAY_SCAN = 78;
    public static final int SCSI_MECHANISM_STATUS = 189;
    public static final int SCSI_GET_EVENT_STATUS = 74;
    private final InputStream in;
    final BufferedOutputStream out;
    private final Socket sock;
    final MediaAccess media = new MediaAccess();
    final ReplyHeader reply = new ReplyHeader();
    final String selectedDevice;
    boolean writeprot = false;
    private boolean please_exit = false;
    final int targetIsDevice;
    final byte[] buffer = new byte[131072];
    final byte[] req = new byte[12];

    SCSI(final Socket var1, final InputStream var2, final BufferedOutputStream var3, final String var4, final int var5) {
        super();
        this.sock = var1;
        this.in = var2;
        this.out = var3;
        this.selectedDevice = var4;
        this.targetIsDevice = var5;
    }

    public static int mk_int32(final byte[] var0, final int var1) {
        final byte var2 = var0[var1];
        final byte var3 = var0[var1 + 1];
        final byte var4 = var0[var1 + 2];
        final byte var5 = var0[var1 + 3];
        return ((int) var2 & 255) << 24 | ((int) var3 & 255) << 16 | ((int) var4 & 255) << 8 | (int) var5 & 255;
    }

    static int mk_int24(final byte[] var0, final int var1) {
        final byte var2 = var0[var1];
        final byte var3 = var0[var1 + 1];
        final byte var4 = var0[var1 + 2];
        return ((int) var2 & 255) << 16 | ((int) var3 & 255) << 8 | (int) var4 & 255;
    }

    static int mk_int16(final byte[] var0, final int var1) {
        final byte var2 = var0[var1];
        final byte var3 = var0[var1 + 1];
        return ((int) var2 & 255) << 8 | (int) var3 & 255;
    }

    public boolean getWriteProt() {
        D.println(3, "media.wp = " + this.media.wp());
        return this.media.wp();
    }

    public void setWriteProt(final boolean var1) {
        this.writeprot = var1;
    }

    public void close() throws IOException {
        this.media.close();
    }

    void read_complete(final byte[] var1, int var2) throws IOException {
        int var3 = 0;

        while (0 < var2) {
            final int var7;
            try {
                this.sock.setSoTimeout(1000);
                var7 = this.in.read(var1, var3, var2);
            } catch (final InterruptedIOException var6) {
                continue;
            }

            if (0 > var7) {
                break;
            }

            var2 -= var7;
            var3 += var7;
        }

    }

    void read_command(final byte[] var1) throws IOException {
        int var3 = 0;

        while (true) {
            try {
                this.sock.setSoTimeout(1000);
                var3 = this.in.read(var1, 0, 12);
            } catch (final InterruptedIOException var5) {
                this.reply.keepalive(true);
                D.println(3, "Sending keepalive");
                this.reply.send(this.out);
                this.out.flush();
                this.reply.keepalive(false);
                if (this.please_exit) {
                    break;
                }
                continue;
            }

            if (254 != ((int) var1[0] & 255)) {
                break;
            }

            this.reply.sendsynch(this.out, var1);
            this.out.flush();
        }

        if (this.please_exit) {
            throw new IOException("Asked to exit");
        } else if (0 > var3) {
            throw new IOException("Socket Closed");
        } else {
        }
    }

    public void send_disconnect() {
        try {
            this.reply.disconnect(true);
            this.reply.send(this.out);
            this.out.flush();
            this.reply.disconnect(false);
        } catch (final Exception var2) {
            D.println(1, "Exception in send_disconnect" + var2);
            var2.printStackTrace();
        }

    }

    public abstract boolean process() throws IOException;

    public void change_disk() {
        this.please_exit = true;
    }
}
