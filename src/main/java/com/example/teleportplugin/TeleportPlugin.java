package com.example.teleportplugin;

import com.example.teleportplugin.commands.HomeCommand;
import com.example.teleportplugin.config.PluginConfig;
import com.example.teleportplugin.cooldown.TeleportCooldown;
import com.example.teleportplugin.cooldown.TeleportCooldownManager;
import com.example.teleportplugin.data.HomeManager;
import com.example.teleportplugin.permissions.PermissionManager;
import com.example.teleportplugin.systems.PlayerMovementSystem;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TeleportPlugin extends JavaPlugin {
    private static TeleportPlugin instance;
    private PluginConfig config;
    private HomeManager homeManager;
    private TeleportCooldownManager cooldownManager;
    private PermissionManager permissionManager;
    private PlayerMovementSystem movementSystem;
    private HomeCommand homeCommand;

    public TeleportPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
        System.out.println("[TeleportPlugin] Plugin loaded!");
    }

    @Override
    protected void setup() {
        super.setup();

        // Initialize configuration
        Path configDir = getPluginDataDirectory();
        config = PluginConfig.load(configDir);

        // Initialize managers
        homeManager = new HomeManager(configDir);
        cooldownManager = new TeleportCooldownManager(config.teleportCooldownSeconds);
        permissionManager = new PermissionManager(this);
        movementSystem = new PlayerMovementSystem();

        // Start movement detection system (placeholder implementation)
        movementSystem.start();

        // Register commands
        homeCommand = new HomeCommand();
        this.getCommandRegistry().registerCommand(homeCommand);

        System.out.println("[TeleportPlugin] Enhanced home system initialized!");
        System.out.println("[TeleportPlugin] Features: JSON persistence, " + config.teleportCooldownSeconds + "s cooldown, permission limits");
        System.out.println("[TeleportPlugin] Commands: /home set <name>, /home tp <name>, /home delete <name>, /home list");
    }

    @Override
    protected void shutdown() {
        // Save all data before shutdown
        if (homeManager != null) {
            homeManager.saveAll();
        }

        // Shutdown cooldown system
        if (cooldownManager != null) {
            cooldownManager.shutdown();
        }

        // Shutdown movement system
        if (movementSystem != null) {
            movementSystem.shutdown();
        }

        // Shutdown cooldown timer
        TeleportCooldown.shutdownTimer();

        System.out.println("[TeleportPlugin] Plugin shutdown complete");
        super.shutdown();
    }

    public static TeleportPlugin getInstance() {
        return instance;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public PluginConfig getConfig() {
        return config;
    }

    public TeleportCooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    /**
     * Get plugin data directory for file storage
     */
    private Path getPluginDataDirectory() {
        // TODO: Use actual Hytale plugin data directory method
        // For now, use a relative path
        return Paths.get("plugins", "TeleportPlugin");
    }
}