package org.remcons;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class cmd implements Runnable {
    public static final int TELNET_PORT = 23;
    protected Thread receiver;
    protected Socket s;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected String login = "";
    protected String host = "";
    protected int port = 23;
    protected int connected = 0;
    remcons cmdHandler;

    public cmd() {
    }

    public synchronized void transmit(String var1) {
        System.out.println("in cmd::transmit");
        if (this.out != null) {
            if (var1.length() != 0) {
                byte[] var2 = new byte[var1.length()];

                for (int var3 = 0; var3 < var1.length(); ++var3) {
                    var2[var3] = (byte) var1.charAt(var3);
                }

                try {
                    this.out.write(var2, 0, var2.length);
                } catch (IOException var5) {
                    System.out.println("telnet.transmit() IOException: " + var5);
                }
            }

        }
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.cmdHandler.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("VSeizeDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    public synchronized void transmitb(byte[] var1, int var2) {
        try {
            this.out.write(var1, 0, var2);
        } catch (IOException var4) {
            System.out.println("cmd.transmitb() IOException: " + var4);
        }

    }

    public void sendBool(boolean var1) {
        byte[] var2 = new byte[4];
        if (var1) {
            var2[0] = 4;
        } else {
            var2[0] = 3;
        }

        var2[1] = 0;
        var2[2] = 0;
        var2[3] = 0;
        this.transmitb(var2, var2.length);
    }

    public void run() {
        byte[] var1 = new byte[12];
        byte[] var2 = new byte[1];
        byte[] var3 = new byte[4];
        byte[] var4 = new byte[128];
        boolean var13 = false;
        boolean var12 = false;
        boolean var11 = false;

        while (true) {
            try {
                int var7 = 0;
                boolean var6 = false;

                int var21;
                while (var7 < 12) {
                    var21 = this.in.read(var2, 0, 1);
                    if (var21 == 1) {
                        var1[var7++] = var2[0];
                    }
                }

                byte var9 = var1[0];
                byte var10 = var1[4];
                short var23 = (short) var1[8];
                short var24 = (short) var1[10];
                String var15;
                String var16;
                String var17;
                String var18;
                switch (var9) {
                    case 2:
                        System.out.println("Received Post complete notification\n");
                        this.cmdHandler.session.post_complete = true;
                        this.cmdHandler.session.set_status(4, "");
                        break;
                    case 3:
                        if (var10 != 1) {
                            System.out.println("Invalid size for cmd: " + var9 + " size:" + var10);
                        }

                        this.in.read(var2, 0, 1);
                        this.cmdHandler.setPwrStatusPower(var2[0]);
                        break;
                    case 4:
                        if (var10 != 1) {
                            System.out.println("Invalid size for cmd: " + var9 + " size:" + var10);
                        }

                        this.in.read(var2, 0, 1);
                        this.cmdHandler.setPwrStatusHealth(var2[0]);
                        break;
                    case 5:
                        if (!this.cmdHandler.session.post_complete) {
                            StringBuffer var26 = new StringBuffer(16);
                            this.in.read(var4, 0, 2);
                            var16 = Integer.toHexString(255 & var4[1]).toUpperCase();
                            var17 = Integer.toHexString(255 & var4[0]).toUpperCase();
                            var15 = var26.append(this.cmdHandler.getLocalString(12582)).append(var16).append(var17).toString();
                            this.cmdHandler.session.set_status(4, var15);
                        }
                        break;
                    case 6:
                        System.out.println("Seized command notification\n");
                        var21 = this.in.read(var4, 0, 128);
                        String var14 = "UNKNOWN";
                        var15 = "UNKNOWN";
                        System.out.println("Data rcvd for acquire " + var4 + "rd count " + var21);
                        if (var21 > 0) {
                            var16 = new String(var4);
                            System.out.println("Pakcet " + var16);
                            var14 = var16.substring(0, 63).trim();
                            var15 = var16.substring(64, 127).trim();
                            if (var14.length() <= 0) {
                                var14 = "UNKNOWN";
                            }

                            if (var15.length() <= 0) {
                                var15 = "UNKNOWN";
                            }
                        } else {
                            System.out.println("Invalid acquire info");
                        }

                        int var25 = this.cmdHandler.seize_dialog(var14, var15, var24);
                        if (var25 == 0) {
                            this.sendBool(true);
                            this.cmdHandler.seize_confirmed();
                        } else {
                            this.sendBool(false);
                        }
                        break;
                    case 7:
                        this.in.read(var3, 0, 4);
                        this.cmdHandler.ack(var3[0], var3[1], var3[2], var3[3]);
                        break;
                    case 8:
                        System.out.println("Playback not supported now.\n");
                        break;
                    case 9:
                        System.out.println("Share command notification\n");
                        var21 = this.in.read(var4, 0, 128);
                        var16 = "UNKNOWN";
                        var17 = "UNKNOWN";
                        if (var21 > 0) {
                            var18 = new String(var4);
                            System.out.println("Pakcet " + var18);
                            var16 = var18.substring(0, 63).trim();
                            var17 = var18.substring(64, 127).trim();
                            if (var16.length() <= 0) {
                                var16 = "UNKNOWN";
                            }

                            if (var17.length() <= 0) {
                                var17 = "UNKNOWN";
                            }
                        } else {
                            System.out.println("Invalid acquire info");
                        }

                        this.sendBool(false);
                        this.cmdHandler.shared(var17, var16);
                        break;
                    case 10:
                        System.out.println("Firmware upgrade in progress notification\n");
                        this.cmdHandler.firmwareUpgrade();
                        break;
                    case 11:
                        System.out.println("Un authorized action performed\n");
                        var18 = "";
                        boolean var19 = false;
                        switch (var24) {
                            case 2:
                                var18 = this.getLocalString(8293);
                                var19 = true;
                                break;
                            case 3:
                                var18 = this.getLocalString(8294);
                                break;
                            case 4:
                                var18 = this.getLocalString(8295);
                                break;
                            default:
                                var18 = "{0x" + var24 + "}";
                        }

                        this.cmdHandler.unAuthorized(var18, var19);
                    case 12:
                    default:
                        break;
                    case 13:
                        this.in.read(var2, 0, 1);
                        System.out.println("VM notification from firmware\n");
                        break;
                    case 14:
                        System.out.println("Unlicensed notification from firmware\n");
                        this.cmdHandler.UnlicensedShutdown();
                        break;
                    case 15:
                        System.out.println("Reset notification from firmware\n");
                        this.cmdHandler.resetShutdown();
                }

                boolean var22 = false;
            } catch (Exception var20) {
                System.out.println("CMD exception: " + var20);
                return;
            }
        }
    }

    public boolean connectCmd(remcons var1, String var2, int var3) {
        try {
            this.cmdHandler = var1;
            byte[] var4 = new byte[32];
            byte[] var5 = new byte[2];
            this.s = new Socket(var2, var3);

            try {
                this.s.setSoLinger(true, 0);
            } catch (SocketException var9) {
                System.out.println("connectCmd linger SocketException: " + var9);
            }

            this.in = new DataInputStream(this.s.getInputStream());
            this.out = new DataOutputStream(this.s.getOutputStream());
            byte var6 = this.in.readByte();
            if (var6 == 80) {
                var5[0] = 2;
                var5[1] = 32;
                var4 = var1.ParentApp.getParameter("RCINFO1").getBytes();
                if (var1.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {
                    for (int var7 = 0; var7 < var4.length; ++var7) {
                        var4[var7] ^= (byte) var1.ParentApp.enc_key.charAt(var7 % var1.ParentApp.enc_key.length());
                    }

                    if (var1.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
                        var5[1] = (byte) (var5[1] | 64);
                    } else {
                        var5[1] = (byte) (var5[1] | 128);
                    }
                }

                byte[] var13 = new byte[var5.length + var4.length];
                System.arraycopy(var5, 0, var13, 0, var5.length);
                System.arraycopy(var4, 0, var13, var5.length, var4.length);
                String var8 = new String(var13);
                this.transmit(var8);
                var6 = this.in.readByte();
                if (var6 == 82) {
                    this.receiver = new Thread(this);
                    this.receiver.setName("cmd_rcvr");
                    this.receiver.start();
                } else {
                    System.out.println("login failed. read data" + var6);
                }
            } else {
                System.out.println("Socket connection failure... ");
            }
        } catch (SocketException var10) {
            System.out.println("telnet.connect() SocketException: " + var10);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
            this.connected = 0;
        } catch (UnknownHostException var11) {
            System.out.println("telnet.connect() UnknownHostException: " + var11);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
            this.connected = 0;
        } catch (IOException var12) {
            System.out.println("telnet.connect() IOException: " + var12);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
            this.connected = 0;
        }

        return true;
    }

    public void disconnectCmd() {
        if (this.receiver != null && this.receiver.isAlive()) {
            this.receiver.stop();
        }

        this.receiver = null;
        if (this.s != null) {
            try {
                System.out.println("Closing socket");
                this.s.close();
            } catch (IOException var2) {
                System.out.println("telnet.disconnect() IOException: " + var2);
            }
        }

        if (this.cmdHandler != null) {
            this.cmdHandler.setPwrStatusHealth(3);
            this.cmdHandler.setPwrStatusPower(0);
            this.cmdHandler = null;
        }

        this.s = null;
        this.in = null;
        this.out = null;
    }
}
