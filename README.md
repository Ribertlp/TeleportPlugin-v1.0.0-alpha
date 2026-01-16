# 🏠 Hytale Teleport Plugin

## 📋 Plugin Information
- **Name**: TeleportPlugin
- **Version**: 1.0.0
- **Authors**: Ribertlp & Claude Code
- **License**: Open Source
- **Hytale Server Version**: All versions

## 📖 Description
A comprehensive home teleportation system for Hytale servers. Players can set, manage, and teleport to personal home locations with an easy-to-use command system.

## ✨ Features
- **🏠 Set Homes**: Create personal teleportation points (with rotation)
- **🚀 Teleport**: Travel to saved home locations with cooldown system
- **📋 List Homes**: View all saved homes with coordinates and rotation
- **🗑️ Delete Homes**: Remove unwanted home locations
- **💾 JSON Data Storage**: Thread-safe persistent storage system
- **⏱️ Cooldown System**: Configurable teleport delays (3s default)
- **🎯 Rotation Storage**: Saves and restores yaw, pitch, roll
- **🚶 Movement Detection**: Cancels teleport when player moves (planned)
- **🔒 Thread-Safe**: Proper Hytale API threading implementation
- **⚡ Fast Performance**: Optimized for server efficiency

## 🎮 Commands

### Main Command
```
/home - Show help and available subcommands
```

### Subcommands
| Command | Description | Usage |
|---------|-------------|-------|
| `/home set <name>` | Set a home at current location | `/home set myhouse` |
| `/home tp <name>` | Teleport to a saved home | `/home tp myhouse` |
| `/home list` | Show all your homes | `/home list` |
| `/home delete <name>` | Delete a home | `/home delete myhouse` |

### Command Examples
```bash
# Set a home called "base" (saves current position and rotation)
/home set base
# Output: "§aHome '§6base§a' wurde gesetzt!"

# Teleport to your base (with 3-second cooldown)
/home tp base
# Output: "§7Teleportation zu §6base§7 startet in 3 Sekunden..."
# Output: "§aDu wurdest zu '§6base§a' teleportiert!"

# List all your homes (shows coordinates and rotation)
/home list
# Output: "§6base §7- §fworld §7(§f123.4§7, §f64.0§7, §f456.7§7) §8[Y:90.5 P:0.0 R:0.0]"

# Delete a home
/home delete base
# Output: "§aHome '§6base§a' wurde gelöscht!"
```

## 🔧 Technical Details

### Threading Architecture
This plugin uses **proper Hytale API threading** with the `world.execute()` pattern:
```java
world.execute(() -> {
    // All component access happens in the correct world thread
    TransformComponent transformComponent = store.ensureAndGetComponent(ref, TransformComponent.getComponentType());
    // Teleportation logic here
});
```

### Teleportation System
Uses a **triple-redundancy approach** for maximum compatibility:
1. `transformComponent.getPosition().assign(homePosition)`
2. `store.addComponent(ref, Teleport.getComponentType(), new Teleport(...))`
3. `transformComponent.getTransform().setPosition(homePosition)`

## 📁 Project Structure
```
src/
├── main/
│   ├── java/com/example/teleportplugin/
│   │   ├── TeleportPlugin.java              # Main plugin class
│   │   ├── commands/
│   │   │   ├── HomeCommand.java             # Main command handler
│   │   │   └── subcommands/
│   │   │       ├── HomeSetCommand.java      # Set home functionality
│   │   │       ├── HomeTpCommand.java       # Teleport functionality
│   │   │       ├── HomeListCommand.java     # List homes functionality
│   │   │       └── HomeDeleteCommand.java   # Delete home functionality
│   │   ├── data/
│   │   │   └── HomeManager.java             # Data management
│   │   ├── config/
│   │   │   └── PluginConfig.java            # Configuration system
│   │   ├── cooldown/
│   │   │   ├── TeleportCooldown.java        # Individual cooldown instances
│   │   │   └── TeleportCooldownManager.java # Cooldown management
│   │   ├── permissions/
│   │   │   └── PermissionManager.java       # Permission handling
│   │   └── systems/
│   │       └── PlayerMovementSystem.java    # Movement detection
│   └── resources/
│       ├── plugin.json                      # Plugin manifest
│       └── README.md                        # This file
```

## 💾 Data Storage

### JSON Structure
Homes are stored in individual JSON files per player:
```
plugins/TeleportPlugin/
├── config.json                    # Plugin configuration
└── homes/
    ├── player-uuid-1.json         # Player 1's homes
    ├── player-uuid-2.json         # Player 2's homes
    └── ...
```

