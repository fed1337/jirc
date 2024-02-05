package org.remcons;


import java.util.Arrays;

public class RC4 {
    byte[] keyData = new byte[16];
    byte[] key = new byte[16];
    byte[] pre = new byte[16];
    byte[] sBox = new byte[256];
    byte[] keyBox = new byte[256];
    int i;
    int j;

    public RC4(byte[] var1) {
        System.arraycopy(var1, 0, this.keyData, 0, this.keyData.length);
        this.Init();
    }

    public void Init() {
        this.i = 0;
        this.j = 0;
        Arrays.fill(this.key, (byte) 0);
        System.arraycopy(this.keyData, 0, this.pre, 0, this.pre.length);
        Arrays.fill(this.sBox, (byte) 0);
        Arrays.fill(this.keyBox, (byte) 0);
        this.update_key();
    }

    public void update_key() {
        System.arraycopy(this.pre, 0, this.key, 0, this.key.length);

        int var1;
        for (var1 = 0; var1 < 256; ++var1) {
            this.sBox[var1] = (byte) (var1 & 255);
            this.keyBox[var1] = this.key[var1 % 16];
        }

        int var2 = 0;

        for (var1 = 0; var1 < 256; ++var1) {
            var2 = (var2 & 255) + (this.sBox[var1] & 255) + (this.keyBox[var1] & 255) & 255;
            byte var3 = this.sBox[var1];
            this.sBox[var1] = this.sBox[var2];
            this.sBox[var2] = var3;
        }

        this.i = 0;
        this.j = 0;
    }

    public int randomValue() {
        this.i = (this.i & 255) + 1 & 255;
        this.j = (this.j & 255) + (this.sBox[this.i] & 255) & 255;
        byte var1 = this.sBox[this.i];
        this.sBox[this.i] = this.sBox[this.j];
        this.sBox[this.j] = var1;
        int var2 = (this.sBox[this.i] & 255) + (this.sBox[this.j] & 255) & 255;
        byte var3 = this.sBox[var2];
        return var3;
    }
}
