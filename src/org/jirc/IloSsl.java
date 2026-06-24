package org.jirc;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URLConnection;
import java.security.cert.X509Certificate;

/**
 * iLO devices ship with self-signed certificates, so the standard JVM trust
 * store rejects them. This installs a trust-all {@link SSLContext} plus a
 * permissive {@link HostnameVerifier} as the JVM defaults, reproducing the
 * lenient behavior the browser plug-in provided.
 *
 * <p>{@link #install()} should be called once from {@code org.jirc.Main.main} before any
 * HTTPS traffic. {@link #apply(URLConnection)} is a belt-and-braces helper that
 * also pins the trust-all factory onto an individual connection.
 */
final class IloSsl {

    private static final X509Certificate[] NO_ISSUERS = new X509Certificate[0];
    private static SSLSocketFactory trustAllFactory = null;
    private static HostnameVerifier trustAllVerifier = null;
    private static boolean installed = false;

    private IloSsl() {
        super();
    }

    public static synchronized void install() {
        if (IloSsl.installed) {
            return;
        }

        final TrustManager[] trustAll = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return IloSsl.NO_ISSUERS;
                    }

                    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                    }

                    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                    }
                }
        };

        IloSsl.trustAllVerifier = (hostname, session) -> true;

        try {
            final SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAll, new java.security.SecureRandom());
            IloSsl.trustAllFactory = ctx.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(IloSsl.trustAllFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(IloSsl.trustAllVerifier);
            IloSsl.installed = true;
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to install trust-all SSL context", e);
        }
    }

    /**
     * Force the trust-all factory/verifier onto a single connection. Safe to
     * call on plain HTTP connections (it becomes a no-op).
     */
    public static void apply(final URLConnection conn) {
        if (!IloSsl.installed) {
            IloSsl.install();
        }
        if (conn instanceof HttpsURLConnection) {
            final HttpsURLConnection https = (HttpsURLConnection) conn;
            https.setSSLSocketFactory(IloSsl.trustAllFactory);
            https.setHostnameVerifier(IloSsl.trustAllVerifier);
        }
    }
}
