package com.hp.ilo2.virtdevs;


import java.io.*;
import java.nio.file.FileSystems;

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
    DirectIO dio = null;
    File file = null;
    RandomAccessFile raf = null;
    boolean dev = false;
    boolean readonly = false;
    int zero_offset = 0;

    public static void cleanup(final virtdevs var0) {
        final String var1 = FileSystems.getDefault().getSeparator();
        String var2 = System.getProperty("java.io.tmpdir");
        final String var3 = System.getProperty("os.name").toLowerCase();
        if (null == var2) {
            var2 = var3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        final File var4 = new File(var2);
        final String[] var5 = var4.list();
        if (!var2.endsWith(var1)) {
            var2 = var2 + var1;
        }

        for (final String value : var5) {
            if (value.startsWith("cpqma-") && value.endsWith(MediaAccess.dllext)) {
                final File var8 = new File(var2 + value);
                var8.delete();
            }
        }

        for (final String string : var5) {
            if (string.startsWith("HpqKbHook-") && string.endsWith(MediaAccess.dllext)) {
                final File var9 = new File(var2 + string);
                var9.delete();
            }
        }

        for (final String s : var5) {
            if (s.startsWith("jirc_strings") && s.endsWith("xml")) {
                final File var10 = new File(var2 + s);
                var10.delete();
            }
        }

    }

    public int open(final String var1, final int var2) throws IOException {
        this.dev = 1 == (var2 & 1);
        final boolean var3 = 2 == (var2 & 2);
        this.zero_offset = 0;
        if (this.dev) {
            if (0 == MediaAccess.dio_setup) {
                if (null == this.dio) {
                    this.dio = new DirectIO();
                }

                return this.dio.open(var1);
            } else {
                throw new IOException("DirectIO not possible (" + MediaAccess.dio_setup + ")");
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
                } catch (final IOException var6) {
                    if (var3) {
                        throw var6;
                    }

                    this.raf = new RandomAccessFile(var1, "r");
                    this.readonly = true;
                }

                final byte[] var4 = new byte[512];
                this.read(0L, 512, var4);
                if (67 == (int) var4[0] && 80 == (int) var4[1] && 81 == (int) var4[2] && 82 == (int) var4[3] && 70 == (int) var4[4] && 66 == (int) var4[5] && 76 == (int) var4[6] && 79 == (int) var4[7]) {
                    this.zero_offset = (int) var4[14] | (int) var4[15] << 8;
                }

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

    public void read(long var1, final int var3, final byte[] var4) throws IOException {
        var1 = var1 + (long) this.zero_offset;
        if (this.dev) {
            final int var5 = this.dio.read(var1, var3, var4);
            if (0 != var5) {
                throw new IOException("DirectIO read error (" + this.dio.sysError(-var5) + ")");
            }
        } else {
            this.raf.seek(var1);
            this.raf.read(var4, 0, var3);
        }

    }

    public void write(long var1, final int var3, final byte[] var4) throws IOException {
        var1 = var1 + (long) this.zero_offset;
        if (this.dev) {
            final int var5 = this.dio.write(var1, var3, var4);
            if (0 != var5) {
                throw new IOException("DirectIO write error (" + this.dio.sysError(-var5) + ")");
            }
        } else {
            this.raf.seek(var1);
            this.raf.write(var4, 0, var3);
        }

    }

    public long size() throws IOException {
        final long var1;
        if (this.dev) {
            var1 = this.dio.size();
        } else {
            var1 = this.raf.length() - (long) this.zero_offset;
        }

        return var1;
    }

    public int format(final int var1, final int var2, final int var3, final int var4, final int var5) {
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
        if (0 == MediaAccess.dio_setup) {
            if (null == this.dio) {
                this.dio = new DirectIO();
            }

            return this.dio.devices();
        } else {
            return null;
        }
    }

    public int devtype(final String var1) {
        if (0 == MediaAccess.dio_setup) {
            if (null == this.dio) {
                this.dio = new DirectIO();
            }

            return this.dio.devtype(var1);
        } else {
            return 0;
        }
    }

    public int scsi(final byte[] var1, final int var2, final int var3, final byte[] var4, final byte[] var5) {
        return this.scsi(var1, var2, var3, var4, var5, 0);
    }

    public int scsi(final byte[] var1, final int var2, final int var3, final byte[] var4, final byte[] var5, final int var6) {
        final int var7;
        if (this.dev) {
            var7 = this.dio.scsi(var1, var2, var3, var4, var5, var6);
        } else {
            var7 = -1;
        }

        return var7;
    }

    public boolean wp() {
        final boolean var1;
        if (this.dev) {
            var1 = 1 == this.dio.wp;
        } else {
            var1 = this.readonly;
        }

        return var1;
    }

    public int type() {
        if (this.dev && null != this.dio) {
            return this.dio.media_type;
        } else {
            return null != this.raf ? 100 : 0;
        }
    }

    public int dllExtract(final String var1, final String var2) {
        final ClassLoader var3 = this.getClass().getClassLoader();
        final byte[] var5 = new byte[4096];
        D.println(1, "dllExtract trying " + var1);
        if (null == var3.getResource(var1)) {
            return -1;
        } else {
            D.println(1, "Extracting " + var3.getResource(var1).toExternalForm() + " to " + var2);

            try {
                final InputStream var6 = var3.getResourceAsStream(var1);
                final FileOutputStream var7 = new FileOutputStream(var2);

                int var4;
                while (-1 != (var4 = var6.read(var5, 0, 4096))) {
                    var7.write(var5, 0, var4);
                }

                var6.close();
                var7.close();
                return 0;
            } catch (final IOException var8) {
                D.println(0, "dllExtract: " + var8);
                return -2;
            }
        }
    }

    public int setup_DirectIO() {
        final String var3 = FileSystems.getDefault().getSeparator();
        String var4 = System.getProperty("java.io.tmpdir");
        final String var5 = System.getProperty("os.name").toLowerCase();
        final String var6 = System.getProperty("java.vm.name");
        String var7 = "unknown";
        if (null == var4) {
            var4 = var5.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        if (var5.startsWith("windows")) {
            if (var6.contains("64")) {
                System.out.println("virt: Detected win 64bit jvm");
                var7 = "x86-win64";
            } else {
                System.out.println("virt: Detected win 32bit jvm");
                var7 = "x86-win32";
            }

            MediaAccess.dllext = ".dll";
        } else if (var5.startsWith("linux")) {
            if (var6.contains("64")) {
                System.out.println("virt: Detected 64bit linux jvm");
                var7 = "x86-linux-64";
            } else {
                System.out.println("virt: Detected 32bit linux jvm");
                var7 = "x86-linux-32";
            }
        }

        final File var8 = new File(var4);
        if (!var8.exists()) {
            var8.mkdir();
        }

        if (!var4.endsWith(var3)) {
            var4 = var4 + var3;
        }

        var4 = var4 + "cpqma-" + Integer.toHexString(virtdevs.UID) + MediaAccess.dllext;
        System.out.println("Checking for " + var4);
        final File var9 = new File(var4);
        if (var9.exists()) {
            System.out.println("DLL present");
            MediaAccess.dio_setup = 0;
            return 0;
        } else {
            System.out.println("DLL not present");
            final int var10 = this.dllExtract("org/virtdevs/cpqma-" + var7, var4);
            MediaAccess.dio_setup = var10;
            return var10;
        }
    }
}
