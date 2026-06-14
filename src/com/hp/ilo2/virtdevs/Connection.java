package com.hp.ilo2.virtdevs;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Connection implements Runnable, ActionListener {
    public static final int FLOPPY = 1;
    public static final int CDROM = 2;
    public static final int USBKEY = 3;
    Socket s = null;
    InputStream in = null;
    BufferedOutputStream out = null;
    final String host;
    final int port;
    int device;
    String target;
    int targetIsDevice;
    SCSI scsi = null;
    boolean writeprot = false;
    final virtdevs v;
    final byte[] pre;
    final byte[] key;
    boolean changing_disks = false;
    VMD5 digest;

    public Connection(final String var1, final int var2, final int var3, final String var4, final int var5, final byte[] var6, final byte[] var7, final virtdevs var8) throws IOException {
        super();
        this.host = var1;
        this.port = var2;
        this.device = var3;
        this.target = var4;
        this.pre = var6;
        this.key = var7;
        this.v = var8;
        final MediaAccess var9 = new MediaAccess();
        final int var10 = var9.devtype(var4);
        if (2 != var10 && 5 != var10) {
            this.targetIsDevice = 0;
            D.println(0, "Got NO CD or removable connection\n");
        } else {
            this.targetIsDevice = 1;
            D.println(0, "Got CD or removable connection\n");
        }

        var9.open(var4, this.targetIsDevice);
        final long var12 = var9.size();
        var9.close();
        if (1 == this.device && 2949120L < var12) {
            this.device = 3;
        }

        this.digest = new VMD5();
    }

    public int connect() throws IOException {
        final byte[] var1 = {(byte) 16, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        this.s = new Socket(this.host, this.port);
        this.s.setTcpNoDelay(true);
        this.in = this.s.getInputStream();
        this.out = new BufferedOutputStream(this.s.getOutputStream());
        this.digest.reset();
        this.digest.update(this.pre);
        this.digest.update(this.key);
        System.arraycopy(this.key, 0, var1, 2, this.key.length);
        var1[1] = (byte) this.device;
        if (0 == this.targetIsDevice) {
            var1[1] = (byte) ((int) var1[1] | -128);
        }

        this.out.write(var1);
        this.out.flush();
        this.in.read(var1, 0, 4);
        D.println(3, "Hello response0: " + D.hex(var1[0], 2));
        D.println(3, "Hello response1: " + D.hex(var1[1], 2));
        if (32 == (int) var1[0] && 0 == (int) var1[1]) {
            D.println(1, "Connected.  Protocol version = " + ((int) var1[3] & 255) + "." + ((int) var1[2] & 255));
            return 0;
        } else {
            D.println(0, "Unexpected Hello Response!");
            this.s.close();
            this.s = null;
            this.in = null;
            this.out = null;
            return (int) var1[0];
        }
    }

    public void close() throws IOException {
        if (null != this.scsi) {
            try {
                this.scsi.send_disconnect();
                final Timer var1 = new Timer(2000, this);
                var1.setRepeats(false);
                var1.start();
                this.scsi.change_disk();
                var1.stop();
            } catch (final Exception var2) {
                this.scsi.change_disk();
            }
        } else {
            this.internal_close();
        }

    }

    public void actionPerformed(final ActionEvent var1) {
        try {
            this.internal_close();
        } catch (final Exception var3) {
        }

    }

    public void internal_close() throws IOException {
        if (null != this.s) {
            this.s.close();
        }

        this.s = null;
        this.in = null;
        this.out = null;
    }

    public void setWriteProt(final boolean var1) {
        this.writeprot = var1;
        if (null != this.scsi) {
            this.scsi.setWriteProt(this.writeprot);
        }

    }

    public void change_disk(final String var1) throws IOException {
        final MediaAccess var3 = new MediaAccess();
        final int var4 = var3.devtype(var1);
        final byte var2;
        if (2 != var4 && 5 != var4) {
            var2 = (byte) 0;
        } else {
            var2 = (byte) 1;
        }

        if (0 == (int) var2) {
            var3.open(var1, 0);
            var3.close();
        }

        this.target = var1;
        this.targetIsDevice = (int) var2;
        this.changing_disks = true;
        this.scsi.change_disk();
    }

    public void run() {
        System.out.println("Message before invoking  connection run method");

        do {
            this.changing_disks = false;

            try {
                if (1 != this.device && 3 != this.device) {
                    if (2 != this.device) {
                        D.println(0, "Unsupported virtual device " + this.device);
                        return;
                    }

                    if (1 == this.targetIsDevice) {
                        this.scsi = new SCSIcdrom(this.s, this.in, this.out, this.target, 1, this.v);
                    } else {
                        this.scsi = new SCSIcdimage(this.s, this.in, this.out, this.target, 0, this.v);
                    }
                } else {
                    this.scsi = new SCSIFloppy(this.s, this.in, this.out, this.target, this.targetIsDevice, this.v);
                }
            } catch (final Exception var3) {
                D.println(0, "Exception while opening " + this.target + "(" + var3 + ")");
            }

            this.scsi.setWriteProt(this.writeprot);

            while (true) {
                try {
                    if (!this.scsi.process()) {
                        System.out.println("Connection can not be established");
                        break;
                    }
                } catch (final IOException var4) {
                    D.println(1, "Exception in Connection::run() " + var4);
                    var4.printStackTrace();
                    break;
                }
            }

            D.println(3, "Closing scsi and socket");

            try {
                this.scsi.close();
                if (!this.changing_disks) {
                    this.internal_close();
                }
            } catch (final IOException var2) {
                D.println(0, "Exception closing connection " + var2);
            }

            this.scsi = null;
        } while (this.changing_disks);

        if (1 != this.device && 3 != this.device) {
            if (2 == this.device) {
                System.out.println("Message before invoking cdDisconnect");
                this.v.cdDisconnect();
            }
        } else {
            System.out.println("Message before invoking fdDisconnect");
            this.v.fdDisconnect();
        }

    }
}
