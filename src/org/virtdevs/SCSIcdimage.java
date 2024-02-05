package org.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SCSIcdimage extends SCSI {
    int fdd_state = 0;
    int event_state = 0;
    long media_sz;
    virtdevs v;

    public SCSIcdimage(Socket var1, InputStream var2, BufferedOutputStream var3, String var4, int var5, virtdevs var6) throws IOException {
        super(var1, var2, var3, var4, var5);
        int var7 = super.media.open(var4, 0);
        D.println(1, "Media open returns " + var7 + " / " + super.media.size() + " bytes");
        this.v = var6;
    }

    public void setWriteProt(boolean var1) {
        super.writeprot = var1;
    }

    public boolean process() throws IOException {
        boolean var1 = true;
        D.println(1, "Device: " + super.selectedDevice + " (" + super.targetIsDevice + ")");
        this.read_command(super.req, 12);
        D.print(1, "SCSI Request: ");
        D.hexdump(1, super.req, 12);
        this.v.ParentApp.remconsObj.setvmAct(1);
        this.media_sz = super.media.size();
        if (this.media_sz == 0L) {
            super.reply.setmedia(0);
            this.fdd_state = 0;
            this.event_state = 4;
        } else {
            super.reply.setmedia(1);
            ++this.fdd_state;
            if (this.fdd_state > 2) {
                this.fdd_state = 2;
            }

            if (this.event_state == 4) {
                this.event_state = 0;
            }

            ++this.event_state;
            if (this.event_state > 2) {
                this.event_state = 2;
            }
        }

        switch (super.req[0] & 255) {
            case 0:
                this.client_test_unit_ready();
                break;
            case 27:
                var1 = this.client_start_stop_unit(super.req);
                break;
            case 29:
                this.client_send_diagnostic();
                break;
            case 30:
                this.client_pa_media_removal(super.req);
                break;
            case 37:
                this.client_read_capacity();
                break;
            case 40:
            case 168:
                this.client_read(super.req);
                break;
            case 67:
                this.client_read_toc(super.req);
                break;
            case 74:
                this.client_get_event_status(super.req);
                break;
            case 90:
                this.client_mode_sense(super.req);
                break;
            default:
                D.println(0, "Unknown request:cmd = " + Integer.toHexString(super.req[0]));
                super.reply.set(5, 36, 0, 0);
                super.reply.send(super.out);
                super.out.flush();
        }

        return var1;
    }

    void client_send_diagnostic() throws IOException {
    }

    void client_read(byte[] var1) throws IOException {
        boolean var5 = var1[0] == 168;
        long var2 = (long) SCSI.mk_int32(var1, 2) * 2048L;
        int var4 = var5 ? SCSI.mk_int32(var1, 6) : SCSI.mk_int16(var1, 7);
        var4 *= 2048;
        D.println(3, "CDImage :Client read " + var2 + ", len=" + var4);
        if (this.fdd_state == 0) {
            D.println(3, "media not present");
            super.reply.set(2, 58, 0, 0);
            var4 = 0;
        } else if (this.fdd_state == 1) {
            D.println(3, "media changed");
            super.reply.set(6, 40, 0, 0);
            var4 = 0;
            this.fdd_state = 2;
        } else if (var2 >= 0L && var2 < this.media_sz) {
            super.media.read(var2, var4, super.buffer);
            super.reply.set(0, 0, 0, var4);
        } else {
            super.reply.set(5, 33, 0, 0);
            var4 = 0;
        }

        super.reply.send(super.out);
        if (var4 != 0) {
            super.out.write(super.buffer, 0, var4);
        }

        super.out.flush();
    }

    void client_pa_media_removal(byte[] var1) throws IOException {
        if ((var1[4] & 1) != 0) {
            D.println(3, "Media removal prevented");
        } else {
            D.println(3, "Media removal allowed");
        }

        super.reply.set(0, 0, 0, 0);
        super.reply.send(super.out);
        super.out.flush();
    }

    boolean client_start_stop_unit(byte[] var1) throws IOException {
        super.reply.set(0, 0, 0, 0);
        super.reply.send(super.out);
        super.out.flush();
        if ((var1[4] & 3) == 2) {
            this.fdd_state = 0;
            this.event_state = 4;
            D.println(3, "Media eject");
            return false;
        } else {
            return true;
        }
    }

    void client_test_unit_ready() throws IOException {
        if (this.fdd_state == 0) {
            D.println(3, "media not present");
            super.reply.set(2, 58, 0, 0);
        } else if (this.fdd_state == 1) {
            D.println(3, "media changed");
            super.reply.set(6, 40, 0, 0);
            this.fdd_state = 2;
        } else {
            D.println(3, "device ready");
            super.reply.set(0, 0, 0, 0);
        }

        super.reply.send(super.out);
        super.out.flush();
    }

    void client_read_capacity() throws IOException {
        byte[] var1 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        super.reply.set(0, 0, 0, var1.length);
        if (this.fdd_state == 0) {
            super.reply.set(2, 58, 0, 0);
        } else if (this.fdd_state == 1) {
            super.reply.set(6, 40, 0, 0);
        } else {
            int var2 = (int) (super.media.size() / 2048L - 1L);
            var1[0] = (byte) (var2 >> 24 & 255);
            var1[1] = (byte) (var2 >> 16 & 255);
            var1[2] = (byte) (var2 >> 8 & 255);
            var1[3] = (byte) (var2 >> 0 & 255);
            var1[6] = 8;
        }

        super.reply.send(super.out);
        if (this.fdd_state == 2) {
            super.out.write(var1, 0, var1.length);
        }

        super.out.flush();
        D.print(3, "client_read_capacity: ");
        D.hexdump(3, var1, 8);
    }

    void client_read_toc(byte[] var1) throws IOException {
        boolean var2 = (var1[1] & 2) != 0;
        int var3 = (var1[9] & 192) >> 6;
        int var4 = (int) (super.media.size() / 2048L);
        double var5 = (double) var4 / 75.0 + 2.0;
        int var7 = (int) var5 / 60;
        int var8 = (int) var5 % 60;
        int var9 = (int) ((var5 - (double) ((int) var5)) * 75.0);
        int var10 = SCSI.mk_int16(var1, 7);

        for (int var11 = 0; var11 < var10; ++var11) {
            super.buffer[var11] = 0;
        }

        if (var3 == 0) {
            super.buffer[0] = 0;
            super.buffer[1] = 18;
            super.buffer[2] = 1;
            super.buffer[3] = 1;
            super.buffer[4] = 0;
            super.buffer[5] = 20;
            super.buffer[6] = 1;
            super.buffer[7] = 0;
            super.buffer[8] = 0;
            super.buffer[9] = 0;
            super.buffer[10] = (byte) (var2 ? 2 : 0);
            super.buffer[11] = 0;
            super.buffer[12] = 0;
            super.buffer[13] = 20;
            super.buffer[14] = -86;
            super.buffer[15] = 0;
            super.buffer[16] = 0;
            super.buffer[17] = var2 ? (byte) var7 : (byte) (var4 >> 16 & 255);
            super.buffer[18] = var2 ? (byte) var8 : (byte) (var4 >> 8 & 255);
            super.buffer[19] = var2 ? (byte) var9 : (byte) (var4 & 255);
        }

        if (var3 == 1) {
            super.buffer[0] = 0;
            super.buffer[1] = 10;
            super.buffer[2] = 1;
            super.buffer[3] = 1;
            super.buffer[4] = 0;
            super.buffer[5] = 20;
            super.buffer[6] = 1;
            super.buffer[7] = 0;
            super.buffer[8] = 0;
            super.buffer[9] = 0;
            super.buffer[10] = (byte) (var2 ? 2 : 0);
            super.buffer[11] = 0;
        }

        var4 = 412;
        if (var10 < var4) {
            var4 = var10;
        }

        D.hexdump(3, super.buffer, var4);
        super.reply.set(0, 0, 0, var4);
        super.reply.send(super.out);
        super.out.write(super.buffer, 0, var4);
        super.out.flush();
    }

    void client_mode_sense(byte[] var1) throws IOException {
        super.buffer[0] = 0;
        super.buffer[1] = 8;
        super.buffer[2] = 1;
        super.buffer[3] = 0;
        super.buffer[4] = 0;
        super.buffer[5] = 0;
        super.buffer[6] = 0;
        super.buffer[7] = 0;
        super.reply.set(0, 0, 0, 8);
        D.hexdump(3, super.buffer, 8);
        super.reply.setmedia(super.buffer[2]);
        super.reply.send(super.out);
        super.out.write(super.buffer, 0, 8);
        super.out.flush();
    }

    void client_get_event_status(byte[] var1) throws IOException {
        byte var2 = var1[4];
        int var3 = SCSI.mk_int16(var1, 7);

        for (int var4 = 0; var4 < var3; ++var4) {
            super.buffer[var4] = 0;
        }

        if ((var1[1] & 1) == 0) {
            super.reply.set(5, 36, 0, 0);
            super.reply.send(super.out);
            super.out.flush();
        }

        if ((var2 & 16) != 0) {
            super.buffer[0] = 0;
            super.buffer[1] = 6;
            super.buffer[2] = 4;
            super.buffer[3] = 16;
            if (this.event_state == 0) {
                super.buffer[4] = 0;
                super.buffer[5] = 0;
            } else if (this.event_state == 1) {
                super.buffer[4] = 4;
                super.buffer[5] = 2;
                if (var3 > 4) {
                    this.event_state = 2;
                }
            } else if (this.event_state == 4) {
                super.buffer[4] = 3;
                super.buffer[5] = 0;
                if (var3 > 4) {
                    this.event_state = 0;
                }
            } else {
                super.buffer[4] = 0;
                super.buffer[5] = 2;
            }

            D.hexdump(3, super.buffer, 8);
            super.reply.set(0, 0, 0, var3 < 8 ? var3 : 8);
            super.reply.send(super.out);
            super.out.write(super.buffer, 0, var3 < 8 ? var3 : 8);
            super.out.flush();
        } else {
            super.buffer[0] = 0;
            super.buffer[1] = 2;
            super.buffer[2] = -128;
            super.buffer[3] = 16;
            D.hexdump(3, super.buffer, 4);
            super.reply.set(0, 0, 0, var3 < 4 ? var3 : 4);
            super.reply.send(super.out);
            super.out.write(super.buffer, 0, var3 < 4 ? var3 : 4);
            super.out.flush();
        }

    }
}
