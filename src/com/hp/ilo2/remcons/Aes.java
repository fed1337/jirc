package com.hp.ilo2.remcons;


public class Aes {
    public static final int Bits128 = 0;
    public static final int Bits192 = 1;
    public static final int Bits256 = 2;
    private final byte[] key;
    final byte[] ofb_iv = new byte[16];
    int ofb_num;
    private int Nb;
    private int Nk = 0;
    private int Nr = 0;
    private byte[][] Sbox;
    private byte[][] iSbox;
    private byte[][] w;
    private byte[][] Rcon;
    private byte[][] State = null;

    public Aes(final int var1, final byte[] var2) {
        super();
        this.SetNbNkNr(var1);
        this.key = new byte[(this.Nk << 2)];
        if (var2.length < this.key.length) {
            System.out.println("Alert: KeyBytes size is less than specified KeySize");
        }

        System.arraycopy(var2, 0, this.key, 0, this.key.length);
        this.BuildSbox();
        this.BuildInvSbox();
        this.BuildRcon();
        this.KeyExpansion();
    }

    private static byte gfmultby01(final byte var0) {
        return var0;
    }

    private static byte gfmultby02(final byte var0) {
        return 128 > ((int) var0 & 255) ? (byte) ((int) var0 << 1 & 255) : (byte) ((int) var0 << 1 & 255 ^ 27);
    }

    private static byte gfmultby03(final byte var0) {
        return (byte) ((int) Aes.gfmultby02(var0) & 255 ^ (int) var0 & 255);
    }

    private static byte gfmultby09(final byte var0) {
        return (byte) ((int) Aes.gfmultby02(Aes.gfmultby02(Aes.gfmultby02(var0))) & 255 ^ (int) var0 & 255);
    }

    private static byte gfmultby0b(final byte var0) {
        return (byte) ((int) Aes.gfmultby02(Aes.gfmultby02(Aes.gfmultby02(var0))) & 255 ^ (int) Aes.gfmultby02(var0) & 255 ^ (int) var0 & 255);
    }

    private static byte gfmultby0d(final byte var0) {
        return (byte) ((int) Aes.gfmultby02(Aes.gfmultby02(Aes.gfmultby02(var0))) & 255 ^ (int) Aes.gfmultby02(Aes.gfmultby02(var0)) & 255 ^ (int) var0 & 255);
    }

    private static byte gfmultby0e(final byte var0) {
        return (byte) ((int) Aes.gfmultby02(Aes.gfmultby02(Aes.gfmultby02(var0))) & 255 ^ (int) Aes.gfmultby02(Aes.gfmultby02(var0)) & 255 ^ (int) Aes.gfmultby02(var0) & 255);
    }

    public void Cipher(final byte[] var1, final byte[] var2) {
        if (16 > var1.length) {
            System.out.println("Alert- InputSize:" + var1.length + " is less than standard size:16");
        }

        if (16 > var2.length) {
            System.out.println("Alert- OutputSize:" + var2.length + " is less than standard size:16");
        }

        this.State = new byte[4][this.Nb];

        for (int var3 = 0; var3 < 4 * this.Nb; ++var3) {
            this.State[var3 % 4][var3 / 4] = var1[var3];
        }

        this.AddRoundKey(0);

        for (int var4 = 1; var4 <= this.Nr - 1; ++var4) {
            this.SubBytes();
            this.ShiftRows();
            this.MixColumns();
            this.AddRoundKey(var4);
        }

        this.SubBytes();
        this.ShiftRows();
        this.AddRoundKey(this.Nr);

        for (int var5 = 0; var5 < 4 * this.Nb; ++var5) {
            var2[var5] = this.State[var5 % 4][var5 / 4];
        }

    }

    public void InvCipher(final byte[] var1, final byte[] var2) {
        this.State = new byte[4][this.Nb];

        for (int var3 = 0; var3 < 4 * this.Nb; ++var3) {
            this.State[var3 % 4][var3 / 4] = var1[var3];
        }

        this.AddRoundKey(this.Nr);

        for (int var4 = this.Nr - 1; 1 <= var4; --var4) {
            this.InvShiftRows();
            this.InvSubBytes();
            this.AddRoundKey(var4);
            this.InvMixColumns();
        }

        this.InvShiftRows();
        this.InvSubBytes();
        this.AddRoundKey(0);

        for (int var5 = 0; var5 < 4 * this.Nb; ++var5) {
            var2[var5] = this.State[var5 % 4][var5 / 4];
        }

    }

