package org.jirc;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class jsonparser {
    private final App ParentApp;

    public jsonparser(final App var1) {
        super();
        this.ParentApp = var1;
    }

    public String postJSONRequest(final String var1, final String var2) {
        HttpURLConnection var3 = null;
        final OutputStreamWriter var4;
        final BufferedReader var5;
        final StringBuilder var6;
        String var7;
        final String var8;
        final String var9;
        final String var10;
        final String var11;
        final URL var12;
        String var13 = null;

        try {
            System.out.println("Making JSON POST Request: " + var1);
            System.out.println("json data: " + var2);
            var8 = this.ParentApp.getCodeBase().getHost();
            final int var23 = this.ParentApp.getCodeBase().getPort();
            System.out.println("chk getPort: " + var23);
            if (0 <= var23) {
                var9 = ":" + var23;
            } else {
                var9 = "";
            }

            var11 = "https://" + var8 + var9 + "/json/" + var1;
            var10 = this.ParentApp.getParameter("RCINFO1");
            var12 = URI.create(var11).toURL();
            var3 = (HttpURLConnection) var12.openConnection();
            IloSsl.apply(var3);
            var3.setRequestMethod("POST");
            var3.setDoOutput(true);
            var3.setDoInput(true);
            var3.setUseCaches(false);
            final String var16 = "sessionKey=" + var10;
            var3.setRequestProperty("Cookie", var16);
            var3.connect();
            var4 = new OutputStreamWriter(var3.getOutputStream());
            var4.write(var2, 0, var2.getBytes().length);
            var4.flush();
            var4.close();
            final int var24 = var3.getResponseCode();
            System.out.println("connect.response code =  " + var24);
            if (200 == var24) {
                var5 = new BufferedReader(new InputStreamReader(var3.getInputStream()));
                var13 = "Success";
            } else {
                var5 = new BufferedReader(new InputStreamReader(var3.getErrorStream()));
            }

            var6 = new StringBuilder();

            while (null != (var7 = var5.readLine())) {
                var6.append(var7 + '\n');
            }

            System.out.println("Response Message = " + var6);
            if (!"Success".equals(var13)) {
                final int var26 = var6.toString().indexOf("SCSI_ERR_NO_LICENSE");
                if (-1 == var26) {
                    var13 = var6.toString();
                } else {
                    var13 = "SCSI_ERR_NO_LICENSE";
                }
            }
        } catch (final Exception var21) {
            final String var17 = System.lineSeparator();
            this.ParentApp.rcErrMessage = var21.getMessage() + "." + var17 + var17 + "Your browser session may have timed out.";
            var21.printStackTrace();
        } finally {
            if (null != var3) {
                var3.disconnect();
            }
        }

        return var13;
    }

    public String getJSONRequest(final String var1) {
        HttpURLConnection var2 = null;
        final BufferedReader var4;
        final StringBuilder var5;
        String var6;
        final String var7;
        final String var8;
        final String var9;
        final String var10;
        final URL var11;
        String var12;

        try {
            var7 = this.ParentApp.getCodeBase().getHost();
            final int var21 = this.ParentApp.getCodeBase().getPort();
            System.out.println("chk getPort: " + var21);
            if (0 <= var21) {
                var8 = ":" + var21;
            } else {
                var8 = "";
            }

            var10 = "https://" + var7 + var8 + "/json/" + var1;
            var9 = this.ParentApp.getParameter("RCINFO1");
            var11 = URI.create(var10).toURL();
            var2 = (HttpURLConnection) var11.openConnection();
            IloSsl.apply(var2);
            var2.setRequestMethod("GET");
            var2.setDoOutput(true);
            var2.setUseCaches(false);
            final String var14 = "sessionKey=" + var9;
            var2.setRequestProperty("Cookie", var14);
            var2.connect();
            var4 = new BufferedReader(new InputStreamReader(var2.getInputStream()));
            var5 = new StringBuilder();

            while (null != (var6 = var4.readLine())) {
                var5.append(var6 + '\n');
            }

            var12 = var5.toString();
        } catch (final Exception var19) {
            final String var15 = System.lineSeparator();
            this.ParentApp.rcErrMessage = var19.getMessage() + "." + var15 + var15 + "Your browser session may have timed out.";
            var19.printStackTrace();
            var12 = null;
        } finally {
            if (null != var2) {
                var2.disconnect();
            }
        }

        return var12;
    }

    public static String getJSONObject(String var1, final String var2) {
        String var3;

        for (var1 = var1.trim(); var1.contains(":{"); var1 = var1.substring(var1.indexOf("},") + 2)) {
            var3 = var1.substring(1, var1.indexOf(":{") - 1);
            var3 = var3.substring(var3.lastIndexOf(",") + 2);
            if (0 == var3.compareToIgnoreCase(var2)) {
                final String var4 = var1.substring(var1.indexOf(":{") + 1);
                return var4.substring(0, var4.indexOf("}") + 1);
            }

            if (!var1.contains("},")) {
                break;
            }
        }

        return null;
    }

    public static int getJSONNumber(String var1, final String var2) {
        var1 = var1.trim();
        var1 = var1.substring(1, var1.length() - 1);
        final String[] var3 = var1.split(",");

        for (final String s : var3) {
            final String[] var5 = s.split(":");
            String var6 = var5[0].trim();
            var6 = var6.substring(1, var6.length() - 1);
            if (0 == var6.compareToIgnoreCase(var2)) {
                return Integer.parseInt(var5[1].trim());
            }
        }

        return 0;
    }

    public static String getJSONString(String var1, final String var2) {
        var1 = var1.trim();
        var1 = var1.substring(1, var1.length() - 1);
        final String[] var3 = var1.split(",");

        for (final String s : var3) {
            final String[] var5 = s.split(":");
            String var6 = var5[0].trim();
            var6 = var6.substring(1, var6.length() - 1);
            if (0 == var6.compareToIgnoreCase(var2)) {
                return var5[1].trim().substring(1, var5[1].length() - 1);
            }
        }

        return "";
    }

    public static String getJSONArray(String var1, final String var2, final int var3) {
        var1 = var1.trim();
        var1 = var1.substring(var1.indexOf("[") + 1);
        var1 = var1.substring(0, var1.indexOf("]") + 1);
        var1 = var1.substring(1, var1.length() - 1);
        final String[] var4 = var1.split("\\},\\{");
        return "{" + var4[var3] + "}";
    }
}
