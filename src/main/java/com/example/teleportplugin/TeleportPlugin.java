package com.example.teleportplugin;

import com.example.teleportplugin.commands.HomeCommand;
import com.example.teleportplugin.data.HomeManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class TeleportPlugin extends JavaPlugin {
    private static TeleportPlugin instance;
    private HomeManager homeManager;
    private HomeCommand homeCommand;

    public TeleportPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
        System.out.println("[TeleportPlugin] Plugin loaded!");
    }

    @Override
    protected void setup() {
        super.setup();

        // Initialize data manager
        homeManager = new HomeManager();

        // Register commands
        homeCommand = new HomeCommand();
        this.getCommandRegistry().registerCommand(homeCommand);

        System.out.println("[TeleportPlugin] Home system initialized!");
        System.out.println("[TeleportPlugin] Commands: /home set <name>, /home tp <name>, /home delete <name>, /home list");
    }

    public static TeleportPlugin getInstance() {
        return instance;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }
}