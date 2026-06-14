package org.jirc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Logs in to an iLO 4 over its JSON API and returns a populated
 * {@link IrcSessionContext}. Reproduces the {@code session_key} that the
 * browser used to hand to the engine via the {@code RCINFO1} parameter.
 *
 * <pre>
 * POST https://{host}[:port]/json/login_session
 * {"method":"login","user_login":"...","password":"..."}
 * </pre>
 */
public final class IloAuthClient {

    /**
     * Maximum time to wait for the iLO to accept and answer the login request.
     */
    private static final int TIMEOUT_MS = 15000;

    private IloAuthClient() {
        super();
    }

    /**
     * @param hostInput host or {@code host:port} as typed by the user
     * @param user      iLO account name
     * @param pass      iLO account password
     * @return a ready-to-use session context
     * @throws IloAuthException with a user-readable message on any failure
     */
    public static IrcSessionContext login(final String hostInput, final String user, final char[] pass) throws IloAuthException {
        if (null == hostInput || hostInput.trim().isEmpty()) {
            throw new IloAuthException("Please enter an iLO IP address or hostname.");
        }

        String host = hostInput.trim();
        int port = IrcSessionContext.DEFAULT_HTTPS_PORT;

        // Strip an accidental scheme and trailing slash if the user pasted a URL.
        if (host.startsWith("https://")) {
            host = host.substring("https://".length());
        } else if (host.startsWith("http://")) {
            host = host.substring("http://".length());
        }
        final int slash = host.indexOf((int) '/');
        if (-1 != slash) {
            host = host.substring(0, slash);
        }

        final int colon = host.lastIndexOf((int) ':');
        if (-1 != colon) {
            final String portStr = host.substring(colon + 1);
            host = host.substring(0, colon);
            try {
                port = Integer.parseInt(portStr.trim());
            } catch (final NumberFormatException e) {
                throw new IloAuthException("Invalid port in \"" + hostInput + "\".", e);
            }
        }

        if (host.isEmpty()) {
            throw new IloAuthException("Please enter a valid iLO IP address or hostname.");
        }

        final String body = "{\"method\":\"login\",\"user_login\":\"" + IloAuthClient.jsonEscape(user)
                + "\",\"password\":\"" + IloAuthClient.jsonEscape(new String(pass)) + "\"}";

        HttpURLConnection conn = null;
        try {
            final URL url = new URI("https", null, host, port, "/json/login_session", null, null).toURL();
            conn = (HttpURLConnection) url.openConnection();
            IloSsl.apply(conn);
            conn.setConnectTimeout(IloAuthClient.TIMEOUT_MS);
            conn.setReadTimeout(IloAuthClient.TIMEOUT_MS);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();

            final OutputStream os = conn.getOutputStream();
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            final int code = conn.getResponseCode();
            if (401 == code || 403 == code) {
                throw new IloAuthException("Login rejected (HTTP " + code
                        + "). Check the username and password.");
            }
            if (200 != code) {
                throw new IloAuthException("iLO returned HTTP " + code
                        + " for the login request.");
            }

            final String response = IloAuthClient.readAll(conn.getInputStream());
            final String sessionKey = IloAuthClient.extractJsonString(response);
            if (null == sessionKey || sessionKey.isEmpty()) {
                throw new IloAuthException("Login succeeded but no session key was returned.");
            }

            return IrcSessionContext.fromLogin(host, port, sessionKey);
        } catch (final IloAuthException e) {
            throw e;
        } catch (final SocketTimeoutException e) {
            throw new IloAuthException("Connection to " + host + ":" + port
                    + " timed out after " + (IloAuthClient.TIMEOUT_MS / 1000)
                    + " seconds. Check the address and that the iLO is reachable.", e);
        } catch (final IOException e) {
            throw new IloAuthException("Could not reach iLO at " + host + ":" + port
                    + " (" + e.getMessage() + ").", e);
        } catch (final Exception e) {
            throw new IloAuthException("Login failed: " + e.getMessage(), e);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    private static String readAll(final InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        final StringBuilder sb = new StringBuilder();
        String line;
        while (null != (line = reader.readLine())) {
            sb.append(line).append('\n');
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Minimal {@code "key":"value"} extractor, matching the lightweight parsing
     * style already used in {@code jsonparser}.
     */
    private static String extractJsonString(final String json) {
        if (null == json) {
            return null;
        }
        final String needle = "\"" + "session_key" + "\"";
        final int k = json.indexOf(needle);
        if (-1 == k) {
            return null;
        }
        final int colon = json.indexOf((int) ':', k + needle.length());
        if (-1 == colon) {
            return null;
        }
        final int firstQuote = json.indexOf((int) '"', colon + 1);
        if (-1 == firstQuote) {
            return null;
        }
        final int endQuote = json.indexOf((int) '"', firstQuote + 1);
        if (-1 == endQuote) {
            return null;
        }
        return json.substring(firstQuote + 1, endQuote);
    }

    private static String jsonEscape(final String s) {
        if (null == s) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Carries a user-readable reason for a failed login.
     */
    public static class IloAuthException extends Exception {
        public IloAuthException(final String message) {
            super(message);
        }

        public IloAuthException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