    private void SetNbNkNr(final int var1) {
        this.Nb = 4;
        if (0 == var1) {
            this.Nk = 4;
            this.Nr = 10;
        } else if (1 == var1) {
            this.Nk = 6;
            this.Nr = 12;
        } else if (2 == var1) {
            this.Nk = 8;
            this.Nr = 14;
        } else {
            System.out.println("Alert: Invalid keysize Specified for SetNbNkNr");
            System.out.println("Pls use constants from Aes.KeySize");
        }

    }

    private void BuildSbox() {
        this.Sbox = new byte[][]{{(byte) 99, (byte) 124, (byte) 119, (byte) 123, (byte) -14, (byte) 107, (byte) 111, (byte) -59, (byte) 48, (byte) 1, (byte) 103, (byte) 43, (byte) -2, (byte) -41, (byte) -85, (byte) 118}, {(byte) -54, (byte) -126, (byte) -55, (byte) 125, (byte) -6, (byte) 89, (byte) 71, (byte) -16, (byte) -83, (byte) -44, (byte) -94, (byte) -81, (byte) -100, (byte) -92, (byte) 114, (byte) -64}, {(byte) -73, (byte) -3, (byte) -109, (byte) 38, (byte) 54, (byte) 63, (byte) -9, (byte) -52, (byte) 52, (byte) -91, (byte) -27, (byte) -15, (byte) 113, (byte) -40, (byte) 49, (byte) 21}, {(byte) 4, (byte) -57, (byte) 35, (byte) -61, (byte) 24, (byte) -106, (byte) 5, (byte) -102, (byte) 7, (byte) 18, (byte) -128, (byte) -30, (byte) -21, (byte) 39, (byte) -78, (byte) 117}, {(byte) 9, (byte) -125, (byte) 44, (byte) 26, (byte) 27, (byte) 110, (byte) 90, (byte) -96, (byte) 82, (byte) 59, (byte) -42, (byte) -77, (byte) 41, (byte) -29, (byte) 47, (byte) -124}, {(byte) 83, (byte) -47, (byte) 0, (byte) -19, (byte) 32, (byte) -4, (byte) -79, (byte) 91, (byte) 106, (byte) -53, (byte) -66, (byte) 57, (byte) 74, (byte) 76, (byte) 88, (byte) -49}, {(byte) -48, (byte) -17, (byte) -86, (byte) -5, (byte) 67, (byte) 77, (byte) 51, (byte) -123, (byte) 69, (byte) -7, (byte) 2, (byte) 127, (byte) 80, (byte) 60, (byte) -97, (byte) -88}, {(byte) 81, (byte) -93, (byte) 64, (byte) -113, (byte) -110, (byte) -99, (byte) 56, (byte) -11, (byte) -68, (byte) -74, (byte) -38, (byte) 33, (byte) 16, (byte) -1, (byte) -13, (byte) -46}, {(byte) -51, (byte) 12, (byte) 19, (byte) -20, (byte) 95, (byte) -105, (byte) 68, (byte) 23, (byte) -60, (byte) -89, (byte) 126, (byte) 61, (byte) 100, (byte) 93, (byte) 25, (byte) 115}, {(byte) 96, (byte) -127, (byte) 79, (byte) -36, (byte) 34, (byte) 42, (byte) -112, (byte) -120, (byte) 70, (byte) -18, (byte) -72, (byte) 20, (byte) -34, (byte) 94, (byte) 11, (byte) -37}, {(byte) -32, (byte) 50, (byte) 58, (byte) 10, (byte) 73, (byte) 6, (byte) 36, (byte) 92, (byte) -62, (byte) -45, (byte) -84, (byte) 98, (byte) -111, (byte) -107, (byte) -28, (byte) 121}, {(byte) -25, (byte) -56, (byte) 55, (byte) 109, (byte) -115, (byte) -43, (byte) 78, (byte) -87, (byte) 108, (byte) 86, (byte) -12, (byte) -22, (byte) 101, (byte) 122, (byte) -82, (byte) 8}, {(byte) -70, (byte) 120, (byte) 37, (byte) 46, (byte) 28, (byte) -90, (byte) -76, (byte) -58, (byte) -24, (byte) -35, (byte) 116, (byte) 31, (byte) 75, (byte) -67, (byte) -117, (byte) -118}, {(byte) 112, (byte) 62, (byte) -75, (byte) 102, (byte) 72, (byte) 3, (byte) -10, (byte) 14, (byte) 97, (byte) 53, (byte) 87, (byte) -71, (byte) -122, (byte) -63, (byte) 29, (byte) -98}, {(byte) -31, (byte) -8, (byte) -104, (byte) 17, (byte) 105, (byte) -39, (byte) -114, (byte) -108, (byte) -101, (byte) 30, (byte) -121, (byte) -23, (byte) -50, (byte) 85, (byte) 40, (byte) -33}, {(byte) -116, (byte) -95, (byte) -119, (byte) 13, (byte) -65, (byte) -26, (byte) 66, (byte) 104, (byte) 65, (byte) -103, (byte) 45, (byte) 15, (byte) -80, (byte) 84, (byte) -69, (byte) 22}};
    }

