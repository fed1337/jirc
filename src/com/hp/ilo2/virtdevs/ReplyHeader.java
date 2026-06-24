package com.hp.ilo2.virtdevs;


import java.io.IOException;
import java.io.OutputStream;

class ReplyHeader {
    public static final int magic = 195936478;
    public static final int WP = 1;
    public static final int KEEPALIVE = 2;
    public static final int DISCONNECT = 4;
    private int flags = 0;
    private byte sense_key = (byte) 0;
    private byte asc = (byte) 0;
    private byte ascq = (byte) 0;
    private byte media = (byte) 0;
    private int length = 0;
    private final byte[] data = new byte[16];

    void set(final int var1, final int var2, final int var3, final int var4) {
        this.sense_key = (byte) var1;
        this.asc = (byte) var2;
        this.ascq = (byte) var3;
        this.length = var4;
    }

    void setmedia(final int var1) {
        this.media = (byte) var1;
    }

    void setflags(final boolean var1) {
        if (var1) {
            this.flags |= 1;
        } else {
            this.flags &= -2;
        }

    }

    void keepalive(final boolean var1) {
        if (var1) {
            this.flags |= 2;
        } else {
            this.flags &= -3;
        }

    }

    void disconnect(final boolean var1) {
        if (var1) {
            this.flags |= 4;
        } else {
            this.flags &= -5;
        }

    }

    void send(final OutputStream var1) throws IOException {
        this.data[0] = (byte) -34;
        this.data[1] = (byte) -64;
        this.data[2] = (byte) -83;
        this.data[3] = (byte) 11;
        this.data[4] = (byte) (this.flags & 255);
        this.data[5] = (byte) (this.flags >> 8 & 255);
        this.data[6] = (byte) (this.flags >> 16 & 255);
        this.data[7] = (byte) (this.flags >> 24 & 255);
        this.data[8] = this.media;
        this.data[9] = this.sense_key;
        this.data[10] = this.asc;
        this.data[11] = this.ascq;
        this.data[12] = (byte) (this.length & 255);
        this.data[13] = (byte) (this.length >> 8 & 255);
        this.data[14] = (byte) (this.length >> 16 & 255);
        this.data[15] = (byte) (this.length >> 24 & 255);
        var1.write(this.data, 0, 16);
    }

    void sendsynch(final OutputStream var1, final byte[] var2) throws IOException {
        this.data[0] = (byte) -34;
        this.data[1] = (byte) -64;
        this.data[2] = (byte) -83;
        this.data[3] = (byte) 11;
        this.data[4] = (byte) (this.flags & 255);
        this.data[5] = (byte) (this.flags >> 8 & 255);
        this.data[6] = (byte) (this.flags >> 16 & 255);
        this.data[7] = (byte) (this.flags >> 24 & 255);
        this.data[8] = var2[4];
        this.data[9] = var2[5];
        this.data[10] = var2[6];
        this.data[11] = var2[7];
        this.data[12] = var2[8];
        this.data[13] = var2[9];
        this.data[14] = var2[10];
        this.data[15] = var2[11];
        var1.write(this.data, 0, 16);
    }
}
