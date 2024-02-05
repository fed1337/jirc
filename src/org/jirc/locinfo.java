package org.jirc;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class locinfo {
    public static final int MENUSTR_1001 = 4097;
    public static final int MENUSTR_1002 = 4098;
    public static final int MENUSTR_1003 = 4099;
    public static final int MENUSTR_1004 = 4100;
    public static final int MENUSTR_1005 = 4101;
    public static final int MENUSTR_1006 = 4102;
    public static final int MENUSTR_1007 = 4103;
    public static final int MENUSTR_1008 = 4104;
    public static final int MENUSTR_1009 = 4105;
    public static final int MENUSTR_100A = 4106;
    public static final int MENUSTR_100B = 4107;
    public static final int MENUSTR_100C = 4108;
    public static final int MENUSTR_100D = 4109;
    public static final int MENUSTR_100E = 4110;
    public static final int MENUSTR_100F = 4111;
    public static final int MENUSTR_1010 = 4112;
    public static final int MENUSTR_1011 = 4113;
    public static final int MENUSTR_1012 = 4114;
    public static final int MENUSTR_1013 = 4115;
    public static final int MENUSTR_1014 = 4116;
    public static final int MENUSTR_1015 = 4117;
    public static final int MENUSTR_1016 = 4118;
    public static final int MENUSTR_1017 = 4119;
    public static final int MENUSTR_1018 = 4120;
    public static final int MENUSTR_1019 = 4121;
    public static final int MENUSTR_101A = 4122;
    public static final int MENUSTR_101B = 4123;
    public static final int MENUSTR_101C = 4124;
    public static final int MENUSTR_101D = 4125;
    public static final int MENUSTR_101E = 4126;
    public static final int MENUSTR_101F = 4127;
    public static final int MENUSTR_1020 = 4128;
    public static final int MENUSTR_1021 = 4129;
    public static final int MENUSTR_1022 = 4130;
    public static final int MENUSTR_1023 = 4131;
    public static final int MENUSTR_1024 = 4132;
    public static final int MENUSTR_1025 = 4133;
    public static final int MENUSTR_1026 = 4134;
    public static final int MENUSTR_1027 = 4135;
    public static final int MENUSTR_1028 = 4136;
    public static final int MENUSTR_1029 = 4137;
    public static final int DIALOGSTR_2001 = 8193;
    public static final int DIALOGSTR_2002 = 8194;
    public static final int DIALOGSTR_2003 = 8195;
    public static final int DIALOGSTR_2004 = 8196;
    public static final int DIALOGSTR_2005 = 8197;
    public static final int DIALOGSTR_2006 = 8198;
    public static final int DIALOGSTR_2007 = 8199;
    public static final int DIALOGSTR_2008 = 8200;
    public static final int DIALOGSTR_2009 = 8201;
    public static final int DIALOGSTR_200a = 8202;
    public static final int DIALOGSTR_200b = 8203;
    public static final int DIALOGSTR_200c = 8204;
    public static final int DIALOGSTR_200d = 8205;
    public static final int DIALOGSTR_200e = 8206;
    public static final int DIALOGSTR_200f = 8207;
    public static final int DIALOGSTR_2010 = 8208;
    public static final int DIALOGSTR_2011 = 8209;
    public static final int DIALOGSTR_2012 = 8210;
    public static final int DIALOGSTR_2013 = 8211;
    public static final int DIALOGSTR_2014 = 8212;
    public static final int DIALOGSTR_2015 = 8213;
    public static final int DIALOGSTR_2016 = 8214;
    public static final int DIALOGSTR_2017 = 8215;
    public static final int DIALOGSTR_2021 = 8225;
    public static final int DIALOGSTR_2022 = 8226;
    public static final int DIALOGSTR_2023 = 8227;
    public static final int DIALOGSTR_2024 = 8228;
    public static final int DIALOGSTR_2025 = 8229;
    public static final int DIALOGSTR_2026 = 8230;
    public static final int DIALOGSTR_2027 = 8231;
    public static final int DIALOGSTR_2028 = 8232;
    public static final int DIALOGSTR_2029 = 8233;
    public static final int DIALOGSTR_202a = 8234;
    public static final int DIALOGSTR_202b = 8235;
    public static final int DIALOGSTR_202c = 8236;
    public static final int DIALOGSTR_202d = 8237;
    public static final int DIALOGSTR_202e = 8238;
    public static final int DIALOGSTR_202f = 8239;
    public static final int DIALOGSTR_2030 = 8240;
    public static final int DIALOGSTR_2031 = 8241;
    public static final int DIALOGSTR_2032 = 8242;
    public static final int DIALOGSTR_2033 = 8243;
    public static final int DIALOGSTR_2034 = 8244;
    public static final int DIALOGSTR_2035 = 8245;
    public static final int DIALOGSTR_2036 = 8246;
    public static final int DIALOGSTR_2037 = 8247;
    public static final int DIALOGSTR_2038 = 8248;
    public static final int DIALOGSTR_2039 = 8249;
    public static final int DIALOGSTR_203a = 8250;
    public static final int DIALOGSTR_203b = 8251;
    public static final int DIALOGSTR_203c = 8252;
    public static final int DIALOGSTR_203d = 8253;
    public static final int DIALOGSTR_203e = 8254;
    public static final int DIALOGSTR_203f = 8255;
    public static final int DIALOGSTR_2040 = 8256;
    public static final int DIALOGSTR_2041 = 8257;
    public static final int DIALOGSTR_2042 = 8258;
    public static final int DIALOGSTR_2043 = 8259;
    public static final int DIALOGSTR_2044 = 8260;
    public static final int DIALOGSTR_2045 = 8261;
    public static final int DIALOGSTR_2046 = 8262;
    public static final int DIALOGSTR_2047 = 8263;
    public static final int DIALOGSTR_2048 = 8264;
    public static final int DIALOGSTR_2049 = 8265;
    public static final int DIALOGSTR_205a = 8282;
    public static final int DIALOGSTR_205b = 8283;
    public static final int DIALOGSTR_205c = 8284;
    public static final int DIALOGSTR_205d = 8285;
    public static final int DIALOGSTR_205e = 8286;
    public static final int DIALOGSTR_205f = 8287;
    public static final int DIALOGSTR_2060 = 8288;
    public static final int DIALOGSTR_2061 = 8289;
    public static final int DIALOGSTR_2062 = 8290;
    public static final int DIALOGSTR_2063 = 8291;
    public static final int DIALOGSTR_2064 = 8292;
    public static final int DIALOGSTR_2065 = 8293;
    public static final int DIALOGSTR_2066 = 8294;
    public static final int DIALOGSTR_2067 = 8295;
    public static final int DIALOGSTR_2068 = 8296;
    public static final int DIALOGSTR_2069 = 8297;
    public static final int DIALOGSTR_206a = 8298;
    public static final int DIALOGSTR_206b = 8299;
    public static final int DIALOGSTR_206c = 8300;
    public static final int STATUSSTR_3001 = 12289;
    public static final int STATUSSTR_3002 = 12290;
    public static final int STATUSSTR_3003 = 12291;
    public static final int STATUSSTR_3004 = 12292;
    public static final int STATUSSTR_3005 = 12293;
    public static final int STATUSSTR_3006 = 12294;
    public static final int STATUSSTR_3007 = 12295;
    public static final int STATUSSTR_3008 = 12296;
    public static final int STATUSSTR_3009 = 12297;
    public static final int STATUSSTR_300a = 12298;
    public static final int STATUSSTR_300b = 12299;
    public static final int STATUSSTR_300c = 12300;
    public static final int STATUSSTR_300d = 12301;
    public static final int STATUSSTR_300e = 12302;
    public static final int STATUSSTR_300f = 12303;
    public static final int STATUSSTR_3010 = 12304;
    public static final int STATUSSTR_3011 = 12305;
    public static final int STATUSSTR_3012 = 12306;
    public static final int STATUSSTR_3013 = 12307;
    public static final int STATUSSTR_3014 = 12308;
    public static final int STATUSSTR_3100 = 12544;
    public static final int STATUSSTR_3101 = 12545;
    public static final int STATUSSTR_3102 = 12546;
    public static final int STATUSSTR_3103 = 12547;
    public static final int STATUSSTR_3104 = 12548;
    public static final int STATUSSTR_3105 = 12549;
    public static final int STATUSSTR_3106 = 12550;
    public static final int STATUSSTR_3107 = 12551;
    public static final int STATUSSTR_3108 = 12552;
    public static final int STATUSSTR_3109 = 12553;
    public static final int STATUSSTR_310a = 12554;
    public static final int STATUSSTR_310b = 12555;
    public static final int STATUSSTR_310c = 12556;
    public static final int STATUSSTR_310d = 12557;
    public static final int STATUSSTR_310e = 12558;
    public static final int STATUSSTR_310f = 12559;
    public static final int STATUSSTR_3110 = 12560;
    public static final int STATUSSTR_3111 = 12561;
    public static final int STATUSSTR_3112 = 12562;
    public static final int STATUSSTR_3113 = 12563;
    public static final int STATUSSTR_3114 = 12564;
    public static final int STATUSSTR_3115 = 12565;
    public static final int STATUSSTR_3116 = 12566;
    public static final int STATUSSTR_3117 = 12567;
    public static final int STATUSSTR_3118 = 12568;
    public static final int STATUSSTR_3119 = 12569;
    public static final int STATUSSTR_3120 = 12576;
    public static final int STATUSSTR_3121 = 12577;
    public static final int STATUSSTR_3122 = 12578;
    public static final int STATUSSTR_3123 = 12579;
    public static final int STATUSSTR_3124 = 12580;
    public static final int STATUSSTR_3125 = 12581;
    public static final int STATUSSTR_3126 = 12582;
    public static final int TOOLSTR_4001 = 16385;
    public static final int TOOLSTR_4002 = 16386;
    public static final int TOOLSTR_4003 = 16387;
    public static final int TOOLSTR_4004 = 16388;
    public static int UID;
    private final intgapp ParentApp;
    public String rcErrMessage = "";
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document document;
    private File file;
    private String localLocStrFile;
    private String lstrVersion = "0001";

    public locinfo(intgapp var1) {
        this.ParentApp = var1;
        this.dbf = null;
        this.db = null;
        this.document = null;
        this.file = null;
        this.lstrVersion = "0001";
        this.rcErrMessage = "";
        this.localLocStrFile = "";
    }

    public boolean retrieveLocStrings(boolean var1) {
        HttpURLConnection var2 = null;
        HttpsURLConnection var3 = null;
        String var4 = null;
        String var5 = null;
        String var6 = null;
        Object var7 = null;
        String var8 = null;
        URL var9 = null;
        boolean var10 = false;
        String var12 = System.getProperty("java.io.tmpdir");
        String var13 = System.getProperty("os.name").toLowerCase();
        String var14 = System.getProperty("file.separator");
        boolean var15 = false;
        String var16 = "com/hp/ilo2/intgapp/";
        String var17 = "jirc_strings";
        String var18 = ".xml";
        String var19 = this.ParentApp.getParameter("RCINFOLANG");
        String var20 = null;
        if (UID == 0) {
            UID = this.hashCode();
        }

        String var21 = Integer.toHexString(UID);
        if (null != var19 && !var19.equalsIgnoreCase("")) {
            System.out.println("langStr received:" + var19);
            var20 = "lang/" + var19 + "/jirc_strings.xml";
            System.out.println("lolcalized xml file shoudl be:" + var20);
        } else {
            var1 = false;
        }

        if (var12 == null) {
            var12 = var13.startsWith("windows") ? "C:\\TEMP" : "/tmp";
        }

        File var22 = new File(var12);
        if (!var22.exists()) {
            var22.mkdir();
        }

        if (!var12.endsWith(var14)) {
            var12 = var12 + var14;
        }

        var12 = var12 + var17 + var21 + var18;
        this.localLocStrFile = var12;
        File var23 = new File(var12);
        if (var23.exists()) {
            System.out.println(this.localLocStrFile + " already exists.");
            var15 = true;
            return var15;
        } else {
            byte[] var24 = new byte[4096];
            System.out.println("Creating" + this.localLocStrFile + "...");
            int var11;
            String var26;
            if (null != var20 && var1) {
                try {
                    System.out.println("try localize file from webserver..");
                    var4 = this.ParentApp.getCodeBase().getProtocol();
                    var5 = this.ParentApp.getCodeBase().getHost();
                    int var35 = this.ParentApp.getCodeBase().getPort();
                    if (var35 >= 0) {
                        var6 = ":" + var35;
                    } else {
                        var6 = "";
                    }

                    var8 = var4 + "://" + var5 + var6 + "/" + var20;
                    System.out.println("trying to retreive webser localize file:" + var8);
                    var9 = new URL(var8);
                    InputStream var25;
                    if (var4.equals("http")) {
                        var2 = null;
                        var2 = (HttpURLConnection) var9.openConnection();
                        var2.setRequestMethod("GET");
                        var2.setDoOutput(true);
                        var2.setUseCaches(false);
                        var2.connect();
                        var25 = var2.getInputStream();
                    } else {
                        var3 = null;
                        var3 = (HttpsURLConnection) var9.openConnection();
                        var3.setRequestMethod("GET");
                        var3.setDoOutput(true);
                        var3.setUseCaches(false);
                        var3.connect();
                        var25 = var3.getInputStream();
                    }

                    FileOutputStream var37 = new FileOutputStream(this.localLocStrFile);

                    while ((var11 = var25.read(var24, 0, 4096)) != -1) {
                        var37.write(var24, 0, var11);
                    }

                    System.out.println("Write xml to" + this.localLocStrFile + "complete");
                    var25.close();
                    var37.close();
                    var15 = true;
                    System.out.println("Message after comp of webserver retrieval");
                } catch (Exception var33) {
                    var26 = System.getProperty("line.separator");
                    this.rcErrMessage = var33.getMessage() + "." + var26 + var26 + "Your browser session may have timed out.";
                    var33.printStackTrace();
                } finally {
                    if (var4.equals("http")) {
                        var2.disconnect();
                        var2 = null;
                    } else {
                        var3.disconnect();
                        var3 = null;
                    }

                }
            }

            if (!var15 || !var1) {
                System.out.println("try localize file from applet..");
                ClassLoader var36 = this.getClass().getClassLoader();
                var26 = var16 + var17 + var18;

                try {
                    InputStream var27 = var36.getResourceAsStream(var26);
                    FileOutputStream var28 = new FileOutputStream(this.localLocStrFile);

                    while ((var11 = var27.read(var24, 0, 4096)) != -1) {
                        var28.write(var24, 0, var11);
                    }

                    var27.close();
                    var28.close();
                    var15 = true;
                    System.out.println("Message after default xml initialization");
                } catch (IOException var32) {
                    System.out.println("xmlExtract: " + var32);
                    this.rcErrMessage = var32.getMessage();
                    var32.printStackTrace();
                }
            }

            return var15;
        }
    }

    public boolean initLocStringsDefault() {
        boolean var1 = false;
        byte var2 = 0;

        try {
            System.out.println("Message from beginning of initLocStringsDefault" + this.localLocStrFile);
            var1 = this.retrieveLocStrings(false);
            if (!var1) {
                var2 = 2;
            } else {
                this.file = new File(this.localLocStrFile);
                if (null == this.file) {
                    var2 = 3;
                } else {
                    this.dbf = DocumentBuilderFactory.newInstance();
                    if (null == this.dbf) {
                        var2 = 4;
                    } else {
                        this.db = this.dbf.newDocumentBuilder();
                        if (null == this.db) {
                            var2 = 5;
                        } else {
                            this.document = this.db.parse(this.file);
                            if (null == this.document) {
                                var2 = 6;
                            } else {
                                this.document.getDocumentElement().normalize();
                                var1 = true;
                                System.out.println("Message after completion of initLocStringsDefault");
                            }
                        }
                    }
                }
            }
        } catch (Exception var5) {
            String var4 = System.getProperty("line.separator");
            this.rcErrMessage = var5.getMessage() + "." + var4 + var4 + "Could not Parse the localization strings.";
            var5.printStackTrace();
        }

        if (!var1) {
            System.out.println("initLocStringsDefault:Error Parsing Xml file:%d" + var2);
        }

        return var1;
    }

    public boolean initLocStrings() {
        boolean var1 = false;
        String var2 = null;
        byte var3 = 0;

        try {
            System.out.println("Message from beginning of initLocStrings" + this.localLocStrFile);
            if (null != this.document) {
                var3 = 1;
            } else {
                var1 = this.retrieveLocStrings(true);
                if (!var1) {
                    var3 = 2;
                } else {
                    this.file = new File(this.localLocStrFile);
                    var1 = false;
                    if (null == this.file) {
                        var3 = 3;
                    } else {
                        this.dbf = DocumentBuilderFactory.newInstance();
                        if (null == this.dbf) {
                            var3 = 4;
                        } else {
                            label39:
                            {
                                try {
                                    var2 = "http://xml.org/sax/features/external-general-entities";
                                    this.dbf.setFeature(var2, false);
                                    var2 = "http://xml.org/sax/features/external-parameter-entities";
                                    this.dbf.setFeature(var2, false);
                                    var2 = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
                                    this.dbf.setFeature(var2, false);
                                    this.dbf.setXIncludeAware(false);
                                    this.dbf.setExpandEntityReferences(false);
                                } catch (ParserConfigurationException var6) {
                                    System.out.println("ParserConfigurationException was thrown. The feature '" + var2 + "' is probably not supported by XML parser.");
                                    break label39;
                                }

                                this.db = this.dbf.newDocumentBuilder();
                                if (null == this.db) {
                                    var3 = 5;
                                } else {
                                    this.document = this.db.parse(this.file);
                                    if (null == this.document) {
                                        var3 = 6;
                                    } else {
                                        this.document.getDocumentElement().normalize();
                                        var1 = true;
                                        System.out.println("Message after completion of initLocStrings");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception var7) {
            String var5 = System.getProperty("line.separator");
            this.rcErrMessage = var7.getMessage() + "." + var5 + var5 + "Could not Parse the localization strings.";
            var7.printStackTrace();
        }

        if (!var1) {
            var1 = this.initLocStringsDefault();
        }

        if (!var1) {
            System.out.println("Error Parsing Xml file:%d" + var3);
        }

        return var1;
    }

    public String getLocString(int var1) {
        boolean var2 = false;
        String var3 = "ID_" + Integer.toHexString(var1);
        String var4 = "";
        String var5 = "";
        String var6 = "";
        byte var7 = 0;

        try {
            if (null == this.document) {
                var7 = 1;
            } else {
                Element var10 = this.document.getElementById(var3);
                if (null == var10) {
                    var7 = 2;
                } else {
                    NodeList var11 = var10.getChildNodes();
                    if (null == var11) {
                        var7 = 3;
                    } else {
                        var4 = var11.item(0).getNodeValue();
                        if (null == var4) {
                            var7 = 4;
                        } else {
                            var2 = true;
                        }
                    }
                }
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        if (!var2) {
            var4 = "LS_NF";
            System.out.println("LSFNound:" + var3 + "rval:" + var7);
        }

        int var8 = var4.indexOf(35);
        if (var8 >= 0) {
            var5 = var4.substring(0, var8);
            int var9 = var4.indexOf(35, var8 + 1);
            var6 = var4.substring(var8 + 1, var9);
            var5 = var5 + this.ParentApp.rebrandToken(var6);
            var5 = var5 + var4.substring(var9 + 1);
            return var5;
        } else {
            return var4;
        }
    }

    public void dumpLocStrings() {
        try {
            NodeList var1 = this.document.getElementsByTagName("javaIRC");

            for (int var2 = 0; var2 < var1.getLength(); ++var2) {
                Node var3 = var1.item(var2);
                if (var3.getNodeType() == 1) {
                    Element var4 = (Element) var3;
                    NodeList var5 = var4.getElementsByTagName("menu");

                    for (int var6 = 0; var6 < var5.getLength(); ++var6) {
                        Element var7 = (Element) var5.item(var6);
                        NodeList var8 = var7.getChildNodes();
                    }

                    NodeList var16 = var4.getElementsByTagName("dialog");

                    for (int var17 = 0; var17 < var16.getLength(); ++var17) {
                        Element var9 = (Element) var16.item(var17);
                        NodeList var10 = var9.getChildNodes();
                    }

                    NodeList var18 = var4.getElementsByTagName("status");

                    for (int var19 = 0; var19 < var18.getLength(); ++var19) {
                        Element var11 = (Element) var18.item(var19);
                        NodeList var12 = var11.getChildNodes();
                    }

                    NodeList var20 = var4.getElementsByTagName("tooltip");

                    for (int var21 = 0; var21 < var20.getLength(); ++var21) {
                        Element var13 = (Element) var20.item(var21);
                        NodeList var14 = var13.getChildNodes();
                    }
                }
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        }

    }
}
