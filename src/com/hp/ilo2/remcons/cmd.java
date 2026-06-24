package com.hp.ilo2.remcons;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

class cmd implements Runnable {
    public static final int TELNET_PORT = 23;
    private Thread receiver = null;
    private Socket s = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    protected String login = "";
    protected String host = "";
    protected int port = 23;
    private volatile boolean running = false;
    private remcons cmdHandler = null;

    private synchronized void transmit(final String var1) {
        System.out.println("in cmd::transmit");
        if (null != this.out) {
            if (!var1.isEmpty()) {
                final byte[] var2 = new byte[var1.length()];

                for (int var3 = 0; var3 < var1.length(); ++var3) {
                    var2[var3] = (byte) var1.charAt(var3);
                }

                try {
                    this.out.write(var2, 0, var2.length);
                } catch (final IOException var5) {
                    System.out.println("telnet.transmit() IOException: " + var5);
                }
            }

        }
    }

    private String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.cmdHandler.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("VSeizeDialog:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    private synchronized void transmitb(final byte[] var1, final int var2) {
        try {
            this.out.write(var1, 0, var2);
        } catch (final IOException var4) {
            System.out.println("cmd.transmitb() IOException: " + var4);
        }

    }

    private void sendBool(final boolean var1) {
        final byte[] var2 = new byte[4];
        if (var1) {
            var2[0] = (byte) 4;
        } else {
            var2[0] = (byte) 3;
        }

        var2[1] = (byte) 0;
        var2[2] = (byte) 0;
        var2[3] = (byte) 0;
        this.transmitb(var2, var2.length);
    }

