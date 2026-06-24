# Java Integrated Remote Console (JIRC)

A standalone remote console for **HP iLO 4** era servers, so you can manage older
hardware without fighting browser delay or obsolete Java Web Start.

> **Disclaimer:** This project is **not affiliated with, endorsed by, or supported
> by HP / HPE in any way.** It is a community effort to keep iLO 4 era hardware
> usable. Use it entirely at your own risk.

## Why

HP's original Integrated Remote Console shipped as a JNLP launched applet driven
by Java Web Start. That stack is effectively dead on modern systems. JIRC wraps
the same remote console engine in a plain desktop application with a normal login
flow, so all you need is a recent JRE.

## Features

- **No Java Web Start / JNLP** - runs as an ordinary `.jar`.
- **Login screen** - enter `host[:port]`, username, and password (port defaults to `443`).
- **Saved server profiles** - create, edit, and delete server profiles for one click reconnects.
- **Remember password** (optional, per profile).
- **Self-signed certificate friendly** - connects to iLO's default self-signed HTTPS cert without manual trust setup.
- **Graceful disconnects** - access-denied, seize, session-ended, and similar events return you to the connection dialog
  with a message instead of leaving a dead window.
- **POST codes history** - you don't have to try to catch them by eyes anymore.

## Requirements

- **Java 8 or newer** (built against Java 8 bytecode).
- An **HP iLO 4** target reachable over HTTPS.

## Run

```bash
java -jar jirc.jar
```

### Where settings live

Profiles are stored at:

```
~/.jirc/profiles.properties
```

> **Security note:** Saved passwords are only lightly **obfuscated, not
> encrypted**. Anyone with read access to this file can recover them. Avoid the
> "Save password" option on shared machines.

## Contributing

Contributions are welcome!

- Found a bug or have a request? **Open an issue.**
- Have a fix or feature? **Send a pull request.** Small, focused PRs are easiest to review.
- Especially helpful: testing against different iLO 4 firmware revisions, virtual
  media, keyboard/locale handling, and platform-specific quirks (Linux/Windows/macOS).

Please keep the codebase on **Java 8** and free of deprecated APIs where a modern
equivalent exists.
