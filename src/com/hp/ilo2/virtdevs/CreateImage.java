package com.hp.ilo2.virtdevs;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;

final class CreateImage extends JDialog implements ActionListener, WindowListener, ItemListener, Runnable {
    private final JLabel statLabel;
    private final Choice fdDrive;
    private final JTextField ImgFile;
    private final JTextField DriveFile;
    private final JButton browse;
    private final JButton create;
    private final JButton cancel;
    private final JButton dimg;
    private final JButton dbrowse;
    private final VProgressBar progress;
    private boolean canceled = false;
    private boolean diskimage = true;
    private boolean iscdrom = false;
    private final JFrame frame;
    private final String[] dev;
    private final int[] devt;
    private static final int retrycount = 10;
    private final JPanel p;
    private final Checkbox drvSel;
    private final Checkbox drvPath;
    private int drvCboxChecked = 0;
    private final virtdevs virtdevsObj;

    public CreateImage(final virtdevs var1) {
        super(var1.parent, var1.getLocalString(12544));
        boolean var2 = true;
        boolean var3 = false;
        this.virtdevsObj = var1;
        this.frame = var1.parent;
        this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(false);
        this.setSize(400, 330);
        this.setResizable(false);
        this.setModal(false);
        this.addWindowListener(this);
        this.setLayout(new GridLayout());
        final GridBagConstraints var4 = new GridBagConstraints();
        var4.fill = 2;
        this.setLayout(new GridBagLayout());
        this.dimg = new JButton(this.getLocalString(12551));
        this.dimg.addActionListener(this);
        final JPanel var5 = new JPanel();
        var5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12577)));
        var5.add(this.dimg);
        var4.gridx = 0;
        var4.gridy = 0;
        this.add(var5, var4);
        final CheckboxGroup drvGroup = new CheckboxGroup();
        this.drvSel = new Checkbox(this.getLocalString(12547), drvGroup, true);
        this.drvSel.addItemListener(this);
        this.drvPath = new Checkbox(this.getLocalString(12548), drvGroup, false);
        this.drvPath.addItemListener(this);
        this.DriveFile = new JTextField();
        this.DriveFile.addActionListener(this);
        this.dbrowse = new JButton(this.getLocalString(12553));
        this.dbrowse.setEnabled(false);
        this.dbrowse.addActionListener(this);
        this.fdDrive = new Choice();
        final MediaAccess var6 = new MediaAccess();
        this.dev = var6.devices();
        this.devt = new int[this.dev.length];

        for (int var7 = 0; var7 < this.dev.length; ++var7) {
            this.devt[var7] = var6.devtype(this.dev[var7]);
            boolean defaultRemovable = false;
            if (2 == this.devt[var7]) {
                this.fdDrive.add(this.dev[var7]);
                var2 = false;
                defaultRemovable = true;
            }

            if (5 == this.devt[var7]) {
                this.fdDrive.add(this.dev[var7]);
                if (0 == var7) {
                    this.iscdrom = true;
                } else if (!defaultRemovable) {
                    this.iscdrom = true;
                    var3 = true;
                }

                var2 = false;
            }
        }

        if (var2) {
            this.fdDrive.add(this.getLocalString(12550));
        }

        this.fdDrive.addItemListener(this);
        final JPanel var8 = new JPanel(new GridBagLayout());
        var8.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12578)));
        final GridBagConstraints var9 = new GridBagConstraints();
        var9.gridx = 0;
        var9.gridy = 0;
        var8.add(this.drvSel, var9);
        var9.gridx = 1;
        var9.gridy = 0;
        var8.add(this.fdDrive, var9);
        var9.gridx = 0;
        var9.gridy = 1;
        var8.add(this.drvPath, var9);
        var9.gridx = 2;
        var9.gridy = 1;
        var9.weighty = 1.0;
        var9.anchor = 19;
        var8.add(this.dbrowse, var9);
        var9.ipadx = 187;
        var9.gridx = 1;
        var9.gridy = 1;
        var8.add(this.DriveFile, var9);
        var4.gridx = 0;
        var4.gridy = 1;
        this.add(var8, var4);
        this.ImgFile = new JTextField();
        this.ImgFile.setSize(250, 30);
        this.ImgFile.addActionListener(this);
        this.browse = new JButton(this.getLocalString(12553));
        this.browse.addActionListener(this);
        final JPanel var10 = new JPanel(new GridBagLayout());
        var10.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12579)));
        final GridBagConstraints var11 = new GridBagConstraints();
        var11.gridx = 1;
        var11.gridy = 0;
        var10.add(this.browse, var11);
        var11.ipadx = 270;
        var11.gridx = 0;
        var11.gridy = 0;
        var10.add(this.ImgFile, var11);
        var4.gridx = 0;
        var4.gridy = 2;
        this.add(var10, var4);
        this.progress = new VProgressBar(350, 25, Color.lightGray, Color.blue, Color.white);
        final JPanel var12 = new JPanel(new GridBagLayout());
        var12.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12580)));
        final GridBagConstraints var13 = new GridBagConstraints();
        this.statLabel = new JLabel(" ");
        this.statLabel.setFont(new Font("Arial", 1, 12));
        var13.gridx = 0;
        var13.gridy = 0;
        var12.add(this.statLabel, var13);
        var13.gridx = 0;
        var13.gridy = 1;
        var12.add(this.progress, var13);
        var4.gridx = 0;
        var4.gridy = 3;
        this.add(var12, var4);
        this.create = new JButton(this.getLocalString(12554));
        this.create.setEnabled(false);
        this.create.addActionListener(this);
        this.cancel = new JButton(this.getLocalString(12555));
        this.cancel.addActionListener(this);
        this.p = new JPanel();
        this.p.setLayout(new FlowLayout(2));
        this.p.add(this.create);
        this.p.add(this.cancel);
        var4.gridx = 0;
        var4.gridy = 4;
        this.add(this.p, var4);
        if (var3) {
            this.dimg.setText(this.getLocalString(12551));
            this.diskimage = true;
            this.dimg.setEnabled(false);
        } else {
            this.dimg.setEnabled(true);
        }

        this.dimg.repaint();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private String getLocalString(final int var1) {
        String var2 = "";

        try {
            var2 = this.virtdevsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (final Exception var4) {
            System.out.println("CreateImage:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    void add(final Component var1, final GridBagConstraints var2, final int var3, final int var4, final int var5, final int var6) {
        var2.gridx = var3;
        var2.gridy = var4;
        var2.gridwidth = var5;
        var2.gridheight = var6;
        this.add(var1, var2);
    }

    public void actionPerformed(final ActionEvent var1) {
        final Object var2 = var1.getSource();
        VFileDialog var3;
        String var4;
        if (var2 == this.browse) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var3 = new VFileDialog(this.getLocalString(12556), null);
            var4 = var3.getString();
            if (null != var4) {
                this.ImgFile.setText(var4);
                this.create.setEnabled((0 == this.drvCboxChecked && !this.fdDrive.getSelectedItem().equals(this.getLocalString(12550))) || (1 == this.drvCboxChecked && !"".equals(this.DriveFile.getText())));
            }
        }

        if (var2 == this.dbrowse) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var3 = new VFileDialog(this.getLocalString(12557), null);
            var4 = var3.getString();
            if (null != var4) {
                this.DriveFile.setText(var4);
                this.create.setEnabled(!"".equals(this.ImgFile.getText()));
            }
        }

        if (var2 == this.create) {
            this.create.setEnabled(false);
            this.browse.setEnabled(false);
            if (0 == this.drvCboxChecked) {
                this.fdDrive.setEnabled(false);
            } else {
                this.DriveFile.setEnabled(false);
                this.dbrowse.setEnabled(false);
            }

            this.ImgFile.setEnabled(false);
            this.dimg.setEnabled(false);
            if (this.diskimage) {
                this.statLabel.setText(this.getLocalString(12558));
            } else {
                this.statLabel.setText(this.getLocalString(12559));
            }

            final Thread var5 = new Thread(this);
            var5.start();
        }

        if (var2 == this.dimg) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            this.diskimage = !this.diskimage;
            if (this.diskimage) {
                this.dimg.setText(this.getLocalString(12551));
            } else {
                this.dimg.setText(this.getLocalString(12552));
            }

            this.dimg.repaint();
        }

        if (var2 == this.cancel) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(true);
            this.canceled = true;
            this.dispose();
        }

        if (var2 == this.ImgFile) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            this.create.setEnabled(!"".equals(Objects.requireNonNull(this.ImgFile).getText()) && ((0 == this.drvCboxChecked && !this.fdDrive.getSelectedItem().equals(this.getLocalString(12550))) || (1 == this.drvCboxChecked && !"".equals(this.DriveFile.getText()))));
        } else if (var2 == this.DriveFile) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            this.create.setEnabled(!"".equals(this.ImgFile.getText()) && !"".equals(Objects.requireNonNull(this.DriveFile).getText()));
        }

    }

    public void itemStateChanged(final ItemEvent var1) {
        final Object var2 = var1.getSource();
        int var3;
        String var4;
        if (var2 == this.fdDrive) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var4 = this.fdDrive.getSelectedItem();

            for (var3 = 0; var3 < this.dev.length && !var4.equals(this.dev[var3]); ++var3) {
            }

            if (var3 < this.dev.length) {
                this.iscdrom = 5 == this.devt[var3];
            } else {
                this.iscdrom = false;
                this.create.setEnabled(false);
            }

            if (this.iscdrom) {
                this.dimg.setText(this.getLocalString(12551));
                this.diskimage = true;
                this.dimg.setEnabled(false);
            } else {
                this.dimg.setEnabled(true);
            }

            this.dimg.repaint();
            this.create.setEnabled(!"".equals(this.ImgFile.getText()) && !this.fdDrive.getSelectedItem().equals(this.getLocalString(12550)));
        }

        if (var1.getSource() == this.drvSel) {
            this.DriveFile.setEditable(false);
            this.dbrowse.setEnabled(false);
            this.fdDrive.setEnabled(true);
            this.drvCboxChecked = 0;
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var4 = this.fdDrive.getSelectedItem();

            for (var3 = 0; var3 < this.dev.length && !var4.equals(this.dev[var3]); ++var3) {
            }

            if (var3 < this.dev.length) {
                this.iscdrom = 5 == this.devt[var3];
            } else {
                this.iscdrom = false;
            }

            if (this.iscdrom) {
                this.dimg.setText(this.getLocalString(12551));
                this.diskimage = true;
                this.dimg.setEnabled(false);
            } else {
                this.dimg.setEnabled(true);
            }

            this.dimg.repaint();
            this.create.setEnabled(!this.fdDrive.getSelectedItem().equals(this.getLocalString(12550)) && !"".equals(this.ImgFile.getText()));
        } else if (var1.getSource() == this.drvPath) {
            this.DriveFile.setEditable(true);
            this.dbrowse.setEnabled(true);
            this.fdDrive.setEnabled(false);
            this.drvCboxChecked = 1;
            this.dimg.setText(this.getLocalString(12551));
            this.diskimage = true;
            this.dimg.setEnabled(false);
            this.dimg.repaint();
            this.create.setEnabled(!"".equals(this.DriveFile.getText()) && !"".equals(this.ImgFile.getText()));
        }

    }

    private static void cdrom_testunitready(final MediaAccess var1) {
        final byte[] var2 = {(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final byte[] var3 = new byte[8];
        final byte[] var4 = new byte[3];
        final int var5 = var1.scsi(var2, 1, 8, var3, var4);

    }

    private static void cdrom_startstopunit(final MediaAccess var1) {
        final byte[] var2 = {(byte) 27, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final byte[] var3 = new byte[8];
        final byte[] var4 = new byte[3];
        final int var5 = var1.scsi(var2, 1, 8, var3, var4);

    }

    private static long cdrom_size(final MediaAccess var1) {
        final byte[] var2 = {(byte) 37, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        final byte[] var3 = new byte[8];
        final byte[] var4 = new byte[3];
        long var5 = (long) var1.scsi(var2, 1, 8, var3, var4);
        if (0L <= var5) {
            var5 = (long) SCSI.mk_int32(var3, 0) * (long) SCSI.mk_int32(var3, 4);
        }

        return var5;
    }

    public static void cdrom_read(final MediaAccess var1, final long var2, final int var4, final byte[] var5) throws IOException {
        final byte[] var6 = new byte[12];
        final byte[] var7 = new byte[3];
        final int var8 = (int) (var2 / 2048L);
        var6[0] = (byte) 40;
        var6[1] = (byte) 0;
        var6[2] = (byte) (var8 >> 24 & 255);
        var6[3] = (byte) (var8 >> 16 & 255);
        var6[4] = (byte) (var8 >> 8 & 255);
        var6[5] = (byte) (var8 & 255);
        var6[6] = (byte) 0;
        var6[7] = (byte) (var4 / 2048 >> 8 & 255);
        var6[8] = (byte) (var4 / 2048 & 255);
        var6[9] = (byte) 0;
        var6[10] = (byte) 0;
        var6[11] = (byte) 0;
        final int var9 = var1.scsi(var6, 1, var4, var5, var7);
        if (-1 == var9) {
            throw new IOException("Error reading CD-ROM.");
        } else if (0 != (int) var7[0]) {
            throw new IOException("Error reading CD-ROM.  Sense data (" + D.hex(var7[0], 1) + "/" + D.hex(var7[1], 2) + "/" + D.hex(var7[2], 2) + ")");
        }
    }

    private static void cdrom_read_retry(final MediaAccess var1, final long var2, final int var4, final byte[] var5) {
        final byte[] var6 = new byte[12];
        final byte[] var7 = new byte[3];
        final byte[] var8 = new byte[12];
        int var12 = 0;
        final int var14 = (int) (var2 / 2048L);
        var6[0] = (byte) 40;
        var6[1] = (byte) 0;
        var6[2] = (byte) (var14 >> 24 & 255);
        var6[3] = (byte) (var14 >> 16 & 255);
        var6[4] = (byte) (var14 >> 8 & 255);
        var6[5] = (byte) (var14 & 255);
        var6[6] = (byte) 0;
        var6[7] = (byte) (var4 / 2048 >> 8 & 255);
        var6[8] = (byte) (var4 / 2048 & 255);
        var6[9] = (byte) 0;
        var6[10] = (byte) 0;
        var6[11] = (byte) 0;

        int var13;
        do {
            var13 = var1.scsi(var6, 1, var4, var5, var7);
            if (0 > var13) {
                CreateImage.cdrom_testunitready(var1);
                CreateImage.cdrom_startstopunit(var1);
                var13 = -1;
            }

            if (41 == (int) var7[1]) {
                var13 = -1;
            }

            if (3 == (int) var7[0] || 4 == (int) var7[0]) {
                if (2 == (int) var7[1] && 0 == (int) var7[2]) {
                    var8[0] = (byte) 43;
                    var8[1] = (byte) 0;
                    var8[2] = var6[2];
                    var8[3] = var6[3];
                    var8[4] = var6[4];
                    var8[5] = var6[5];
                    var8[6] = (byte) 0;
                    var8[7] = (byte) 0;
                    var8[8] = (byte) 0;
                    var8[9] = (byte) 0;
                    var8[10] = (byte) 0;
                    var8[11] = (byte) 0;
                    var1.scsi(var8, 1, var4, var5, var7);
                    CreateImage.cdrom_testunitready(var1);
                } else if (17 == (int) var7[1]) {
                    CreateImage.cdrom_testunitready(var1);
                    CreateImage.cdrom_startstopunit(var1);
                } else {
                    CreateImage.cdrom_testunitready(var1);
                }

                var13 = -1;
            }
        } while (0 > var13 && var12++ < CreateImage.retrycount);

        if (var12 >= CreateImage.retrycount) {
            D.println(0, "RETRIES FAILED ! ");
        }

    }

    public void run() {
        int var3 = 0;
        long var5 = 0L;
        final String var9 = this.ImgFile.getText();
        boolean var11 = false;
        if ("".equals(var9)) {
            this.browse.setEnabled(true);
            if (0 == this.drvCboxChecked) {
                this.fdDrive.setEnabled(true);
            } else {
                this.DriveFile.setEnabled(true);
                this.dbrowse.setEnabled(true);
            }

            this.ImgFile.setEnabled(true);
            this.DriveFile.setEnabled(true);
            this.dimg.setEnabled(true);
        } else {
            final MediaAccess var12 = new MediaAccess();
            final MediaAccess var13 = new MediaAccess();
            System.out.println("Message from CreateImage");

            try {
                final int var10;
                if (0 == this.drvCboxChecked && this.iscdrom) {
                    var10 = var12.open(this.fdDrive.getSelectedItem(), 1);
                    if (0 > var10) {
                        var11 = true;
                        new VErrorDialog(this.getLocalString(8247) + " (" + var12.dio.sysError(-var10) + ")", false);
                        throw new IOException("Couldn't open cdrom " + var10);
                    }

                    CreateImage.cdrom_testunitready(var12);
                    var5 = CreateImage.cdrom_size(var12);
                    var3 = 65536;
                } else {
                    int targetIsCdrom = 0;
                    int targetIsDevice = 1;
                    if (0 == this.drvCboxChecked) {
                        var10 = var12.open(this.fdDrive.getSelectedItem(), 1);
                        System.out.println("CrtDev " + this.fdDrive.getSelectedItem() + " " + var10 + " " + targetIsDevice);
                    } else {
                        final int var14 = var12.devtype(this.DriveFile.getText());
                        if (5 == var14) {
                            targetIsCdrom = 1;
                        } else if (2 == var14) {
                        } else {
                            targetIsDevice = 0;
                        }

                        var10 = var12.open(this.DriveFile.getText(), targetIsDevice);
                        System.out.println("CrtFile " + this.DriveFile.getText() + " " + var10 + " " + targetIsDevice);
                    }

                    if (1 == targetIsDevice) {
                        if (1 == targetIsCdrom) {
                            CreateImage.cdrom_testunitready(var12);
                            var5 = CreateImage.cdrom_size(var12);
                            var3 = 65536;
                        } else {
                            var5 = var12.size();
                        }

                        System.out.println("CrtDev actual Dev size" + var5 + " " + var3);
                    } else {
                        var5 = var12.size();
                        var3 = (int) (var5 / 512L);
                        System.out.println("CrtFile static Dev size" + var5 + " " + var3);
                    }
                }
            } catch (final IOException var21) {
                System.out.println("Exception opening media access");
            }

            if (!this.diskimage && var12.wp()) {
                new VErrorDialog(this.frame, this.getLocalString(8248) + " " + this.fdDrive.getSelectedItem() + this.getLocalString(8249));
                this.create.setEnabled(true);
                this.browse.setEnabled(true);
                if (0 == this.drvCboxChecked) {
                    this.fdDrive.setEnabled(true);
                } else {
                    this.DriveFile.setEnabled(true);
                    this.dbrowse.setEnabled(true);
                }

                this.ImgFile.setEnabled(true);
                this.DriveFile.setEnabled(true);
                this.dimg.setEnabled(true);

                try {
                    var12.close();
                } catch (final IOException var17) {
                }

            } else {
                this.setCursor(Cursor.getPredefinedCursor(3));
                long var1 = var5;
                if (0 != var3 && 0L != var5) {
                    try {
                        var13.open(var9, this.diskimage ? 2 : 0);
                    } catch (final IOException var19) {
                        new VErrorDialog(this.frame, this.getLocalString(8251) + var9 + ".");
                    }
                } else {
                    final String var22 = this.getLocalString(8250) + " " + this.getLocalString(8241);
                    new VErrorDialog(this.frame, var22);
                    var11 = true;
                    var3 = 0;
                    var1 = 0L;
                }

                long var7 = 0L;
                final byte[] var23 = new byte[var3];

                try {
                    while (0L < var1 && !this.canceled) {
                        final int var4 = (long) var3 < var1 ? var3 : (int) var1;
                        if (this.diskimage) {
                            if (this.iscdrom) {
                                CreateImage.cdrom_read_retry(var12, var7, var4, var23);
                            } else {
                                var12.read(var7, var4, var23);
                            }

                            var13.write(var7, var4, var23);
                        } else {
                            var13.read(var7, var4, var23);
                            var12.write(var7, var4, var23);
                        }

                        var7 = var7 + (long) var4;
                        var1 = var1 - (long) var4;
                        if (!this.diskimage && 0.95 <= (double) ((float) var7 / (float) var5)) {
                            this.progress.updateBar(0.95F);
                        } else {
                            this.progress.updateBar((float) var7 / (float) var5);
                        }
                    }
                } catch (final IOException var20) {
                    var11 = true;
                    new VErrorDialog(this.frame, this.getLocalString(8252) + (this.diskimage ? this.getLocalString(8253) : this.getLocalString(8254)) + this.getLocalString(8255) + " (" + var20 + ")");
                }

                this.setCursor(Cursor.getPredefinedCursor(0));
                if (var11) {
                    this.statLabel.setText(" ");
                } else {
                    try {
                        var12.close();
                        var13.close();
                    } catch (final IOException var18) {
                        D.println(0, "Closing: " + var18);
                    }

                    this.progress.updateBar((float) var7 / (float) var5);
                    if (this.diskimage) {
                        this.statLabel.setText(this.getLocalString(12560));
                    } else {
                        this.statLabel.setText(this.getLocalString(12561));
                    }

                    this.p.remove(this.create);
                    this.cancel.setText(this.getLocalString(12566));
                }

                this.create.setEnabled(true);
                this.browse.setEnabled(true);
                if (0 == this.drvCboxChecked) {
                    this.fdDrive.setEnabled(true);
                } else {
                    this.DriveFile.setEnabled(true);
                    this.dbrowse.setEnabled(true);
                }

                this.ImgFile.setEnabled(true);
                this.DriveFile.setEnabled(true);
                this.dimg.setEnabled(!this.iscdrom);

            }
        }
    }

    public void windowClosing(final WindowEvent var1) {
        this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(true);
        this.canceled = true;
        this.dispose();
    }

    public void windowActivated(final WindowEvent var1) {
    }

    public void windowClosed(final WindowEvent var1) {
    }

    public void windowDeactivated(final WindowEvent var1) {
    }

    public void windowDeiconified(final WindowEvent var1) {
    }

    public void windowIconified(final WindowEvent var1) {
    }

    public void windowOpened(final WindowEvent var1) {
    }
}
