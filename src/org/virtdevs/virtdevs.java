package org.virtdevs;


import org.jirc.intgapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class virtdevs extends JPanel implements Runnable {
    public static final int UNQF_HIDEFLP = 1;
    static final int ImageDone = 39;
    public static int UID;
    public static boolean cd_support = true;
    public static Properties prop;
    public int dev_cd_device = 0;
    public int dev_fd_device = 0;
    public int unq_feature = 0;
    public intgapp ParentApp;
    public boolean cdConnected = false;
    public boolean fdConnected = false;
    public Connection fdConnection;
    public Connection cdConnection;
    public Thread fdThread;
    public Thread cdThread;
    protected boolean stopFlag = false;
    protected boolean running = false;
    String host;
    String base;
    String configuration;
    String dev_floppy;
    String dev_cdrom;
    String dev_auto;
    boolean force_config = false;
    boolean thread_init = false;
    byte[] pre = new byte[16];
    byte[] key = new byte[32];
    int fdport = 17988;
    JFrame parent;
    String hostAddress;

    public virtdevs(intgapp var1) {
        this.ParentApp = var1;
    }

    public static int getSockFd(Socket var0) {
        int var2 = -1;
        Class var3 = null;
        Field var4 = null;
        Field var5 = null;

        try {
            var3 = class$java$net$Socket == null ? (class$java$net$Socket = class$("java.net.Socket")) : class$java$net$Socket;
            Field[] var6 = var3.getDeclaredFields();

            int var1;
            for (var1 = 0; var1 < var6.length; ++var1) {
                if (var6[var1].getName().equals("impl")) {
                    var4 = var6[var1];
                    var4.setAccessible(true);
                    break;
                }
            }

            SocketImpl var7 = (SocketImpl) var4.get(var0);
            var3 = class$java$net$SocketImpl == null ? (class$java$net$SocketImpl = class$("java.net.SocketImpl")) : class$java$net$SocketImpl;
            var6 = var3.getDeclaredFields();

            for (var1 = 0; var1 < var6.length; ++var1) {
                if (var6[var1].getName().equals("fd")) {
                    var5 = var6[var1];
                    var5.setAccessible(true);
                    break;
                }
            }

            FileDescriptor var8 = (FileDescriptor) var5.get(var7);
            var3 = class$java$io$FileDescriptor == null ? (class$java$io$FileDescriptor = class$("java.io.FileDescriptor")) : class$java$io$FileDescriptor;
            var6 = var3.getDeclaredFields();

            for (var1 = 0; var1 < var6.length; ++var1) {
                if (var6[var1].getName().equals("fd")) {
                    var5 = var6[var1];
                    var5.setAccessible(true);
                    break;
                }
            }

            var2 = var5.getInt(var8);
        } catch (Exception var9) {
            System.out.println("Ex: " + var9);
        }

        return var2;
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("virdevs:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public Image get(String var1) {
        ClassLoader var2 = this.getClass().getClassLoader();
        return this.ParentApp.getImage(var2.getResource("com/hp/ilo2/virtdevs/" + var1));
    }

    public void init() {
        if (UID == 0) {
            UID = this.hashCode();
        }

        URL var2 = this.ParentApp.getDocumentBase();
        this.host = this.ParentApp.getParameter("hostAddress");
        if (this.host == null) {
            this.host = var2.getHost();
        }

        this.base = var2.getProtocol() + "://" + var2.getHost();
        if (var2.getPort() != -1) {
            this.base = this.base + ":" + var2.getPort();
        }

        this.base = this.base + "/";
        String var3 = this.ParentApp.getParameter("INFO0");
        int var1;
        if (var3 != null) {
            try {
                for (var1 = 0; var1 < 16; ++var1) {
                    this.pre[var1] = (byte) Integer.parseInt(var3.substring(2 * var1, 2 * var1 + 2), 16);
                }
            } catch (NumberFormatException var9) {
                D.println(0, "Couldn't parse INFO0: " + var9);
            }
        }

        try {
            if (null != this.ParentApp.vm_port) {
                this.fdport = Integer.parseInt(this.ParentApp.vm_port);
            }
        } catch (NumberFormatException var8) {
            D.println(0, "Couldn't parse INFO1: " + var8);
        }

        this.configuration = this.ParentApp.getParameter("INFO2");
        if (this.configuration == null) {
            this.configuration = "auto";
        }

        this.dev_floppy = this.ParentApp.getParameter("floppy");
        this.dev_cdrom = this.ParentApp.getParameter("cdrom");
        this.dev_auto = this.ParentApp.getParameter("device");
        String var4 = this.ParentApp.getParameter("config");
        if (var4 != null) {
            this.configuration = var4;
            this.force_config = true;
        }

        String var5 = this.ParentApp.getParameter("UNIQUE_FEATURES");

        try {
            if (var5 != null) {
                this.unq_feature = Integer.parseInt(var5);
            }
        } catch (NumberFormatException var7) {
            D.println(0, "Couldn't parse UNIQUE_FEATURES: " + var7);
        }

        this.key = this.ParentApp.getParameter("RCINFO1").getBytes();
        if (this.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
            for (var1 = 0; var1 < this.key.length; ++var1) {
                byte[] var10000 = this.key;
                var10000[var1] ^= (byte) this.ParentApp.enc_key.charAt(var1 % this.ParentApp.enc_key.length());
            }
        }

        this.parent = this.ParentApp.dispFrame;
    }

    public void start() {
        Thread var1 = new Thread(this);
        var1.start();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var3) {
            System.out.println("Exception: " + var3);
        }

        this.hostAddress = this.host;
        if (this.ui_init(this.base)) {
            if (this.force_config) {
                this.updateconfig();
            }

            this.show();
            if (this.dev_floppy != null) {
                this.do_floppy(this.dev_floppy);
            }

            if (this.dev_cdrom != null) {
                this.do_cdrom(this.dev_cdrom);
            }
        }

    }

    public void stop() {
        D.println(3, "Stop " + this);
        if (this.fdConnection != null) {
            try {
                this.fdConnection.close();
                this.fdThread = null;
            } catch (IOException var3) {
                D.println(3, var3.toString());
            }
        }

        if (this.cdConnection != null) {
            try {
                this.cdConnection.close();
                this.cdThread = null;
            } catch (IOException var2) {
                D.println(3, var2.toString());
            }
        }

    }

    public void destroy() {
        Thread var1 = new Thread(this);
        var1.start();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var3) {
            System.out.println("Exception: " + var3);
        }

    }

    public synchronized void run() {
        if (!this.thread_init) {
            prop = new Properties();

            try {
                prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));
            } catch (Exception var2) {
                System.out.println("Exception: " + var2);
            }

            cd_support = Boolean.valueOf(prop.getProperty("org.virtdevs.cdimage", "true"));
            MediaAccess var1 = new MediaAccess();
            var1.setup_DirectIO();
            this.thread_init = true;
            this.ParentApp.updateVdMenu();
        } else {
            MediaAccess.cleanup(this);
            this.thread_init = false;
        }

    }

    public boolean ui_init(String var1) {
        MouseAdapter var2 = new MouseAdapter() {
            public void mouseClicked(MouseEvent var1) {
                if ((var1.getModifiers() & 2) != 0) {
                    ++D.debug;
                    System.out.println("Debug set to " + D.debug);
                }

                if ((var1.getModifiers() & 8) != 0) {
                    --D.debug;
                    System.out.println("Debug set to " + D.debug);
                }

            }
        };
        this.addMouseListener(var2);
        return true;
    }

    public void add(Component var1, GridBagConstraints var2, int var3, int var4, int var5, int var6) {
        var2.gridx = var3;
        var2.gridy = var4;
        var2.gridwidth = var5;
        var2.gridheight = var6;
        this.add(var1, var2);
    }

    public void createImage() {
        new CreateImage(this);
    }

    public boolean do_floppy(String var1) {
        if (!this.fdConnected) {
            try {
                this.fdConnection = new Connection(this.hostAddress, this.fdport, 1, var1, 0, this.pre, this.key, this);
            } catch (Exception var6) {
                new VErrorDialog(this.parent, this.getLocalString(8212), var6.getMessage());
                return false;
            }

            System.out.println("Starting fd non-Read-Only");
            this.fdConnection.setWriteProt(false);
            this.setCursor(Cursor.getPredefinedCursor(3));

            int var2;
            try {
                var2 = this.fdConnection.connect();
            } catch (Exception var5) {
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
                    String var3;
                    if (this.rekey("/html/java_irc.html")) {
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
        } else {
            try {
                this.fdConnection.close();
            } catch (Exception var4) {
                D.println(0, "Exception during close: " + var4);
            }
        }

        return true;
    }

    public boolean do_cdrom(String var1) {
        if (!this.cdConnected) {
            try {
                this.cdConnection = new Connection(this.hostAddress, this.fdport, 2, var1, 0, this.pre, this.key, this);
            } catch (Exception var6) {
                new VErrorDialog(this.parent, this.getLocalString(8212), var6.getMessage());
                return false;
            }

            this.cdConnection.setWriteProt(true);

            int var2;
            try {
                var2 = this.cdConnection.connect();
            } catch (Exception var5) {
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
                    String var3;
                    if (this.rekey("/html/java_irc.html")) {
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
        } else {
            try {
                this.cdConnection.close();
            } catch (Exception var4) {
                D.println(0, "Exception during close: " + var4);
            }
        }

        return true;
    }

    public void paint(Graphics var1) {
        super.paintComponent(var1);
    }

    public void update(Graphics var1) {
        this.paint(var1);
    }

    void updateconfig() {
        try {
            URL var2 = new URL(this.base + "modusb.cgi?usb=" + this.configuration);
            URLConnection var3 = var2.openConnection();
            BufferedReader var4 = new BufferedReader(new InputStreamReader(var2.openStream()));

            String var1;
            while ((var1 = var4.readLine()) != null) {
                D.println(3, "updcfg: " + var1);
            }

            var4.close();
        } catch (Exception var5) {
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8208) + "(" + var5 + ")");
            var5.printStackTrace();
        }

    }

    public boolean rekey(String var1) {
        String var3 = null;

        try {
            D.println(3, "Downloading new key: " + this.base + var1);
            URL var5 = new URL(this.base + var1);
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var5.openStream()));

            while (true) {
                String var2;
                if ((var2 = var6.readLine()) != null) {
                    D.println(0, "rekey: " + var2);
                    if (!var2.startsWith("info0=\"")) {
                        continue;
                    }

                    var3 = var2.substring(7, 39);
                }

                var6.close();
                break;
            }
        } catch (Exception var8) {
            D.println(0, "rekey: " + var8);
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8209));
            return false;
        }

        if (var3 == null) {
            new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8209));
            return false;
        } else {
            try {
                for (int var4 = 0; var4 < 16; ++var4) {
                    this.pre[var4] = (byte) Integer.parseInt(var3.substring(2 * var4, 2 * var4 + 2), 16);
                }

                return true;
            } catch (NumberFormatException var7) {
                D.println(0, "Couldn't parse new key: " + var7);
                new VErrorDialog(this.parent, this.getLocalString(8212), this.getLocalString(8210));
                return false;
            }
        }
    }

    public void change_disk(Connection var1, String var2) {
        try {
            var1.change_disk(var2);
        } catch (IOException var4) {
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
