package com.hp.ilo2.virtdevs;


import java.io.FileOutputStream;
import java.io.PrintStream;

final class D {
    public static final int NONE = -1;
    public static final int FATAL = 0;
    public static final int INFORM = 1;
    public static final int WARNING = 2;
    public static final int VERBOSE = 3;
    public static int debug = 0;
    private static PrintStream out;

    static {
        String var0 = virtdevs.prop.getProperty("org.virtdevs.debugfile");

        try {
            if (null == var0) {
                D.out = System.out;
            } else {
                D.out = new PrintStream(new FileOutputStream(var0));
            }
        } catch (final Exception var2) {
            D.out = System.out;
            D.out.println("Exception trying to open debug trace\n" + var2);
        }

        var0 = virtdevs.prop.getProperty("org.virtdevs.debug");
        if (null != var0) {
            D.debug = Integer.parseInt(var0);
        }

    }

    public static void println(final int var0, final String var1) {
        if (D.debug >= var0) {
            D.out.println(var1);
        }

    }

    public static void print(final int var0, final String var1) {
        if (D.debug >= var0) {
            D.out.println(var1);
        }

    }

    public static String hex(final byte var0, final int var1) {
        return D.hex((int) var0 & 255, var1);
    }

    public static String hex(final short var0, final int var1) {
        return D.hex((int) var0 & (int) '\uffff', var1);
    }

    public static String hex(final int var0, final int var1) {
        String var2;
        for (var2 = Integer.toHexString(var0); var2.length() < var1; var2 = "0" + var2) {
        }

        return var2;
    }

    public static String hex(final long var0, final int var2) {
        String var3;
        for (var3 = Long.toHexString(var0); var3.length() < var2; var3 = "0" + var3) {
        }

        return var3;
    }

    public static void hexdump(final int var0, final byte[] var1, int var2) {
        if (D.debug >= var0) {
            if (0 == var2) {
                var2 = var1.length;
            }

            for (int var3 = 0; var3 < var2; ++var3) {
                if (0 == var3 % 16) {
                    D.out.print("\n");
                }

                D.out.print(D.hex(var1[var3], 2) + " ");
            }

            D.out.print("\n");
        }
    }
}
