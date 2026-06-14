package org.jirc;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Replacement for the parameters that the JNLP container used to inject.
 * Backs the {@code getParameter} / {@code getCodeBase} / {@code getDocumentBase}
 * accessors that the legacy engine ({@code App}, {@code virtdevs}, {@code jsonparser},
 * {@code locinfo}, {@code telnet}, {@code cmd}) still relies on.
 */
public class IrcSessionContext {
    public static final int DEFAULT_HTTPS_PORT = 443;

    private final String host;
    private final int port;
    private final String sessionKey;
    private final Map<String, String> params = new HashMap<>();

    public IrcSessionContext(final String host, final int port, final String sessionKey) {
        super();
        this.host = host;
        this.port = 0 >= port ? IrcSessionContext.DEFAULT_HTTPS_PORT : port;
        this.sessionKey = sessionKey;
    }

    /**
     * Build a context for a freshly logged-in iLO session, seeding the same
     * parameter defaults the original JNLP descriptor supplied.
     */
    public static IrcSessionContext fromLogin(final String host, final int port, final String sessionKey) {
        final IrcSessionContext ctx = new IrcSessionContext(host, port, sessionKey);
        ctx.params.put("RCINFO1", sessionKey);
        ctx.params.put("RCINFOLANG", "en");
        ctx.params.put("INFO0", "7AC3BDEBC9AC64E85734454B53BB73CE");
        ctx.params.put("INFO1", "17988");
        ctx.params.put("INFO2", "composite");
        return ctx;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public void setParameter(final String name, final String value) {
        this.params.put(name, value);
    }

    /**
     * Returns {@code null} for unknown names (same contract the container provided).
     */
    public String getParameter(final String name) {
        return this.params.get(name);
    }

    /**
     * Base URL of the iLO: {@code https://host:port/}.
     */
    public URL getCodeBase() {
        try {
            return new URI("https", null, this.host, this.port, "/", null, null).toURL();
        } catch (final URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException("Invalid codebase for host " + this.host, e);
        }
    }

    /**
     * URL of the page that used to host the engine.
     */
    public URL getDocumentBase() {
        try {
            return new URI("https", null, this.host, this.port, "/html/java_irc.html", null, null).toURL();
        } catch (final URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException("Invalid documentbase for host " + this.host, e);
        }
    }
}
