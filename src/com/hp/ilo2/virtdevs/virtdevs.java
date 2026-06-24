package com.hp.ilo2.virtdevs;


import org.jirc.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class virtdevs extends JPanel implements Runnable {
    public static final int UNQF_HIDEFLP = 1;
    static final int ImageDone = 39;
    public static int UID = 0;
    private static boolean cd_support = true;
    public static Properties prop = null;
    public int dev_cd_device = 0;
    public int dev_fd_device = 0;
    public final App ParentApp;
    public boolean cdConnected = false;
    public boolean fdConnected = false;
    public Connection fdConnection = null;
    public Connection cdConnection = null;
    public Thread fdThread = null;
    public Thread cdThread = null;
    protected boolean stopFlag = false;
    protected boolean running = false;
    private String host = null;
    private String base = null;
    private String configuration = null;
    private String dev_floppy = null;
    private String dev_cdrom = null;
    private boolean force_config = false;
    private boolean thread_init = false;
    private final byte[] pre = new byte[16];
    private byte[] key = new byte[32];
    private int fdport = 17988;
    JFrame parent = null;
    private String hostAddress = null;

    public virtdevs(final App var1) {
        super();
        this.ParentApp = var1;
    }

    public static int getSockFd(final Socket var0) {
        int var2 = -1;
        Field var4 = null;
        Field var5 = null;

        try {
            Field[] var6 = Socket.class.getDeclaredFields();

            int var1;
            for (var1 = 0; var1 < var6.length; ++var1) {
                if ("impl".equals(var6[var1].getName())) {
                    var4 = var6[var1];
                    var4.setAccessible(true);
                    break;
                }
            }

            final SocketImpl var7 = (SocketImpl) Objects.requireNonNull(var4).get(var0);
            var6 = SocketImpl.class.getDeclaredFields();

            for (var1 = 0; var1 < var6.length; ++var1) {
                if ("fd".equals(var6[var1].getName())) {
                    var5 = var6[var1];
                    var5.setAccessible(true);
                    break;
                }
            }

            final FileDescriptor var8 = (FileDescriptor) Objects.requireNonNull(var5).get(var7);
            var6 = FileDescriptor.class.getDeclaredFields();

            for (var1 = 0; var1 < var6.length; ++var1) {
                if ("fd".equals(var6[var1].getName())) {
                    var5 = var6[var1];
                    var5.setAccessible(true);
                    break;
                }
            }

            var2 = var5.getInt(var8);
        } catch (final Exception var9) {
            System.out.println("Ex: " + var9);
        }

        return var2;
    }

    public String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("virdevs:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public Image get(final String var1) {
        final ClassLoader var2 = this.getClass().getClassLoader();
        return App.getImage(var2.getResource("org/virtdevs/" + var1));
    }

    public void init() {
        if (0 == virtdevs.UID) {
            virtdevs.UID = this.hashCode();
        }

        final URL var2 = this.ParentApp.getDocumentBase();
        this.host = this.ParentApp.getParameter("hostAddress");
        if (null == this.host) {
            this.host = var2.getHost();
        }

        this.base = var2.getProtocol() + "://" + var2.getHost();
        if (-1 != var2.getPort()) {
            this.base = this.base + ":" + var2.getPort();
        }

        this.base = this.base + "/";
        final String var3 = this.ParentApp.getParameter("INFO0");
        int var1;
        if (null != var3) {
            try {
                for (var1 = 0; 16 > var1; ++var1) {
                    this.pre[var1] = (byte) Integer.parseInt(var3.substring(2 * var1, 2 * var1 + 2), 16);
                }
            } catch (final NumberFormatException var9) {
                D.println(0, "Couldn't parse INFO0: " + var9);
            }
        }

        try {
            if (null != this.ParentApp.vm_port) {
                this.fdport = Integer.parseInt(this.ParentApp.vm_port);
            }
        } catch (final NumberFormatException var8) {
            D.println(0, "Couldn't parse INFO1: " + var8);
        }

        this.configuration = this.ParentApp.getParameter("INFO2");
        if (null == this.configuration) {
            this.configuration = "auto";
        }

        this.dev_floppy = this.ParentApp.getParameter("floppy");
        this.dev_cdrom = this.ParentApp.getParameter("cdrom");
        final String dev_auto = this.ParentApp.getParameter("device");
        final String var4 = this.ParentApp.getParameter("config");
        if (null != var4) {
            this.configuration = var4;
            this.force_config = true;
        }

        final String var5 = this.ParentApp.getParameter("UNIQUE_FEATURES");

        try {
            if (null != var5) {
                final int unq_feature = Integer.parseInt(var5);
            }
        } catch (final NumberFormatException var7) {
            D.println(0, "Couldn't parse UNIQUE_FEATURES: " + var7);
        }

        this.key = this.ParentApp.getParameter("RCINFO1").getBytes();
        if (null != this.ParentApp.optional_features && this.ParentApp.optional_features.contains("ENCRYPT_VMKEY")) {
            for (var1 = 0; var1 < this.key.length; ++var1) {
                final byte[] var10000 = this.key;
                var10000[var1] = (byte) ((int) var10000[var1] ^ (int) (byte) this.ParentApp.enc_key.charAt(var1 % this.ParentApp.enc_key.length()));
            }
        }

        this.parent = this.ParentApp.dispFrame;
    }

    public void start() {
        final Thread var1 = new Thread(this);
        var1.start();

        try {
            Thread.sleep(1000L);
        } catch (final InterruptedException var3) {
            System.out.println("Exception: " + var3);
        }

        this.hostAddress = this.host;
        if (this.ui_init(this.base)) {
            if (this.force_config) {
                this.updateconfig();
            }

            this.setVisible(true);
            if (null != this.dev_floppy) {
                this.do_floppy(this.dev_floppy);
            }

            if (null != this.dev_cdrom) {
                this.do_cdrom(this.dev_cdrom);
            }
        }

    }

    public void stop() {
        D.println(3, "Stop " + this);
        if (null != this.fdConnection) {
            try {
                this.fdConnection.close();
                this.fdThread = null;
            } catch (final IOException var3) {
                D.println(3, var3.toString());
            }
        }

        if (null != this.cdConnection) {
            try {
                this.cdConnection.close();
                this.cdThread = null;
            } catch (final IOException var2) {
                D.println(3, var2.toString());
            }
        }

    }

    public void destroy() {
        final Thread var1 = new Thread(this);
        var1.start();

        try {
            Thread.sleep(1000L);
        } catch (final InterruptedException var3) {
            System.out.println("Exception: " + var3);
        }

    }

    public synchronized void run() {
        if (this.thread_init) {
            MediaAccess.cleanup(this);
            this.thread_init = false;
        } else {
            virtdevs.prop = new Properties();

            try {
                virtdevs.prop.load(Files.newInputStream(Paths.get(System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + ".java" + FileSystems.getDefault().getSeparator() + "hp.properties")));
            } catch (final Exception var2) {
                System.out.println("Exception: " + var2);
            }

            virtdevs.cd_support = Boolean.parseBoolean(virtdevs.prop.getProperty("org.virtdevs.cdimage", "true"));
            final MediaAccess var1 = new MediaAccess();
            var1.setup_DirectIO();
            this.thread_init = true;
            this.ParentApp.updateVdMenu();
        }

    }

    @SuppressWarnings("deprecation")
    private boolean ui_init(final String var1) {
        final MouseListener var2 = new MouseAdapter() {
            public void mouseClicked(final MouseEvent var1) {
                if (0 != (var1.getModifiers() & 2)) {
                    ++D.debug;
                    System.out.println("Debug set to " + D.debug);
                }

                if (0 != (var1.getModifiers() & 8)) {
                    --D.debug;
                    System.out.println("Debug set to " + D.debug);
                }

            }
        };
        this.addMouseListener(var2);
        return true;
    }

    public void add(final Component var1, final GridBagConstraints var2, final int var3, final int var4, final int var5, final int var6) {
        var2.gridx = var3;
        var2.gridy = var4;
        var2.gridwidth = var5;
        var2.gridheight = var6;
        this.add(var1, var2);
    }

    public void createImage() {
        new CreateImage(this);
    }

    public boolean do_floppy(final String var1) {
        if (this.fdConnected) {
            try {
                this.fdConnection.close();
            } catch (final Exception var4) {
                D.println(0, "Exception during close: " + var4);
            }
        } else {
            try {
                this.fdConnection = new Connection(this.hostAddress, this.fdport, 1, var1, 0, this.pre, this.key, this);
            } catch (final Exception var6) {
                new VErrorDialog(this.parent, this.getLocalString(8212), var6.getMessage());
                return false;
            }

            System.out.println("Starting fd non-Read-Only");
            this.fdConnection.setWriteProt(false);
            this.setCursor(Cursor.getPredefinedCursor(3));

            final int var2;
            try {
                var2 = this.fdConnection.connect();
            } catch (final Exception var5) {
                this.setCursor(Cursor.getPredefinedCursor(0));
                D.println(0, "Couldn't connect!\n");
                new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8197) + "(" + var5 + ")");
                return false;
            }

            this.setCursor(Cursor.getPredefinedCursor(0));
            switch (var2) {
                case 0:
                    this.fdThread = new Thread(this.fdConnection, "fdConnection");
                    this.fdThread.start();
                    this.fdConnected = true;
                    break;
                case 33:
                    this.ParentApp.lockFdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8198));
                    return false;
                case 34:
                    final String var3;
                    if (this.rekey()) {
                        var3 = this.getLocalString(8199);
                    } else {
                        var3 = this.getLocalString(8200);
                    }

                    new VErrorDialog(this.parent, this.getLocalString(8212), var3);
                    return false;
                case 35:
                    this.ParentApp.lockFdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8201));
                    return false;
                case 37:
                    this.ParentApp.lockFdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8202));
                    return false;
                case 38:
                    this.ParentApp.lockFdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8203));
                    return false;
                default:
                    this.ParentApp.lockFdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8204) + "(" + Integer.toHexString(var2) + ")." + this.getLocalString(8205));
                    return false;
            }
        }

        return true;
    }

    public boolean do_cdrom(final String var1) {
        if (this.cdConnected) {
            try {
                this.cdConnection.close();
            } catch (final Exception var4) {
                D.println(0, "Exception during close: " + var4);
            }
        } else {
            try {
                this.cdConnection = new Connection(this.hostAddress, this.fdport, 2, var1, 0, this.pre, this.key, this);
            } catch (final Exception var6) {
                new VErrorDialog(this.parent, this.getLocalString(8212), var6.getMessage());
                return false;
            }

            this.cdConnection.setWriteProt(true);

            final int var2;
            try {
                var2 = this.cdConnection.connect();
            } catch (final Exception var5) {
                D.println(0, "Couldn't connect!\n");
                new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8206) + " (" + var5 + ")");
                return false;
            }

            switch (var2) {
                case 0:
                    this.cdThread = new Thread(this.cdConnection, "cdConnection");
                    this.cdThread.start();
                    this.cdConnected = true;
                    break;
                case 33:
                    this.ParentApp.lockCdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8198));
                    return false;
                case 34:
                    final String var3;
                    if (this.rekey()) {
                        var3 = this.getLocalString(8199);
                    } else {
                        var3 = this.getLocalString(8200);
                    }

                    new VErrorDialog(this.parent, this.getLocalString(8212), var3);
                    return false;
                case 35:
                    this.ParentApp.lockCdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8201));
                    return false;
                case 37:
                    this.ParentApp.lockCdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8207));
                    return false;
                case 38:
                    this.ParentApp.lockCdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8203));
                    return false;
                default:
                    this.ParentApp.lockCdMenu(true, "");
                    new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8204) + " (" + Integer.toHexString(var2) + ")." + this.getLocalString(8205));
                    return false;
            }
        }

        return true;
    }

    public void paint(final Graphics var1) {
        super.paintComponent(var1);
    }

    public void update(final Graphics var1) {
        this.paint(var1);
    }

    private void updateconfig() {
        try {
            final URL var2 = URI.create(this.base + "modusb.cgi?usb=" + this.configuration).toURL();
            final BufferedReader var4 = new BufferedReader(new InputStreamReader(var2.openStream()));

            String var1;
            while (null != (var1 = var4.readLine())) {
                D.println(3, "updcfg: " + var1);
            }

            var4.close();
        } catch (final Exception var5) {
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8208) + "(" + var5 + ")");
            var5.printStackTrace();
        }

    }

    private boolean rekey() {
        String var3 = null;

        try {
            D.println(3, "Downloading new key: " + this.base + "/html/java_irc.html");
            final URL var5 = URI.create(this.base + "/html/java_irc.html").toURL();
            final BufferedReader var6 = new BufferedReader(new InputStreamReader(var5.openStream()));

            while (true) {
                final String var2;
                if (null != (var2 = var6.readLine())) {
                    D.println(0, "rekey: " + var2);
                    if (!var2.startsWith("info0=\"")) {
                        continue;
                    }

                    var3 = var2.substring(7, 39);
                }

                var6.close();
                break;
            }
        } catch (final Exception var8) {
            D.println(0, "rekey: " + var8);
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8209));
            return false;
        }

        if (null == var3) {
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8209));
            return false;
        } else {
            try {
                for (int var4 = 0; 16 > var4; ++var4) {
                    this.pre[var4] = (byte) Integer.parseInt(var3.substring(2 * var4, 2 * var4 + 2), 16);
                }

                return true;
            } catch (final NumberFormatException var7) {
                D.println(0, "Couldn't parse new key: " + var7);
                new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8210));
                return false;
            }
        }
    }

    public void change_disk(final Connection var1, final String var2) {
        try {
            var1.change_disk(var2);
        } catch (final IOException var4) {
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8211) + " (" + var4 + ")");
        }

    }

    public void fdDisconnect() {
        this.fdThread = null;
        this.repaint();
        this.fdConnected = false;
        this.ParentApp.lockFdMenu(true, "");
        this.ParentApp.remconsObj.setvmAct(0);
    }

    public void cdDisconnect() {
        this.cdThread = null;
        this.repaint();
        this.cdConnected = false;
        this.ParentApp.lockCdMenu(true, "");
        this.ParentApp.remconsObj.setvmAct(0);
    }
}
