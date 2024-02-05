package org.virtdevs;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;

public class SCSIFloppy extends SCSI {
    int fdd_state = 0;
    long media_sz;
    Date date = new Date();
    byte[] rcs_resp = new byte[]{0, 0, 0, 16, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 11, 64, 0, 0, 2, 0};
    virtdevs v;
    VErrorDialog dlg;

    public SCSIFloppy(Socket var1, InputStream var2, BufferedOutputStream var3, String var4, int var5, virtdevs var6) throws IOException {
        super(var1, var2, var3, var4, var5);
        int var7 = super.media.open(var4, var5);
        D.print(1, "open returns " + var7);
        this.v = var6;
    }

    public void setWriteProt(boolean var1) {
        super.writeprot = var1;
        if (this.fdd_state == 2) {
            this.fdd_state = 0;
        }

    }

    public boolean process() throws IOException {
        this.date.setTime(System.currentTimeMillis());
        D.println(1, "Date = " + this.date);
        D.println(1, "Device: " + super.selectedDevice + " (" + super.targetIsDevice + ")");
        this.read_command(super.req, 12);
        D.print(1, "SCSI Request: ");
        D.hexdump(1, super.req, 12);
        this.media_sz = super.media.size();
        this.v.ParentApp.remconsObj.setvmAct(1);
        if (this.media_sz < 0L || super.media.dio != null && super.media.dio.filehandle == -1) {
            D.println(1, "Disk change detected\n");
            super.media.close();
            super.media.open(super.selectedDevice, super.targetIsDevice);
            this.media_sz = super.media.size();
            this.fdd_state = 0;
        }

        D.println(1, "retval=" + this.media_sz + " type=" + super.media.type() + " physdrive=" + (super.media.dio != null ? super.media.dio.PhysicalDevice : -1));
        if (this.media_sz == -6L) {
            new VErrorDialog(this.v.ParentApp.dispFrame, super.selectedDevice + " " + this.v.ParentApp.remconsObj.getLocalString(8288) + "\n\n" + this.v.ParentApp.remconsObj.getLocalString(8239));
            return false;
        } else {
            if (this.media_sz <= 0L) {
                super.reply.setmedia(0);
                this.fdd_state = 0;
            } else {
                super.reply.setmedia(36);
                ++this.fdd_state;
                if (this.fdd_state > 2) {
                    this.fdd_state = 2;
                }
            }

            if (!super.writeprot && super.media.wp()) {
                super.writeprot = true;
            }

            switch (super.req[0] & 255) {
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
                    D.println(0, "Unknown request:cmd = " + Integer.toHexString(super.req[0]));
            }

            return true;
        }
    }