    private void BuildInvSbox() {
        this.iSbox = new byte[][]{{(byte) 82, (byte) 9, (byte) 106, (byte) -43, (byte) 48, (byte) 54, (byte) -91, (byte) 56, (byte) -65, (byte) 64, (byte) -93, (byte) -98, (byte) -127, (byte) -13, (byte) -41, (byte) -5}, {(byte) 124, (byte) -29, (byte) 57, (byte) -126, (byte) -101, (byte) 47, (byte) -1, (byte) -121, (byte) 52, (byte) -114, (byte) 67, (byte) 68, (byte) -60, (byte) -34, (byte) -23, (byte) -53}, {(byte) 84, (byte) 123, (byte) -108, (byte) 50, (byte) -90, (byte) -62, (byte) 35, (byte) 61, (byte) -18, (byte) 76, (byte) -107, (byte) 11, (byte) 66, (byte) -6, (byte) -61, (byte) 78}, {(byte) 8, (byte) 46, (byte) -95, (byte) 102, (byte) 40, (byte) -39, (byte) 36, (byte) -78, (byte) 118, (byte) 91, (byte) -94, (byte) 73, (byte) 109, (byte) -117, (byte) -47, (byte) 37}, {(byte) 114, (byte) -8, (byte) -10, (byte) 100, (byte) -122, (byte) 104, (byte) -104, (byte) 22, (byte) -44, (byte) -92, (byte) 92, (byte) -52, (byte) 93, (byte) 101, (byte) -74, (byte) -110}, {(byte) 108, (byte) 112, (byte) 72, (byte) 80, (byte) -3, (byte) -19, (byte) -71, (byte) -38, (byte) 94, (byte) 21, (byte) 70, (byte) 87, (byte) -89, (byte) -115, (byte) -99, (byte) -124}, {(byte) -112, (byte) -40, (byte) -85, (byte) 0, (byte) -116, (byte) -68, (byte) -45, (byte) 10, (byte) -9, (byte) -28, (byte) 88, (byte) 5, (byte) -72, (byte) -77, (byte) 69, (byte) 6}, {(byte) -48, (byte) 44, (byte) 30, (byte) -113, (byte) -54, (byte) 63, (byte) 15, (byte) 2, (byte) -63, (byte) -81, (byte) -67, (byte) 3, (byte) 1, (byte) 19, (byte) -118, (byte) 107}, {(byte) 58, (byte) -111, (byte) 17, (byte) 65, (byte) 79, (byte) 103, (byte) -36, (byte) -22, (byte) -105, (byte) -14, (byte) -49, (byte) -50, (byte) -16, (byte) -76, (byte) -26, (byte) 115}, {(byte) -106, (byte) -84, (byte) 116, (byte) 34, (byte) -25, (byte) -83, (byte) 53, (byte) -123, (byte) -30, (byte) -7, (byte) 55, (byte) -24, (byte) 28, (byte) 117, (byte) -33, (byte) 110}, {(byte) 71, (byte) -15, (byte) 26, (byte) 113, (byte) 29, (byte) 41, (byte) -59, (byte) -119, (byte) 111, (byte) -73, (byte) 98, (byte) 14, (byte) -86, (byte) 24, (byte) -66, (byte) 27}, {(byte) -4, (byte) 86, (byte) 62, (byte) 75, (byte) -58, (byte) -46, (byte) 121, (byte) 32, (byte) -102, (byte) -37, (byte) -64, (byte) -2, (byte) 120, (byte) -51, (byte) 90, (byte) -12}, {(byte) 31, (byte) -35, (byte) -88, (byte) 51, (byte) -120, (byte) 7, (byte) -57, (byte) 49, (byte) -79, (byte) 18, (byte) 16, (byte) 89, (byte) 39, (byte) -128, (byte) -20, (byte) 95}, {(byte) 96, (byte) 81, (byte) 127, (byte) -87, (byte) 25, (byte) -75, (byte) 74, (byte) 13, (byte) 45, (byte) -27, (byte) 122, (byte) -97, (byte) -109, (byte) -55, (byte) -100, (byte) -17}, {(byte) -96, (byte) -32, (byte) 59, (byte) 77, (byte) -82, (byte) 42, (byte) -11, (byte) -80, (byte) -56, (byte) -21, (byte) -69, (byte) 60, (byte) -125, (byte) 83, (byte) -103, (byte) 97}, {(byte) 23, (byte) 43, (byte) 4, (byte) 126, (byte) -70, (byte) 119, (byte) -42, (byte) 38, (byte) -31, (byte) 105, (byte) 20, (byte) 99, (byte) 85, (byte) 33, (byte) 12, (byte) 125}};
    }

