package com.hp.ilo2.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SCSIcdimage extends SCSI {
    int fdd_state = 0;
    int event_state = 0;
    long media_sz = 0L;
    virtdevs v;

    public SCSIcdimage(final Socket var1, final InputStream var2, final BufferedOutputStream var3, final String var4, final int var5, final virtdevs var6) throws IOException {
        super(var1, var2, var3, var4, var5);
        final int var7 = super.media.open(var4, 0);
        D.println(1, "Media open returns " + var7 + " / " + super.media.size() + " bytes");
        this.v = var6;
    }

    public boolean process() throws IOException {
        boolean var1 = true;
        D.println(1, "Device: " + super.selectedDevice + " (" + super.targetIsDevice + ")");
        this.read_command(super.req);
        D.print(1, "SCSI Request: ");
        D.hexdump(1, super.req, 12);
        this.v.ParentApp.remconsObj.setvmAct(1);
        this.media_sz = super.media.size();
        if (0L == this.media_sz) {
            super.reply.setmedia(0);
            this.fdd_state = 0;
            this.event_state = 4;
        } else {
            super.reply.setmedia(1);
            ++this.fdd_state;
            if (2 < this.fdd_state) {
                this.fdd_state = 2;
            }

            if (4 == this.event_state) {
                this.event_state = 0;
            }

            ++this.event_state;
            if (2 < this.event_state) {
                this.event_state = 2;
            }
        }

        switch ((int) super.req[0] & 255) {
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
                D.println(0, "Unknown request:cmd = " + Integer.toHexString((int) super.req[0]));
                super.reply.set(5, 36, 0, 0);
                super.reply.send(super.out);
                super.out.flush();
        }

        return var1;
    }

    void client_send_diagnostic() {
    }

    void client_read(final byte[] var1) throws IOException {
        final long var2 = (long) SCSI.mk_int32(var1, 2) << 11;
        int var4 = SCSI.mk_int16(var1, 7);
        var4 <<= 11;
        D.println(3, "CDImage :Client read " + var2 + ", len=" + var4);
        if (0 == this.fdd_state) {
            D.println(3, "media not present");
            super.reply.set(2, 58, 0, 0);
            var4 = 0;
        } else if (1 == this.fdd_state) {
            D.println(3, "media changed");
            super.reply.set(6, 40, 0, 0);
            var4 = 0;
            this.fdd_state = 2;
        } else if (0L <= var2 && var2 < this.media_sz) {
            super.media.read(var2, var4, super.buffer);
            super.reply.set(0, 0, 0, var4);
        } else {
            super.reply.set(5, 33, 0, 0);
            var4 = 0;
        }

        super.reply.send(super.out);
        if (0 != var4) {
            super.out.write(super.buffer, 0, var4);
        }

        super.out.flush();
    }

    void client_pa_media_removal(final byte[] var1) throws IOException {
        if (0 == ((int) var1[4] & 1)) {
            D.println(3, "Media removal allowed");
        } else {
            D.println(3, "Media removal prevented");
        }

        super.reply.set(0, 0, 0, 0);
        super.reply.send(super.out);
        super.out.flush();
    }

    boolean client_start_stop_unit(final byte[] var1) throws IOException {
        super.reply.set(0, 0, 0, 0);
        super.reply.send(super.out);
        super.out.flush();
        if (2 == ((int) var1[4] & 3)) {
            this.fdd_state = 0;
            this.event_state = 4;
            D.println(3, "Media eject");
            return false;
        } else {
            return true;
        }
    }

    void client_test_unit_ready() throws IOException {
        if (0 == this.fdd_state) {
            D.println(3, "media not present");
            super.reply.set(2, 58, 0, 0);
        } else if (1 == this.fdd_state) {
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
        final byte[] var1 = {(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        super.reply.set(0, 0, 0, var1.length);
        if (0 == this.fdd_state) {
            super.reply.set(2, 58, 0, 0);
        } else if (1 == this.fdd_state) {
            super.reply.set(6, 40, 0, 0);
        } else {
            final int var2 = (int) (super.media.size() / 2048L - 1L);
            var1[0] = (byte) (var2 >> 24 & 255);
            var1[1] = (byte) (var2 >> 16 & 255);
            var1[2] = (byte) (var2 >> 8 & 255);
            var1[3] = (byte) (var2 & 255);
            var1[6] = (byte) 8;
        }

        super.reply.send(super.out);
        if (2 == this.fdd_state) {
            super.out.write(var1, 0, var1.length);
        }

        super.out.flush();
        D.print(3, "client_read_capacity: ");
        D.hexdump(3, var1, 8);
    }

    void client_read_toc(final byte[] var1) throws IOException {
        final boolean var2 = 0 != ((int) var1[1] & 2);
        final int var3 = ((int) var1[9] & 192) >> 6;
        int var4 = (int) (super.media.size() / 2048L);
        final double var5 = (double) var4 / 75.0 + 2.0;
        final int var7 = (int) var5 / 60;
        final int var8 = (int) var5 % 60;
        final int var9 = (int) ((var5 - (double) ((int) var5)) * 75.0);
        final int var10 = SCSI.mk_int16(var1, 7);

        for (int var11 = 0; var11 < var10; ++var11) {
            super.buffer[var11] = (byte) 0;
        }

        if (0 == var3) {
            super.buffer[0] = (byte) 0;
            super.buffer[1] = (byte) 18;
            super.buffer[2] = (byte) 1;
            super.buffer[3] = (byte) 1;
            super.buffer[4] = (byte) 0;
            super.buffer[5] = (byte) 20;
            super.buffer[6] = (byte) 1;
            super.buffer[7] = (byte) 0;
            super.buffer[8] = (byte) 0;
            super.buffer[9] = (byte) 0;
            super.buffer[10] = (byte) (var2 ? 2 : 0);
            super.buffer[11] = (byte) 0;
            super.buffer[12] = (byte) 0;
            super.buffer[13] = (byte) 20;
            super.buffer[14] = (byte) -86;
            super.buffer[15] = (byte) 0;
            super.buffer[16] = (byte) 0;
            super.buffer[17] = var2 ? (byte) var7 : (byte) (var4 >> 16 & 255);
            super.buffer[18] = var2 ? (byte) var8 : (byte) (var4 >> 8 & 255);
            super.buffer[19] = var2 ? (byte) var9 : (byte) (var4 & 255);
        }

        if (1 == var3) {
            super.buffer[0] = (byte) 0;
            super.buffer[1] = (byte) 10;
            super.buffer[2] = (byte) 1;
            super.buffer[3] = (byte) 1;
            super.buffer[4] = (byte) 0;
            super.buffer[5] = (byte) 20;
            super.buffer[6] = (byte) 1;
            super.buffer[7] = (byte) 0;
            super.buffer[8] = (byte) 0;
            super.buffer[9] = (byte) 0;
            super.buffer[10] = (byte) (var2 ? 2 : 0);
            super.buffer[11] = (byte) 0;
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

    void client_mode_sense(final byte[] var1) throws IOException {
        super.buffer[0] = (byte) 0;
        super.buffer[1] = (byte) 8;
        super.buffer[2] = (byte) 1;
        super.buffer[3] = (byte) 0;
        super.buffer[4] = (byte) 0;
        super.buffer[5] = (byte) 0;
        super.buffer[6] = (byte) 0;
        super.buffer[7] = (byte) 0;
        super.reply.set(0, 0, 0, 8);
        D.hexdump(3, super.buffer, 8);
        super.reply.setmedia((int) super.buffer[2]);
        super.reply.send(super.out);
        super.out.write(super.buffer, 0, 8);
        super.out.flush();
    }

    void client_get_event_status(final byte[] var1) throws IOException {
        final byte var2 = var1[4];
        final int var3 = SCSI.mk_int16(var1, 7);

        for (int var4 = 0; var4 < var3; ++var4) {
            super.buffer[var4] = (byte) 0;
        }

        if (0 == ((int) var1[1] & 1)) {
            super.reply.set(5, 36, 0, 0);
            super.reply.send(super.out);
            super.out.flush();
        }

        if (0 == ((int) var2 & 16)) {
            super.buffer[0] = (byte) 0;
            super.buffer[1] = (byte) 2;
            super.buffer[2] = (byte) -128;
            super.buffer[3] = (byte) 16;
            D.hexdump(3, super.buffer, 4);
            super.reply.set(0, 0, 0, Math.min(4, var3));
            super.reply.send(super.out);
            super.out.write(super.buffer, 0, Math.min(4, var3));
            super.out.flush();
        } else {
            super.buffer[0] = (byte) 0;
            super.buffer[1] = (byte) 6;
            super.buffer[2] = (byte) 4;
            super.buffer[3] = (byte) 16;
            if (0 == this.event_state) {
                super.buffer[4] = (byte) 0;
                super.buffer[5] = (byte) 0;
            } else if (1 == this.event_state) {
                super.buffer[4] = (byte) 4;
                super.buffer[5] = (byte) 2;
                if (4 < var3) {
                    this.event_state = 2;
                }
            } else if (4 == this.event_state) {
                super.buffer[4] = (byte) 3;
                super.buffer[5] = (byte) 0;
                if (4 < var3) {
                    this.event_state = 0;
                }
            } else {
                super.buffer[4] = (byte) 0;
                super.buffer[5] = (byte) 2;
            }

            D.hexdump(3, super.buffer, 8);
            super.reply.set(0, 0, 0, Math.min(8, var3));
            super.reply.send(super.out);
            super.out.write(super.buffer, 0, Math.min(8, var3));
            super.out.flush();
        }

    }
}
