package org.virtdevs;


public class DirectIO {
    public static int keydrive = 1;

    static {
        String var0 = "cpqma-" + Integer.toHexString(virtdevs.UID) + MediaAccess.dllext;
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
        var0 = virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.dll");
        String var4 = virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.keydrive", "true");
        keydrive = Boolean.valueOf(var4) ? 1 : 0;
        if (var0 != null) {
            var2 = var0;
        }

        System.out.println("Loading " + var2);
        System.load(var2);
    }

    public int media_type;
    public int StartCylinder;
    public int EndCylinder;
    public int StartHead;
    public int EndHead;
    public int Cylinders;
    public int TracksPerCyl;
    public int SecPerTrack;
    public int BytesPerSec;
    public int media_size;
    public int filehandle = -1;
    public int aux_handle = -1;
    public long bufferaddr;
    public int wp;
    public int misc0;
    public int PhysicalDevice;

    public DirectIO() {
    }

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

    protected void finalize() {
        if (this.filehandle != -1) {
            this.close();
        }

    }
}
