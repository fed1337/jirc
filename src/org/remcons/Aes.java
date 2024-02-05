package org.remcons;


public class Aes {
    public static final int Bits128 = 0;
    public static final int Bits192 = 1;
    public static final int Bits256 = 2;
    private final byte[] key;
    byte[] ofb_iv = new byte[16];
    int ofb_num;
    private int Nb;
    private int Nk;
    private int Nr;
    private byte[][] Sbox;
    private byte[][] iSbox;
    private byte[][] w;
    private byte[][] Rcon;
    private byte[][] State;

    public Aes(int var1, byte[] var2) {
        this.SetNbNkNr(var1);
        this.key = new byte[this.Nk * 4];
        if (var2.length < this.key.length) {
            System.out.println("Alert: KeyBytes size is less than specified KeySize");
        }

        System.arraycopy(var2, 0, this.key, 0, this.key.length);
        this.BuildSbox();
        this.BuildInvSbox();
        this.BuildRcon();
        this.KeyExpansion();
    }

    private static byte gfmultby01(byte var0) {
        return var0;
    }

    private static byte gfmultby02(byte var0) {
        return (var0 & 255) < 128 ? (byte) (var0 << 1 & 255) : (byte) (var0 << 1 & 255 ^ 27);
    }

    private static byte gfmultby03(byte var0) {
        return (byte) (gfmultby02(var0) & 255 ^ var0 & 255);
    }

