package com.hp.ilo2.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.LocalDateTime;

public class SCSIFloppy extends SCSI {
    int fdd_state = 0;
    long media_sz = 0L;
    final byte[] rcs_resp = {(byte) 0, (byte) 0, (byte) 0, (byte) 16, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 11, (byte) 64, (byte) 0, (byte) 0, (byte) 2, (byte) 0};
    virtdevs v;
    VErrorDialog dlg = null;

    public SCSIFloppy(final Socket var1, final InputStream var2, final BufferedOutputStream var3, final String var4, final int var5, final virtdevs var6) throws IOException {
        super(var1, var2, var3, var4, var5);
        final int var7 = super.media.open(var4, var5);
        D.print(1, "open returns " + var7);
        this.v = var6;
    }

    public void setWriteProt(final boolean var1) {
        super.writeprot = var1;
        if (2 == this.fdd_state) {
            this.fdd_state = 0;
        }

    }

    public boolean process() throws IOException {
        D.println(1, "Date = " + LocalDateTime.now());
        D.println(1, "Device: " + super.selectedDevice + " (" + super.targetIsDevice + ")");
        this.read_command(super.req);
        D.print(1, "SCSI Request: ");
        D.hexdump(1, super.req, 12);
        this.media_sz = super.media.size();
        this.v.ParentApp.remconsObj.setvmAct(1);
        if (0L > this.media_sz || null != super.media.dio) {
            D.println(1, "Disk change detected\n");
            super.media.close();
            super.media.open(super.selectedDevice, super.targetIsDevice);
            this.media_sz = super.media.size();
            this.fdd_state = 0;
        }

        D.println(1, "retval=" + this.media_sz + " type=" + super.media.type() + " physdrive=" + (null != super.media.dio ? super.media.dio.PhysicalDevice : -1));
        if (-6L == this.media_sz) {
            new VErrorDialog(this.v.ParentApp.dispFrame, super.selectedDevice + " " + this.v.ParentApp.remconsObj.getLocalString(8288) + "\n\n" + this.v.ParentApp.remconsObj.getLocalString(8239));
            return false;
        } else {
            if (0L >= this.media_sz) {
                super.reply.setmedia(0);
                this.fdd_state = 0;
            } else {
                super.reply.setmedia(36);
                ++this.fdd_state;
                if (2 < this.fdd_state) {
                    this.fdd_state = 2;
                }
            }

            if (!super.writeprot && super.media.wp()) {
                super.writeprot = true;
            }

            switch ((int) super.req[0] & 255) {
                case 0:
                    this.client_test_unit_ready();
                    break;
                case 4:
                    this.client_format_unit(super.req);
                    break;
                case 27:
                    this.client_start_stop_unit(super.req);
                    break;
                case 29:
                    this.client_send_diagnostic();
                    break;
                case 30:
                    this.client_pa_media_removal(super.req);
                    break;
                case 35:
                    this.client_read_capacities();
                    break;
                case 37:
                    this.client_read_capacity();
                    break;
                case 40:
                case 168:
                    this.client_read(super.req);
                    break;
                case 42:
                case 46:
                case 170:
                    this.client_write(super.req);
                    break;
                default:
                    D.println(0, "Unknown request:cmd = " + Integer.toHexString((int) super.req[0]));
            }

            return true;
        }
    }