    private void BuildRcon() {
        this.Rcon = new byte[][]{{(byte) 0, (byte) 0, (byte) 0, (byte) 0}, {(byte) 1, (byte) 0, (byte) 0, (byte) 0}, {(byte) 2, (byte) 0, (byte) 0, (byte) 0}, {(byte) 4, (byte) 0, (byte) 0, (byte) 0}, {(byte) 8, (byte) 0, (byte) 0, (byte) 0}, {(byte) 16, (byte) 0, (byte) 0, (byte) 0}, {(byte) 32, (byte) 0, (byte) 0, (byte) 0}, {(byte) 64, (byte) 0, (byte) 0, (byte) 0}, {(byte) -128, (byte) 0, (byte) 0, (byte) 0}, {(byte) 27, (byte) 0, (byte) 0, (byte) 0}, {(byte) 54, (byte) 0, (byte) 0, (byte) 0}};
    }

    private void AddRoundKey(final int var1) {
        for (int var2 = 0; 4 > var2; ++var2) {
            for (int var3 = 0; 4 > var3; ++var3) {
                this.State[var2][var3] = (byte) ((int) this.State[var2][var3] & 255 ^ (int) this.w[(var1 << 2) + var3][var2] & 255);
            }
        }

    }

    private void SubBytes() {
        for (int var1 = 0; 4 > var1; ++var1) {
            for (int var2 = 0; 4 > var2; ++var2) {
                this.State[var1][var2] = this.Sbox[(int) (byte) ((int) this.State[var1][var2] >> 4 & 15)][(int) this.State[var1][var2] & 15];
            }
        }

    }

    private void InvSubBytes() {
        for (int var1 = 0; 4 > var1; ++var1) {
            for (int var2 = 0; 4 > var2; ++var2) {
                this.State[var1][var2] = this.iSbox[(int) (byte) ((int) this.State[var1][var2] >> 4 & 15)][(int) this.State[var1][var2] & 15];
            }
        }

    }

    private void ShiftRows() {
        final byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; 4 > var2; ++var2) {
            for (var3 = 0; 4 > var3; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 1; 4 > var3; ++var3) {
            for (int var4 = 0; 4 > var4; ++var4) {
                this.State[var3][var4] = var1[var3][(var4 + var3) % this.Nb];
            }
        }

    }

