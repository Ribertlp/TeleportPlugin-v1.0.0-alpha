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
- **🏠 Set Homes**: Create personal teleportation points
- **🚀 Teleport**: Instantly travel to saved home locations
- **📋 List Homes**: View all saved homes with coordinates
- **🗑️ Delete Homes**: Remove unwanted home locations
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
# Set a home called "base"
/home set base

# Teleport to your base
/home tp base

# List all your homes
/home list

# Delete a home
/home delete base
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
│   │   └── data/
│   │       └── HomeManager.java             # Data management
│   └── resources/
│       ├── plugin.json                      # Plugin manifest
│       └── README.md                        # This file
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
// Set a home
homeManager.setHome(String playerId, String homeName, double x, double y, double z)

// Get a home
HomeLocation home = homeManager.getHome(String playerId, String homeName)

// List homes
Map<String, HomeLocation> homes = homeManager.getHomes(String playerId)

// Delete a home
homeManager.deleteHome(String playerId, String homeName)
```

### HomeLocation Class
```java
public static class HomeLocation {
    public double x, y, z;
    public String world;
    // Constructor and methods
}
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
- **Website**: https://grafy.org

---

**Built with ❤️ for the Hytale community**

*This plugin serves as a foundation and learning resource for other Hytale plugin developers. The threading solutions and API patterns can be applied to many other plugin types.*
