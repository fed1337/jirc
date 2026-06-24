package com.hp.ilo2.remcons;


final class VMD5 {
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;
    private byte[] digestBits;
    private String algorithm = null;
    private int[] state;
    private long count;
    private byte[] buffer;
    private int[] transformBuffer;

    private VMD5() {
        super();
        this.init();
    }

    private VMD5(final VMD5 var1) {
        this();
        this.state = new int[var1.state.length];
        System.arraycopy(var1.state, 0, this.state, 0, var1.state.length);
        this.transformBuffer = new int[var1.transformBuffer.length];
        System.arraycopy(var1.transformBuffer, 0, this.transformBuffer, 0, var1.transformBuffer.length);
        this.buffer = new byte[var1.buffer.length];
        System.arraycopy(var1.buffer, 0, this.buffer, 0, var1.buffer.length);
        this.digestBits = new byte[var1.digestBits.length];
        System.arraycopy(var1.digestBits, 0, this.digestBits, 0, var1.digestBits.length);
        this.count = var1.count;
        this.algorithm = var1.algorithm;
    }

    private static int F(final int var1, final int var2, final int var3) {
        return var1 & var2 | ~var1 & var3;
    }

    private static int G(final int var1, final int var2, final int var3) {
        return var1 & var3 | var2 & ~var3;
    }

    private static int H(final int var1, final int var2, final int var3) {
        return var1 ^ var2 ^ var3;
    }

    private static int I(final int var1, final int var2, final int var3) {
        return var2 ^ (var1 | ~var3);
    }

    private static int rotateLeft(final int var1, final int var2) {
        return var1 << var2 | var1 >>> 32 - var2;
    }

    private static int FF(int var1, final int var2, final int var3, final int var4, final int var5, final int var6, final int var7) {
        var1 += VMD5.F(var2, var3, var4) + var5 + var7;
        var1 = VMD5.rotateLeft(var1, var6);
        var1 += var2;
        return var1;
    }

    private static int GG(int var1, final int var2, final int var3, final int var4, final int var5, final int var6, final int var7) {
        var1 += VMD5.G(var2, var3, var4) + var5 + var7;
        var1 = VMD5.rotateLeft(var1, var6);
        var1 += var2;
        return var1;
    }

    private static int HH(int var1, final int var2, final int var3, final int var4, final int var5, final int var6, final int var7) {
        var1 += VMD5.H(var2, var3, var4) + var5 + var7;
        var1 = VMD5.rotateLeft(var1, var6);
        var1 += var2;
        return var1;
    }

    private static int II(int var1, final int var2, final int var3, final int var4, final int var5, final int var6, final int var7) {
        var1 += VMD5.I(var2, var3, var4) + var5 + var7;
        var1 = VMD5.rotateLeft(var1, var6);
        var1 += var2;
        return var1;
    }

