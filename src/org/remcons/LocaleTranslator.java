package org.remcons;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

class LocaleTranslator {
    public boolean showgui = false;
    public boolean windows = true;
    Hashtable locales = new Hashtable();
    Hashtable aliases = new Hashtable();
    Hashtable selected;
    Hashtable reverse_alias = new Hashtable();
    String selected_name;
    String euro1 = " €\u001b[+4";
    String euro2 = " €\u001b[+e";
    String belgian = "\u0001\u0011 \u0011\u0001 \u0017\u001a \u001a\u0017 !8 \"3 #\u001b[+3 $] %\" &1 '4 (5 )- *} +? ,m -= .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð =/ >ñ ?M @\u001b[+2 AQ M: QA WZ ZW [\u001b[+[ \\\u001b[+ð ]\u001b[+] ^[  _+ `\u001b[+\\  aq m; qa wz zw {\u001b[+9 |\u001b[+1 }\u001b[+0 ~\u001b[+/  £| §6 ¨{  °_ ²` ³~ ´\u001b[+'  µ\\ À\u001b[+\\Q Á\u001b[+'Q Â[Q Ã\u001b[+/Q Ä{Q È\u001b[+\\E É\u001b[+'E Ê[E Ë{E Ì\u001b[+\\I Í\u001b[+'I Î[I Ï{I Ñ\u001b[+/N Ò\u001b[+\\O Ó\u001b[+'O Ô[O Õ\u001b[+/O Ö{O Ù\u001b[+\\U Ú\u001b[+'U Û[U Ü{U Ý\u001b[+'Y à\u001b[+\\q á\u001b[+'q â[q ã\u001b[+/q ä{q ç9 è\u001b[+\\e é\u001b[+'e ê[e ë{e ì\u001b[+\\i í\u001b[+'i î[i ï{i ñ\u001b[+/n ò\u001b[+\\o ó\u001b[+'o ô[o õ\u001b[+/o ö{o ù\u001b[+\\u ú\u001b[+'u û[u ü{u ý\u001b[+'y ÿ{y";
    String british = "\"@ #\\ @\" \\ð |ñ ~| £# ¦\u001b[+` ¬~ Á\u001b[+A á\u001b[+a É\u001b[+E é\u001b[+e Í\u001b[+I í\u001b[+i Ó\u001b[+O ó\u001b[+o Ú\u001b[+U ú\u001b[+u";
    String danish = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\\u001b[+ð ]\u001b[+9 ^}  _? `+  {\u001b[+7 |\u001b[+= }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §~ ¨]  ´=  ½` À+A Á=A Â}A Ã\u001b[+]A Ä]A Å{ Æ: È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó=O Ô}O Õ\u001b[+]O Ö]O Ø\" Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\u001b[+]a ä]a å[ æ; è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\u001b[+]n ò+o ó=o ô}o õ\u001b[+]o ö]o ø' ù+u ú=u û}u ü]u ý=y ÿ]y";
    String finnish = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\\u001b[+- ]\u001b[+9 ^}  _? `+  {\u001b[+7 |\u001b[+ð }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\u001b[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó=O Ô}O Õ\u001b[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\u001b[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\u001b[+]n ò+o ó=o ô}o õ\u001b[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
    String french = "\u0001\u0011 \u0011\u0001 \u0017\u001a \u001a\u0017 !/ \"3 #\u001b[+3 $] %\" &1 '4 (5 )- *\\ ,m -6 .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð >ñ ?M @\u001b[+0 AQ M: QA WZ ZW [\u001b[+5 \\\u001b[+8 ]\u001b[+- ^\u001b[+9 _8 `\u001b[+7 aq m; qa wz zw {\u001b[+4 |\u001b[+6 }\u001b[+= ~\u001b[+2 £} ¤\u001b[+] §? ¨{  °_ ²` µ| Â[Q Ä{Q Ê[E Ë{E Î[I Ï{I Ô[O Ö{O Û[U Ü{U à0 â[q ä{q ç9 è7 é2 ê[e ë{e î[i ï{i ô[o ö{o ù' û[u ü{u ÿ{y";
    String french_canadian = "\"@ #` '< /# <\\ >| ?^ @\u001b[+2 [\u001b[+[ \\\u001b[+` ]\u001b[+] ^[  `'  {\u001b[+' |~ }\u001b[+\\ ~\u001b[+; ¢\u001b[+4 £\u001b[+3 ¤\u001b[+5 ¦\u001b[+7 §\u001b[+o ¨}  «ð ¬\u001b[+6 \u00ad\u001b[+. ¯\u001b[+, °\u001b[+ð ±\u001b[+1 ²\u001b[+8 ³\u001b[+9 ´\u001b[+/  µ\u001b[+m ¶\u001b[+p ¸]  »ñ ¼\u001b[+0 ½\u001b[+- ¾\u001b[+= À'A Á\u001b[+/A Â[A Ä}A Ç]C È'E É? Ê[E Ë}E Ì'I Í\u001b[+/I Î[I Ï}I Ò'O Ó\u001b[+/O Ô[O Ö}O Ù'U Ú\u001b[+/U Û[U Ü}U Ý\u001b[+/Y à'a á\u001b[+/a â[a ä}a ç]c è'e é\u001b[+/e ê[e ë}e ì'i í\u001b[+/i î[i ï}i ò'o ó\u001b[+/o ô[o ö}o ù'u ú\u001b[+/u û[u ü}u ý\u001b[+/y ÿ}y";
    String german = "\u0019\u001a \u001a\u0019 \"@ #\\ &^ '| (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+q YZ ZY [\u001b[+8 \\\u001b[+- ]\u001b[+9 ^`  _? `+  yz zy {\u001b[+7 |\u001b[+ð }\u001b[+0 ~\u001b[+] §# °~ ²\u001b[+2 ³\u001b[+3 ´=  µ\u001b[+m À+A Á=A Â`A Ä\" È+E É=E Ê`E Ì+I Í=I Î`I Ò+O Ó=O Ô`O Ö: Ù+U Ú=U Û`U Ü{ Ý=Z ß- à+a á=a â`a ä' è+e é=e ê`e ì+i í=i î`i ò+o ó=o ô`o ö; ù+u ú=u û`u ü[ ý=z";
    String italian = "\"@ #\u001b[+' &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+; [\u001b[+[ \\` ]\u001b[+] ^+ _? |~ £# §| °\" à' ç: è[ é{ ì= ò; ù\\";
    String japanese = "\"@ &^ '& (* )( *\" +: :' =_ @[ [] \\ò ]\\ ^= _ó `{ {} ¥ô |õ }| ~+";
    String latin_american = "\"@ &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+q [\" \\\u001b[+- ]| ^\u001b[+'  _? `\u001b[+\\  {' |` }\\ ~\u001b[+] ¡+ ¨{  ¬\u001b[+` °~ ´[  ¿= À\u001b[+\\A Á[A Â\u001b[+'A Ä{A È\u001b[+\\E É[E Ê\u001b[+'E Ë{E Ì\u001b[+\\I Í[I Î\u001b[+'I Ï{I Ñ: Ò\u001b[+\\O Ó[O Ô\u001b[+'O Ö{O Ù\u001b[+\\U Ú[U Û\u001b[+'U Ü{U Ý[Y à\u001b[+\\a á[a â\u001b[+'a ä{a è\u001b[+\\e é[e ê\u001b[+'e ë{e ì\u001b[+\\i í[i î\u001b[+'i ï{i ñ; ò\u001b[+\\o ó[o ô\u001b[+'o ö{o ù\u001b[+\\u ú[u û\u001b[+'u ü{u ý[y ÿ{y";
    String norwegian = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\= ]\u001b[+9 ^}  _? `+  {\u001b[+7 |` }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §~ ¨]  ´\u001b[+=  À+A Á\u001b[+=A Â}A Ã\u001b[+]A Ä]A Å{ Æ\" È+E É\u001b[+=E Ê}E Ë]E Ì+I Í\u001b[+=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó\u001b[+=O Ô}O Õ\u001b[+]O Ö]O Ø: Ù+U Ú\u001b[+=U Û}U Ü]U Ý\u001b[+=Y à+a á\u001b[+=a â}a ã\u001b[+]a ä]a å[ æ' è+e é\u001b[+=e ê}e ë]e ì+i í\u001b[+=i î}i ï]i ñ\u001b[+]n ò+o ó\u001b[+=o ô}o õ\u001b[+]o ö]o ø; ù+u ú\u001b[+=u û}u ü]u ý\u001b[+=y ÿ]y";
    String portuguese = "\"@ &^ '- (* )( *{ +[ -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\` ]\u001b[+9 ^|  _? `}  {\u001b[+7 |~ }\u001b[+0 ~\\  £\u001b[+3 §\u001b[+4 ¨\u001b[+[  ª\" «= ´]  º' »+ À}A Á]A Â|A Ã\\A Ä\u001b[+[A Ç: È}E É]E Ê|E Ë\u001b[+[E Ì}I Í]I Î|I Ï\u001b[+[I Ñ\\N Ò}O Ó]O Ô|O Õ\\O Ö\u001b[+[O Ù}U Ú]U Û|U Ü\u001b[+[U Ý]Y à}a á]a â|a ã\\a ä\u001b[+[a ç; è}e é]e ê|e ë\u001b[+[e ì}i í]i î|i ï\u001b[+[i ñ\\n ò}o ó]o ô|o õ\\o ö\u001b[+[o ù}u ú]u û|u ü\u001b[+[u ý]y ÿ\u001b[+[y";
    String spanish = "\"@ #\u001b[+3 &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+[ \\\u001b[+` ]\u001b[+] ^{  _? `[  {\u001b[+' |\u001b[+1 }\u001b[+\\ ¡= ¨\"  ª~ ¬\u001b[+6 ´'  ·# º` ¿+ À[A Á'A Â{A Ä\"A Ç| È[E É'E Ê{E Ë\"E Ì[I Í'I Î{I Ï\"I Ñ: Ò[O Ó'O Ô{O Ö\"O Ù[U Ú'U Û{U Ü\"U Ý'Y à[a á'a â{a ä\"a ç\\ è[e é'e ê{e ë\"e ì[i í'i î{i ï\"i ñ; ò[o ó'o ô{o ö\"o ù[u ú'u û{u ü\"u ý'y ÿ\"y";
    String swedish = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\\u001b[+- ]\u001b[+9 ^}  _? `+  {\u001b[+7 |\u001b[+ð }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\u001b[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó=O Ô}O Õ\u001b[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\u001b[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\u001b[+]n ò+o ó=o ô}o õ\u001b[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
    String swiss_french = "\u0019\u001a \u001a\u0019 !} \"@ #\u001b[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 YZ ZY [\u001b[+[ \\\u001b[+ð ]\u001b[+] ^=  _? `+  yz zy {\u001b[+' |\u001b[+7 }\u001b[+\\ ~\u001b[+=  ¢\u001b[+8 £| ¦\u001b[+1 §` ¨]  ¬\u001b[+6 °~ ´\u001b[+-  À+A Á\u001b[+-A Â=A Ã\u001b[+=A Ä]A È+E É\u001b[+-E Ê=E Ë]E Ì+I Í\u001b[+-I Î=I Ï]I Ñ\u001b[+=N Ò+O Ó\u001b[+-O Ô=O Õ\u001b[+=O Ö]O Ù+U Ú\u001b[+-U Û=U Ü]U Ý\u001b[+-Z à+a á\u001b[+-a â=a ã\u001b[+=a ä]a ç$ è+e é\u001b[+-e ê=e ë]e ì+i í\u001b[+-i î=i ï]i ñ\u001b[+=n ò+o ó\u001b[+-o ô=o õ\u001b[+=o ö]o ù+u ú\u001b[+-u û=u ü]u ý\u001b[+-z ÿ]z";
    String swiss_german = "\u0019\u001a \u001a\u0019 !} \"@ #\u001b[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 YZ ZY [\u001b[+[ \\\u001b[+ð ]\u001b[+] ^=  _? `+  yz zy {\u001b[+' |\u001b[+7 }\u001b[+\\ ~\u001b[+=  ¢\u001b[+8 £| ¦\u001b[+1 §` ¨]  ¬\u001b[+6 °~ ´\u001b[+-  À+A Á\u001b[+-A Â=A Ã\u001b[+=A Ä]A È+E É\u001b[+-E Ê=E Ë]E Ì+I Í\u001b[+-I Î=I Ï]I Ñ\u001b[+=N Ò+O Ó\u001b[+-O Ô=O Õ\u001b[+=O Ö]O Ù+U Ú\u001b[+-U Û=U Ü]U Ý\u001b[+-Z à+a á\u001b[+-a â=a ã\u001b[+=a ä]a ç$ è+e é\u001b[+-e ê=e ë]e ì+i í\u001b[+-i î=i ï]i ñ\u001b[+=n ò+o ó\u001b[+-o ô=o õ\u001b[+=o ö]o ù+u ú\u001b[+-u û=u ü]u ý\u001b[+-z ÿ]z";

    public LocaleTranslator() {
        String var2 = null;
        this.locales.put("en_US", new Hashtable());
        this.add_alias("en_US", "English (United States)");
        this.add_locale("en_GB", this.british + this.euro1, "English (United Kingdom)");
        this.add_locale("fr_FR", this.french + this.euro2, "French");
        this.add_locale("it_IT", this.italian + this.euro2, "Italian");
        this.add_locale("de_DE", this.german + this.euro2, "German");
        this.add_locale("es_ES", this.spanish + this.euro2, "Spanish (Spain)");
        this.add_locale("ja_JP", this.japanese, "Japanese");
        this.add_locale("es_MX", this.latin_american + this.euro2, "Spanish (Latin America)");
        this.add_iso_alias("es_MX", "es_AR");
        this.add_iso_alias("es_MX", "es_BO");
        this.add_iso_alias("es_MX", "es_CL");
        this.add_iso_alias("es_MX", "es_CO");
        this.add_iso_alias("es_MX", "es_CR");
        this.add_iso_alias("es_MX", "es_DO");
        this.add_iso_alias("es_MX", "es_EC");
        this.add_iso_alias("es_MX", "es_GT");
        this.add_iso_alias("es_MX", "es_HN");
        this.add_iso_alias("es_MX", "es_NI");
        this.add_iso_alias("es_MX", "es_PA");
        this.add_iso_alias("es_MX", "es_PE");
        this.add_iso_alias("es_MX", "es_PR");
        this.add_iso_alias("es_MX", "es_PY");
        this.add_iso_alias("es_MX", "es_SV");
        this.add_iso_alias("es_MX", "es_UY");
        this.add_iso_alias("es_MX", "es_VE");
        this.add_locale("fr_BE", this.belgian + this.euro2, "French Belgium");
        this.add_locale("fr_CA", this.french_canadian + this.euro2, "French Canadian");
        this.add_locale("da_DK", this.danish + this.euro2, "Danish");
        this.add_locale("no_NO", this.norwegian + this.euro2, "Norwegian");
        this.add_locale("pt_PT", this.portuguese + this.euro2, "Portugese");
        this.add_locale("sv_SE", this.swedish + this.euro2, "Swedish");
        this.add_locale("fi_FI", this.finnish + this.euro2, "Finnish");
        this.add_locale("fr_CH", this.swiss_french + this.euro2, "Swiss (French)");
        this.add_locale("de_CH", this.swiss_german + this.euro2, "Swiss (German)");
        Enumeration var3 = remcons.prop.propertyNames();

        while (var3.hasMoreElements()) {
            String var1 = (String) var3.nextElement();
            if (var1.equals("locale.override")) {
                var2 = remcons.prop.getProperty(var1);
                System.out.println("Locale override: " + var2);
            } else if (var1.startsWith("locale.windows")) {
                this.windows = Boolean.valueOf(remcons.prop.getProperty(var1));
            } else if (var1.startsWith("locale.showgui")) {
                this.showgui = Boolean.valueOf(remcons.prop.getProperty(var1));
            } else if (var1.startsWith("locale.")) {
                String var4 = var1.substring(7);
                String var5 = remcons.prop.getProperty(var1);
                System.out.println("Adding user defined local for " + var4);
                this.add_locale(var4, var5, var4 + " (User Defined)");
            }
        }

        if (var2 != null) {
            System.out.println("Trying to select locale: " + var2);
            if (this.selectLocale(var2) != 0) {
                System.out.println("No keyboard definition for " + var2);
            }
        } else {
            Locale var6 = Locale.getDefault();
            System.out.println("Trying to select locale: " + var6.toString());
            if (this.selectLocale(var6.toString()) != 0) {
                System.out.println("No keyboard definition for '" + var6 + "'");
            }
        }

    }

    String create_accents(String var1, String var2) {
        StringBuffer var3 = new StringBuffer(256);

        for (int var5 = 0; var5 < var1.length(); ++var5) {
            char var4 = var1.charAt(var5);
            if (var4 == '*') {
                var3.append(var2);
            } else {
                var3.append(var4);
            }
        }

        return var3.toString();
    }

    void parse_locale_str(String var1, Hashtable var2) {
        int var4 = 0;
        boolean var5 = false;
        Character var6 = null;
        StringBuffer var7 = new StringBuffer(16);

        for (int var3 = 0; var3 < var1.length(); ++var3) {
            char var8 = var1.charAt(var3);
            if (var4 == 0 && var8 != ' ') {
                ++var4;
                var6 = new Character(var8);
            } else {
                if (var4 == 1 && var8 != ' ') {
                    if (var8 == 160) {
                        var8 = ' ';
                    }

                    var7.append(var8);
                }

                if (var4 == 1 && var8 == ' ') {
                    var2.put(var6, var7.toString());
                    var4 = 0;
                    var7 = new StringBuffer(16);
                }
            }
        }

        var2.put(var6, var7.toString());
    }

    void add_locale(String var1, String var2, String var3) {
        Hashtable var4 = new Hashtable();
        this.parse_locale_str(var2, var4);
        this.locales.put(var1, var4);
        this.aliases.put(var3, var1);
        this.reverse_alias.put(var1, var3);
    }

    void add_iso_alias(String var1, String var2) {
        this.locales.put(var2, this.locales.get(var1));
        this.reverse_alias.put(var2, this.reverse_alias.get(var1));
    }

    void add_alias(String var1, String var2) {
        this.aliases.put(var2, var1);
        this.reverse_alias.put(var1, var2);
    }

    public int selectLocale(String var1) {
        String var2 = (String) this.aliases.get(var1);
        if (var2 != null) {
            var1 = var2;
        }

        this.selected = (Hashtable) this.locales.get(var1);
        this.selected_name = (String) this.reverse_alias.get(var1);
        return this.selected != null ? 0 : -1;
    }

    public String translate(char var1) {
        Character var2 = new Character(var1);
        String var3 = null;
        if (this.selected != null) {
            var3 = (String) this.selected.get(var2);
        }

        return var3 == null ? var2.toString() : var3;
    }

    public String[] getLocales() {
        int var1 = this.aliases.size();
        String[] var2 = new String[var1];
        Enumeration var4 = this.aliases.keys();

        int var5;
        for (var5 = 0; var4.hasMoreElements(); var2[var5++] = (String) var4.nextElement()) {
        }

        for (var5 = 0; var5 < var1 - 1; ++var5) {
            for (int var6 = var5 + 1; var6 < var1; ++var6) {
                if (var2[var6].compareTo(var2[var5]) < 0) {
                    String var3 = var2[var6];
                    var2[var6] = var2[var5];
                    var2[var5] = var3;
                }
            }
        }

        return var2;
    }

    public String getSelected() {
        return this.selected_name;
    }
}
