package org.jirc;

/**
 * A saved iLO server entry shown in the connection dialog's favourites list.
 * <p>
 * {@code host} keeps the {@code host:port} form exactly as typed (port
 * defaults to {@link IrcSessionContext#DEFAULT_HTTPS_PORT} when omitted, see
 * {@code IloAuthClient}). The password is only persisted when
 * {@link #isSavePassword()} is {@code true}.
 */
class ServerProfile {

    private String name;
    private String host;
    private String user;
    private String password;
    private boolean savePassword;

    public ServerProfile() {
        this("", "", "", "", false);
    }

    public ServerProfile(final String name, final String host, final String user, final String password, final boolean savePassword) {
        super();
        this.name = name;
        this.host = host;
        this.user = user;
        this.password = password;
        this.savePassword = savePassword;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isSavePassword() {
        return this.savePassword;
    }

    public void setSavePassword(final boolean savePassword) {
        this.savePassword = savePassword;
    }

    /**
     * Shown verbatim by the favourites {@code JList}.
     */
    public String toString() {
        return null == this.name || this.name.isEmpty() ? "(unnamed)" : this.name;
    }
}
