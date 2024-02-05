package org.virtdevs;


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
    static final int[] commands = new int[]{30, 33554432, 37, 16777224, 29, 33554432, 0, 33554432, 40, 25296903, 168, 25427974, 27, 33554432, 190, 25362438, 185, 16777216, 68, 16777224, 66, 16908295, 67, 16908295, 78, 33554432, 189, 16908296, 90, 16908295, 74, 16908295};
    byte[] sense = new byte[3];
    int retrycount;
    VErrorDialog dlg;
    boolean do_split_reads = false;
    virtdevs v;

    public SCSIcdrom(Socket var1, InputStream var2, BufferedOutputStream var3, String var4, int var5, virtdevs var6) throws IOException {
        super(var1, var2, var3, var4, var5);
        D.println(1, "Media opening " + var4 + "(" + (var5 | 2) + ")");
        int var7 = super.media.open(var4, var5);
        D.println(1, "Media open returns " + var7);
        this.retrycount = Integer.valueOf(virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.retrycount", "10"));
        this.v = var6;
    }

    void media_err(byte[] var1, byte[] var2) {
        String var3 = "The CDROM drive reports a media error:\nCommand: " + D.hex(var1[0], 2) + " " + D.hex(var1[1], 2) + " " + D.hex(var1[2], 2) + " " + D.hex(var1[3], 2) + " " + D.hex(var1[4], 2) + " " + D.hex(var1[5], 2) + " " + D.hex(var1[6], 2) + " " + D.hex(var1[7], 2) + " " + D.hex(var1[8], 2) + " " + D.hex(var1[9], 2) + " " + D.hex(var1[10], 2) + " " + D.hex(var1[11], 2) + "\n" + "Sense Code: " + D.hex(var2[0], 2) + "/" + D.hex(var2[1], 2) + "/" + D.hex(var2[2], 2) + "\n\n";
        this.dlg = new VErrorDialog(var3, false);
    }

    public void close() throws IOException {
        super.req[0] = 30;
        super.req[1] = super.req[2] = super.req[3] = super.req[4] = super.req[5] = super.req[7] = super.req[7] = super.req[8] = super.req[9] = super.req[10] = super.req[11] = 0;
        super.media.scsi(super.req, 2, 0, super.buffer, (byte[]) null);
        super.close();
    }

    int scsi_length(int var1, byte[] var2) {
        int var3 = 0;
        ++var1;
        switch (commands[var1] & 8323072) {
            case 0:
                var3 = commands[var1] & '\uffff';
                break;
            case 65536:
                var3 = var2[commands[var1] & '\uffff'] & 255;
                break;
            case 131072:
                var3 = SCSI.mk_int16(var2, commands[var1] & '\uffff');
                break;
            case 196608:
                var3 = SCSI.mk_int24(var2, commands[var1] & '\uffff');
                break;
            case 262144:
                var3 = SCSI.mk_int32(var2, commands[var1] & '\uffff');
                break;
            default:
                D.println(0, "Unknown Size!");
        }

        if ((commands[var1] & 8388608) == 8388608) {
            var3 *= 2048;
        }

        return var3;
    }

    void start_stop_unit() {
        byte[] var1 = new byte[]{27, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
        byte[] var2 = new byte[3];
        int var3 = super.media.scsi(var1, 2, 0, super.buffer, var2);
        D.println(3, "Start/Stop unit = " + var3 + " " + var2[0] + "/" + var2[1] + "/" + var2[2]);
    }

    boolean within_75(byte[] var1) {
        byte[] var2 = new byte[8];
        byte[] var3 = new byte[]{37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean var4 = var1[0] == 168;
        int var5 = SCSI.mk_int32(var1, 2);
        int var6 = var4 ? SCSI.mk_int32(var1, 6) : SCSI.mk_int16(var1, 7);
        super.media.scsi(var3, 1, 8, var2, (byte[]) null);
        int var7 = SCSI.mk_int32(var2, 0);
        return var5 > var7 - 75 || var5 + var6 > var7 - 75;
    }

    int split_read() {
        boolean var1 = super.req[0] == 168;
        int var2 = SCSI.mk_int32(super.req, 2);
        int var3 = var1 ? SCSI.mk_int32(super.req, 6) : SCSI.mk_int16(super.req, 7);
        int var4 = var3 > 32 ? 32 : var3;
        byte var7 = 1;
        super.req[2] = (byte) (var2 >> 24);
        super.req[3] = (byte) (var2 >> 16);
        super.req[4] = (byte) (var2 >> 8);
        super.req[5] = (byte) var2;
        if (var1) {
            super.req[6] = (byte) (var4 >> 24);
            super.req[7] = (byte) (var4 >> 16);
            super.req[8] = (byte) (var4 >> 8);
            super.req[9] = (byte) var4;
        } else {
            super.req[7] = (byte) (var4 >> 8);
            super.req[8] = (byte) var4;
        }

        int var5 = super.media.scsi(super.req, var7, var4 * 2048, super.buffer, this.sense);
        if (var5 < 0) {
            return var5;
        } else {
            var3 -= var4;
            if (var3 <= 0) {
                return var5;
            } else {
                var2 += var4;
                super.req[2] = (byte) (var2 >> 24);
                super.req[3] = (byte) (var2 >> 16);
                super.req[4] = (byte) (var2 >> 8);
                super.req[5] = (byte) var2;
                if (var1) {
                    super.req[6] = (byte) (var3 >> 24);
                    super.req[7] = (byte) (var3 >> 16);
                    super.req[8] = (byte) (var3 >> 8);
                    super.req[9] = (byte) var3;
                } else {
                    super.req[7] = (byte) (var3 >> 8);
                    super.req[8] = (byte) var3;
                }

                int var6 = super.media.scsi(super.req, var7, var3 * 2048, super.buffer, this.sense, 65536);
                return var6 < 0 ? var6 : var5 + var6;
            }
        }
    }

    public boolean process() throws IOException {
        boolean var2 = false;
        boolean var3 = false;
        this.read_command(super.req, 12);
        D.println(1, "SCSI Request:");
        D.hexdump(1, super.req, 12);
        this.v.ParentApp.remconsObj.setvmAct(1);
        int var5;
        if (super.media.dio.filehandle == -1) {
            var5 = super.media.open(super.selectedDevice, super.targetIsDevice);
            if (var5 < 0) {
                new VErrorDialog("Could not open CDROM (" + super.media.dio.sysError(-var5) + ")", false);
                throw new IOException("Couldn't open cdrom " + var5);
            }
        }

        int var1;
        for (var1 = 0; var1 < commands.length && super.req[0] != (byte) commands[var1]; var1 += 2) {
        }

        int var10;
        if (var1 != commands.length) {
            var10 = this.scsi_length(var1, super.req);
            int var11 = commands[var1 + 1] >> 24;
            var1 = super.req[0] & 255;
            if (var11 == 0) {
                this.read_complete(super.buffer, var10);
            }

            D.println(1, "SCSI dir=" + var11 + " len=" + var10);
            int var4 = 0;

            do {
                long var6 = System.currentTimeMillis();
                if ((var1 == 40 || var1 == 168) && this.do_split_reads) {
                    var5 = this.split_read();
                } else {
                    var5 = super.media.scsi(super.req, var11, var10, super.buffer, this.sense);
                }

                long var8 = System.currentTimeMillis();
                D.println(1, "ret=" + var5 + " sense=" + D.hex(this.sense[0], 2) + " " + D.hex(this.sense[1], 2) + " " + D.hex(this.sense[2], 2) + " Time=" + (var8 - var6));
                if (var1 == 90) {
                    D.println(1, "media type: " + D.hex(super.buffer[3], 2));
                    super.reply.setmedia(super.buffer[3]);
                }

                if (var1 == 67) {
                    D.hexdump(3, super.buffer, var10);
                }

                if (var1 == 27) {
                    var5 = 0;
                }

                if (var1 == 40 || var1 == 168) {
                    if (this.sense[1] == 41) {
                        var5 = -1;
                    } else if (var5 < 0 && this.within_75(super.req)) {
                        this.sense[0] = 5;
                        this.sense[1] = 33;
                        this.sense[2] = 0;
                        var5 = 0;
                    } else if (var5 < 0) {
                        this.do_split_reads = true;
                    }
                }

                if (this.sense[0] == 3 || this.sense[0] == 4) {
                    this.media_err(super.req, this.sense);
                    var5 = -1;
                }
            } while (var5 < 0 && var4++ < this.retrycount);

            var10 = var5;
            if (var5 >= 0 && var5 <= 131072) {
                super.reply.set(this.sense[0], this.sense[1], this.sense[2], var5);
            } else {
                D.println(0, "AIEE! len out of bounds: " + var5 + ", cmd: " + D.hex(var1, 2) + "\n");
                var10 = 0;
                super.reply.set(5, 32, 0, 0);
            }
        } else {
            D.println(0, "AIEE! Unhandled command" + D.hex(super.req[0], 2) + "\n");
            super.reply.set(5, 32, 0, 0);
            var10 = 0;
        }

        super.reply.send(super.out);
        if (var10 != 0) {
            super.out.write(super.buffer, 0, var10);
        }

        super.out.flush();
        return true;
    }
}
