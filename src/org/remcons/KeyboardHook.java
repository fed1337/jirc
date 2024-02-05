package org.remcons;


import org.virtdevs.virtdevs;

import java.util.Arrays;

public class KeyboardHook {
    static {
        String var0 = "HpqKbHook-" + Integer.toHexString(virtdevs.UID) + ".dll";
        String var1 = System.getProperty("file.separator");
        String var2 = System.getProperty("java.io.tmpdir");
        String var3 = System.getProperty("os.name").toLowerCase();
        if (var2 == null) {
            var2 = var3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        if (!var2.endsWith(var1)) {
            var2 = var2 + var1;
        }

        var2 = var2 + var0;

        try {
            System.out.println(" Loading " + var2 + "...");
            System.load(var2);
            System.out.println(" Loaded..!");
        } catch (Exception var5) {
            System.out.println("Error loading library HpqKbHook.dll - " + var5);
        }

    }

    private final int[] winkey_to_hid_dll_en_US = new int[]{0, 0, 0, 72, 0, 0, 0, 0, 42, 43, 0, 0, 0, 40, 0, 0, 0, 0, 0, 72, 57, 0, 0, 0, 0, 0, 0, 41, 0, 0, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 70, 73, 76, 0, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 0, 0, 0, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 227, 231, 101, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 0, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 229, 224, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, 46, 54, 45, 55, 56, 53, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 49, 48, 52, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255};
    private final int[] winkey_to_hid_dll_ja_JP = new int[]{0, 0, 0, 72, 0, 0, 0, 0, 42, 43, 0, 0, 0, 40, 0, 0, 0, 0, 0, 72, 57, 0, 0, 0, 0, 53, 0, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 70, 73, 76, 0, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 0, 0, 0, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 227, 231, 101, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 0, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 229, 224, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 52, 51, 54, 45, 55, 56, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 137, 50, 46, 0, 0, 0, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 57, 0, 136, 53, 53, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255};
    private final int[] linkey_to_hid_dll_en_US = new int[]{0, 41, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 45, 46, 42, 43, 20, 26, 8, 21, 23, 28, 24, 12, 18, 19, 47, 48, 40, 224, 4, 22, 7, 9, 10, 11, 13, 14, 15, 51, 52, 53, 225, 49, 29, 27, 6, 25, 5, 17, 16, 54, 55, 56, 229, 85, 226, 44, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 83, 71, 95, 96, 97, 86, 92, 93, 94, 87, 89, 90, 91, 98, 99, 0, 0, 100, 68, 69, 135, 0, 0, 0, 0, 0, 0, 88, 228, 84, 70, 230, 100, 74, 82, 75, 80, 79, 77, 81, 78, 73, 76, 0, 104, 105, 0, 0, 0, 0, 0, 0, 0, 0, 0, 137, 227, 231, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] keyMap = new int[256];
    public byte[] kcmd = new byte[10];
    public boolean kcmdValid = false;
    private int keyboardLayoutId = 0;

    public KeyboardHook() {
    }

    public native int InstallKeyboardHook();

    public native int UnInstallKeyboardHook();

    public native int GetKeyData();

    public native int setLocalKbdLayout(int var1);

    public void clearKeymap() {
        for (int var1 = 0; var1 < 256; ++var1) {
            this.keyMap[var1] = 0;
        }

    }

    public void setKeyboardLayoutId(int var1) {
        this.keyboardLayoutId = var1;
    }

    public void HandleSpecialKey(int var1, int var2) {
        if (1041 == this.keyboardLayoutId && 1 == this.keyMap[25] && 0 == this.keyMap[164]) {
            this.keyMap[25] = 0;
        }

    }

    public byte[] HandleHookKey(int var1, int var2, boolean var3, boolean var4) {
        int var5 = this.keyMap[var1];
        Arrays.fill(this.kcmd, (byte) 0);
        this.kcmd[0] = 1;
        this.kcmdValid = false;
        if (var4) {
            System.out.println("HandleHookKey ctl-Alt-Del clearkeymap");
            this.clearKeymap();
            this.kcmdValid = true;
        } else {
            if (1041 == this.keyboardLayoutId && (243 == var1 || 244 == var1)) {
                var1 = 243;
                var5 = this.keyMap[var1];
                if (var3) {
                    this.keyMap[var1] = 0;
                } else {
                    this.keyMap[var1] = 1;
                }
            } else if (var3) {
                this.keyMap[var1] = 1;
            } else {
                this.keyMap[var1] = 0;
            }

            if (var5 != this.keyMap[var1]) {
                this.kcmdValid = true;
                this.HandleSpecialKey(var1, var3 ? 1 : 0);
                int var6 = 0;

                for (int var7 = 0; var6 < 256; ++var6) {
                    if (this.keyMap[var6] != 0) {
                        int var8;
                        if (-16711935 == this.keyboardLayoutId) {
                            var8 = this.linkey_to_hid_dll_en_US[var6];
                        } else if (1041 == this.keyboardLayoutId) {
                            var8 = this.winkey_to_hid_dll_ja_JP[var6];
                        } else {
                            var8 = this.winkey_to_hid_dll_en_US[var6];
                        }

                        if (var8 != 0 && var8 != 255) {
                            if ((var8 & 224) == 224) {
                                var8 ^= 224;
                                byte[] var10000 = this.kcmd;
                                var10000[2] |= (byte) (1 << var8);
                            } else {
                                this.kcmd[4 + var7] = (byte) var8;
                                ++var7;
                                if (var7 == 6) {
                                    var7 = 5;
                                }
                            }
                        }
                    }
                }
            }
        }

        this.kcmd[0] = 1;
        return this.kcmd;
    }
}
