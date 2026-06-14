package com.hp.ilo2.virtdevs;


import java.nio.file.FileSystems;

public class DirectIO {
    public static int keydrive = 1;

    static {
        String var0 = "cpqma-" + Integer.toHexString(virtdevs.UID) + MediaAccess.dllext;
        final String var1 = FileSystems.getDefault().getSeparator();
        String var2 = System.getProperty("java.io.tmpdir");
        final String var3 = System.getProperty("os.name").toLowerCase();
        if (null == var2) {
            var2 = var3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        if (!var2.endsWith(var1)) {
            var2 = var2 + var1;
        }

        var2 = var2 + var0;
        var0 = virtdevs.prop.getProperty("org.virtdevs.dll");
        final String var4 = virtdevs.prop.getProperty("org.virtdevs.keydrive", "true");
        DirectIO.keydrive = Boolean.parseBoolean(var4) ? 1 : 0;
        if (null != var0) {
            var2 = var0;
        }

        System.out.println("Loading " + var2);
        System.load(var2);
    }

    public int media_type = 0;
    public int StartCylinder = 0;
    public int EndCylinder = 0;
    public int StartHead = 0;
    public int EndHead = 0;
    public int Cylinders = 0;
    public int TracksPerCyl = 0;
    public int SecPerTrack = 0;
    public int BytesPerSec = 0;
    public int media_size = 0;
    public static final int filehandle = -1;
    public int aux_handle = -1;
    public long bufferaddr = 0L;
    public int wp = 0;
    public int misc0 = 0;
    public int PhysicalDevice = 0;

    public native int open(String var1);

    public native int close();

    public native int read(long var1, int var3, byte[] var4);

    public native int write(long var1, int var3, byte[] var4);

    public native long size();

    public native int format();

    public native String[] devices();

    public native int devtype(String var1);

    public native int scsi(byte[] var1, int var2, int var3, byte[] var4, byte[] var5, int var6);

    public native String sysError(int var1);
}
