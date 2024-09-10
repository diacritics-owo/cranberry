# Cranberry

## Client Installation

If installed only on the client, pressing the keybind (M by default) will open a screen that shows what music is currently being played and allows you to play/pause it. By default, the background of the screen is dynamically tinted with the dominant color from the image - this can be disabled in the configuration (see [Configuration Options](#configuration-options)).

## Server Installation

Installing the mod on the server allows clients to see each other's music statuses - clients may disable sending or receiving statuses. Note that if this mod is installed on the server, [owo-lib](https://modrinth.com/mod/owo-lib) (a library mod that this mod depends on) ***must*** be installed on every connecting client, or they will not be able to connect due to registry mismatches.

## Configuration Options

You may edit configuration values in `config/cranberry-client-config.json5`. Alternatively, installing Mod Menu will allow you to change the following configuration options via a GUI:

- Send music statuses (default: true; only takes effect when playing multiplayer)
- Receive music statuses (default: true; only takes effect when playing multiplayer)
- Dynamic background (default: true)

## Support

- [x] macOS (via `MediaRemote`)
- [ ] Linux (via MPRIS)
- [ ] Windows (via `MediaPlayer`)
