package com.example.teleportplugin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration management for TeleportPlugin
 */
public class PluginConfig {
    private static final String CONFIG_FILE = "teleport_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration values with defaults
    public int teleportCooldownSeconds = 3;
    public boolean enableMovementCancellation = false; // Temporarily disabled until proper API access
    public String permissionPrefix = "teleport";
    public int defaultMaxHomes = 5;

    // Permission nodes
    public String getHomeSetPermission() {
        return permissionPrefix + ".home.set";
    }

    public String getHomeTpPermission() {
        return permissionPrefix + ".home.tp";
    }

    public String getHomeDeletePermission() {
        return permissionPrefix + ".home.delete";
    }

    public String getHomeListPermission() {
        return permissionPrefix + ".home.list";
    }

    public String getHomeSetLimitPermission(int limit) {
        return permissionPrefix + ".home.set." + limit;
    }

    /**
     * Load configuration from file or create default
     */
    public static PluginConfig load(Path configDir) {
        Path configFile = configDir.resolve(CONFIG_FILE);

        try {
            if (Files.exists(configFile)) {
                String json = Files.readString(configFile);
                PluginConfig config = GSON.fromJson(json, PluginConfig.class);
                System.out.println("[PluginConfig] Loaded configuration from " + configFile);
                return config;
            } else {
                PluginConfig config = new PluginConfig();
                config.save(configDir);
                System.out.println("[PluginConfig] Created default configuration at " + configFile);
                return config;
            }
        } catch (IOException e) {
            System.err.println("[PluginConfig] Error loading config: " + e.getMessage());
            System.out.println("[PluginConfig] Using default configuration");
            return new PluginConfig();
        }
    }

    /**
     * Save configuration to file
     */
    public void save(Path configDir) {
        try {
            Files.createDirectories(configDir);
            Path configFile = configDir.resolve(CONFIG_FILE);
            String json = GSON.toJson(this);
            Files.writeString(configFile, json);
            System.out.println("[PluginConfig] Saved configuration to " + configFile);
        } catch (IOException e) {
            System.err.println("[PluginConfig] Error saving config: " + e.getMessage());
        }
    }
}