    void client_read_capacities() throws IOException {
        if (1 == this.fdd_state) {
            super.reply.set(6, 40, 0, this.rcs_resp.length);
            this.fdd_state = 2;
        } else {
            super.reply.set(0, 0, 0, this.rcs_resp.length);
        }

        if (0 == super.media.type()) {
            this.rcs_resp[4] = this.rcs_resp[5] = this.rcs_resp[6] = this.rcs_resp[7] = this.rcs_resp[10] = this.rcs_resp[11] = (byte) 0;
        } else {
            final long var1;
            if (100 == super.media.type()) {
                var1 = super.media.size() / 512L;
                this.rcs_resp[4] = (byte) ((int) (var1 >> 24 & 255L));
                this.rcs_resp[5] = (byte) ((int) (var1 >> 16 & 255L));
                this.rcs_resp[6] = (byte) ((int) (var1 >> 8 & 255L));
                this.rcs_resp[7] = (byte) ((int) (var1 & 255L));
                this.rcs_resp[10] = (byte) 2;
                this.rcs_resp[11] = (byte) 0;
            } else {
                var1 = super.media.size() / (long) super.media.dio.BytesPerSec;
                this.rcs_resp[4] = (byte) ((int) (var1 >> 24 & 255L));
                this.rcs_resp[5] = (byte) ((int) (var1 >> 16 & 255L));
                this.rcs_resp[6] = (byte) ((int) (var1 >> 8 & 255L));
                this.rcs_resp[7] = (byte) ((int) (var1 & 255L));
                this.rcs_resp[10] = (byte) (super.media.dio.BytesPerSec >> 8 & 255);
                this.rcs_resp[11] = (byte) (super.media.dio.BytesPerSec & 255);
            }
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.write(this.rcs_resp, 0, this.rcs_resp.length);
        super.out.flush();
    }

    void client_send_diagnostic() {
        this.fdd_state = 1;
    }

    void client_read(final byte[] var1) throws IOException {
        final long var2 = (long) SCSI.mk_int32(var1, 2) << 9;
        int var4 = SCSI.mk_int16(var1, 7);
        var4 <<= 9;
        D.println(3, "FDIO.client_read:Client read " + var2 + ", len=" + var4);
        if (0L <= var2 && var2 < this.media_sz) {
            try {
                super.media.read(var2, var4, super.buffer);
                super.reply.set(0, 0, 0, var4);
            } catch (final IOException var7) {
                D.println(0, "Exception during read: " + var7);
                super.reply.set(3, 16, 0, 0);
                var4 = 0;
            }
        } else {
            super.reply.set(5, 33, 0, 0);
            var4 = 0;
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        if (0 != var4) {
            super.out.write(super.buffer, 0, var4);
        }

        super.out.flush();
    }

    void client_write(final byte[] var1) throws IOException {
        final long var2 = (long) SCSI.mk_int32(var1, 2) << 9;
        int var4 = SCSI.mk_int16(var1, 7);
        var4 <<= 9;
        D.println(3, "FDIO.client_write:lba = " + var2 + ", length = " + var4);
        this.read_complete(super.buffer, var4);
        if (super.writeprot) {
            super.reply.set(7, 39, 0, 0);
        } else {
            if (0L <= var2 && var2 < this.media_sz) {
                try {
                    super.media.write(var2, var4, super.buffer);
                    super.reply.set(0, 0, 0, 0);
                } catch (final IOException var7) {
                    D.println(0, "Exception during write: " + var7);
                    super.reply.set(3, 16, 0, 0);
                }
            } else {
                super.reply.set(5, 33, 0, 0);
            }
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
    }

    void client_pa_media_removal(final byte[] var1) throws IOException {
        if (0 == ((int) var1[4] & 1)) {
            super.reply.set(0, 0, 0, 0);
        } else {
            super.reply.set(5, 36, 0, 0);
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
    }

    void client_start_stop_unit(final byte[] var1) throws IOException {
        if (0 == ((int) var1[4] & 2)) {
            super.reply.set(0, 0, 0, 0);
        } else {
            super.reply.set(5, 36, 0, 0);
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
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

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
    }

    void client_format_unit(final byte[] var1) throws IOException {
        final byte[] var2 = new byte[100];
        final int var3 = SCSI.mk_int16(var1, 7);
        this.read_complete(var2, var3);
        D.print(3, "Format params: ");
        D.hexdump(3, var2, var3);
        final int var5 = (int) var2[1] & 1;
        final byte var6;
        if (2880 == SCSI.mk_int32(var2, 4) && 512 == SCSI.mk_int24(var2, 9)) {
            var6 = (byte) 2;
        } else if (1440 == SCSI.mk_int32(var2, 4) && 512 == SCSI.mk_int24(var2, 9)) {
            var6 = (byte) 5;
        } else {
            var6 = (byte) 0;
        }

        if (super.writeprot) {
            super.reply.set(7, 39, 0, 0);
        } else if (0 != (int) var6) {
            final int var7 = (int) var1[2] & 255;
            super.media.format((int) var6, var7, var7, var5, var5);
            D.println(3, "format");
            super.reply.set(0, 0, 0, 0);
        } else {
            super.reply.set(5, 38, 0, 0);
        }

        super.reply.setflags(super.writeprot);
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
        } else if (0 != super.media.type()) {
            final long var2;
            if (100 == super.media.type()) {
                var2 = super.media.size() / 512L - 1L;
                var1[0] = (byte) ((int) (var2 >> 24 & 255L));
                var1[1] = (byte) ((int) (var2 >> 16 & 255L));
                var1[2] = (byte) ((int) (var2 >> 8 & 255L));
                var1[3] = (byte) ((int) (var2 & 255L));
                var1[6] = (byte) 2;
            } else {
                var2 = super.media.size() / (long) super.media.dio.BytesPerSec - 1L;
                var1[0] = (byte) ((int) (var2 >> 24 & 255L));
                var1[1] = (byte) ((int) (var2 >> 16 & 255L));
                var1[2] = (byte) ((int) (var2 >> 8 & 255L));
                var1[3] = (byte) ((int) (var2 & 255L));
                var1[6] = (byte) (super.media.dio.BytesPerSec >> 8 & 255);
                var1[7] = (byte) (super.media.dio.BytesPerSec & 255);
            }
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        if (2 == this.fdd_state) {
            super.out.write(var1, 0, var1.length);
        }

        super.out.flush();
        D.print(3, "FDIO.client_read_capacity: ");
        D.hexdump(3, var1, 8);
    }
}