    private static byte gfmultby09(byte var0) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(var0))) & 255 ^ var0 & 255);
    }

    private static byte gfmultby0b(byte var0) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(var0))) & 255 ^ gfmultby02(var0) & 255 ^ var0 & 255);
    }

    private static byte gfmultby0d(byte var0) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(var0))) & 255 ^ gfmultby02(gfmultby02(var0)) & 255 ^ var0 & 255);
    }

    private static byte gfmultby0e(byte var0) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(var0))) & 255 ^ gfmultby02(gfmultby02(var0)) & 255 ^ gfmultby02(var0) & 255);
    }

    public void Cipher(byte[] var1, byte[] var2) {
        if (var1.length < 16) {
            System.out.println("Alert- InputSize:" + var1.length + " is less than standard size:16");
        }

        if (var2.length < 16) {
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

    public void InvCipher(byte[] var1, byte[] var2) {
        this.State = new byte[4][this.Nb];

        for (int var3 = 0; var3 < 4 * this.Nb; ++var3) {
            this.State[var3 % 4][var3 / 4] = var1[var3];
        }

        this.AddRoundKey(this.Nr);

        for (int var4 = this.Nr - 1; var4 >= 1; --var4) {
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

    private void SetNbNkNr(int var1) {
        this.Nb = 4;
        if (var1 == 0) {
            this.Nk = 4;
            this.Nr = 10;
        } else if (var1 == 1) {
            this.Nk = 6;
            this.Nr = 12;
        } else if (var1 == 2) {
            this.Nk = 8;
            this.Nr = 14;
        } else {
            System.out.println("Alert: Invalid keysize Specified for SetNbNkNr");
            System.out.println("Pls use constants from Aes.KeySize");
        }

    }

    private void BuildSbox() {
        byte[][] var1 = new byte[][]{{99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118}, {-54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64}, {-73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21}, {4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117}, {9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124}, {83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49}, {-48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88}, {81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46}, {-51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115}, {96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37}, {-32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121}, {-25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8}, {-70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118}, {112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98}, {-31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33}, {-116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22}};
        this.Sbox = var1;
    }

    private void BuildInvSbox() {
        byte[][] var1 = new byte[][]{{82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5}, {124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53}, {84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78}, {8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37}, {114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110}, {108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124}, {-112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6}, {-48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107}, {58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115}, {-106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110}, {71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27}, {-4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12}, {31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95}, {96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17}, {-96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97}, {23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125}};
        this.iSbox = var1;
    }

    private void BuildRcon() {
        byte[][] var1 = new byte[][]{{0, 0, 0, 0}, {1, 0, 0, 0}, {2, 0, 0, 0}, {4, 0, 0, 0}, {8, 0, 0, 0}, {16, 0, 0, 0}, {32, 0, 0, 0}, {64, 0, 0, 0}, {-128, 0, 0, 0}, {27, 0, 0, 0}, {54, 0, 0, 0}};
        this.Rcon = var1;
    }

    private void AddRoundKey(int var1) {
        for (int var2 = 0; var2 < 4; ++var2) {
            for (int var3 = 0; var3 < 4; ++var3) {
                this.State[var2][var3] = (byte) (this.State[var2][var3] & 255 ^ this.w[var1 * 4 + var3][var2] & 255);
            }
        }

    }

    private void SubBytes() {
        for (int var1 = 0; var1 < 4; ++var1) {
            for (int var2 = 0; var2 < 4; ++var2) {
                this.State[var1][var2] = this.Sbox[(byte) (this.State[var1][var2] >> 4 & 15)][this.State[var1][var2] & 15];
            }
        }

    }

    private void InvSubBytes() {
        for (int var1 = 0; var1 < 4; ++var1) {
            for (int var2 = 0; var2 < 4; ++var2) {
                this.State[var1][var2] = this.iSbox[(byte) (this.State[var1][var2] >> 4 & 15)][this.State[var1][var2] & 15];
            }
        }

    }

    private void ShiftRows() {
        byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; var2 < 4; ++var2) {
            for (var3 = 0; var3 < 4; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 1; var3 < 4; ++var3) {
            for (int var4 = 0; var4 < 4; ++var4) {
                this.State[var3][var4] = var1[var3][(var4 + var3) % this.Nb];
            }
        }

    }

    private void InvShiftRows() {
        byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; var2 < 4; ++var2) {
            for (var3 = 0; var3 < 4; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 1; var3 < 4; ++var3) {
            for (int var4 = 0; var4 < 4; ++var4) {
                this.State[var3][(var4 + var3) % this.Nb] = var1[var3][var4];
            }
        }

    }

    private void MixColumns() {
        byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; var2 < 4; ++var2) {
            for (var3 = 0; var3 < 4; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 0; var3 < 4; ++var3) {
            this.State[0][var3] = (byte) (gfmultby02(var1[0][var3]) & 255 ^ gfmultby03(var1[1][var3]) & 255 ^ gfmultby01(var1[2][var3]) & 255 ^ gfmultby01(var1[3][var3]) & 255);
            this.State[1][var3] = (byte) (gfmultby01(var1[0][var3]) & 255 ^ gfmultby02(var1[1][var3]) & 255 ^ gfmultby03(var1[2][var3]) & 255 ^ gfmultby01(var1[3][var3]) & 255);
            this.State[2][var3] = (byte) (gfmultby01(var1[0][var3]) & 255 ^ gfmultby01(var1[1][var3]) & 255 ^ gfmultby02(var1[2][var3]) & 255 ^ gfmultby03(var1[3][var3]) & 255);
            this.State[3][var3] = (byte) (gfmultby03(var1[0][var3]) & 255 ^ gfmultby01(var1[1][var3]) & 255 ^ gfmultby01(var1[2][var3]) & 255 ^ gfmultby02(var1[3][var3]) & 255);
        }

    }

    private void InvMixColumns() {
        byte[][] var1 = new byte[4][4];

        int var3;
        for (int var2 = 0; var2 < 4; ++var2) {
            for (var3 = 0; var3 < 4; ++var3) {
                var1[var2][var3] = this.State[var2][var3];
            }
        }

        for (var3 = 0; var3 < 4; ++var3) {
            this.State[0][var3] = (byte) (gfmultby0e(var1[0][var3]) & 255 ^ gfmultby0b(var1[1][var3]) & 255 ^ gfmultby0d(var1[2][var3]) & 255 ^ gfmultby09(var1[3][var3]) & 255);
            this.State[1][var3] = (byte) (gfmultby09(var1[0][var3]) & 255 ^ gfmultby0e(var1[1][var3]) & 255 ^ gfmultby0b(var1[2][var3]) & 255 ^ gfmultby0d(var1[3][var3]) & 255);
            this.State[2][var3] = (byte) (gfmultby0d(var1[0][var3]) & 255 ^ gfmultby09(var1[1][var3]) & 255 ^ gfmultby0e(var1[2][var3]) & 255 ^ gfmultby0b(var1[3][var3]) & 255);
            this.State[3][var3] = (byte) (gfmultby0b(var1[0][var3]) & 255 ^ gfmultby0d(var1[1][var3]) & 255 ^ gfmultby09(var1[2][var3]) & 255 ^ gfmultby0e(var1[3][var3]) & 255);
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
            if (var3 % this.Nk == 0) {
                var2 = this.SubWord(this.RotWord(var2));
                var2[0] = (byte) (var2[0] & 255 ^ this.Rcon[var3 / this.Nk][0] & 255);
                var2[1] = (byte) (var2[1] & 255 ^ this.Rcon[var3 / this.Nk][1] & 255);
                var2[2] = (byte) (var2[2] & 255 ^ this.Rcon[var3 / this.Nk][2] & 255);
                var2[3] = (byte) (var2[3] & 255 ^ this.Rcon[var3 / this.Nk][3] & 255);
            } else if (this.Nk > 6 && var3 % this.Nk == 4) {
                var2 = this.SubWord(var2);
            }

            this.w[var3][0] = (byte) (this.w[var3 - this.Nk][0] & 255 ^ var2[0] & 255);
            this.w[var3][1] = (byte) (this.w[var3 - this.Nk][1] & 255 ^ var2[1] & 255);
            this.w[var3][2] = (byte) (this.w[var3 - this.Nk][2] & 255 ^ var2[2] & 255);
            this.w[var3][3] = (byte) (this.w[var3 - this.Nk][3] & 255 ^ var2[3] & 255);
        }

        this.InitOfbIv();
    }

    private void InitOfbIv() {
        this.ofb_num = 0;

        for (int var1 = 0; var1 < this.ofb_iv.length; ++var1) {
            this.ofb_iv[var1] = 0;
        }

    }

    private byte[] SubWord(byte[] var1) {
        byte[] var2 = new byte[]{this.Sbox[(byte) (var1[0] >> 4 & 15)][var1[0] & 15], this.Sbox[(byte) (var1[1] >> 4 & 15)][var1[1] & 15], this.Sbox[(byte) (var1[2] >> 4 & 15)][var1[2] & 15], this.Sbox[(byte) (var1[3] >> 4 & 15)][var1[3] & 15]};
        return var2;
    }

    private byte[] RotWord(byte[] var1) {
        byte[] var2 = new byte[]{var1[1], var1[2], var1[3], var1[0]};
        return var2;
    }

    public void Dump() {
        System.out.println("Nb = " + this.Nb + " Nk = " + this.Nk + " Nr = " + this.Nr);
        System.out.println("\nThe key is \n" + this.DumpKey());
        System.out.println("\nThe Sbox is \n" + this.DumpTwoByTwo(this.Sbox));
        System.out.println("\nThe w array is \n" + this.DumpTwoByTwo(this.w));
        System.out.println("\nThe State array is \n" + this.DumpTwoByTwo(this.State));
    }

    public String DumpKey() {
        String var1 = "";
        String var2 = "";

        for (int var3 = 0; var3 < this.key.length; ++var3) {
            var2 = Integer.toHexString(this.key[var3] & 255);
            if (var2.length() == 1) {
                var1 = var1 + "0";
            }

            var1 = var1 + var2 + " ";
        }

        return var1;
    }

    public String DumpTwoByTwo(byte[][] var1) {
        String var2 = "";
        String var3 = "";

        for (int var4 = 0; var4 < var1.length; ++var4) {
            var2 = var2 + "[" + var4 + "]" + " ";

            for (int var5 = 0; var5 < var1[var4].length; ++var5) {
                var3 = Integer.toHexString(var1[var4][var5] & 255);
                if (var3.length() == 1) {
                    var2 = var2 + "0";
                }

                var2 = var2 + var3 + " ";
            }

            var2 = var2 + "\n";
        }

        return var2;
    }

    public byte randomValue() {
        if (this.ofb_num == 0) {
            this.Cipher(this.ofb_iv, this.ofb_iv);
        }

        byte var1 = this.ofb_iv[this.ofb_num];
        this.ofb_num = this.ofb_num + 1 & 15;
        return var1;
    }
}
