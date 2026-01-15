package com.example.teleportplugin.commands;

import com.example.teleportplugin.commands.subcommands.HomeDeleteCommand;
import com.example.teleportplugin.commands.subcommands.HomeListCommand;
import com.example.teleportplugin.commands.subcommands.HomeSetCommand;
import com.example.teleportplugin.commands.subcommands.HomeTpCommand;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Main /home command with subcommands
 * Usage: /home <set|tp|delete|list> [args]
 */
public class HomeCommand extends AbstractCommandCollection {

    public HomeCommand() {
        super("home", "Teleport home system - /home <set|tp|delete|list>");

        // Add all subcommands
        addSubCommand((AbstractCommand) new HomeSetCommand());
        addSubCommand((AbstractCommand) new HomeTpCommand());
        addSubCommand((AbstractCommand) new HomeDeleteCommand());
        addSubCommand((AbstractCommand) new HomeListCommand());

        System.out.println("[HomeCommand] Home command system registered with subcommands");
    }
}