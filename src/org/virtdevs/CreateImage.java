package org.virtdevs;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class CreateImage extends JDialog implements ActionListener, WindowListener, ItemListener, Runnable {
    private final JLabel statLabel;
    Choice fdDrive;
    JTextField ImgFile;
    JTextField DriveFile;
    JButton browse;
    JButton create;
    JButton cancel;
    JButton dimg;
    JButton dbrowse;
    VProgressBar progress;
    boolean canceled = false;
    boolean diskimage = true;
    boolean iscdrom = false;
    JFrame frame;
    String[] dev;
    int[] devt;
    boolean defaultRemovable = false;
    int retrycount = 10;
    JPanel p;
    CheckboxGroup drvGroup;
    Checkbox drvSel;
    Checkbox drvPath;
    int drvCboxChecked = 0;
    int targetIsDevice = 1;
    int targetIsCdrom = 0;
    virtdevs virtdevsObj;

    public CreateImage(virtdevs var1) {
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
        GridBagConstraints var4 = new GridBagConstraints();
        var4.fill = 2;
        this.setLayout(new GridBagLayout());
        this.dimg = new JButton(this.getLocalString(12551));
        this.dimg.addActionListener(this);
        JPanel var5 = new JPanel();
        var5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12577)));
        var5.add(this.dimg);
        var4.gridx = 0;
        var4.gridy = 0;
        this.add(var5, var4);
        this.drvGroup = new CheckboxGroup();
        this.drvSel = new Checkbox(this.getLocalString(12547), this.drvGroup, true);
        this.drvSel.addItemListener(this);
        this.drvPath = new Checkbox(this.getLocalString(12548), this.drvGroup, false);
        this.drvPath.addItemListener(this);
        this.DriveFile = new JTextField();
        this.DriveFile.addActionListener(this);
        this.dbrowse = new JButton(this.getLocalString(12553));
        this.dbrowse.setEnabled(false);
        this.dbrowse.addActionListener(this);
        this.fdDrive = new Choice();
        MediaAccess var6 = new MediaAccess();
        this.dev = var6.devices();
        this.devt = new int[this.dev.length];

        for (int var7 = 0; var7 < this.dev.length; ++var7) {
            this.devt[var7] = var6.devtype(this.dev[var7]);
            if (this.devt[var7] == 2) {
                this.fdDrive.add(this.dev[var7]);
                var2 = false;
                this.defaultRemovable = true;
            }

            if (this.devt[var7] == 5) {
                this.fdDrive.add(this.dev[var7]);
                if (var7 == 0) {
                    this.iscdrom = true;
                } else if (!this.defaultRemovable) {
                    this.iscdrom = true;
                    var3 = true;
                }

                var2 = false;
            }
        }

        if (var2) {
            this.fdDrive.add(this.getLocalString(12550));
        }

        var6 = null;
        this.fdDrive.addItemListener(this);
        JPanel var8 = new JPanel(new GridBagLayout());
        var8.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12578)));
        GridBagConstraints var9 = new GridBagConstraints();
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
        JPanel var10 = new JPanel(new GridBagLayout());
        var10.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12579)));
        GridBagConstraints var11 = new GridBagConstraints();
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
        JPanel var12 = new JPanel(new GridBagLayout());
        var12.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), this.getLocalString(12580)));
        GridBagConstraints var13 = new GridBagConstraints();
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
            this.dimg.setLabel(this.getLocalString(12551));
            this.diskimage = true;
            this.dimg.setEnabled(false);
        } else {
            this.dimg.setEnabled(true);
        }

        this.dimg.repaint();
        this.setLocationRelativeTo((Component) null);
        this.setVisible(true);
    }

    public String getLocalString(int var1) {
        String var2 = "";

        try {
            var2 = this.virtdevsObj.ParentApp.locinfoObj.getLocString(var1);
        } catch (Exception var4) {
            System.out.println("CreateImage:getLocalString" + var4.getMessage());
        }

        return var2;
    }

    void add(Component var1, GridBagConstraints var2, int var3, int var4, int var5, int var6) {
        var2.gridx = var3;
        var2.gridy = var4;
        var2.gridwidth = var5;
        var2.gridheight = var6;
        this.add(var1, var2);
    }

    public void actionPerformed(ActionEvent var1) {
        Object var2 = var1.getSource();
        VFileDialog var3;
        String var4;
        if (var2 == this.browse) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var3 = new VFileDialog(this.getLocalString(12556), (String) null);
            var4 = var3.getString();
            if (var4 != null) {
                this.ImgFile.setText(var4);
                this.create.setEnabled((0 == this.drvCboxChecked && !this.fdDrive.getSelectedItem().equals(this.getLocalString(12550))) || (1 == this.drvCboxChecked && !this.DriveFile.getText().equals("")));
            }
        }

        if (var2 == this.dbrowse) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var3 = new VFileDialog(this.getLocalString(12557), (String) null);
            var4 = var3.getString();
            if (var4 != null) {
                this.DriveFile.setText(var4);
                this.create.setEnabled(!this.ImgFile.getText().equals(""));
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

            Thread var5 = new Thread(this);
            var5.start();
        }

        if (var2 == this.dimg) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            this.diskimage = !this.diskimage;
            if (this.diskimage) {
                this.dimg.setLabel(this.getLocalString(12551));
            } else {
                this.dimg.setLabel(this.getLocalString(12552));
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
            this.create.setEnabled(!this.ImgFile.getText().equals("") && ((0 == this.drvCboxChecked && !this.fdDrive.getSelectedItem().equals(this.getLocalString(12550))) || (1 == this.drvCboxChecked && !this.DriveFile.getText().equals(""))));
        } else if (var2 == this.DriveFile) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            this.create.setEnabled(!this.ImgFile.getText().equals("") && !this.DriveFile.getText().equals(""));
        }

    }

    public void itemStateChanged(ItemEvent var1) {
        Object var2 = var1.getSource();
        int var3;
        String var4;
        if (var2 == this.fdDrive) {
            this.statLabel.setText(" ");
            this.progress.updateBar(0.0F);
            var4 = this.fdDrive.getSelectedItem();

            for (var3 = 0; var3 < this.dev.length && !var4.equals(this.dev[var3]); ++var3) {
            }

            if (var3 < this.dev.length) {
                this.iscdrom = this.devt[var3] == 5;
            } else {
                this.iscdrom = false;
                this.create.setEnabled(false);
            }

            if (this.iscdrom) {
                this.dimg.setLabel(this.getLocalString(12551));
                this.diskimage = true;
                this.dimg.setEnabled(false);
            } else {
                this.dimg.setEnabled(true);
            }

            this.dimg.repaint();
            this.create.setEnabled(!this.ImgFile.getText().equals("") && !this.fdDrive.getSelectedItem().equals(this.getLocalString(12550)));
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
                this.iscdrom = this.devt[var3] == 5;
            } else {
                this.iscdrom = false;
            }

            if (this.iscdrom) {
                this.dimg.setLabel(this.getLocalString(12551));
                this.diskimage = true;
                this.dimg.setEnabled(false);
            } else {
                this.dimg.setEnabled(true);
            }

            this.dimg.repaint();
            this.create.setEnabled(!this.fdDrive.getSelectedItem().equals(this.getLocalString(12550)) && !this.ImgFile.getText().equals(""));
        } else if (var1.getSource() == this.drvPath) {
            this.DriveFile.setEditable(true);
            this.dbrowse.setEnabled(true);
            this.fdDrive.setEnabled(false);
            this.drvCboxChecked = 1;
            this.dimg.setLabel(this.getLocalString(12551));
            this.diskimage = true;
            this.dimg.setEnabled(false);
            this.dimg.repaint();
            this.create.setEnabled(!this.DriveFile.getText().equals("") && !this.ImgFile.getText().equals(""));
        }

    }

    public int cdrom_testunitready(MediaAccess var1) {
        byte[] var2 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] var3 = new byte[8];
        byte[] var4 = new byte[3];
        int var5 = var1.scsi(var2, 1, 8, var3, var4);
        if (var5 >= 0) {
            var5 = SCSI.mk_int32(var3, 0) * SCSI.mk_int32(var3, 4);
        }

        return var5;
    }

    public int cdrom_startstopunit(MediaAccess var1) {
        byte[] var2 = new byte[]{27, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
        byte[] var3 = new byte[8];
        byte[] var4 = new byte[3];
        int var5 = var1.scsi(var2, 1, 8, var3, var4);
        if (var5 >= 0) {
            var5 = SCSI.mk_int32(var3, 0) * SCSI.mk_int32(var3, 4);
        }

        return var5;
    }

    public long cdrom_size(MediaAccess var1) {
        byte[] var2 = new byte[]{37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] var3 = new byte[8];
        byte[] var4 = new byte[3];
        long var5 = (long) var1.scsi(var2, 1, 8, var3, var4);
        if (var5 >= 0L) {
            var5 = (long) SCSI.mk_int32(var3, 0) * (long) SCSI.mk_int32(var3, 4);
        }

        return var5;
    }

    public void cdrom_read(MediaAccess var1, long var2, int var4, byte[] var5) throws IOException {
        byte[] var6 = new byte[12];
        byte[] var7 = new byte[3];
        int var8 = (int) (var2 / 2048L);
        var6[0] = 40;
        var6[1] = 0;
        var6[2] = (byte) (var8 >> 24 & 255);
        var6[3] = (byte) (var8 >> 16 & 255);
        var6[4] = (byte) (var8 >> 8 & 255);
        var6[5] = (byte) (var8 >> 0 & 255);
        var6[6] = 0;
        var6[7] = (byte) (var4 / 2048 >> 8 & 255);
        var6[8] = (byte) (var4 / 2048 >> 0 & 255);
        var6[9] = 0;
        var6[10] = 0;
        var6[11] = 0;
        int var9 = var1.scsi(var6, 1, var4, var5, var7);
        if (var9 == -1) {
            throw new IOException("Error reading CD-ROM.");
        } else if (var7[0] != 0) {
            throw new IOException("Error reading CD-ROM.  Sense data (" + D.hex(var7[0], 1) + "/" + D.hex(var7[1], 2) + "/" + D.hex(var7[2], 2) + ")");
        }
    }

    public void cdrom_read_retry(MediaAccess var1, long var2, int var4, byte[] var5) throws IOException {
        byte[] var6 = new byte[12];
        byte[] var7 = new byte[3];
        byte[] var8 = new byte[12];
        boolean var10 = false;
        boolean var11 = false;
        int var12 = 0;
        int var14 = (int) (var2 / 2048L);
        var6[0] = 40;
        var6[1] = 0;
        var6[2] = (byte) (var14 >> 24 & 255);
        var6[3] = (byte) (var14 >> 16 & 255);
        var6[4] = (byte) (var14 >> 8 & 255);
        var6[5] = (byte) (var14 >> 0 & 255);
        var6[6] = 0;
        var6[7] = (byte) (var4 / 2048 >> 8 & 255);
        var6[8] = (byte) (var4 / 2048 >> 0 & 255);
        var6[9] = 0;
        var6[10] = 0;
        var6[11] = 0;

        int var13;
        do {
            long var15 = System.currentTimeMillis();
            var13 = var1.scsi(var6, 1, var4, var5, var7);
            long var17 = System.currentTimeMillis();
            if (var13 < 0) {
                this.cdrom_testunitready(var1);
                this.cdrom_startstopunit(var1);
                var13 = -1;
            }

            if (var7[1] == 41) {
                var13 = -1;
            }

            if (var7[0] == 3 || var7[0] == 4) {
                if (var7[1] == 2 && var7[2] == 0) {
                    var8[0] = 43;
                    var8[1] = 0;
                    var8[2] = var6[2];
                    var8[3] = var6[3];
                    var8[4] = var6[4];
                    var8[5] = var6[5];
                    var8[6] = 0;
                    var8[7] = 0;
                    var8[8] = 0;
                    var8[9] = 0;
                    var8[10] = 0;
                    var8[11] = 0;
                    var1.scsi(var8, 1, var4, var5, var7);
                    this.cdrom_testunitready(var1);
                } else if (var7[1] == 17) {
                    this.cdrom_testunitready(var1);
                    this.cdrom_startstopunit(var1);
                } else {
                    this.cdrom_testunitready(var1);
                }

                var13 = -1;
            }
        } while (var13 < 0 && var12++ < this.retrycount);

        if (var12 >= this.retrycount) {
            D.println(0, "RETRIES FAILED ! ");
        }

    }

    public void run() {
        int var3 = 0;
        long var5 = 0L;
        String var9 = this.ImgFile.getText();
        boolean var11 = false;
        if (var9.equals("")) {
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
            MediaAccess var12 = new MediaAccess();
            MediaAccess var13 = new MediaAccess();
            System.out.println("Message from CreateImage");

            try {
                int var10;
                if (0 == this.drvCboxChecked && this.iscdrom) {
                    var10 = var12.open(this.fdDrive.getSelectedItem(), 1);
                    if (var10 < 0) {
                        var11 = true;
                        new VErrorDialog(this.getLocalString(8247) + " (" + var12.dio.sysError(-var10) + ")", false);
                        throw new IOException("Couldn't open cdrom " + var10);
                    }

                    this.cdrom_testunitready(var12);
                    var5 = this.cdrom_size(var12);
                    var3 = 65536;
                } else {
                    if (0 == this.drvCboxChecked) {
                        var10 = var12.open(this.fdDrive.getSelectedItem(), 1);
                        this.targetIsDevice = 1;
                        this.targetIsCdrom = 0;
                        System.out.println("CrtDev " + this.fdDrive.getSelectedItem() + " " + var10 + " " + this.targetIsDevice);
                    } else {
                        int var14 = var12.devtype(this.DriveFile.getText());
                        if (var14 == 5) {
                            this.targetIsDevice = 1;
                            this.targetIsCdrom = 1;
                        } else if (var14 == 2) {
                            this.targetIsDevice = 1;
                            this.targetIsCdrom = 0;
                        } else {
                            this.targetIsDevice = 0;
                            this.targetIsCdrom = 0;
                        }

                        var10 = var12.open(this.DriveFile.getText(), this.targetIsDevice);
                        System.out.println("CrtFile " + this.DriveFile.getText() + " " + var10 + " " + this.targetIsDevice);
                    }

                    if (1 == this.targetIsDevice) {
                        if (1 == this.targetIsCdrom) {
                            this.cdrom_testunitready(var12);
                            var5 = this.cdrom_size(var12);
                            var3 = 65536;
                        } else {
                            var5 = var12.size();
                            var3 = var12.dio.BytesPerSec * var12.dio.SecPerTrack;
                        }

                        System.out.println("CrtDev actual Dev size" + var5 + " " + var3);
                    } else {
                        var5 = var12.size();
                        var3 = (int) (var5 / 512L);
                        System.out.println("CrtFile static Dev size" + var5 + " " + var3);
                    }
                }
            } catch (IOException var21) {
                System.out.println("Exception opening media access");
            }

            if (!this.diskimage && var12.wp()) {
                new VErrorDialog(this.frame, this.getLocalString(8248) + " " + this.fdDrive.getSelectedItem() + this.getLocalString(8249));
                var11 = true;
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
                } catch (IOException var17) {
                }

            } else {
                this.setCursor(Cursor.getPredefinedCursor(3));
                long var1 = var5;
                if (var3 != 0 && var5 != 0L) {
                    try {
                        var13.open(var9, this.diskimage ? 2 : 0);
                    } catch (IOException var19) {
                        new VErrorDialog(this.frame, this.getLocalString(8251) + var9 + ".");
                    }
                } else {
                    String var22 = this.getLocalString(8250) + " " + this.getLocalString(8241);
                    new VErrorDialog(this.frame, var22);
                    var11 = true;
                    var3 = 0;
                    var1 = 0L;
                }

                long var7 = 0L;
                byte[] var23 = new byte[var3];
                boolean var15 = false;

                try {
                    while (var1 > 0L && !this.canceled) {
                        int var4 = (long) var3 < var1 ? var3 : (int) var1;
                        if (this.diskimage) {
                            if (this.iscdrom) {
                                this.cdrom_read_retry(var12, var7, var4, var23);
                            } else {
                                var12.read(var7, var4, var23);
                            }

                            var13.write(var7, var4, var23);
                        } else {
                            var13.read(var7, var4, var23);
                            var12.write(var7, var4, var23);
                        }

                        var7 += (long) var4;
                        var1 -= (long) var4;
                        if (!this.diskimage && (double) ((float) var7 / (float) var5) >= 0.95) {
                            this.progress.updateBar(0.95F);
                        } else {
                            this.progress.updateBar((float) var7 / (float) var5);
                        }
                    }
                } catch (IOException var20) {
                    var11 = true;
                    new VErrorDialog(this.frame, this.getLocalString(8252) + (this.diskimage ? this.getLocalString(8253) : this.getLocalString(8254)) + this.getLocalString(8255) + " (" + var20 + ")");
                }

                this.setCursor(Cursor.getPredefinedCursor(0));
                if (!var11) {
                    try {
                        var12.close();
                        var13.close();
                    } catch (IOException var18) {
                        D.println(0, "Closing: " + var18);
                    }

                    this.progress.updateBar((float) var7 / (float) var5);
                    if (this.diskimage) {
                        this.statLabel.setText(this.getLocalString(12560));
                    } else {
                        this.statLabel.setText(this.getLocalString(12561));
                    }

                    this.p.remove(this.create);
                    this.cancel.setLabel(this.getLocalString(12566));
                } else {
                    this.statLabel.setText(" ");
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

    public void windowClosing(WindowEvent var1) {
        this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(true);
        this.canceled = true;
        this.dispose();
    }

    public void windowActivated(WindowEvent var1) {
    }

    public void windowClosed(WindowEvent var1) {
    }

    public void windowDeactivated(WindowEvent var1) {
    }

    public void windowDeiconified(WindowEvent var1) {
    }

    public void windowIconified(WindowEvent var1) {
    }

    public void windowOpened(WindowEvent var1) {
    }
}