    private void transform(final byte[] var1, final int var2) {
        final int[] var3 = this.transformBuffer;
        int var4 = this.state[0];
        int var5 = this.state[1];
        int var6 = this.state[2];
        int var7 = this.state[3];

        for (int var8 = 0; 16 > var8; ++var8) {
            var3[var8] = (int) var1[(var8 << 2) + var2] & 255;

            for (int var9 = 1; 4 > var9; ++var9) {
                var3[var8] += ((int) var1[(var8 << 2) + var9 + var2] & 255) << (var9 << 3);
            }
        }

        var4 = VMD5.FF(var4, var5, var6, var7, var3[0], 7, -680876936);
        var7 = VMD5.FF(var7, var4, var5, var6, var3[1], 12, -389564586);
        var6 = VMD5.FF(var6, var7, var4, var5, var3[2], 17, 606105819);
        var5 = VMD5.FF(var5, var6, var7, var4, var3[3], 22, -1044525330);
        var4 = VMD5.FF(var4, var5, var6, var7, var3[4], 7, -176418897);
        var7 = VMD5.FF(var7, var4, var5, var6, var3[5], 12, 1200080426);
        var6 = VMD5.FF(var6, var7, var4, var5, var3[6], 17, -1473231341);
        var5 = VMD5.FF(var5, var6, var7, var4, var3[7], 22, -45705983);
        var4 = VMD5.FF(var4, var5, var6, var7, var3[8], 7, 1770035416);
        var7 = VMD5.FF(var7, var4, var5, var6, var3[9], 12, -1958414417);
        var6 = VMD5.FF(var6, var7, var4, var5, var3[10], 17, -42063);
        var5 = VMD5.FF(var5, var6, var7, var4, var3[11], 22, -1990404162);
        var4 = VMD5.FF(var4, var5, var6, var7, var3[12], 7, 1804603682);
        var7 = VMD5.FF(var7, var4, var5, var6, var3[13], 12, -40341101);
        var6 = VMD5.FF(var6, var7, var4, var5, var3[14], 17, -1502002290);
        var5 = VMD5.FF(var5, var6, var7, var4, var3[15], 22, 1236535329);
        var4 = VMD5.GG(var4, var5, var6, var7, var3[1], 5, -165796510);
        var7 = VMD5.GG(var7, var4, var5, var6, var3[6], 9, -1069501632);
        var6 = VMD5.GG(var6, var7, var4, var5, var3[11], 14, 643717713);
        var5 = VMD5.GG(var5, var6, var7, var4, var3[0], 20, -373897302);
        var4 = VMD5.GG(var4, var5, var6, var7, var3[5], 5, -701558691);
        var7 = VMD5.GG(var7, var4, var5, var6, var3[10], 9, 38016083);
        var6 = VMD5.GG(var6, var7, var4, var5, var3[15], 14, -660478335);
        var5 = VMD5.GG(var5, var6, var7, var4, var3[4], 20, -405537848);
        var4 = VMD5.GG(var4, var5, var6, var7, var3[9], 5, 568446438);
        var7 = VMD5.GG(var7, var4, var5, var6, var3[14], 9, -1019803690);
        var6 = VMD5.GG(var6, var7, var4, var5, var3[3], 14, -187363961);
        var5 = VMD5.GG(var5, var6, var7, var4, var3[8], 20, 1163531501);
        var4 = VMD5.GG(var4, var5, var6, var7, var3[13], 5, -1444681467);
        var7 = VMD5.GG(var7, var4, var5, var6, var3[2], 9, -51403784);
        var6 = VMD5.GG(var6, var7, var4, var5, var3[7], 14, 1735328473);
        var5 = VMD5.GG(var5, var6, var7, var4, var3[12], 20, -1926607734);
        var4 = VMD5.HH(var4, var5, var6, var7, var3[5], 4, -378558);
        var7 = VMD5.HH(var7, var4, var5, var6, var3[8], 11, -2022574463);
        var6 = VMD5.HH(var6, var7, var4, var5, var3[11], 16, 1839030562);
        var5 = VMD5.HH(var5, var6, var7, var4, var3[14], 23, -35309556);
        var4 = VMD5.HH(var4, var5, var6, var7, var3[1], 4, -1530992060);
        var7 = VMD5.HH(var7, var4, var5, var6, var3[4], 11, 1272893353);
        var6 = VMD5.HH(var6, var7, var4, var5, var3[7], 16, -155497632);
        var5 = VMD5.HH(var5, var6, var7, var4, var3[10], 23, -1094730640);
        var4 = VMD5.HH(var4, var5, var6, var7, var3[13], 4, 681279174);
        var7 = VMD5.HH(var7, var4, var5, var6, var3[0], 11, -358537222);
        var6 = VMD5.HH(var6, var7, var4, var5, var3[3], 16, -722521979);
        var5 = VMD5.HH(var5, var6, var7, var4, var3[6], 23, 76029189);
        var4 = VMD5.HH(var4, var5, var6, var7, var3[9], 4, -640364487);
        var7 = VMD5.HH(var7, var4, var5, var6, var3[12], 11, -421815835);
        var6 = VMD5.HH(var6, var7, var4, var5, var3[15], 16, 530742520);
        var5 = VMD5.HH(var5, var6, var7, var4, var3[2], 23, -995338651);
        var4 = VMD5.II(var4, var5, var6, var7, var3[0], 6, -198630844);
        var7 = VMD5.II(var7, var4, var5, var6, var3[7], 10, 1126891415);
        var6 = VMD5.II(var6, var7, var4, var5, var3[14], 15, -1416354905);
        var5 = VMD5.II(var5, var6, var7, var4, var3[5], 21, -57434055);
        var4 = VMD5.II(var4, var5, var6, var7, var3[12], 6, 1700485571);
        var7 = VMD5.II(var7, var4, var5, var6, var3[3], 10, -1894986606);
        var6 = VMD5.II(var6, var7, var4, var5, var3[10], 15, -1051523);
        var5 = VMD5.II(var5, var6, var7, var4, var3[1], 21, -2054922799);
        var4 = VMD5.II(var4, var5, var6, var7, var3[8], 6, 1873313359);
        var7 = VMD5.II(var7, var4, var5, var6, var3[15], 10, -30611744);
        var6 = VMD5.II(var6, var7, var4, var5, var3[6], 15, -1560198380);
        var5 = VMD5.II(var5, var6, var7, var4, var3[13], 21, 1309151649);
        var4 = VMD5.II(var4, var5, var6, var7, var3[4], 6, -145523070);
        var7 = VMD5.II(var7, var4, var5, var6, var3[11], 10, -1120210379);
        var6 = VMD5.II(var6, var7, var4, var5, var3[2], 15, 718787259);
        var5 = VMD5.II(var5, var6, var7, var4, var3[9], 21, -343485551);
        final int[] var10000 = this.state;
        var10000[0] += var4;
        var10000[1] += var5;
        var10000[2] += var6;
        var10000[3] += var7;
    }