    public void run() {
        final byte[] var1 = new byte[12];
        final byte[] var2 = new byte[1];
        final byte[] var3 = new byte[4];
        final byte[] var4 = new byte[128];

        while (this.running) {
            try {
                int var7 = 0;

                int var21;
                while (12 > var7) {
                    var21 = this.in.read(var2, 0, 1);
                    if (1 == var21) {
                        var1[var7++] = var2[0];
                    }
                }

                final byte var9 = var1[0];
                final byte var10 = var1[4];
                final short var24 = (short) var1[10];
                String var15;
                String var16;
                String var17;
                final String var18;
                switch (var9) {
                    case 2:
                        System.out.println("Received Post complete notification\n");
                        this.cmdHandler.session.post_complete = true;
                        break;
                    case 3:
                        if (1 != (int) var10) {
                            System.out.println("Invalid size for cmd: " + var9 + " size:" + var10);
                        }

                        this.in.read(var2, 0, 1);
                        this.cmdHandler.setPwrStatusPower((int) var2[0]);
                        break;
                    case 4:
                        if (1 != (int) var10) {
                            System.out.println("Invalid size for cmd: " + var9 + " size:" + var10);
                        }

                        this.in.read(var2, 0, 1);
                        this.cmdHandler.setPwrStatusHealth((int) var2[0]);
                        break;
                    case 5:
                        if (!this.cmdHandler.session.post_complete) {
                            this.in.read(var4, 0, 2);
                            var16 = Integer.toHexString(255 & (int) var4[1]).toUpperCase();
                            var17 = Integer.toHexString(255 & (int) var4[0]).toUpperCase();
                            this.cmdHandler.session.appendPostCodeEntry(var16 + var17);
                        }
                        break;
                    case 6:
                        System.out.println("Seized command notification\n");
                        var21 = this.in.read(var4, 0, 128);
                        String var14 = "UNKNOWN";
                        var15 = "UNKNOWN";
                        System.out.println("Data rcvd for acquire, rd count " + var21);
                        if (0 < var21) {
                            var16 = new String(var4);
                            System.out.println("Pakcet " + var16);
                            var14 = var16.substring(0, 63).trim();
                            var15 = var16.substring(64, 127).trim();
                            if (var14.isEmpty()) {
                                var14 = "UNKNOWN";
                            }

                            if (var15.isEmpty()) {
                                var15 = "UNKNOWN";
                            }
                        } else {
                            System.out.println("Invalid acquire info");
                        }

                        final int var25 = this.cmdHandler.seize_dialog(var14, var15, (int) var24);
                        if (0 == var25) {
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
                        if (0 < var21) {
                            var18 = new String(var4);
                            System.out.println("Pakcet " + var18);
                            var16 = var18.substring(0, 63).trim();
                            var17 = var18.substring(64, 127).trim();
                            if (var16.isEmpty()) {
                                var16 = "UNKNOWN";
                            }

                            if (var17.isEmpty()) {
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

            } catch (final Exception var20) {
                System.out.println("CMD exception: " + var20);
                return;
            }
        }
    }

    public void connectCmd(final remcons var1, final String var2, final int var3) {
        final int connected = 0;
        try {
            this.cmdHandler = var1;
            final byte[] var4;
            final byte[] var5 = new byte[2];
            this.s = new Socket(var2, var3);

            try {
                this.s.setSoLinger(true, 0);
            } catch (final SocketException var9) {
                System.out.println("connectCmd linger SocketException: " + var9);
            }

            this.in = new DataInputStream(this.s.getInputStream());
            this.out = new DataOutputStream(this.s.getOutputStream());
            byte var6 = this.in.readByte();
            if (80 == (int) var6) {
                var5[0] = (byte) 2;
                var5[1] = (byte) 32;
                var4 = var1.ParentApp.getParameter("RCINFO1").getBytes();
                if (var1.ParentApp.optional_features.contains("ENCRYPT_KEY")) {
                    for (int var7 = 0; var7 < var4.length; ++var7) {
                        var4[var7] = (byte) ((int) var4[var7] ^ (int) (byte) var1.ParentApp.enc_key.charAt(var7 % var1.ParentApp.enc_key.length()));
                    }

                    if (var1.ParentApp.optional_features.contains("ENCRYPT_VMKEY")) {
                        var5[1] = (byte) ((int) var5[1] | 64);
                    } else {
                        var5[1] = (byte) ((int) var5[1] | 128);
                    }
                }

                final byte[] var13 = new byte[var5.length + var4.length];
                System.arraycopy(var5, 0, var13, 0, var5.length);
                System.arraycopy(var4, 0, var13, var5.length, var4.length);
                final String var8 = new String(var13);
                this.transmit(var8);
                var6 = this.in.readByte();
                if (82 == (int) var6) {
                    this.running = true;
                    this.receiver = new Thread(this);
                    this.receiver.setName("cmd_rcvr");
                    this.receiver.start();
                } else {
                    System.out.println("login failed. read data" + var6);
                }
            } else {
                System.out.println("Socket connection failure... ");
            }
        } catch (final SocketException var10) {
            System.out.println("telnet.connect() SocketException: " + var10);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
        } catch (final UnknownHostException var11) {
            System.out.println("telnet.connect() UnknownHostException: " + var11);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
        } catch (final IOException var12) {
            System.out.println("telnet.connect() IOException: " + var12);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
        }

    }

    public void disconnectCmd() {
        // Cooperative shutdown: signal the loop to stop, then close the socket so
        // the blocking read in run() throws and the thread exits on its own.
        this.running = false;
        final Thread var1 = this.receiver;
        this.receiver = null;
        if (null != this.s) {
            try {
                System.out.println("Closing socket");
                this.s.close();
            } catch (final IOException var2) {
                System.out.println("telnet.disconnect() IOException: " + var2);
            }
        }

        if (null != var1 && var1 != Thread.currentThread()) {
            try {
                var1.join(2000L);
            } catch (final InterruptedException var3) {
                Thread.currentThread().interrupt();
            }
        }

        if (null != this.cmdHandler) {
            this.cmdHandler.setPwrStatusHealth(3);
            this.cmdHandler.setPwrStatusPower(0);
            this.cmdHandler = null;
        }

        this.s = null;
        this.in = null;
        this.out = null;
    }
}
