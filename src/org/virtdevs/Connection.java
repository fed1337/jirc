package org.virtdevs;


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
    Socket s;
    InputStream in;
    BufferedOutputStream out;
    String host;
    int port;
    int device;
    String target;
    int targetIsDevice;
    SCSI scsi;
    boolean writeprot = false;
    virtdevs v;
    byte[] pre;
    byte[] key;
    boolean changing_disks;
    VMD5 digest;

    public Connection(String var1, int var2, int var3, String var4, int var5, byte[] var6, byte[] var7, virtdevs var8) throws IOException {
        this.host = var1;
        this.port = var2;
        this.device = var3;
        this.target = var4;
        this.pre = var6;
        this.key = var7;
        this.v = var8;
        MediaAccess var9 = new MediaAccess();
        int var10 = var9.devtype(var4);
        if (var10 != 2 && var10 != 5) {
            this.targetIsDevice = 0;
            D.println(0, "Got NO CD or removable connection\n");
        } else {
            this.targetIsDevice = 1;
            D.println(0, "Got CD or removable connection\n");
        }

        var9.open(var4, this.targetIsDevice);
        long var12 = var9.size();
        var9.close();
        if (this.device == 1 && var12 > 2949120L) {
            this.device = 3;
        }

        this.digest = new VMD5();
    }

    public int connect() throws IOException {
        byte[] var1 = new byte[]{16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.s = new Socket(this.host, this.port);
        this.s.setTcpNoDelay(true);
        this.in = this.s.getInputStream();
        this.out = new BufferedOutputStream(this.s.getOutputStream());
        this.digest.reset();
        this.digest.update(this.pre);
        this.digest.update(this.key);
        System.arraycopy(this.key, 0, var1, 2, this.key.length);
        var1[1] = (byte) this.device;
        if (this.targetIsDevice == 0) {
            var1[1] |= -128;
        }

        this.out.write(var1);
        this.out.flush();
        this.in.read(var1, 0, 4);
        D.println(3, "Hello response0: " + D.hex(var1[0], 2));
        D.println(3, "Hello response1: " + D.hex(var1[1], 2));
        if (var1[0] == 32 && var1[1] == 0) {
            D.println(1, "Connected.  Protocol version = " + (var1[3] & 255) + "." + (var1[2] & 255));
            return 0;
        } else {
            D.println(0, "Unexpected Hello Response!");
            this.s.close();
            this.s = null;
            this.in = null;
            this.out = null;
            return var1[0];
        }
    }

    public void close() throws IOException {
        if (this.scsi != null) {
            try {
                this.scsi.send_disconnect();
                Timer var1 = new Timer(2000, this);
                var1.setRepeats(false);
                var1.start();
                this.scsi.change_disk();
                var1.stop();
            } catch (Exception var2) {
                this.scsi.change_disk();
            }
        } else {
            this.internal_close();
        }

    }

    public void actionPerformed(ActionEvent var1) {
        try {
            this.internal_close();
        } catch (Exception var3) {
        }

    }

    public void internal_close() throws IOException {
        if (this.s != null) {
            this.s.close();
        }

        this.s = null;
        this.in = null;
        this.out = null;
    }

    public void setWriteProt(boolean var1) {
        this.writeprot = var1;
        if (this.scsi != null) {
            this.scsi.setWriteProt(this.writeprot);
        }

    }

    public void change_disk(String var1) throws IOException {
        MediaAccess var3 = new MediaAccess();
        int var4 = var3.devtype(var1);
        byte var2;
        if (var4 != 2 && var4 != 5) {
            var2 = 0;
        } else {
            var2 = 1;
        }

        if (var2 == 0) {
            var3.open(var1, 0);
            var3.close();
        }

        this.target = var1;
        this.targetIsDevice = var2;
        this.changing_disks = true;
        this.scsi.change_disk();
    }

    public void run() {
        System.out.println("Message before invoking  connection run method");

        do {
            this.changing_disks = false;

            try {
                if (this.device != 1 && this.device != 3) {
                    if (this.device != 2) {
                        D.println(0, "Unsupported virtual device " + this.device);
                        return;
                    }

                    if (this.targetIsDevice == 1) {
                        this.scsi = new SCSIcdrom(this.s, this.in, this.out, this.target, 1, this.v);
                    } else {
                        this.scsi = new SCSIcdimage(this.s, this.in, this.out, this.target, 0, this.v);
                    }
                } else {
                    this.scsi = new SCSIFloppy(this.s, this.in, this.out, this.target, this.targetIsDevice, this.v);
                }
            } catch (Exception var3) {
                D.println(0, "Exception while opening " + this.target + "(" + var3 + ")");
            }

            this.scsi.setWriteProt(this.writeprot);

            while (true) {
                try {
                    if (!this.scsi.process()) {
                        System.out.println("Connection can not be stablished");
                        break;
                    }
                } catch (IOException var4) {
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
            } catch (IOException var2) {
                D.println(0, "Exception closing connection " + var2);
            }

            this.scsi = null;
        } while (this.changing_disks);

        if (this.device != 1 && this.device != 3) {
            if (this.device == 2) {
                System.out.println("Message before invoking cdDisconnect");
                this.v.cdDisconnect();
            }
        } else {
            System.out.println("Message before invoking fdDisconnect");
            this.v.fdDisconnect();
        }

    }
}