### Configuration
```json
{
  "teleportCooldownSeconds": 3,
  "enableMovementCancellation": true,
  "defaultMaxHomes": 3,
  "enableCrossWorldTeleportation": true
}
```

### Home Data Format
```json
{
  "spawn": {
    "worldId": "world",
    "x": 0.0,
    "y": 64.0,
    "z": 0.0,
    "yaw": 90.5,
    "pitch": 0.0,
    "roll": 0.0
  }
}
```

## 🚀 Installation

### For Server Administrators
1. Download `TeleportPlugin-1.0.0.jar`
2. Place in your server's `mods/` directory
3. Restart the server
4. Plugin will automatically create necessary directories

### For Developers
1. Clone this repository
2. Run `./gradlew shadowJar`
3. Find built JAR in `build/libs/`

## 🛠️ Building from Source

### Requirements
- Java 17+
- Gradle 7.0+
- Hytale Server API

### Build Commands
```bash
# Clean and build
./gradlew clean shadowJar

# Build only
./gradlew shadowJar

# Run tests
./gradlew test
```

## 🐛 Known Issues & Solutions

### Threading Errors
**Issue**: `Assert not in thread! WorldThread but was in ForkJoinPool`
**Solution**: All store/component operations are wrapped in `world.execute()`

### Teleportation Not Working
**Issue**: Success message shown but player doesn't move
**Solution**: Uses triple-method approach for maximum compatibility

### Movement Detection Not Working
**Issue**: Teleport doesn't cancel when player moves
**Status**: ⚠️ Known limitation - requires Hytale Player Movement Events
**Workaround**: System is implemented but needs real Hytale API integration

### TimerTask isCancelled() Error
**Issue**: `Cannot resolve method 'isCancelled' in 'TimerTask'`
**Solution**: ✅ Fixed - removed isCancelled() calls, using only cancel()

### Plugin Not Loading
**Issue**: Plugin doesn't appear in server
**Solution**: Check `plugin.json` manifest and JAR placement in `mods/` folder

## 🔍 Debugging

### Enable Debug Output
The plugin includes extensive debug logging:
```java
System.out.println("[HomeSetCommand] Player set home 'example' at 123,64,456");
System.out.println("[HomeTpCommand] Player teleported to home 'example'");
```

### Common Debug Steps
1. Check server console for error messages
2. Verify `plugins/TeleportPlugin/` directory creation
3. Check JSON files for proper formatting
4. Test with simple home names (no special characters)

## 📚 API Documentation

### HomeManager Methods
```java
// Set a home (with rotation)
homeManager.setHome(String playerId, String homeName, String worldId,
                   double x, double y, double z, float yaw, float pitch, float roll)

// Set a home (legacy, no rotation)
homeManager.setHome(String playerId, String homeName, String worldId, double x, double y, double z)

// Get a home
Optional<HomeLocation> home = homeManager.getHome(String playerId, String homeName)

// List homes
Set<String> homeNames = homeManager.getPlayerHomes(String playerId)

// Delete a home
boolean success = homeManager.deleteHome(String playerId, String homeName)

// Check if home exists
boolean exists = homeManager.hasHome(String playerId, String homeName)

// Get home count
int count = homeManager.getHomeCount(String playerId)
```

### HomeLocation Class
```java
public static class HomeLocation {
    private final String worldId;
    private final double x, y, z;
    private final float yaw, pitch, roll;

    // Getters
    public String getWorldId() { return worldId; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public float getRoll() { return roll; }
}
```

### Cooldown System API
```java
// Start a cooldown
cooldownManager.startCooldown(String playerId, String homeName,
                             Runnable onSuccess, Runnable onCancel);

// Check if player has cooldown
boolean hasCooldown = cooldownManager.hasCooldown(String playerId);

// Get remaining seconds
int remaining = cooldownManager.getRemainingSeconds(String playerId);

// Cancel cooldown
cooldownManager.cancelCooldown(String playerId);
```

## 🤝 Contributing

This is an open-source project! Contributions welcome:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

### Development Guidelines
- Follow existing code style
- Add debug logging for new features
- Test all threading-critical operations
- Update documentation for new commands

## 📄 License

**Open Source** - Free to use, modify, and distribute.

## 🙏 Credits

- **Ribertlp**: Original concept, testing, and project management
- **Claude Code**: Implementation, threading solutions, and documentation
- **Hytale Community**: API research and feedback

## 📞 Support

- **Issues**: Report bugs on GitHub Issues
- **Email**: robert@grafy.org
- **Website**: https://grafy.org (coming soon)

---

**Built with ❤️ for the Hytale community**

*This plugin serves as a foundation and learning resource for other Hytale plugin developers. The threading solutions and API patterns can be applied to many other plugin types.*
