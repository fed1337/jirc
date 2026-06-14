package org.jirc;

import com.hp.ilo2.remcons.aboutJircDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Standalone connection window: collects iLO host + credentials, logs in on a
 * background {@link SwingWorker}, then starts the remote console engine. The
 * window stays visible (showing "Connecting..." / "Starting...") until the
 * console is responsive, then hides itself; it reappears when the console
 * window is closed, and closing it ends the process.
 * <p>
 * A favourites panel on the left provides CRUD over saved
 * {@link ServerProfile} entries (persisted by {@link ProfileStore}); selecting
 * one fills the form, and the "Save password" checkbox controls whether the
 * password is persisted with the profile.
 */
public class ConnectionDialog extends JFrame {

    /**
     * Safety net: hide this window even if no readiness signal ever arrives.
     */
    private static final int READY_TIMEOUT_MS = 45000;

    private final JTextField nameField = new JTextField(24);
    private final JTextField hostField = new JTextField(24);
    private final JTextField userField = new JTextField(24);
    private final JPasswordField passField = new JPasswordField(24);
    private final JCheckBox savePasswordCheck = new JCheckBox("Save password");
    private final JButton connectButton = new JButton("Connect");
    private final JButton aboutButton = new JButton("About");
    private final JLabel statusLabel = new JLabel(" ");

    private final ProfileStore store = new ProfileStore();
    private final DefaultListModel<ServerProfile> profileModel = new DefaultListModel<>();
    private final JList<ServerProfile> profileList = new JList<>(this.profileModel);
    private final JButton newButton = new JButton("New");
    private final JButton saveButton = new JButton("Save");
    private final JButton deleteButton = new JButton("Delete");

    private Timer readyTimeout = null;
    private boolean startupFinished = false;

    public ConnectionDialog(final String appName) {
        super(appName + " - Connect to iLO");
        this.createUI();
        this.loadProfiles();
    }

    private void createUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(this.createFavouritesPanel(), BorderLayout.WEST);
        content.add(this.createFormPanel(), BorderLayout.CENTER);

        final ActionListener connectAction = e -> this.doConnect();
        this.connectButton.addActionListener(connectAction);
        this.passField.addActionListener(connectAction);
        this.aboutButton.addActionListener(e -> new aboutJircDialog(this, "About"));

