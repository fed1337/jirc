package org.jirc;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class jsonparser {
    private final intgapp ParentApp;

    public jsonparser(intgapp var1) {
        this.ParentApp = var1;
    }

    public String postJSONRequest(String var1, String var2) {
        HttpURLConnection var3 = null;
        OutputStreamWriter var4 = null;
        BufferedReader var5 = null;
        StringBuffer var6 = null;
        String var7 = null;
        String var8 = null;
        String var9 = null;
        String var10 = null;
        String var11 = null;
        URL var12 = null;
        String var13 = null;
        boolean var14 = false;
        boolean var15 = false;

        try {
            System.out.println("Making JSON POST Request: " + var1);
            System.out.println("json data: " + var2);
            var8 = this.ParentApp.getCodeBase().getHost();
            int var23 = this.ParentApp.getCodeBase().getPort();
            System.out.println("chk getPort: " + var23);
            if (var23 >= 0) {
                var9 = ":" + var23;
            } else {
                var9 = "";
            }

            var11 = "https://" + var8 + var9 + "/json/" + var1;
            var10 = this.ParentApp.getParameter("RCINFO1");
            var12 = new URL(var11);
            var3 = null;
            var3 = (HttpURLConnection) var12.openConnection();
            var3.setRequestMethod("POST");
            var3.setDoOutput(true);
            var3.setDoInput(true);
            var3.setUseCaches(false);
            String var16 = "sessionKey=" + var10;
            var3.setRequestProperty("Cookie", var16);
            var3.connect();
            var4 = new OutputStreamWriter(var3.getOutputStream());
            var4.write(var2, 0, var2.getBytes().length);
            var4.flush();
            var4.close();
            int var24 = var3.getResponseCode();
            System.out.println("connect.response code =  " + var24);
            if (var24 == 200) {
                var5 = new BufferedReader(new InputStreamReader(var3.getInputStream()));
                var13 = "Success";
            } else {
                var5 = new BufferedReader(new InputStreamReader(var3.getErrorStream()));
            }

            var6 = new StringBuffer();

            while ((var7 = var5.readLine()) != null) {
                var6.append(var7 + '\n');
            }

            System.out.println("Response Message = " + var6);
            if (var13 != "Success") {
                boolean var25 = false;
                int var26 = var6.toString().indexOf("SCSI_ERR_NO_LICENSE");
                if (var26 != -1) {
                    var13 = "SCSI_ERR_NO_LICENSE";
                } else {
                    var13 = var6.toString();
                }
            }
        } catch (Exception var21) {
            String var17 = System.getProperty("line.separator");
            this.ParentApp.rcErrMessage = var21.getMessage() + "." + var17 + var17 + "Your browser session may have timed out.";
            var21.printStackTrace();
        } finally {
            var3.disconnect();
            var5 = null;
            var6 = null;
            var4 = null;
            var3 = null;
        }

        return var13;
    }

    public String getJSONRequest(String var1) {
        HttpURLConnection var2 = null;
        Object var3 = null;
        BufferedReader var4 = null;
        StringBuffer var5 = null;
        String var6 = null;
        String var7 = null;
        String var8 = null;
        String var9 = null;
        String var10 = null;
        URL var11 = null;
        String var12 = null;
        boolean var13 = false;

        try {
            var7 = this.ParentApp.getCodeBase().getHost();
            int var21 = this.ParentApp.getCodeBase().getPort();
            System.out.println("chk getPort: " + var21);
            if (var21 >= 0) {
                var8 = ":" + var21;
            } else {
                var8 = "";
            }

            var10 = "https://" + var7 + var8 + "/json/" + var1;
            var9 = this.ParentApp.getParameter("RCINFO1");
            var11 = new URL(var10);
            var2 = null;
            var2 = (HttpURLConnection) var11.openConnection();
            var2.setRequestMethod("GET");
            var2.setDoOutput(true);
            var2.setUseCaches(false);
            String var14 = "sessionKey=" + var9;
            var2.setRequestProperty("Cookie", var14);
            var2.connect();
            var4 = new BufferedReader(new InputStreamReader(var2.getInputStream()));
            var5 = new StringBuffer();

            while ((var6 = var4.readLine()) != null) {
                var5.append(var6 + '\n');
            }

            var12 = var5.toString();
        } catch (Exception var19) {
            String var15 = System.getProperty("line.separator");
            this.ParentApp.rcErrMessage = var19.getMessage() + "." + var15 + var15 + "Your browser session may have timed out.";
            var19.printStackTrace();
            var12 = null;
        } finally {
            var2.disconnect();
            var4 = null;
            var5 = null;
            var3 = null;
            var2 = null;
        }

        return var12;
    }

    public String getJSONObject(String var1, String var2) {
        String var3 = "";

        for (var1 = var1.trim(); var1.indexOf(":{") > -1; var1 = var1.substring(var1.indexOf("},") + 2)) {
            var3 = var1.substring(1, var1.indexOf(":{") - 1);
            var3 = var3.substring(var3.lastIndexOf(",") + 2);
            if (var3.compareToIgnoreCase(var2) == 0) {
                String var4 = var1.substring(var1.indexOf(":{") + 1);
                return var4.substring(0, var4.indexOf("}") + 1);
            }

            if (var1.indexOf("},") <= -1) {
                break;
            }
        }

        return null;
    }

    public int getJSONNumber(String var1, String var2) {
        var1 = var1.trim();
        var1 = var1.substring(1, var1.length() - 1);
        String[] var3 = var1.split(",");

        for (int var4 = 0; var4 < var3.length; ++var4) {
            String[] var5 = var3[var4].split(":");
            String var6 = var5[0].trim();
            var6 = var6.substring(1, var6.length() - 1);
            if (var6.compareToIgnoreCase(var2) == 0) {
                return Integer.parseInt(var5[1].trim());
            }
        }

        return 0;
    }

    public String getJSONString(String var1, String var2) {
        var1 = var1.trim();
        var1 = var1.substring(1, var1.length() - 1);
        String[] var3 = var1.split(",");

        for (int var4 = 0; var4 < var3.length; ++var4) {
            String[] var5 = var3[var4].split(":");
            String var6 = var5[0].trim();
            var6 = var6.substring(1, var6.length() - 1);
            if (var6.compareToIgnoreCase(var2) == 0) {
                return var5[1].trim().substring(1, var5[1].length() - 1);
            }
        }

        return "";
    }

    public String getJSONArray(String var1, String var2, int var3) {
        var1 = var1.trim();
        var1 = var1.substring(var1.indexOf("[") + 1);
        var1 = var1.substring(0, var1.indexOf("]") + 1);
        var1 = var1.substring(1, var1.length() - 1);
        String[] var4 = var1.split("\\},\\{");
        return "{" + var4[var3] + "}";
    }
}
