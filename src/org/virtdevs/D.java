package org.virtdevs;


import java.io.FileOutputStream;
import java.io.PrintStream;

public class D {
    public static final int NONE = -1;
    public static final int FATAL = 0;
    public static final int INFORM = 1;
    public static final int WARNING = 2;
    public static final int VERBOSE = 3;
    public static int debug = 0;
    public static PrintStream out;

    static {
        String var0 = virtdevs.prop.getProperty("org.virtdevs.debugfile");

        try {
            if (var0 == null) {
                out = System.out;
            } else {
                out = new PrintStream(new FileOutputStream(var0));
            }
        } catch (Exception var2) {
            out = System.out;
            out.println("Exception trying to open debug trace\n" + var2);
        }

        var0 = virtdevs.prop.getProperty("org.virtdevs.debug");
        if (var0 != null) {
            debug = Integer.valueOf(var0);
        }

    }

    public D() {
    }

    public static void println(int var0, String var1) {
        if (debug >= var0) {
            out.println(var1);
        }

    }

    public static void print(int var0, String var1) {
        if (debug >= var0) {
            out.println(var1);
        }

    }

    public static String hex(byte var0, int var1) {
        return hex(var0 & 255, var1);
    }

    public static String hex(short var0, int var1) {
        return hex(var0 & '\uffff', var1);
    }

    public static String hex(int var0, int var1) {
        String var2;
        for (var2 = Integer.toHexString(var0); var2.length() < var1; var2 = "0" + var2) {
        }

        return var2;
    }

    public static String hex(long var0, int var2) {
        String var3;
        for (var3 = Long.toHexString(var0); var3.length() < var2; var3 = "0" + var3) {
        }

        return var3;
    }

    public static void hexdump(int var0, byte[] var1, int var2) {
        if (debug >= var0) {
            if (var2 == 0) {
                var2 = var1.length;
            }

            for (int var3 = 0; var3 < var2; ++var3) {
                if (var3 % 16 == 0) {
                    out.print("\n");
                }

                out.print(hex((byte) var1[var3], 2) + " ");
            }

            out.print("\n");
        }
    }
}
