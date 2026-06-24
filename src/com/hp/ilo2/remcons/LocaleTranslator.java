package com.hp.ilo2.remcons;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

final class LocaleTranslator {
    public boolean windows = true;
    private final Map<String, Map<Character, String>> locales = new HashMap<>(64);
    private final Map<String, String> aliases = new HashMap<>(64);
    private Map<Character, String> selected = null;
    private final Map<String, String> reverse_alias = new HashMap<>(64);
    private String selected_name = null;
    private static final String euro1 = " €\u001b[+4";
    private static final String euro2 = " €\u001b[+e";
    private static final String belgian = "\u0001\u0011 \u0011\u0001 \u0017\u001a \u001a\u0017 !8 \"3 #\u001b[+3 $] %\" &1 '4 (5 )- *} +? ,m -= .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð =/ >ñ ?M @\u001b[+2 AQ M: QA WZ ZW [\u001b[+[ \\\u001b[+ð ]\u001b[+] ^[  _+ `\u001b[+\\  aq m; qa wz zw {\u001b[+9 |\u001b[+1 }\u001b[+0 ~\u001b[+/  £| §6 ¨{  °_ ²` ³~ ´\u001b[+'  µ\\ À\u001b[+\\Q Á\u001b[+'Q Â[Q Ã\u001b[+/Q Ä{Q È\u001b[+\\E É\u001b[+'E Ê[E Ë{E Ì\u001b[+\\I Í\u001b[+'I Î[I Ï{I Ñ\u001b[+/N Ò\u001b[+\\O Ó\u001b[+'O Ô[O Õ\u001b[+/O Ö{O Ù\u001b[+\\U Ú\u001b[+'U Û[U Ü{U Ý\u001b[+'Y à\u001b[+\\q á\u001b[+'q â[q ã\u001b[+/q ä{q ç9 è\u001b[+\\e é\u001b[+'e ê[e ë{e ì\u001b[+\\i í\u001b[+'i î[i ï{i ñ\u001b[+/n ò\u001b[+\\o ó\u001b[+'o ô[o õ\u001b[+/o ö{o ù\u001b[+\\u ú\u001b[+'u û[u ü{u ý\u001b[+'y ÿ{y";
    private static final String british = "\"@ #\\ @\" \\ð |ñ ~| £# ¦\u001b[+` ¬~ Á\u001b[+A á\u001b[+a É\u001b[+E é\u001b[+e Í\u001b[+I í\u001b[+i Ó\u001b[+O ó\u001b[+o Ú\u001b[+U ú\u001b[+u";
    private static final String danish = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\\u001b[+ð ]\u001b[+9 ^}  _? `+  {\u001b[+7 |\u001b[+= }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §~ ¨]  ´=  ½` À+A Á=A Â}A Ã\u001b[+]A Ä]A Å{ Æ: È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó=O Ô}O Õ\u001b[+]O Ö]O Ø\" Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\u001b[+]a ä]a å[ æ; è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\u001b[+]n ò+o ó=o ô}o õ\u001b[+]o ö]o ø' ù+u ú=u û}u ü]u ý=y ÿ]y";
    private static final String finnish = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\\u001b[+- ]\u001b[+9 ^}  _? `+  {\u001b[+7 |\u001b[+ð }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\u001b[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó=O Ô}O Õ\u001b[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\u001b[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\u001b[+]n ò+o ó=o ô}o õ\u001b[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
    private static final String french = "\u0001\u0011 \u0011\u0001 \u0017\u001a \u001a\u0017 !/ \"3 #\u001b[+3 $] %\" &1 '4 (5 )- *\\ ,m -6 .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð >ñ ?M @\u001b[+0 AQ M: QA WZ ZW [\u001b[+5 \\\u001b[+8 ]\u001b[+- ^\u001b[+9 _8 `\u001b[+7 aq m; qa wz zw {\u001b[+4 |\u001b[+6 }\u001b[+= ~\u001b[+2 £} ¤\u001b[+] §? ¨{  °_ ²` µ| Â[Q Ä{Q Ê[E Ë{E Î[I Ï{I Ô[O Ö{O Û[U Ü{U à0 â[q ä{q ç9 è7 é2 ê[e ë{e î[i ï{i ô[o ö{o ù' û[u ü{u ÿ{y";
    private static final String french_canadian = "\"@ #` '< /# <\\ >| ?^ @\u001b[+2 [\u001b[+[ \\\u001b[+` ]\u001b[+] ^[  `'  {\u001b[+' |~ }\u001b[+\\ ~\u001b[+; ¢\u001b[+4 £\u001b[+3 ¤\u001b[+5 ¦\u001b[+7 §\u001b[+o ¨}  «ð ¬\u001b[+6 \u00ad\u001b[+. ¯\u001b[+, °\u001b[+ð ±\u001b[+1 ²\u001b[+8 ³\u001b[+9 ´\u001b[+/  µ\u001b[+m ¶\u001b[+p ¸]  »ñ ¼\u001b[+0 ½\u001b[+- ¾\u001b[+= À'A Á\u001b[+/A Â[A Ä}A Ç]C È'E É? Ê[E Ë}E Ì'I Í\u001b[+/I Î[I Ï}I Ò'O Ó\u001b[+/O Ô[O Ö}O Ù'U Ú\u001b[+/U Û[U Ü}U Ý\u001b[+/Y à'a á\u001b[+/a â[a ä}a ç]c è'e é\u001b[+/e ê[e ë}e ì'i í\u001b[+/i î[i ï}i ò'o ó\u001b[+/o ô[o ö}o ù'u ú\u001b[+/u û[u ü}u ý\u001b[+/y ÿ}y";
    private static final String german = "\u0019\u001a \u001a\u0019 \"@ #\\ &^ '| (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+q YZ ZY [\u001b[+8 \\\u001b[+- ]\u001b[+9 ^`  _? `+  yz zy {\u001b[+7 |\u001b[+ð }\u001b[+0 ~\u001b[+] §# °~ ²\u001b[+2 ³\u001b[+3 ´=  µ\u001b[+m À+A Á=A Â`A Ä\" È+E É=E Ê`E Ì+I Í=I Î`I Ò+O Ó=O Ô`O Ö: Ù+U Ú=U Û`U Ü{ Ý=Z ß- à+a á=a â`a ä' è+e é=e ê`e ì+i í=i î`i ò+o ó=o ô`o ö; ù+u ú=u û`u ü[ ý=z";
    private static final String italian = "\"@ #\u001b[+' &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+; [\u001b[+[ \\` ]\u001b[+] ^+ _? |~ £# §| °\" à' ç: è[ é{ ì= ò; ù\\";
    private static final String japanese = "\"@ &^ '& (* )( *\" +: :' =_ @[ [] \\ò ]\\ ^= _ó `{ {} ¥ô |õ }| ~+";
    private static final String latin_american = "\"@ &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+q [\" \\\u001b[+- ]| ^\u001b[+'  _? `\u001b[+\\  {' |` }\\ ~\u001b[+] ¡+ ¨{  ¬\u001b[+` °~ ´[  ¿= À\u001b[+\\A Á[A Â\u001b[+'A Ä{A È\u001b[+\\E É[E Ê\u001b[+'E Ë{E Ì\u001b[+\\I Í[I Î\u001b[+'I Ï{I Ñ: Ò\u001b[+\\O Ó[O Ô\u001b[+'O Ö{O Ù\u001b[+\\U Ú[U Û\u001b[+'U Ü{U Ý[Y à\u001b[+\\a á[a â\u001b[+'a ä{a è\u001b[+\\e é[e ê\u001b[+'e ë{e ì\u001b[+\\i í[i î\u001b[+'i ï{i ñ; ò\u001b[+\\o ó[o ô\u001b[+'o ö{o ù\u001b[+\\u ú[u û\u001b[+'u ü{u ý[y ÿ{y";
    private static final String norwegian = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\= ]\u001b[+9 ^}  _? `+  {\u001b[+7 |` }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §~ ¨]  ´\u001b[+=  À+A Á\u001b[+=A Â}A Ã\u001b[+]A Ä]A Å{ Æ\" È+E É\u001b[+=E Ê}E Ë]E Ì+I Í\u001b[+=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó\u001b[+=O Ô}O Õ\u001b[+]O Ö]O Ø: Ù+U Ú\u001b[+=U Û}U Ü]U Ý\u001b[+=Y à+a á\u001b[+=a â}a ã\u001b[+]a ä]a å[ æ' è+e é\u001b[+=e ê}e ë]e ì+i í\u001b[+=i î}i ï]i ñ\u001b[+]n ò+o ó\u001b[+=o ô}o õ\u001b[+]o ö]o ø; ù+u ú\u001b[+=u û}u ü]u ý\u001b[+=y ÿ]y";
    private static final String portuguese = "\"@ &^ '- (* )( *{ +[ -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\` ]\u001b[+9 ^|  _? `}  {\u001b[+7 |~ }\u001b[+0 ~\\  £\u001b[+3 §\u001b[+4 ¨\u001b[+[  ª\" «= ´]  º' »+ À}A Á]A Â|A Ã\\A Ä\u001b[+[A Ç: È}E É]E Ê|E Ë\u001b[+[E Ì}I Í]I Î|I Ï\u001b[+[I Ñ\\N Ò}O Ó]O Ô|O Õ\\O Ö\u001b[+[O Ù}U Ú]U Û|U Ü\u001b[+[U Ý]Y à}a á]a â|a ã\\a ä\u001b[+[a ç; è}e é]e ê|e ë\u001b[+[e ì}i í]i î|i ï\u001b[+[i ñ\\n ò}o ó]o ô|o õ\\o ö\u001b[+[o ù}u ú]u û|u ü\u001b[+[u ý]y ÿ\u001b[+[y";
    private static final String spanish = "\"@ #\u001b[+3 &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+[ \\\u001b[+` ]\u001b[+] ^{  _? `[  {\u001b[+' |\u001b[+1 }\u001b[+\\ ¡= ¨\"  ª~ ¬\u001b[+6 ´'  ·# º` ¿+ À[A Á'A Â{A Ä\"A Ç| È[E É'E Ê{E Ë\"E Ì[I Í'I Î{I Ï\"I Ñ: Ò[O Ó'O Ô{O Ö\"O Ù[U Ú'U Û{U Ü\"U Ý'Y à[a á'a â{a ä\"a ç\\ è[e é'e ê{e ë\"e ì[i í'i î{i ï\"i ñ; ò[o ó'o ô{o ö\"o ù[u ú'u û{u ü\"u ý'y ÿ\"y";
    private static final String swedish = "\"@ $\u001b[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 [\u001b[+8 \\\u001b[+- ]\u001b[+9 ^}  _? `+  {\u001b[+7 |\u001b[+ð }\u001b[+0 ~\u001b[+]  £\u001b[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\u001b[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\u001b[+]N Ò+O Ó=O Ô}O Õ\u001b[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\u001b[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\u001b[+]n ò+o ó=o ô}o õ\u001b[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
    private static final String swiss_french = "\u0019\u001a \u001a\u0019 !} \"@ #\u001b[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 YZ ZY [\u001b[+[ \\\u001b[+ð ]\u001b[+] ^=  _? `+  yz zy {\u001b[+' |\u001b[+7 }\u001b[+\\ ~\u001b[+=  ¢\u001b[+8 £| ¦\u001b[+1 §` ¨]  ¬\u001b[+6 °~ ´\u001b[+-  À+A Á\u001b[+-A Â=A Ã\u001b[+=A Ä]A È+E É\u001b[+-E Ê=E Ë]E Ì+I Í\u001b[+-I Î=I Ï]I Ñ\u001b[+=N Ò+O Ó\u001b[+-O Ô=O Õ\u001b[+=O Ö]O Ù+U Ú\u001b[+-U Û=U Ü]U Ý\u001b[+-Z à+a á\u001b[+-a â=a ã\u001b[+=a ä]a ç$ è+e é\u001b[+-e ê=e ë]e ì+i í\u001b[+-i î=i ï]i ñ\u001b[+=n ò+o ó\u001b[+-o ô=o õ\u001b[+=o ö]o ù+u ú\u001b[+-u û=u ü]u ý\u001b[+-z ÿ]z";
    private static final String swiss_german = "\u0019\u001a \u001a\u0019 !} \"@ #\u001b[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\u001b[+2 YZ ZY [\u001b[+[ \\\u001b[+ð ]\u001b[+] ^=  _? `+  yz zy {\u001b[+' |\u001b[+7 }\u001b[+\\ ~\u001b[+=  ¢\u001b[+8 £| ¦\u001b[+1 §` ¨]  ¬\u001b[+6 °~ ´\u001b[+-  À+A Á\u001b[+-A Â=A Ã\u001b[+=A Ä]A È+E É\u001b[+-E Ê=E Ë]E Ì+I Í\u001b[+-I Î=I Ï]I Ñ\u001b[+=N Ò+O Ó\u001b[+-O Ô=O Õ\u001b[+=O Ö]O Ù+U Ú\u001b[+-U Û=U Ü]U Ý\u001b[+-Z à+a á\u001b[+-a â=a ã\u001b[+=a ä]a ç$ è+e é\u001b[+-e ê=e ë]e ì+i í\u001b[+-i î=i ï]i ñ\u001b[+=n ò+o ó\u001b[+-o ô=o õ\u001b[+=o ö]o ù+u ú\u001b[+-u û=u ü]u ý\u001b[+-z ÿ]z";

    public LocaleTranslator() {
        super();
        String var2 = null;
        this.locales.put("en_US", new HashMap<>(128));
        this.add_alias();
        this.add_locale("en_GB", LocaleTranslator.british + LocaleTranslator.euro1, "English (United Kingdom)");
        this.add_locale("fr_FR", LocaleTranslator.french + LocaleTranslator.euro2, "French");
        this.add_locale("it_IT", LocaleTranslator.italian + LocaleTranslator.euro2, "Italian");
        this.add_locale("de_DE", LocaleTranslator.german + LocaleTranslator.euro2, "German");
        this.add_locale("es_ES", LocaleTranslator.spanish + LocaleTranslator.euro2, "Spanish (Spain)");
        this.add_locale("ja_JP", LocaleTranslator.japanese, "Japanese");
        this.add_locale("es_MX", LocaleTranslator.latin_american + LocaleTranslator.euro2, "Spanish (Latin America)");
        this.add_iso_alias("es_AR");
        this.add_iso_alias("es_BO");
        this.add_iso_alias("es_CL");
        this.add_iso_alias("es_CO");
        this.add_iso_alias("es_CR");
        this.add_iso_alias("es_DO");
        this.add_iso_alias("es_EC");
        this.add_iso_alias("es_GT");
        this.add_iso_alias("es_HN");
        this.add_iso_alias("es_NI");
        this.add_iso_alias("es_PA");
        this.add_iso_alias("es_PE");
        this.add_iso_alias("es_PR");
        this.add_iso_alias("es_PY");
        this.add_iso_alias("es_SV");
        this.add_iso_alias("es_UY");
        this.add_iso_alias("es_VE");
        this.add_locale("fr_BE", LocaleTranslator.belgian + LocaleTranslator.euro2, "French Belgium");
        this.add_locale("fr_CA", LocaleTranslator.french_canadian + LocaleTranslator.euro2, "French Canadian");
        this.add_locale("da_DK", LocaleTranslator.danish + LocaleTranslator.euro2, "Danish");
        this.add_locale("no_NO", LocaleTranslator.norwegian + LocaleTranslator.euro2, "Norwegian");
        this.add_locale("pt_PT", LocaleTranslator.portuguese + LocaleTranslator.euro2, "Portuguese");
        this.add_locale("sv_SE", LocaleTranslator.swedish + LocaleTranslator.euro2, "Swedish");
        this.add_locale("fi_FI", LocaleTranslator.finnish + LocaleTranslator.euro2, "Finnish");
        this.add_locale("fr_CH", LocaleTranslator.swiss_french + LocaleTranslator.euro2, "Swiss (French)");
        this.add_locale("de_CH", LocaleTranslator.swiss_german + LocaleTranslator.euro2, "Swiss (German)");
        for (final String var1 : remcons.prop.stringPropertyNames()) {
            if ("locale.override".equals(var1)) {
                var2 = remcons.prop.getProperty(var1);
                System.out.println("Locale override: " + var2);
            } else if (var1.startsWith("locale.windows")) {
                this.windows = Boolean.parseBoolean(remcons.prop.getProperty(var1));
            } else if (var1.startsWith("locale.showgui")) {
                final boolean showgui = Boolean.parseBoolean(remcons.prop.getProperty(var1));
            } else if (var1.startsWith("locale.")) {
                final String var4 = var1.substring(7);
                final String var5 = remcons.prop.getProperty(var1);
                System.out.println("Adding user defined local for " + var4);
                this.add_locale(var4, var5, var4 + " (User Defined)");
            }
        }

        if (null != var2) {
            System.out.println("Trying to select locale: " + var2);
            if (0 != this.selectLocale(var2)) {
                System.out.println("No keyboard definition for " + var2);
            }
        } else {
            final Locale var6 = Locale.getDefault();
            System.out.println("Trying to select locale: " + var6.toString());
            if (0 != this.selectLocale(var6.toString())) {
                System.out.println("No keyboard definition for '" + var6 + "'");
            }
        }

    }

    static String create_accents(final CharSequence var1, final String var2) {
        final StringBuilder var3 = new StringBuilder(256);

        for (int var5 = 0; var5 < var1.length(); ++var5) {
            final char var4 = var1.charAt(var5);
            if ((int) '*' == (int) var4) {
                var3.append(var2);
            } else {
                var3.append(var4);
            }
        }

        return var3.toString();
    }

    private static void parse_locale_str(final CharSequence var1, final Map<Character, String> var2) {
        int var4 = 0;
        Character var6 = null;
        StringBuilder var7 = new StringBuilder(16);

        for (int var3 = 0; var3 < var1.length(); ++var3) {
            char var8 = var1.charAt(var3);
            if (0 == var4 && (int) ' ' != (int) var8) {
                ++var4;
                var6 = var8;
            } else {
                if (1 == var4 && (int) ' ' != (int) var8) {
                    if (160 == (int) var8) {
                        var8 = ' ';
                    }

                    var7.append(var8);
                }

                if (1 == var4 && (int) ' ' == (int) var8) {
                    var2.put(var6, var7.toString());
                    var4 = 0;
                    var7 = new StringBuilder(16);
                }
            }
        }

        var2.put(var6, var7.toString());
    }

    private void add_locale(final String var1, final CharSequence var2, final String var3) {
        final Map<Character, String> var4 = new HashMap<>(128);
        LocaleTranslator.parse_locale_str(var2, var4);
        this.locales.put(var1, var4);
        this.aliases.put(var3, var1);
        this.reverse_alias.put(var1, var3);
    }

    private void add_iso_alias(final String var2) {
        this.locales.put(var2, this.locales.get("es_MX"));
        this.reverse_alias.put(var2, this.reverse_alias.get("es_MX"));
    }

    private void add_alias() {
        this.aliases.put("English (United States)", "en_US");
        this.reverse_alias.put("en_US", "English (United States)");
    }

    public int selectLocale(String var1) {
        final String var2 = this.aliases.get(var1);
        if (null != var2) {
            var1 = var2;
        }

        this.selected = this.locales.get(var1);
        this.selected_name = this.reverse_alias.get(var1);
        return null != this.selected ? 0 : -1;
    }

    public String translate(final char var1) {
        final Character var2 = var1;
        String var3 = null;
        if (null != this.selected) {
            var3 = this.selected.get(var2);
        }

        return null == var3 ? var2.toString() : var3;
    }

    public String[] getLocales() {
        final int var1 = this.aliases.size();
        final String[] var2 = new String[var1];
        final Iterator<String> iterator = this.aliases.keySet().iterator();

        int var5;
        for (var5 = 0; iterator.hasNext(); var2[var5++] = iterator.next()) {
        }

        for (var5 = 0; var5 < var1 - 1; ++var5) {
            for (int var6 = var5 + 1; var6 < var1; ++var6) {
                if (0 > var2[var6].compareTo(var2[var5])) {
                    final String var3 = var2[var6];
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
