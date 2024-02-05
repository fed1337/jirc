package org.virtdevs;


import java.io.*;

public class MediaAccess {
    public static final int Unknown = 0;
    public static final int F5_1Pt2_512 = 1;
    public static final int F3_1Pt44_512 = 2;
    public static final int F3_2Pt88_512 = 3;
    public static final int F3_20Pt88_512 = 4;
    public static final int F3_720_512 = 5;
    public static final int F5_360_512 = 6;
    public static final int F5_320_512 = 7;
    public static final int F5_320_1024 = 8;
    public static final int F5_180_512 = 9;
    public static final int F5_160_512 = 10;
    public static final int RemovableMedia = 11;
    public static final int FixedMedia = 12;
    public static final int F3_120M_512 = 13;
    public static final int ImageFile = 100;
    public static final int NoRootDir = 1;
    public static final int Removable = 2;
    public static final int Fixed = 3;
    public static final int Remote = 4;
    public static final int CDROM = 5;
    public static final int Ramdisk = 6;
    public static String dllext = "";
    static int dio_setup = -1;
    DirectIO dio;
    File file;
    RandomAccessFile raf;
    boolean dev = false;
    boolean readonly = false;
    int zero_offset = 0;

    public MediaAccess() {
    }

    public static void cleanup(virtdevs var0) {
        String var1 = System.getProperty("file.separator");
        String var2 = System.getProperty("java.io.tmpdir");
        String var3 = System.getProperty("os.name").toLowerCase();
        if (var2 == null) {
            var2 = var3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        File var4 = new File(var2);
        String[] var5 = var4.list();
        String var6 = "";
        if (!var2.endsWith(var1)) {
            var2 = var2 + var1;
        }

        for (int var7 = 0; var7 < var5.length; ++var7) {
            if (var5[var7].startsWith("cpqma-") && var5[var7].endsWith(dllext)) {
                File var8 = new File(var2 + var5[var7]);
                var8.delete();
            }
        }

        for (int var11 = 0; var11 < var5.length; ++var11) {
            if (var5[var11].startsWith("HpqKbHook-") && var5[var11].endsWith(dllext)) {
                File var9 = new File(var2 + var5[var11]);
                var9.delete();
            }
        }

        for (int var12 = 0; var12 < var5.length; ++var12) {
            if (var5[var12].startsWith("jirc_strings") && var5[var12].endsWith("xml")) {
                File var10 = new File(var2 + var5[var12]);
                var10.delete();
            }
        }

    }

    public int open(String var1, int var2) throws IOException {
        this.dev = (var2 & 1) == 1;
        boolean var3 = (var2 & 2) == 2;
        this.zero_offset = 0;
        if (this.dev) {
            if (dio_setup != 0) {
                throw new IOException("DirectIO not possible (" + dio_setup + ")");
            } else {
                if (this.dio == null) {
                    this.dio = new DirectIO();
                }

                return this.dio.open(var1);
            }
        } else {
            this.readonly = false;
            this.file = new File(var1);
            if (!this.file.exists() && !var3) {
                throw new IOException("File " + var1 + " does not exist");
            } else if (this.file.isDirectory()) {
                throw new IOException("File " + var1 + " is a directory");
            } else {
                try {
                    this.raf = new RandomAccessFile(var1, "rw");
                } catch (IOException var6) {
                    if (var3) {
                        throw var6;
                    }

                    this.raf = new RandomAccessFile(var1, "r");
                    this.readonly = true;
                }

                byte[] var4 = new byte[512];
                this.read(0L, 512, var4);
                if (var4[0] == 67 && var4[1] == 80 && var4[2] == 81 && var4[3] == 82 && var4[4] == 70 && var4[5] == 66 && var4[6] == 76 && var4[7] == 79) {
                    this.zero_offset = var4[14] | var4[15] << 8;
                }

                Object var7 = null;
                return 0;
            }
        }
    }

    public int close() throws IOException {
        if (this.dev) {
            return this.dio.close();
        } else {
            this.raf.close();
            return 0;
        }
    }

    public void read(long var1, int var3, byte[] var4) throws IOException {
        var1 += (long) this.zero_offset;
        if (this.dev) {
            int var5 = this.dio.read(var1, var3, var4);
            if (var5 != 0) {
                throw new IOException("DirectIO read error (" + this.dio.sysError(-var5) + ")");
            }
        } else {
            this.raf.seek(var1);
            this.raf.read(var4, 0, var3);
        }

    }

    public void write(long var1, int var3, byte[] var4) throws IOException {
        var1 += (long) this.zero_offset;
        if (this.dev) {
            int var5 = this.dio.write(var1, var3, var4);
            if (var5 != 0) {
                throw new IOException("DirectIO write error (" + this.dio.sysError(-var5) + ")");
            }
        } else {
            this.raf.seek(var1);
            this.raf.write(var4, 0, var3);
        }

    }

    public long size() throws IOException {
        long var1;
        if (this.dev) {
            var1 = this.dio.size();
        } else {
            var1 = this.raf.length() - (long) this.zero_offset;
        }

        return var1;
    }

    public int format(int var1, int var2, int var3, int var4, int var5) throws IOException {
        if (this.dev) {
            this.dio.media_type = var1;
            this.dio.StartCylinder = var2;
            this.dio.EndCylinder = var3;
            this.dio.StartHead = var4;
            this.dio.EndHead = var5;
            return this.dio.format();
        } else {
            return 0;
        }
    }

    public String[] devices() {
        if (dio_setup != 0) {
            return null;
        } else {
            if (this.dio == null) {
                this.dio = new DirectIO();
            }

            return this.dio.devices();
        }
    }

    public int devtype(String var1) {
        if (dio_setup != 0) {
            return 0;
        } else {
            if (this.dio == null) {
                this.dio = new DirectIO();
            }

            return this.dio.devtype(var1);
        }
    }

    public int scsi(byte[] var1, int var2, int var3, byte[] var4, byte[] var5) {
        return this.scsi(var1, var2, var3, var4, var5, 0);
    }

    public int scsi(byte[] var1, int var2, int var3, byte[] var4, byte[] var5, int var6) {
        int var7;
        if (this.dev) {
            var7 = this.dio.scsi(var1, var2, var3, var4, var5, var6);
        } else {
            var7 = -1;
        }

        return var7;
    }

    public boolean wp() {
        boolean var1;
        if (this.dev) {
            var1 = this.dio.wp == 1;
        } else {
            var1 = this.readonly;
        }

        return var1;
    }

    public int type() {
        if (this.dev && this.dio != null) {
            return this.dio.media_type;
        } else {
            return this.raf != null ? 100 : 0;
        }
    }

    public int dllExtract(String var1, String var2) {
        ClassLoader var3 = this.getClass().getClassLoader();
        byte[] var5 = new byte[4096];
        D.println(1, "dllExtract trying " + var1);
        if (var3.getResource(var1) == null) {
            return -1;
        } else {
            D.println(1, "Extracting " + var3.getResource(var1).toExternalForm() + " to " + var2);

            try {
                InputStream var6 = var3.getResourceAsStream(var1);
                FileOutputStream var7 = new FileOutputStream(var2);

                int var4;
                while ((var4 = var6.read(var5, 0, 4096)) != -1) {
                    var7.write(var5, 0, var4);
                }

                var6.close();
                var7.close();
                return 0;
            } catch (IOException var8) {
                D.println(0, "dllExtract: " + var8);
                return -2;
            }
        }
    }

    public int setup_DirectIO() {
        boolean var2 = false;
        String var3 = System.getProperty("file.separator");
        String var4 = System.getProperty("java.io.tmpdir");
        String var5 = System.getProperty("os.name").toLowerCase();
        String var6 = System.getProperty("java.vm.name");
        String var7 = "unknown";
        if (var4 == null) {
            var4 = var5.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        if (var5.startsWith("windows")) {
            if (var6.indexOf("64") != -1) {
                System.out.println("virt: Detected win 64bit jvm");
                var7 = "x86-win64";
            } else {
                System.out.println("virt: Detected win 32bit jvm");
                var7 = "x86-win32";
            }

            dllext = ".dll";
        } else if (var5.startsWith("linux")) {
            if (var6.indexOf("64") != -1) {
                System.out.println("virt: Detected 64bit linux jvm");
                var7 = "x86-linux-64";
            } else {
                System.out.println("virt: Detected 32bit linux jvm");
                var7 = "x86-linux-32";
            }
        }

        File var8 = new File(var4);
        if (!var8.exists()) {
            var8.mkdir();
        }

        if (!var4.endsWith(var3)) {
            var4 = var4 + var3;
        }

        var4 = var4 + "cpqma-" + Integer.toHexString(virtdevs.UID) + dllext;
        System.out.println("Checking for " + var4);
        File var9 = new File(var4);
        if (var9.exists()) {
            System.out.println("DLL present");
            dio_setup = 0;
            return 0;
        } else {
            System.out.println("DLL not present");
            int var10 = this.dllExtract("com/hp/ilo2/virtdevs/cpqma-" + var7, var4);
            dio_setup = var10;
            return var10;
        }
    }
}