    private void InvShiftRows() {
        final byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; 4 > var2; ++var2) {
            for (var3 = 0; 4 > var3; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 1; 4 > var3; ++var3) {
            for (int var4 = 0; 4 > var4; ++var4) {
                this.State[var3][(var4 + var3) % this.Nb] = var1[var3][var4];
            }
        }

    }

    private void MixColumns() {
        final byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; 4 > var2; ++var2) {
            for (var3 = 0; 4 > var3; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 0; 4 > var3; ++var3) {
            this.State[0][var3] = (byte) ((int) Aes.gfmultby02(var1[0][var3]) & 255 ^ (int) Aes.gfmultby03(var1[1][var3]) & 255 ^ (int) Aes.gfmultby01(var1[2][var3]) & 255 ^ (int) Aes.gfmultby01(var1[3][var3]) & 255);
            this.State[1][var3] = (byte) ((int) Aes.gfmultby01(var1[0][var3]) & 255 ^ (int) Aes.gfmultby02(var1[1][var3]) & 255 ^ (int) Aes.gfmultby03(var1[2][var3]) & 255 ^ (int) Aes.gfmultby01(var1[3][var3]) & 255);
            this.State[2][var3] = (byte) ((int) Aes.gfmultby01(var1[0][var3]) & 255 ^ (int) Aes.gfmultby01(var1[1][var3]) & 255 ^ (int) Aes.gfmultby02(var1[2][var3]) & 255 ^ (int) Aes.gfmultby03(var1[3][var3]) & 255);
            this.State[3][var3] = (byte) ((int) Aes.gfmultby03(var1[0][var3]) & 255 ^ (int) Aes.gfmultby01(var1[1][var3]) & 255 ^ (int) Aes.gfmultby01(var1[2][var3]) & 255 ^ (int) Aes.gfmultby02(var1[3][var3]) & 255);
        }

    }

    private void InvMixColumns() {
        final byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; 4 > var2; ++var2) {
            for (var3 = 0; 4 > var3; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 0; 4 > var3; ++var3) {
            this.State[0][var3] = (byte) ((int) Aes.gfmultby0e(var1[0][var3]) & 255 ^ (int) Aes.gfmultby0b(var1[1][var3]) & 255 ^ (int) Aes.gfmultby0d(var1[2][var3]) & 255 ^ (int) Aes.gfmultby09(var1[3][var3]) & 255);
            this.State[1][var3] = (byte) ((int) Aes.gfmultby09(var1[0][var3]) & 255 ^ (int) Aes.gfmultby0e(var1[1][var3]) & 255 ^ (int) Aes.gfmultby0b(var1[2][var3]) & 255 ^ (int) Aes.gfmultby0d(var1[3][var3]) & 255);
            this.State[2][var3] = (byte) ((int) Aes.gfmultby0d(var1[0][var3]) & 255 ^ (int) Aes.gfmultby09(var1[1][var3]) & 255 ^ (int) Aes.gfmultby0e(var1[2][var3]) & 255 ^ (int) Aes.gfmultby0b(var1[3][var3]) & 255);
            this.State[3][var3] = (byte) ((int) Aes.gfmultby0b(var1[0][var3]) & 255 ^ (int) Aes.gfmultby0d(var1[1][var3]) & 255 ^ (int) Aes.gfmultby09(var1[2][var3]) & 255 ^ (int) Aes.gfmultby0e(var1[3][var3]) & 255);
        }

    }

