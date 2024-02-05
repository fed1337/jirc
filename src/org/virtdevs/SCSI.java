package org.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.Socket;

public abstract class SCSI {
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
    protected InputStream in;
    protected BufferedOutputStream out;
    protected Socket sock;
    MediaAccess media = new MediaAccess();
    ReplyHeader reply = new ReplyHeader();
    String selectedDevice;
    boolean writeprot = false;
    boolean please_exit = false;
    int targetIsDevice = 0;
    byte[] buffer = new byte[131072];
    byte[] req = new byte[12];

    public SCSI(Socket var1, InputStream var2, BufferedOutputStream var3, String var4, int var5) {
        this.sock = var1;
        this.in = var2;
        this.out = var3;
        this.selectedDevice = var4;
        this.targetIsDevice = var5;
    }

    public static int mk_int32(byte[] var0, int var1) {
        byte var2 = var0[var1];
        byte var3 = var0[var1 + 1];
        byte var4 = var0[var1 + 2];
        byte var5 = var0[var1 + 3];
        int var6 = (var2 & 255) << 24 | (var3 & 255) << 16 | (var4 & 255) << 8 | var5 & 255;
        return var6;
    }

    public static int mk_int24(byte[] var0, int var1) {
        byte var2 = var0[var1];
        byte var3 = var0[var1 + 1];
        byte var4 = var0[var1 + 2];
        int var5 = (var2 & 255) << 16 | (var3 & 255) << 8 | var4 & 255;
        return var5;
    }

    public static int mk_int16(byte[] var0, int var1) {
        byte var2 = var0[var1];
        byte var3 = var0[var1 + 1];
        int var4 = (var2 & 255) << 8 | var3 & 255;
        return var4;
    }

    public boolean getWriteProt() {
        D.println(3, "media.wp = " + this.media.wp());
        return this.media.wp();
    }

    public void setWriteProt(boolean var1) {
        this.writeprot = var1;
    }

    public void close() throws IOException {
        this.media.close();
    }

    protected int read_complete(byte[] var1, int var2) throws IOException {
        int var3 = 0;
        boolean var4 = false;

        while (var2 > 0) {
            int var7;
            try {
                this.sock.setSoTimeout(1000);
                var7 = this.in.read(var1, var3, var2);
            } catch (InterruptedIOException var6) {
                continue;
            }

            if (var7 < 0) {
                break;
            }

            var2 -= var7;
            var3 += var7;
        }

        return var3;
    }

    protected int read_command(byte[] var1, int var2) throws IOException {
        int var3 = 0;

        while (true) {
            try {
                this.sock.setSoTimeout(1000);
                var3 = this.in.read(var1, 0, var2);
            } catch (InterruptedIOException var5) {
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

            if ((var1[0] & 255) != 254) {
                break;
            }

            this.reply.sendsynch(this.out, var1);
            this.out.flush();
        }

        if (this.please_exit) {
            throw new IOException("Asked to exit");
        } else if (var3 < 0) {
            throw new IOException("Socket Closed");
        } else {
            return var3;
        }
    }

    public void send_disconnect() {
        try {
            this.reply.disconnect(true);
            this.reply.send(this.out);
            this.out.flush();
            this.reply.disconnect(false);
        } catch (Exception var2) {
            D.println(1, "Exception in send_disconnect" + var2);
            var2.printStackTrace();
        }

    }

    public abstract boolean process() throws IOException;

    public void change_disk() {
        this.please_exit = true;
    }
}