        this.getRootPane().setDefaultButton(this.connectButton);
        this.setContentPane(content);
        this.pack();
        this.setMinimumSize(this.getSize());
        this.setLocationRelativeTo(null);
    }

    private JComponent createFavouritesPanel() {
        final JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Favourites"));

        this.profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.profileList.setVisibleRowCount(8);
        this.profileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.onProfileSelected();
            }
        });
        final JScrollPane scroll = new JScrollPane(this.profileList);
        scroll.setPreferredSize(new Dimension(180, 0));
        panel.add(scroll, BorderLayout.CENTER);

        final JPanel buttons = new JPanel(new GridLayout(1, 3, 4, 0));
        this.newButton.addActionListener(e -> this.clearForm());
        this.saveButton.addActionListener(e -> this.saveProfile());
        this.deleteButton.addActionListener(e -> this.deleteProfile());
        buttons.add(this.newButton);
        buttons.add(this.saveButton);
        buttons.add(this.deleteButton);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent createFormPanel() {
        final JPanel form = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        ConnectionDialog.addRow(form, gbc, 0, "Server Name", this.nameField);
        ConnectionDialog.addRow(form, gbc, 1, "IP/FQDN(:port)", this.hostField);
        ConnectionDialog.addRow(form, gbc, 2, "Username", this.userField);
        ConnectionDialog.addRow(form, gbc, 3, "Password", this.passField);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        form.add(this.savePasswordCheck, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        form.add(this.aboutButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(this.connectButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        this.statusLabel.setForeground(Color.GRAY);
        form.add(this.statusLabel, gbc);

        return form;
    }

    private static void addRow(final JPanel form, final GridBagConstraints gbc, final int row, final String label, final JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = (double) 0;
        form.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(field, gbc);
    }

    // --- Favourites CRUD -------------------------------------------------

    private void loadProfiles() {
        this.setProfiles(this.store.load());
    }

    private void setProfiles(final List<ServerProfile> profiles) {
        this.profileModel.clear();
        for (final ServerProfile p : profiles) {
            this.profileModel.addElement(p);
        }
    }

    private void onProfileSelected() {
        final ServerProfile p = this.profileList.getSelectedValue();
        if (null == p) {
            return;
        }
        this.nameField.setText(p.getName());
        this.hostField.setText(p.getHost());
        this.userField.setText(p.getUser());
        this.savePasswordCheck.setSelected(p.isSavePassword());
        this.passField.setText(p.isSavePassword() ? p.getPassword() : "");
    }

    private void clearForm() {
        this.profileList.clearSelection();
        this.nameField.setText("");
        this.hostField.setText("");
        this.userField.setText("");
        this.passField.setText("");
        this.savePasswordCheck.setSelected(false);
        this.nameField.requestFocusInWindow();
    }

    private void saveProfile() {
        final String name = this.nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter a name for this profile before saving.",
                    "Name required", JOptionPane.WARNING_MESSAGE);
            this.nameField.requestFocusInWindow();
            return;
        }

        final boolean savePass = this.savePasswordCheck.isSelected();
        final char[] pass = this.passField.getPassword();
        final ServerProfile profile = new ServerProfile(
                name,
                this.hostField.getText().trim(),
                this.userField.getText().trim(),
                savePass ? new String(pass) : "",
                savePass);
        java.util.Arrays.fill(pass, '\0');

        try {
            final List<ServerProfile> updated = this.store.save(profile);
            this.setProfiles(updated);
            this.selectByName(name);
            this.statusLabel.setText("Saved \"" + name + "\".");
        } catch (final RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Could not save profile", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProfile() {
        final ServerProfile selected = this.profileList.getSelectedValue();
        final String name = null != selected ? selected.getName() : this.nameField.getText().trim();
        if (null == name || name.isEmpty()) {
            return;
        }
        final int answer = JOptionPane.showConfirmDialog(this,
                "Delete profile \"" + name + "\"?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION != answer) {
            return;
        }
        try {
            final List<ServerProfile> updated = this.store.delete(name);
            this.setProfiles(updated);
            this.clearForm();
            this.statusLabel.setText("Deleted \"" + name + "\".");
        } catch (final RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Could not delete profile", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectByName(final String name) {
        for (int i = 0; i < this.profileModel.size(); i++) {
            if (name.equalsIgnoreCase(this.profileModel.get(i).getName())) {
                this.profileList.setSelectedIndex(i);
                this.profileList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    // --- Connect flow ----------------------------------------------------

    private void doConnect() {
        final String host = this.hostField.getText();
        final String user = this.userField.getText();
        final char[] pass = this.passField.getPassword();

        this.setBusy(true, "Connecting...");

        final SwingWorker<IrcSessionContext, Void> worker = new SwingWorker<IrcSessionContext, Void>() {
            protected IrcSessionContext doInBackground() throws Exception {
                return IloAuthClient.login(host, user, pass);
            }

            protected void done() {
                java.util.Arrays.fill(pass, '\0');
                try {
                    final IrcSessionContext ctx = this.get();
                    ConnectionDialog.this.startEngine(ctx);
                } catch (final Exception ex) {
                    final Throwable cause = null != ex.getCause() ? ex.getCause() : ex;
                    ConnectionDialog.this.setBusy(false, " ");
                    JOptionPane.showMessageDialog(
                            ConnectionDialog.this,
                            cause.getMessage(),
                            "Connection failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void startEngine(final IrcSessionContext ctx) {
        this.statusLabel.setText("Starting remote console...");
        this.startupFinished = false;

        this.readyTimeout = new Timer(ConnectionDialog.READY_TIMEOUT_MS, e -> this.consoleReady());
        this.readyTimeout.setRepeats(false);
        this.readyTimeout.start();

        final App engine = new App(ctx);
        engine.launch(
                this::consoleReady,
                err -> {
                    this.stopTimeout();
                    this.setBusy(false, " ");
                    final String msg = null != err && null != err.getMessage() ? err.getMessage() : String.valueOf(err);
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to start the remote console: " + msg,
                            "Startup failed",
                            JOptionPane.ERROR_MESSAGE);
                },
                this::consoleClosed);
    }

    /**
     * Console is up and responsive: hide this window (kept alive so it can be
     * shown again when the console closes). Runs once per connect.
     */
    private void consoleReady() {
        if (this.startupFinished) {
            return;
        }
        this.startupFinished = true;
        this.stopTimeout();
        this.setVisible(false);
    }

    /**
     * The remote console was torn down: come back to this dialog so the user can
     * connect again, surfacing {@code reason} (if any) as an error. Closing this
     * dialog (see {@code EXIT_ON_CLOSE}) ends the process.
     */
    private void consoleClosed(final String reason) {
        this.stopTimeout();
        this.startupFinished = false;
        this.setBusy(false, " ");
        this.setVisible(true);
        this.toFront();
        this.requestFocus();
        if (null != reason && !reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    reason,
                    "Disconnected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void stopTimeout() {
        if (null != this.readyTimeout) {
            this.readyTimeout.stop();
            this.readyTimeout = null;
        }
    }

    private void setBusy(final boolean busy, final String status) {
        this.connectButton.setEnabled(!busy);
        this.nameField.setEnabled(!busy);
        this.hostField.setEnabled(!busy);
        this.userField.setEnabled(!busy);
        this.passField.setEnabled(!busy);
        this.savePasswordCheck.setEnabled(!busy);
        this.profileList.setEnabled(!busy);
        this.newButton.setEnabled(!busy);
        this.saveButton.setEnabled(!busy);
        this.deleteButton.setEnabled(!busy);
        this.statusLabel.setText(status);
    }
}