    private void KeyExpansion() {
        this.w = new byte[this.Nb * (this.Nr + 1)][4];

        for (int var1 = 0; var1 < this.Nk; ++var1) {
            this.w[var1][0] = this.key[4 * var1];
            this.w[var1][1] = this.key[4 * var1 + 1];
            this.w[var1][2] = this.key[4 * var1 + 2];
            this.w[var1][3] = this.key[4 * var1 + 3];
        }

        byte[] var2 = new byte[4];

        for (int var3 = this.Nk; var3 < this.Nb * (this.Nr + 1); ++var3) {
            var2[0] = this.w[var3 - 1][0];
            var2[1] = this.w[var3 - 1][1];
            var2[2] = this.w[var3 - 1][2];
            var2[3] = this.w[var3 - 1][3];
            if (0 == var3 % this.Nk) {
                var2 = this.SubWord(Aes.RotWord(var2));
                var2[0] = (byte) ((int) var2[0] & 255 ^ (int) this.Rcon[var3 / this.Nk][0] & 255);
                var2[1] = (byte) ((int) var2[1] & 255 ^ (int) this.Rcon[var3 / this.Nk][1] & 255);
                var2[2] = (byte) ((int) var2[2] & 255 ^ (int) this.Rcon[var3 / this.Nk][2] & 255);
                var2[3] = (byte) ((int) var2[3] & 255 ^ (int) this.Rcon[var3 / this.Nk][3] & 255);
            } else if (6 < this.Nk && 4 == var3 % this.Nk) {
                var2 = this.SubWord(var2);
            }

            this.w[var3][0] = (byte) ((int) this.w[var3 - this.Nk][0] & 255 ^ (int) var2[0] & 255);
            this.w[var3][1] = (byte) ((int) this.w[var3 - this.Nk][1] & 255 ^ (int) var2[1] & 255);
            this.w[var3][2] = (byte) ((int) this.w[var3 - this.Nk][2] & 255 ^ (int) var2[2] & 255);
            this.w[var3][3] = (byte) ((int) this.w[var3 - this.Nk][3] & 255 ^ (int) var2[3] & 255);
        }

        this.InitOfbIv();
    }

    private void InitOfbIv() {
        this.ofb_num = 0;

        for (int var1 = 0; var1 < this.ofb_iv.length; ++var1) {
            this.ofb_iv[var1] = (byte) 0;
        }

    }

    private byte[] SubWord(final byte[] var1) {
        return new byte[]{this.Sbox[(int) (byte) ((int) var1[0] >> 4 & 15)][(int) var1[0] & 15], this.Sbox[(int) (byte) ((int) var1[1] >> 4 & 15)][(int) var1[1] & 15], this.Sbox[(int) (byte) ((int) var1[2] >> 4 & 15)][(int) var1[2] & 15], this.Sbox[(int) (byte) ((int) var1[3] >> 4 & 15)][(int) var1[3] & 15]};
    }

    private static byte[] RotWord(final byte[] var1) {
        return new byte[]{var1[1], var1[2], var1[3], var1[0]};
    }

    public void Dump() {
        System.out.println("Nb = " + this.Nb + " Nk = " + this.Nk + " Nr = " + this.Nr);
        System.out.println("\nThe key is \n" + this.DumpKey());
        System.out.println("\nThe Sbox is \n" + Aes.DumpTwoByTwo(this.Sbox));
        System.out.println("\nThe w array is \n" + Aes.DumpTwoByTwo(this.w));
        System.out.println("\nThe State array is \n" + Aes.DumpTwoByTwo(this.State));
    }

    public String DumpKey() {
        final StringBuilder var1 = new StringBuilder(this.key.length * 3);

        for (final byte b : this.key) {
            final String var2 = Integer.toHexString((int) b & 255);
            if (1 == var2.length()) {
                var1.append("0");
            }

            var1.append(var2).append(" ");
        }

        return var1.toString();
    }

    public static String DumpTwoByTwo(final byte[][] var1) {
        final StringBuilder var2 = new StringBuilder();
        final int var6 = var1.length;

        for (int var4 = 0; var4 < var6; ++var4) {
            var2.append("[").append(var4).append("]").append(" ");
            final byte[] var7 = var1[var4];

            for (final byte b : var7) {
                final String var3 = Integer.toHexString((int) b & 255);
                if (1 == var3.length()) {
                    var2.append("0");
                }

                var2.append(var3).append(" ");
            }

            var2.append("\n");
        }

        return var2.toString();
    }

    public byte randomValue() {
        if (0 == this.ofb_num) {
            this.Cipher(this.ofb_iv, this.ofb_iv);
        }

        final byte var1 = this.ofb_iv[this.ofb_num];
        this.ofb_num = this.ofb_num + 1 & 15;
        return var1;
    }
}
