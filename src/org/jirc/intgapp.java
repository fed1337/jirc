package org.jirc;


import org.remcons.OkCancelDialog;
import org.remcons.URLDialog;
import org.remcons.remcons;
import org.virtdevs.MediaAccess;
import org.virtdevs.VErrorDialog;
import org.virtdevs.VFileDialog;
import org.virtdevs.virtdevs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class intgapp extends JApplet implements Runnable, ActionListener, ItemListener {
    private final int REMCONS_MAX_FN_KEYS = 12;
    private final int REMCONS_MAX_KBD_LAYOUT = 17;
    public virtdevs virtdevsObj = new virtdevs(this);
    public remcons remconsObj = new remcons(this);
    public locinfo locinfoObj = new locinfo(this);
    public jsonparser jsonObj = new jsonparser(this);
    public String optional_features;
    public String moniker;
    public boolean moniker_cached = false;
    public JFrame dispFrame;
    public JPanel mainPanel;
    public JMenuItem vdMenuItemCrImage;
    public String enc_key;
    public String rc_port;
    public String vm_key;
    public String vm_port;
    public String server_name;
    public String ilo_fqdn;
    public String enclosure;
    public int blade = 0;
    public int bay = 0;
    public byte[] enc_key_val = new byte[16];
    public int dwidth;
    public int dheight;
    public boolean exit = false;
    public boolean fdSelected = false;
    public boolean cdSelected = false;
    public boolean in_enclosure = false;
    JMenuBar mainMenuBar;
    JMenu psMenu;
    JMenu vdMenu;
    JMenu kbMenu;
    JMenu kbCAFMenu;
    JMenu kbAFMenu;
    JMenu kbLangMenu;
    JMenu hlpMenu;
    int vdmenuIndx;
    int fdMenuItems;
    int cdMenuItems;
    JCheckBoxMenuItem[] vdMenuItems;
    JMenuItem momPress;
    JMenuItem pressHold;
    JMenuItem powerCycle;
    JMenuItem sysReset;
    JMenuItem ctlAltDel;
    JMenuItem numLock;
    JMenuItem capsLock;
    JMenuItem ctlAltBack;
    JMenuItem hotKeys;
    JMenuItem aboutJirc;
    JMenuItem[] ctlAltFn;
    JMenuItem[] AltFn;
    JCheckBoxMenuItem[] localKbdLayout;
    JPanel dispStatusBar;
    JMenuItem mdebug1;
    JMenuItem mdebug2;
    JMenuItem mdebug3;
    JScrollPane scroller;
    String rcErrMessage;
    private MediaAccess ma;

    public intgapp() {
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("remcons:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public void init() {
        boolean var1 = true;
        System.out.println("Started Retrieving parameters from ILO..");
        String var2 = this.jsonObj.getJSONRequest("rc_info");
        if (var2 != null) {
            this.ApplyRcInfoParameters(var2);
            System.out.println("Completed Retrieving parameters from ILO");
        }

        var1 = this.locinfoObj.initLocStrings();
        this.virtdevsObj.init();
        this.remconsObj.init();
        this.ui_init();
        if (null == var2) {
            System.out.println("Failed to retrive parameters from ILO");
            new VErrorDialog(this.dispFrame, this.getLocalString(8212), this.rcErrMessage, true);
            this.dispFrame.setVisible(false);
        } else if (!var1) {
            new VErrorDialog(this.dispFrame, this.getLocalString(8212), this.locinfoObj.rcErrMessage, true);
            this.dispFrame.setVisible(false);
        }

    }

    public void start() {
        try {
            this.virtdevsObj.start();
            this.remconsObj.start();
            this.dispFrame.getContentPane().add(this.scroller, "Center");
            this.dispFrame.getContentPane().add(this.dispStatusBar, "South");
            this.scroller.validate();
            this.dispStatusBar.validate();
            this.dispFrame.validate();
            System.out.println("Set Inital focus for session..");
            this.remconsObj.session.requestFocus();
            this.run();
        } catch (Exception var2) {
            System.out.println("FAILURE: exception starting applet");
            var2.printStackTrace();
        }

    }

    public void stop() {
        this.exit = true;
        this.virtdevsObj.stop();
        this.remconsObj.remconsUnInstallKeyboardHook();
        this.remconsObj.stop();
    }

    public void destroy() {
        System.out.println("Destroying subsustems");
        this.exit = true;
        this.remconsObj.remconsUnInstallKeyboardHook();
        this.virtdevsObj.destroy();
        this.remconsObj.destroy();
        this.dispFrame.dispose();
    }

    public synchronized void run() {
        boolean var5 = false;
        boolean var6 = false;

        while (true) {
            try {
                int var11 = 0;
                int var1 = 0;
                this.ma = new MediaAccess();
                String[] var7 = this.ma.devices();

                int var2;
                for (int var8 = 0; var7 != null && var8 < var7.length; ++var8) {
                    var2 = this.ma.devtype(var7[var8]);
                    if (var2 == 2 || var2 == 5) {
                        ++var1;
                    }
                }

                int var3;
                int var4;
                if (var1 > this.vdmenuIndx - 4) {
                    ClassLoader var9 = this.getClass().getClassLoader();

                    for (var3 = 0; var7 != null && var3 < var7.length; ++var3) {
                        var6 = false;
                        var2 = this.ma.devtype(var7[var3]);

                        for (var4 = 0; var4 < this.vdmenuIndx - 4; ++var4) {
                            if (var7[var3].equals(this.vdMenu.getItem(var4).getText())) {
                                var6 = true;
                                ++var11;
                            }
                        }

                        if (!var6) {
                            if (var2 == 2) {
                                System.out.println("Device attached: " + var7[var3]);
                                this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(var7[var3]);
                                this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + var7[var3]);
                                this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                                if (!var7[var3].equals("A:") && !var7[var3].equals("B:")) {
                                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var9.getResource("com/hp/ilo2/remcons/images/usb.png"))));
                                } else {
                                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var9.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
                                }

                                this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx], var11);
                                this.vdMenu.updateUI();
                                ++this.vdmenuIndx;
                                break;
                            }

                            if (var2 == 5) {
                                System.out.println("CDROM Hot plug device auto-update no supported at this time");
                            }
                        }
                    }
                } else if (var1 < this.vdmenuIndx - 4) {
                    for (var4 = 0; var4 < this.vdmenuIndx - 4; ++var4) {
                        var6 = false;

                        for (var3 = 0; var7 != null && var3 < var7.length; ++var3) {
                            var2 = this.ma.devtype(var7[var3]);
                            if ((var2 == 2 || var2 == 5) && this.vdMenu.getItem(var4).getText().equals(var7[var3])) {
                                var6 = true;
                            }
                        }

                        if (!var6) {
                            System.out.println("Device removed: " + this.vdMenu.getItem(var4).getText());
                            this.vdMenu.remove(var4);
                            this.vdMenu.updateUI();
                            --this.vdmenuIndx;
                            break;
                        }
                    }
                }

                this.ma = null;
                this.remconsObj.session.set_status(3, "");
                this.remconsObj.sleepAtLeast(5000L);
                if (this.exit) {
                    break;
                }
            } catch (InterruptedException var10) {
                System.out.println("Exception on intgapp");
            }
        }

        System.out.println("Intgapp stopped running");
    }

    public void paintComponent(Graphics var1) {
        super.paintComponents(var1);
        var1.drawString("Remote Console JApplet Loaded", 10, 50);
    }

    public void ui_init() {
        String var1 = "";
        System.out.println("Message from ui_init55");
        this.dispFrame = new JFrame("JavaApplet IRC Window");
        this.dispFrame.getContentPane().setLayout(new BorderLayout());
        this.dispFrame.addWindowListener(new WindowCloser());
        this.mainMenuBar = new JMenuBar();
        this.dispStatusBar = new JPanel(new BorderLayout());
        this.dispStatusBar.add(this.remconsObj.session.status_box, "West");
        this.dispStatusBar.add(this.remconsObj.pwrStatusPanel, "East");
        String var3 = this.jsonObj.getJSONRequest("session_info");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        this.dispFrame.setJMenuBar(this.mainMenuBar);
        if (var3 != null) {
            this.makePsMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(var3, "reset_priv"));
            this.makeVdMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(var3, "virtual_media_priv"));
        }

        this.makeKbMenu(this.mainMenuBar);
        String var2 = this.jsonObj.getJSONRequest("login_session");
        if (var2 != null) {
            var1 = this.jsonObj.getJSONObject(var2, "alt");
            if (var1 == null || var1 != null && this.jsonObj.getJSONNumber(var1, "mode") == 0) {
                this.makeHlpMenu(this.mainMenuBar);
            }
        }

        this.scroller = new JScrollPane(this.remconsObj.session, 20, 30);
        this.scroller.setVisible(true);

        try {
            String var4 = this.getLocalString(4132) + " " + this.server_name + " " + this.getLocalString(4133) + " " + this.ilo_fqdn;
            if (this.blade == 1 && this.in_enclosure) {
                var4 = var4 + " " + this.getLocalString(4134) + " " + this.enclosure + " " + this.getLocalString(4135) + " " + this.bay;
            }

            this.dispFrame.setTitle(var4);
        } catch (Exception var13) {
            this.dispFrame.setTitle(this.getLocalString(4132) + " " + this.getCodeBase().getHost());
            System.out.println("IRC title not available");
        }

        int var15 = Toolkit.getDefaultToolkit().getScreenSize().width;
        int var5 = Toolkit.getDefaultToolkit().getScreenSize().height;
        int var6 = var15 < 1054 ? var15 : 1054;
        int var7 = var5 < 874 ? var5 - 30 : 874;
        int var8 = var15 > 1054 ? (var15 - 1054) / 2 : 0;
        int var9 = var5 > 874 ? (var5 - 874) / 2 : 0;
        this.dispFrame.setSize(var6, var7);
        this.dispFrame.setLocation(var8, var9);
        System.out.println("check dimensions " + var6 + " " + var7 + " " + var8 + " " + var9);
        this.dispFrame.setVisible(true);

        try {
            Insets var10 = this.dispFrame.getInsets();
            ClassLoader var11 = this.getClass().getClassLoader();
            if (var1 == null || var1 != null && this.jsonObj.getJSONNumber(var1, "mode") == 0) {
                this.dispFrame.setIconImage(this.getImage(var11.getResource("com/hp/ilo2/remcons/images/ilo_logo.png")));
            }

            Image var12 = this.dispFrame.getIconImage();
            if (var12 == null) {
                System.out.println("Dimage is null");
            }
        } catch (Exception var14) {
            System.out.println("JIRC icon not available");
        }

    }

    protected void makeHlpMenu(JMenuBar var1) {
        this.hlpMenu = new JMenu(this.getLocalString(4136));
        this.aboutJirc = new JMenuItem(this.getLocalString(4137));
        this.aboutJirc.addActionListener(this);
        this.hlpMenu.add(this.aboutJirc);
        var1.add(this.hlpMenu);
    }

    protected void makeVdMenu(JMenuBar var1, int var2) {
        this.vdMenu = new JMenu(this.getLocalString(4098));
        if (var2 == 1) {
            var1.add(this.vdMenu);
        }

    }

    public void updateVdMenu() {
        this.ma = new MediaAccess();
        ClassLoader var2 = this.getClass().getClassLoader();
        String var3 = this.jsonObj.getJSONRequest("vm_status");
        String var4 = this.jsonObj.getJSONArray(var3, "options", 0);
        String var5 = this.jsonObj.getJSONArray(var3, "options", 1);
        String[] var6 = this.ma.devices();
        this.vdmenuIndx = 0;
        if (var6 != null) {
            this.vdMenuItems = new JCheckBoxMenuItem[var6.length + 5];

            for (int var1 = 0; var1 < var6.length; ++var1) {
                int var7 = this.ma.devtype(var6[var1]);
                if (var7 == 5) {
                    this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(var6[var1]);
                    this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + var6[var1]);
                    this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/CD_Drive.png"))));
                    this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
                    ++this.vdmenuIndx;
                } else if (var7 == 2) {
                    this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(var6[var1]);
                    this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + var6[var1]);
                    this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                    if (!var6[var1].equals("A:") && !var6[var1].equals("B:")) {
                        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/usb.png"))));
                    } else {
                        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
                    }

                    this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
                    ++this.vdmenuIndx;
                }
            }
        } else {
            this.vdMenuItems = new JCheckBoxMenuItem[5];
            System.out.println("Media Access not available...");
        }

        this.ma = null;
        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4130) + " " + this.getLocalString(4106));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + this.getLocalString(12567));
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        ++this.vdmenuIndx;
        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4131) + this.getLocalString(4106));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("FLOPPY");
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/Network.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        ++this.vdmenuIndx;
        if (this.jsonObj.getJSONNumber(var4, "vm_url_connected") == 1 && this.jsonObj.getJSONNumber(var4, "vm_connected") == 1) {
            this.fdSelected = true;
            this.lockFdMenu(false, "URL Removable Media");
        }

        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4130) + " " + this.getLocalString(4107));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + this.getLocalString(12567));
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        ++this.vdmenuIndx;
        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(this.getLocalString(4131) + this.getLocalString(4107));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("CDROM");
        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(this.getImage(var2.getResource("com/hp/ilo2/remcons/images/Network.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        ++this.vdmenuIndx;
        if (this.jsonObj.getJSONNumber(var5, "vm_url_connected") == 1 && this.jsonObj.getJSONNumber(var5, "vm_connected") == 1) {
            this.cdSelected = true;
            this.lockCdMenu(false, "URL CD/DVD-ROM");
        }

        this.vdMenu.addSeparator();
        this.vdMenuItemCrImage = new JMenuItem(this.getLocalString(4109));
        this.vdMenuItemCrImage.setActionCommand("CreateDiskImage");
        this.vdMenuItemCrImage.addActionListener(this);
        this.vdMenu.add(this.vdMenuItemCrImage);
    }

    public void lockCdMenu(boolean var1, String var2) {
        boolean var3 = false;

        for (int var5 = 0; var5 < this.vdmenuIndx; ++var5) {
            this.vdMenu.getItem(var5).removeItemListener(this);
            if (this.vdMenu.getItem(var5).getActionCommand().startsWith("cd") || this.vdMenu.getItem(var5).getActionCommand().equals("CDROM")) {
                if (var2.equals(this.vdMenu.getItem(var5).getText())) {
                    this.vdMenu.getItem(var5).setSelected(!var1);
                } else {
                    this.vdMenu.getItem(var5).setSelected(false);
                    this.vdMenu.getItem(var5).setEnabled(var1);
                }
            }

            this.vdMenu.getItem(var5).addItemListener(this);
        }

    }

    public void lockFdMenu(boolean var1, String var2) {
        boolean var3 = false;

        for (int var5 = 0; var5 < this.vdmenuIndx; ++var5) {
            this.vdMenu.getItem(var5).removeItemListener(this);
            if (this.vdMenu.getItem(var5).getActionCommand().startsWith("fd") || this.vdMenu.getItem(var5).getActionCommand().equals("FLOPPY")) {
                if (var2.equals(this.vdMenu.getItem(var5).getText())) {
                    this.vdMenu.getItem(var5).setSelected(!var1);
                } else {
                    this.vdMenu.getItem(var5).setSelected(false);
                    this.vdMenu.getItem(var5).setEnabled(var1);
                }
            }

            this.vdMenu.getItem(var5).addItemListener(this);
        }

    }

    protected void makePsMenu(JMenuBar var1, int var2) {
        ClassLoader var3 = this.getClass().getClassLoader();
        this.psMenu = new JMenu(this.getLocalString(4097));
        this.momPress = new JMenuItem(this.getLocalString(4100));
        this.momPress.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/press.png"))));
        this.momPress.setActionCommand("psMomPress");
        this.momPress.addActionListener(this);
        this.psMenu.add(this.momPress);
        this.pressHold = new JMenuItem(this.getLocalString(4101));
        this.pressHold.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/hold.png"))));
        this.pressHold.setActionCommand("psPressHold");
        this.pressHold.addActionListener(this);
        this.powerCycle = new JMenuItem(this.getLocalString(4102));
        this.powerCycle.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/coldboot.png"))));
        this.powerCycle.setActionCommand("psPowerCycle");
        this.powerCycle.addActionListener(this);
        this.sysReset = new JMenuItem(this.getLocalString(4103));
        this.sysReset.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/reset.png"))));
        this.sysReset.setActionCommand("psSysReset");
        this.sysReset.addActionListener(this);
        if (var2 == 1) {
            var1.add(this.psMenu);
        }

    }

    public void updatePsMenu(int var1) {
        if (0 == var1) {
            this.psMenu.remove(this.pressHold);
            this.psMenu.remove(this.powerCycle);
            this.psMenu.remove(this.sysReset);
        } else {
            this.psMenu.remove(this.pressHold);
            this.psMenu.remove(this.powerCycle);
            this.psMenu.remove(this.sysReset);
            this.psMenu.add(this.pressHold);
            this.psMenu.add(this.powerCycle);
            this.psMenu.add(this.sysReset);
        }

    }

    protected void makeKbMenu(JMenuBar var1) {
        ClassLoader var3 = this.getClass().getClassLoader();
        this.kbMenu = new JMenu(this.getLocalString(4099));
        this.kbCAFMenu = new JMenu("CTRL-ALT-Fn");
        this.kbAFMenu = new JMenu("ALT-Fn");
        this.kbLangMenu = new JMenu(this.getLocalString(4110));
        this.ctlAltDel = new JMenuItem(this.getLocalString(4104));
        this.ctlAltDel.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.ctlAltDel.setActionCommand("kbCtlAltDel");
        this.ctlAltDel.addActionListener(this);
        this.kbMenu.add(this.ctlAltDel);
        this.numLock = new JMenuItem(this.getLocalString(4105));
        this.numLock.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.numLock.setActionCommand("kbNumLock");
        this.numLock.addActionListener(this);
        this.kbMenu.add(this.numLock);
        this.capsLock = new JMenuItem(this.getLocalString(4128));
        this.capsLock.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.capsLock.setActionCommand("kbCapsLock");
        this.capsLock.addActionListener(this);
        this.kbMenu.add(this.capsLock);
        this.ctlAltBack = new JMenuItem("CTRL-ALT-BACKSPACE");
        this.ctlAltBack.setIcon(new ImageIcon(this.getImage(var3.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.ctlAltBack.setActionCommand("kbCtlAltBack");
        this.ctlAltBack.addActionListener(this);
        this.ctlAltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];

        int var2;
        for (var2 = 0; var2 < this.REMCONS_MAX_FN_KEYS; ++var2) {
            this.ctlAltFn[var2] = new JMenuItem("CTRL-ALT-F" + (var2 + 1));
            this.ctlAltFn[var2].setActionCommand("kbCtrlAltFn" + var2);
            this.ctlAltFn[var2].addActionListener(this);
            this.kbCAFMenu.add(this.ctlAltFn[var2]);
        }

        this.AltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];

        for (var2 = 0; var2 < this.REMCONS_MAX_FN_KEYS; ++var2) {
            this.AltFn[var2] = new JMenuItem("ALT-F" + (var2 + 1));
            this.AltFn[var2].setActionCommand("kbAltFn" + var2);
            this.AltFn[var2].addActionListener(this);
            this.kbAFMenu.add(this.AltFn[var2]);
        }

        this.localKbdLayout = new JCheckBoxMenuItem[this.REMCONS_MAX_KBD_LAYOUT];

        for (var2 = 0; var2 < this.REMCONS_MAX_KBD_LAYOUT; ++var2) {
            this.localKbdLayout[var2] = new JCheckBoxMenuItem(this.getLocalString(4111 + var2));
            this.localKbdLayout[var2].setActionCommand("localKbdLayout" + var2);
            this.localKbdLayout[var2].addItemListener(this);
            this.kbLangMenu.add(this.localKbdLayout[var2]);
        }

        this.localKbdLayout[0].setSelected(true);
        String var4 = System.getProperty("os.name").toLowerCase();
        if (!var4.startsWith("windows")) {
            this.kbMenu.add(this.ctlAltBack);
            this.kbMenu.add(this.kbCAFMenu);
            this.kbMenu.add(this.kbAFMenu);
            this.kbMenu.add(this.kbLangMenu);
        }

        this.kbMenu.addSeparator();
        this.hotKeys = new JMenuItem(this.getLocalString(4129));
        this.hotKeys.addActionListener(this);
        this.kbMenu.add(this.hotKeys);
        var1.add(this.kbMenu);
    }

    public void actionPerformed(ActionEvent var1) {
        OkCancelDialog var3;
        if (var1.getSource() == this.momPress) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8297), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendMomPress();
            }
        } else if (var1.getSource() == this.pressHold) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8298), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendPressHold();
            }
        } else if (var1.getSource() == this.powerCycle) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8299), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendPowerCycle();
            }
        } else if (var1.getSource() == this.sysReset) {
            var3 = new OkCancelDialog(this.remconsObj, this.dispFrame, this.getLocalString(8300), this.getLocalString(4097));
            if (var3.result()) {
                this.remconsObj.session.sendSystemReset();
            }
        } else if (var1.getSource() == this.ctlAltDel) {
            this.remconsObj.session.send_ctrl_alt_del();
        } else if (var1.getSource() == this.numLock) {
            this.remconsObj.session.send_num_lock();
        } else if (var1.getSource() == this.capsLock) {
            this.remconsObj.session.send_caps_lock();
        } else if (var1.getSource() == this.ctlAltBack) {
            this.remconsObj.session.send_ctrl_alt_back();
        } else if (var1.getSource() == this.hotKeys) {
            this.remconsObj.viewHotKeys();
        } else if (var1.getSource() == this.vdMenuItemCrImage) {
            this.virtdevsObj.createImage();
        } else if (var1.getSource() == this.aboutJirc) {
            this.remconsObj.viewAboutJirc();
        } else {
            int var2;
            for (var2 = 0; var2 < this.REMCONS_MAX_FN_KEYS; ++var2) {
                if (var1.getSource() == this.ctlAltFn[var2]) {
                    this.remconsObj.session.send_ctrl_alt_fn(var2);
                    break;
                }

                if (var1.getSource() == this.AltFn[var2]) {
                    this.remconsObj.session.send_alt_fn(var2);
                    break;
                }
            }

            if (var2 >= this.REMCONS_MAX_FN_KEYS) {
                System.out.println("Unhandled ActionItem" + var1.getActionCommand());
            }
        }

    }

    public void itemStateChanged(ItemEvent var1) {
        boolean var3 = false;
        JCheckBoxMenuItem var4 = null;
        String var5 = null;
        String var6 = null;
        int var7 = var1.getStateChange();

        int var2;
        for (var2 = 0; var2 < this.REMCONS_MAX_KBD_LAYOUT; ++var2) {
            if (this.localKbdLayout[var2] == var1.getSource() && var7 == 1) {
                System.out.println(var2);
                this.localKbdLayout[var2].setSelected(true);
                this.kbdLayoutMenuHandler(var2);
                return;
            }
        }

        for (var2 = 0; var2 < this.vdmenuIndx; ++var2) {
            if (this.vdMenuItems[var2] == var1.getSource()) {
                var4 = this.vdMenuItems[var2];
                var5 = var4.getActionCommand();
                var6 = var4.getLabel();
                break;
            }
        }

        if (var4 != null && var5 != null) {
            String var8;
            VFileDialog var14;
            if (var5.equals("fd" + this.getLocalString(12567))) {
                var8 = null;
                if (var7 == 2) {
                    this.virtdevsObj.do_floppy(var6);
                    this.lockFdMenu(true, var6);
                } else if (var7 == 1) {
                    this.dispFrame.setVisible(false);
                    var14 = new VFileDialog(this.getLocalString(8261), "*.img");
                    var8 = var14.getString();
                    this.dispFrame.setVisible(true);
                    if (var8 != null) {
                        if (this.virtdevsObj.fdThread != null) {
                            this.virtdevsObj.change_disk(this.virtdevsObj.fdConnection, var8);
                        }

                        System.out.println("Image file: " + var8);
                        var3 = this.virtdevsObj.do_floppy(var8);
                        this.lockFdMenu(!var3, var6);
                    } else {
                        this.lockFdMenu(true, var6);
                    }
                }

            } else if (var5.equals("cd" + this.getLocalString(12567))) {
                var8 = null;
                if (var7 == 2) {
                    this.virtdevsObj.do_cdrom(var6);
                    this.lockCdMenu(true, var6);
                } else if (var7 == 1) {
                    this.dispFrame.setVisible(false);
                    var14 = new VFileDialog(this.getLocalString(8261), "*.iso");
                    var8 = var14.getString();
                    this.dispFrame.setVisible(true);
                    if (var8 != null) {
                        if (this.virtdevsObj.cdThread != null) {
                            this.virtdevsObj.change_disk(this.virtdevsObj.cdConnection, var8);
                        }

                        System.out.println("Image file: " + var8);
                        var3 = this.virtdevsObj.do_cdrom(var8);
                        this.lockCdMenu(!var3, var6);
                    } else {
                        this.lockCdMenu(true, var6);
                    }
                }

            } else if (var5.startsWith("cd")) {
                var3 = this.virtdevsObj.do_cdrom(var6);
                if (var3) {
                    this.lockCdMenu(var7 != 1, var6);
                }

            } else if (var5.startsWith("fd")) {
                var3 = this.virtdevsObj.do_floppy(var6);
                if (var3) {
                    this.lockFdMenu(var7 != 1, var6);
                }

            } else if (var5.equals("FLOPPY") || var5.equals("CDROM")) {
                String var10 = "";
                boolean var11 = false;
                String var9;
                if (var7 == 2) {
                    var9 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + var5 + "\", \"command\":\"EJECT\", \"session_key\":\"" + this.getParameter("RCINFO1") + "\"}";
                    this.jsonObj.postJSONRequest("vm_status", var9);
                    this.remconsObj.session.set_status(3, "Unmounted URL");
                } else if (var7 == 1) {
                    this.remconsObj.setDialogIsOpen(true);
                    URLDialog var12 = new URLDialog(this.remconsObj);
                    var8 = var12.getUserInput();
                    if (var8.compareTo("userhitcancel") == 0 || var8.compareTo("userhitclose") == 0) {
                        var8 = null;
                    }

                    if (var8 != null) {
                        var8 = var8.replaceAll("[\u0000-\u001f]", "");
                        System.out.println("url:  " + var8);
                    }

                    this.remconsObj.setDialogIsOpen(false);
                    if (var8 != null) {
                        var9 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + var5 + "\", \"command\":\"INSERT\", \"url\":\"" + var8 + "\", \"session_key\":\"" + this.getParameter("RCINFO1") + "\"}";
                        var10 = this.jsonObj.postJSONRequest("vm_status", var9);
                        if (var10 == "Success") {
                            var9 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + var5 + "\", \"boot_option\":\"CONNECT\", \"command\":\"SET\", \"url\":\"" + var8 + "\", \"session_key\":\"" + this.getParameter("RCINFO1") + "\"}";
                            var10 = this.jsonObj.postJSONRequest("vm_status", var9);
                        }

                        if (var10 == "SCSI_ERR_NO_LICENSE") {
                            String var13 = "<html>" + this.getLocalString(8213) + " " + this.getLocalString(8214) + " " + this.getLocalString(8237) + "<br><br>" + this.getLocalString(8238) + "</html>";
                            new VErrorDialog(this.dispFrame, this.getLocalString(8236), var13, true);
                        } else if (var10 != "Success") {
                            new VErrorDialog(this.dispFrame, this.getLocalString(8212), this.getLocalString(8292), true);
                        } else {
                            var11 = true;
                            this.remconsObj.session.set_status(3, this.getLocalString(12581));
                        }
                    }
                }

                if (var5.equals("FLOPPY")) {
                    this.lockFdMenu(!var11, var6);
                } else if (var5.equals("CDROM")) {
                    this.lockCdMenu(!var11, var6);
                }

            }
        } else {
            System.out.println("Unhandled item event");
        }
    }

    public void kbdLayoutMenuHandler(int var1) {
        for (int var2 = 0; var2 < this.REMCONS_MAX_KBD_LAYOUT; ++var2) {
            if (var2 != var1) {
                this.localKbdLayout[var2].setSelected(false);
            }
        }

        this.remconsObj.setLocalKbdLayout(var1);
    }

    private void ApplyRcInfoParameters(String var1) {
        this.enc_key = this.rc_port = this.vm_key = this.vm_port = null;
        Arrays.fill(this.enc_key_val, (byte) 0);
        var1 = var1.trim();
        var1 = var1.substring(1, var1.length() - 1);
        String[] var2 = var1.split(",");

        for (int var3 = 0; var3 < var2.length; ++var3) {
            String[] var4 = var2[var3].split(":");
            if (var4.length != 2) {
                System.out.println("Error in ApplyRcInfoParameters");
                return;
            }

            String var5 = var4[0].trim();
            var5 = var5.substring(1, var5.length() - 1);
            String var6 = var4[1].trim();
            if (var6.charAt(0) == '"') {
                var6 = var6.substring(1, var6.length() - 1);
            }

            if (var5.compareToIgnoreCase("enc_key") == 0) {
                this.enc_key = var6;

                for (int var7 = 0; var7 < this.enc_key_val.length; ++var7) {
                    String var8 = this.enc_key.substring(var7 * 2, var7 * 2 + 2);

                    try {
                        this.enc_key_val[var7] = (byte) Integer.parseInt(var8, 16);
                    } catch (NumberFormatException var10) {
                        System.out.println("Failed to Parse enc_key");
                    }
                }
            } else if (var5.compareToIgnoreCase("rc_port") == 0) {
                System.out.println("rc_port:" + var6);
                this.rc_port = var6;
            } else if (var5.compareToIgnoreCase("vm_key") == 0) {
                this.vm_key = var6;
            } else if (var5.compareToIgnoreCase("vm_port") == 0) {
                System.out.println("vm_port:" + var6);
                this.vm_port = var6;
            } else if (var5.equalsIgnoreCase("optional_features")) {
                System.out.println("optional_features:" + var6);
                this.optional_features = var6;
            } else if (var5.compareToIgnoreCase("server_name") == 0) {
                System.out.println("server_name:" + var6);
                this.server_name = var6;
            } else if (var5.compareToIgnoreCase("ilo_fqdn") == 0) {
                System.out.println("ilo_fqdn:" + var6);
                this.ilo_fqdn = var6;
            } else if (var5.compareToIgnoreCase("blade") == 0) {
                this.blade = Integer.parseInt(var6);
                System.out.println("blade:" + this.blade);
            } else if (this.blade == 1 && var5.compareToIgnoreCase("enclosure") == 0) {
                if (!var6.equals("null")) {
                    this.in_enclosure = true;
                    System.out.println("enclosure:" + var6);
                    this.enclosure = var6;
                }
            } else if (this.blade == 1 && var5.compareToIgnoreCase("bay") == 0) {
                this.bay = Integer.parseInt(var6);
                System.out.println("bay:" + this.bay);
            }
        }

    }

    public void moveUItoInit(boolean var1) {
        System.out.println("Disable Menus\n");
        this.psMenu.setEnabled(var1);
        this.vdMenu.setEnabled(var1);
        this.kbMenu.setEnabled(var1);
    }

    public String rebrandToken(String var1) {
        if (!this.moniker_cached) {
            String var4 = this.jsonObj.getJSONRequest("login_session");
            if (var4 == null) {
                return var1;
            }

            this.moniker = this.jsonObj.getJSONObject(var4, "moniker");
            if (this.moniker == null) {
                return var1;
            }

            this.moniker_cached = true;
        }

        String var3 = this.jsonObj.getJSONString(this.moniker, var1);
        return var3 == "" ? var1 : var3;
    }

    class WindowCloser extends WindowAdapter {
        WindowCloser() {
        }

        public void windowClosing(WindowEvent var1) {
            intgapp.this.stop();
            intgapp.this.exit = true;
        }
    }
}