    private void init() {
        this.state = new int[4];
        this.transformBuffer = new int[16];
        this.buffer = new byte[64];
        this.digestBits = new byte[16];
        this.count = 0L;
        this.state[0] = 1732584193;
        this.state[1] = -271733879;
        this.state[2] = -1732584194;
        this.state[3] = 271733878;

        for (int var1 = 0; var1 < this.digestBits.length; ++var1) {
            this.digestBits[var1] = (byte) 0;
        }

    }

    private void engineReset() {
        this.init();
    }

    private synchronized void engineUpdate(final byte var1) {
        final int var2 = (int) (this.count >>> 3 & 63L);
        this.count += 8L;
        this.buffer[var2] = var1;
        if (63 == var2) {
            this.transform(this.buffer, 0);
        }

    }

    private synchronized void engineUpdate(final byte[] var1, final int var2, int var3) {
        int var4 = var2;

        while (true) {
            while (0 < var3) {
                final int var5 = (int) (this.count >>> 3 & 63L);
                if (0 == var5 && 64 < var3) {
                    this.count += 512L;
                    this.transform(var1, var4);
                    var3 -= 64;
                    var4 += 64;
                } else {
                    this.count += 8L;
                    this.buffer[var5] = var1[var4];
                    if (63 == var5) {
                        this.transform(this.buffer, 0);
                    }

                    ++var4;
                    --var3;
                }
            }

            return;
        }
    }

    private void finish() {
        final byte[] var1 = new byte[8];

        int var6;
        for (var6 = 0; 8 > var6; ++var6) {
            var1[var6] = (byte) ((int) (this.count >>> (var6 << 3) & 255L));
        }

        final int var8 = (int) (this.count >> 3) & 63;
        var6 = 56 > var8 ? 56 - var8 : 120 - var8;
        final byte[] var5 = new byte[var6];
        var5[0] = (byte) -128;
        this.engineUpdate(var5, 0, var5.length);
        this.engineUpdate(var1, 0, var1.length);

        for (var6 = 0; 4 > var6; ++var6) {
            for (int var7 = 0; 4 > var7; ++var7) {
                this.digestBits[(var6 << 2) + var7] = (byte) (this.state[var6] >>> (var7 << 3) & 255);
            }
        }

    }

    private byte[] engineDigest() {
        this.finish();
        final byte[] var1 = new byte[16];
        System.arraycopy(this.digestBits, 0, var1, 0, 16);
        this.init();
        return var1;
    }

    public void reset() {
        this.engineReset();
    }

    public void update(final byte var1) {
        this.engineUpdate(var1);
    }

    public void update(final byte[] var1, final int var2, final int var3) {
        this.engineUpdate(var1, var2, var3);
    }

    public void update(final byte[] var1) {
        this.engineUpdate(var1, 0, var1.length);
    }

    public byte[] digest() {
        this.digestBits = this.engineDigest();
        return this.digestBits;
    }
}
