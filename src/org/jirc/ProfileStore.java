package org.jirc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

/**
 * Persists the favourites/profiles list to {@code ~/.jirc/profiles.properties}
 * and provides the CRUD operations the connection dialog drives.
 * <p>
 * Profiles are addressed by their (case-insensitive) {@code name}, so saving
 * a profile whose name already exists updates it in place. Saved passwords are
 * only lightly obfuscated (XOR + Base64), not encrypted; this
 * just keeps them from sitting in plain sight in the file.
 */
class ProfileStore {

    private static final String COUNT_KEY = "profile.count";
    private static final byte[] OBFUSCATION_KEY = {(byte) 0x4a, (byte) 0x69, (byte) 0x72, (byte) 0x63, (byte) 0x21}; // "Jirc!"

    private final File file;

    public ProfileStore() {
        this(ProfileStore.defaultFile());
    }

    private ProfileStore(final File file) {
        super();
        this.file = file;
    }

    private static File defaultFile() {
        final File dir = new File(System.getProperty("user.home", "."), ".jirc");
        return new File(dir, "profiles.properties");
    }

    /**
     * @return all stored profiles in their saved order (never {@code null})
     */
    public synchronized List<ServerProfile> load() {
        final List<ServerProfile> profiles = new ArrayList<>();
        if (!this.file.exists()) {
            return profiles;
        }
        final Properties props = new Properties();
        InputStream in = null;
        try {
            in = Files.newInputStream(this.file.toPath());
            props.load(in);
        } catch (final IOException e) {
            return profiles;
        } finally {
            ProfileStore.closeQuietly(in);
        }

        final int count = ProfileStore.parseInt(props.getProperty(ProfileStore.COUNT_KEY));
        for (int i = 0; i < count; i++) {
            final String prefix = "profile." + i + ".";
            final String name = props.getProperty(prefix + "name");
            if (null == name) {
                continue;
            }
            final boolean savePassword = Boolean.parseBoolean(props.getProperty(prefix + "savePassword", "false"));
            String password = "";
            if (savePassword) {
                password = ProfileStore.deobfuscate(props.getProperty(prefix + "password", ""));
            }
            profiles.add(new ServerProfile(
                    name,
                    props.getProperty(prefix + "host", ""),
                    props.getProperty(prefix + "user", ""),
                    password,
                    savePassword));
        }
        return profiles;
    }

    /**
     * Insert or update {@code profile} (matched by name) and persist the list.
     *
     * @return the full, updated list of profiles
     */
    public synchronized List<ServerProfile> save(final ServerProfile profile) {
        final List<ServerProfile> profiles = this.load();
        final int existing = ProfileStore.indexOfName(profiles, profile.getName());
        if (0 <= existing) {
            profiles.set(existing, profile);
        } else {
            profiles.add(profile);
        }
        this.writeAll(profiles);
        return profiles;
    }

    /**
     * Remove the profile with the given name (if present) and persist.
     *
     * @return the full, updated list of profiles
     */
    public synchronized List<ServerProfile> delete(final String name) {
        final List<ServerProfile> profiles = this.load();
        final int existing = ProfileStore.indexOfName(profiles, name);
        if (0 <= existing) {
            profiles.remove(existing);
            this.writeAll(profiles);
        }
        return profiles;
    }

    private void writeAll(final List<ServerProfile> profiles) {
        final Properties props = new Properties();
        props.setProperty(ProfileStore.COUNT_KEY, String.valueOf(profiles.size()));
        for (int i = 0; i < profiles.size(); i++) {
            final ServerProfile p = profiles.get(i);
            final String prefix = "profile." + i + ".";
            props.setProperty(prefix + "name", ProfileStore.nullToEmpty(p.getName()));
            props.setProperty(prefix + "host", ProfileStore.nullToEmpty(p.getHost()));
            props.setProperty(prefix + "user", ProfileStore.nullToEmpty(p.getUser()));
            props.setProperty(prefix + "savePassword", String.valueOf(p.isSavePassword()));
            if (p.isSavePassword()) {
                props.setProperty(prefix + "password", ProfileStore.obfuscate(ProfileStore.nullToEmpty(p.getPassword())));
            }
        }

        final File dir = this.file.getParentFile();
        if (null != dir && !dir.exists()) {
            dir.mkdirs();
        }
        OutputStream out = null;
        try {
            out = Files.newOutputStream(this.file.toPath());
            props.store(out, "jirc saved server profiles");
        } catch (final IOException e) {
            throw new IllegalStateException("Could not save profiles to " + this.file + ": " + e.getMessage(), e);
        } finally {
            ProfileStore.closeQuietly(out);
        }
    }

    private static int indexOfName(final List<ServerProfile> profiles, final String name) {
        if (null == name) {
            return -1;
        }
        for (int i = 0; i < profiles.size(); i++) {
            if (name.equalsIgnoreCase(profiles.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    private static String obfuscate(final String plain) {
        final byte[] data = plain.getBytes(StandardCharsets.UTF_8);
        ProfileStore.xor(data);
        return Base64.getEncoder().encodeToString(data);
    }

    private static String deobfuscate(final String stored) {
        if (null == stored || stored.isEmpty()) {
            return "";
        }
        try {
            final byte[] data = Base64.getDecoder().decode(stored);
            ProfileStore.xor(data);
            return new String(data, StandardCharsets.UTF_8);
        } catch (final RuntimeException e) {
            return "";
        }
    }

    private static void xor(final byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) ((int) data[i] ^ (int) ProfileStore.OBFUSCATION_KEY[i % ProfileStore.OBFUSCATION_KEY.length]);
        }
    }

    private static int parseInt(final String value) {
        if (null == value) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    private static String nullToEmpty(final String s) {
        return null == s ? "" : s;
    }

    private static void closeQuietly(final java.io.Closeable c) {
        if (null != c) {
            try {
                c.close();
            } catch (final IOException ignored) {
                // best effort
            }
        }
    }
}
