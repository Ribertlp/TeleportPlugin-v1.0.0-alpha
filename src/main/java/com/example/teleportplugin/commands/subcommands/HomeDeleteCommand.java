package com.example.teleportplugin.commands.subcommands;

import com.example.teleportplugin.TeleportPlugin;
import com.example.teleportplugin.data.HomeManager;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import javax.annotation.Nonnull;

/**
 * /home delete <name> - Delete a home
 */
public class HomeDeleteCommand extends CommandBase {

    private final RequiredArg<String> homeNameArg = withRequiredArg("name", "Name of the home to delete", ArgTypes.STRING);

    public HomeDeleteCommand() {
        super("delete", "Delete one of your homes - /home delete <name>");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        try {
            // Check if executed by player
            if (!context.isPlayer()) {
                context.sendMessage(Message.raw("This command can only be used by players!").color("#ff5555"));
                return;
            }

            String playerId = context.sender().getDisplayName();
            String homeName = homeNameArg.get(context);

            // Delete home
            HomeManager homeManager = TeleportPlugin.getInstance().getHomeManager();
            boolean deleted = homeManager.deleteHome(playerId, homeName);

            if (!deleted) {
                context.sendMessage(Message.raw("[X] Home '" + homeName + "' not found!").color("#ff5555"));
                context.sendMessage(Message.raw("Use '/home list' to see your homes.").color("#aaaaaa"));
                return;
            }

            // Send success message
            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
            context.sendMessage(Message.raw("|        HOME DELETED                |").color("#ff5555").bold(true));
            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
            context.sendMessage(Message.raw("Deleted home: ").color("#ffaa00").bold(true)
                    .insert(Message.raw(homeName).color("#ffffff")));
            context.sendMessage(Message.raw("This action cannot be undone!").color("#aaaaaa"));

            System.out.println("[HomeDeleteCommand] Player " + playerId + " deleted home '" + homeName + "'");

        } catch (Exception e) {
            System.err.println("[HomeDeleteCommand] Error: " + e.getMessage());
            e.printStackTrace();
            context.sendMessage(Message.raw("[X] Error deleting home!").color("#ff5555"));
        }
    }
}