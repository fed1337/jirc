package com.hp.ilo2.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SCSIcdrom extends SCSI {
    public static final int SCSI_IOCTL_DATA_OUT = 0;
    public static final int SCSI_IOCTL_DATA_IN = 1;
    public static final int SCSI_IOCTL_DATA_UNSPECIFIED = 2;
    public static final int CONST = 0;
    public static final int BLKS = 8388608;
    static final int WRITE = 0;
    static final int READ = 16777216;
    static final int NONE = 33554432;
    static final int B32 = 262144;
    static final int B24 = 196608;
    static final int B16 = 131072;
    static final int B08 = 65536;
    private static final int[] commands = {30, 33554432, 37, 16777224, 29, 33554432, 0, 33554432, 40, 25296903, 168, 25427974, 27, 33554432, 190, 25362438, 185, 16777216, 68, 16777224, 66, 16908295, 67, 16908295, 78, 33554432, 189, 16908296, 90, 16908295, 74, 16908295};
    private final byte[] sense = new byte[3];
    private final int retrycount;
    private boolean do_split_reads = false;
    private final virtdevs v;

    public SCSIcdrom(final Socket var1, final InputStream var2, final BufferedOutputStream var3, final String var4, final int var5, final virtdevs var6) throws IOException {
        super(var1, var2, var3, var4, var5);
        D.println(1, "Media opening " + var4 + "(" + (var5 | 2) + ")");
        final int var7 = super.media.open(var4, var5);
        D.println(1, "Media open returns " + var7);
        this.retrycount = Integer.parseInt(virtdevs.prop.getProperty("org.virtdevs.retrycount", "10"));
        this.v = var6;
    }

    private static void media_err(final byte[] var1, final byte[] var2) {
        final String var3 = "The CDROM drive reports a media error:\nCommand: " + D.hex(var1[0], 2) + " " + D.hex(var1[1], 2) + " " + D.hex(var1[2], 2) + " " + D.hex(var1[3], 2) + " " + D.hex(var1[4], 2) + " " + D.hex(var1[5], 2) + " " + D.hex(var1[6], 2) + " " + D.hex(var1[7], 2) + " " + D.hex(var1[8], 2) + " " + D.hex(var1[9], 2) + " " + D.hex(var1[10], 2) + " " + D.hex(var1[11], 2) + "\n" + "Sense Code: " + D.hex(var2[0], 2) + "/" + D.hex(var2[1], 2) + "/" + D.hex(var2[2], 2) + "\n\n";
        final VErrorDialog dlg = new VErrorDialog(var3, false);
    }

    public void close() throws IOException {
        super.req[0] = (byte) 30;
        super.req[1] = super.req[2] = super.req[3] = super.req[4] = super.req[5] = super.req[6] = super.req[7] = super.req[8] = super.req[9] = super.req[10] = super.req[11] = (byte) 0;
        super.media.scsi(super.req, 2, 0, super.buffer, null);
        super.close();
    }

    private static int scsi_length(int var1, final byte[] var2) {
        int var3 = 0;
        ++var1;
        switch (SCSIcdrom.commands[var1] & 8323072) {
            case 0:
                var3 = SCSIcdrom.commands[var1] & (int) '\uffff';
                break;
            case 65536:
                var3 = (int) var2[SCSIcdrom.commands[var1] & (int) '\uffff'] & 255;
                break;
            case 131072:
                var3 = SCSI.mk_int16(var2, SCSIcdrom.commands[var1] & (int) '\uffff');
                break;
            case 196608:
                var3 = SCSI.mk_int24(var2, SCSIcdrom.commands[var1] & (int) '\uffff');
                break;
            case 262144:
                var3 = SCSI.mk_int32(var2, SCSIcdrom.commands[var1] & (int) '\uffff');
                break;
            default:
                D.println(0, "Unknown Size!");
        }

        if (8388608 == (SCSIcdrom.commands[var1] & 8388608)) {
            var3 <<= 11;
        }

        return var3;
    }

    void start_stop_unit() {
        final byte[] var1 = {(byte) 27, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final byte[] var2 = new byte[3];
        final int var3 = super.media.scsi(var1, 2, 0, super.buffer, var2);
        D.println(3, "Start/Stop unit = " + var3 + " " + var2[0] + "/" + var2[1] + "/" + var2[2]);
    }

    private boolean within_75(final byte[] var1) {
        final byte[] var2 = new byte[8];
        final byte[] var3 = {(byte) 37, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final int var5 = SCSI.mk_int32(var1, 2);
        final int var6 = SCSI.mk_int16(var1, 7);
        super.media.scsi(var3, 1, 8, var2, null);
        final int var7 = SCSI.mk_int32(var2, 0);
        return var5 > var7 - 75 || var5 + var6 > var7 - 75;
    }

    private int split_read() {
        int var2 = SCSI.mk_int32(super.req, 2);
        int var3 = SCSI.mk_int16(super.req, 7);
        final int var4 = Math.min(32, var3);
        final byte var7 = (byte) 1;
        super.req[2] = (byte) (var2 >> 24);
        super.req[3] = (byte) (var2 >> 16);
        super.req[4] = (byte) (var2 >> 8);
        super.req[5] = (byte) var2;
        super.req[7] = (byte) (var4 >> 8);
        super.req[8] = (byte) var4;

        final int var5 = super.media.scsi(super.req, (int) var7, var4 << 11, super.buffer, this.sense);
        if (0 > var5) {
            return var5;
        } else {
            var3 -= var4;
            if (0 >= var3) {
                return var5;
            } else {
                var2 += var4;
                super.req[2] = (byte) (var2 >> 24);
                super.req[3] = (byte) (var2 >> 16);
                super.req[4] = (byte) (var2 >> 8);
                super.req[5] = (byte) var2;
                super.req[7] = (byte) (var3 >> 8);
                super.req[8] = (byte) var3;

                final int var6 = super.media.scsi(super.req, (int) var7, var3 << 11, super.buffer, this.sense, 65536);
                return 0 > var6 ? var6 : var5 + var6;
            }
        }
    }

    public boolean process() throws IOException {
        this.read_command(super.req);
        D.println(1, "SCSI Request:");
        D.hexdump(1, super.req, 12);
        this.v.ParentApp.remconsObj.setvmAct(1);
        int var5;
        var5 = super.media.open(super.selectedDevice, super.targetIsDevice);
        if (0 > var5) {
            new VErrorDialog("Could not open CDROM (" + super.media.dio.sysError(-var5) + ")", false);
            throw new IOException("Couldn't open cdrom " + var5);
        }

        int var1;
        for (var1 = 0; var1 < SCSIcdrom.commands.length && (int) super.req[0] != (int) (byte) SCSIcdrom.commands[var1]; var1 += 2) {
        }

        int var10;
        if (var1 == SCSIcdrom.commands.length) {
            D.println(0, "AIEE! Unhandled command" + D.hex(super.req[0], 2) + "\n");
            super.reply.set(5, 32, 0, 0);
            var10 = 0;
        } else {
            var10 = SCSIcdrom.scsi_length(var1, super.req);
            final int var11 = SCSIcdrom.commands[var1 + 1] >> 24;
            var1 = (int) super.req[0] & 255;

            D.println(1, "SCSI dir=" + var11 + " len=" + var10);
            int var4 = 0;

            do {
                final long var6 = System.currentTimeMillis();
                if ((40 == var1 || 168 == var1) && this.do_split_reads) {
                    var5 = this.split_read();
                } else {
                    var5 = super.media.scsi(super.req, var11, var10, super.buffer, this.sense);
                }

                final long var8 = System.currentTimeMillis();
                D.println(1, "ret=" + var5 + " sense=" + D.hex(this.sense[0], 2) + " " + D.hex(this.sense[1], 2) + " " + D.hex(this.sense[2], 2) + " Time=" + (var8 - var6));
                if (90 == var1) {
                    D.println(1, "media type: " + D.hex(super.buffer[3], 2));
                    super.reply.setmedia((int) super.buffer[3]);
                }

                if (67 == var1) {
                    D.hexdump(3, super.buffer, var10);
                }

                if (27 == var1) {
                    var5 = 0;
                }

                if (40 == var1 || 168 == var1) {
                    if (41 == (int) this.sense[1]) {
                        var5 = -1;
                    } else if (0 > var5 && this.within_75(super.req)) {
                        this.sense[0] = (byte) 5;
                        this.sense[1] = (byte) 33;
                        this.sense[2] = (byte) 0;
                        var5 = 0;
                    } else if (0 > var5) {
                        this.do_split_reads = true;
                    }
                }

                if (3 == (int) this.sense[0] || 4 == (int) this.sense[0]) {
                    SCSIcdrom.media_err(super.req, this.sense);
                    var5 = -1;
                }
            } while (0 > var5 && var4++ < this.retrycount);

            var10 = var5;
            if (0 <= var5 && 131072 >= var5) {
                super.reply.set((int) this.sense[0], (int) this.sense[1], (int) this.sense[2], var5);
            } else {
                D.println(0, "AIEE! len out of bounds: " + var5 + ", cmd: " + D.hex(var1, 2) + "\n");
                var10 = 0;
                super.reply.set(5, 32, 0, 0);
            }
        }

        super.reply.send(super.out);
        if (0 != var10) {
            super.out.write(super.buffer, 0, var10);
        }

        super.out.flush();
        return true;
    }
}