    void client_read_capacities() throws IOException {
        if (this.fdd_state != 1) {
            super.reply.set(0, 0, 0, this.rcs_resp.length);
        } else {
            super.reply.set(6, 40, 0, this.rcs_resp.length);
            this.fdd_state = 2;
        }

        if (super.media.type() == 0) {
            this.rcs_resp[4] = this.rcs_resp[5] = this.rcs_resp[6] = this.rcs_resp[7] = this.rcs_resp[10] = this.rcs_resp[11] = 0;
        } else {
            long var1;
            if (super.media.type() == 100) {
                var1 = super.media.size() / 512L;
                this.rcs_resp[4] = (byte) ((int) (var1 >> 24 & 255L));
                this.rcs_resp[5] = (byte) ((int) (var1 >> 16 & 255L));
                this.rcs_resp[6] = (byte) ((int) (var1 >> 8 & 255L));
                this.rcs_resp[7] = (byte) ((int) (var1 >> 0 & 255L));
                this.rcs_resp[10] = 2;
                this.rcs_resp[11] = 0;
            } else {
                var1 = super.media.size() / (long) super.media.dio.BytesPerSec;
                this.rcs_resp[4] = (byte) ((int) (var1 >> 24 & 255L));
                this.rcs_resp[5] = (byte) ((int) (var1 >> 16 & 255L));
                this.rcs_resp[6] = (byte) ((int) (var1 >> 8 & 255L));
                this.rcs_resp[7] = (byte) ((int) (var1 >> 0 & 255L));
                this.rcs_resp[10] = (byte) (super.media.dio.BytesPerSec >> 8 & 255);
                this.rcs_resp[11] = (byte) (super.media.dio.BytesPerSec & 255);
            }
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.write(this.rcs_resp, 0, this.rcs_resp.length);
        super.out.flush();
    }

    void client_send_diagnostic() throws IOException {
        this.fdd_state = 1;
    }

    void client_read(byte[] var1) throws IOException {
        boolean var5 = var1[0] == 168;
        long var2 = (long) SCSI.mk_int32(var1, 2) * 512L;
        int var4 = var5 ? SCSI.mk_int32(var1, 6) : SCSI.mk_int16(var1, 7);
        var4 *= 512;
        D.println(3, "FDIO.client_read:Client read " + var2 + ", len=" + var4);
        if (var2 >= 0L && var2 < this.media_sz) {
            try {
                super.media.read(var2, var4, super.buffer);
                super.reply.set(0, 0, 0, var4);
            } catch (IOException var7) {
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
        if (var4 != 0) {
            super.out.write(super.buffer, 0, var4);
        }

        super.out.flush();
    }

    void client_write(byte[] var1) throws IOException {
        boolean var5 = var1[0] == 170;
        long var2 = (long) SCSI.mk_int32(var1, 2) * 512L;
        int var4 = var5 ? SCSI.mk_int32(var1, 6) : SCSI.mk_int16(var1, 7);
        var4 *= 512;
        D.println(3, "FDIO.client_write:lba = " + var2 + ", length = " + var4);
        this.read_complete(super.buffer, var4);
        if (!super.writeprot) {
            if (var2 >= 0L && var2 < this.media_sz) {
                try {
                    super.media.write(var2, var4, super.buffer);
                    super.reply.set(0, 0, 0, 0);
                } catch (IOException var7) {
                    D.println(0, "Exception during write: " + var7);
                    super.reply.set(3, 16, 0, 0);
                }
            } else {
                super.reply.set(5, 33, 0, 0);
            }
        } else {
            super.reply.set(7, 39, 0, 0);
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
    }

    void client_pa_media_removal(byte[] var1) throws IOException {
        if ((var1[4] & 1) != 0) {
            super.reply.set(5, 36, 0, 0);
        } else {
            super.reply.set(0, 0, 0, 0);
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
    }

    void client_start_stop_unit(byte[] var1) throws IOException {
        if ((var1[4] & 2) != 0) {
            super.reply.set(5, 36, 0, 0);
        } else {
            super.reply.set(0, 0, 0, 0);
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
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

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        super.out.flush();
    }

    void client_format_unit(byte[] var1) throws IOException {
        byte[] var2 = new byte[100];
        int var3 = SCSI.mk_int16(var1, 7);
        this.read_complete(var2, var3);
        D.print(3, "Format params: ");
        D.hexdump(3, var2, var3);
        int var5 = var2[1] & 1;
        byte var6;
        if (SCSI.mk_int32(var2, 4) == 2880 && SCSI.mk_int24(var2, 9) == 512) {
            var6 = 2;
        } else if (SCSI.mk_int32(var2, 4) == 1440 && SCSI.mk_int24(var2, 9) == 512) {
            var6 = 5;
        } else {
            var6 = 0;
        }

        if (super.writeprot) {
            super.reply.set(7, 39, 0, 0);
        } else if (var6 != 0) {
            int var7 = var1[2] & 255;
            super.media.format(var6, var7, var7, var5, var5);
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
        byte[] var1 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        super.reply.set(0, 0, 0, var1.length);
        if (this.fdd_state == 0) {
            super.reply.set(2, 58, 0, 0);
        } else if (this.fdd_state == 1) {
            super.reply.set(6, 40, 0, 0);
        } else if (super.media.type() != 0) {
            long var2;
            if (super.media.type() == 100) {
                var2 = super.media.size() / 512L - 1L;
                var1[0] = (byte) ((int) (var2 >> 24 & 255L));
                var1[1] = (byte) ((int) (var2 >> 16 & 255L));
                var1[2] = (byte) ((int) (var2 >> 8 & 255L));
                var1[3] = (byte) ((int) (var2 >> 0 & 255L));
                var1[6] = 2;
            } else {
                var2 = super.media.size() / (long) super.media.dio.BytesPerSec - 1L;
                var1[0] = (byte) ((int) (var2 >> 24 & 255L));
                var1[1] = (byte) ((int) (var2 >> 16 & 255L));
                var1[2] = (byte) ((int) (var2 >> 8 & 255L));
                var1[3] = (byte) ((int) (var2 >> 0 & 255L));
                var1[6] = (byte) (super.media.dio.BytesPerSec >> 8 & 255);
                var1[7] = (byte) (super.media.dio.BytesPerSec & 255);
            }
        }

        super.reply.setflags(super.writeprot);
        super.reply.send(super.out);
        if (this.fdd_state == 2) {
            super.out.write(var1, 0, var1.length);
        }

        super.out.flush();
        D.print(3, "FDIO.client_read_capacity: ");
        D.hexdump(3, var1, 8);
    }
}
