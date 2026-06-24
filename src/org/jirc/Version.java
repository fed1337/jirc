package org.jirc;


import java.io.InputStream;
import java.util.Properties;

public final class Version {
    private static final String VERSION = Version.load();

    private Version() {
        super();
    }

    public static String get() {
        return Version.VERSION;
    }

    private static String load() {
        try (final InputStream in = Version.class.getResourceAsStream("/org/jirc/version.properties")) {
            if (null == in) {
                return "dev";
            }

            final Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("version", "dev");
        } catch (final Exception ex) {
            return "dev";
        }
    }
}
