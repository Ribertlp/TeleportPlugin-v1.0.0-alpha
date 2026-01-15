package com.example.teleportplugin.commands.subcommands;

import com.example.teleportplugin.TeleportPlugin;
import com.example.teleportplugin.data.HomeManager;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import javax.annotation.Nonnull;
import java.util.Set;

/**
 * /home list - List all homes
 */
public class HomeListCommand extends CommandBase {

    public HomeListCommand() {
        super("list", "List all your homes - /home list");
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

            // Get player homes
            HomeManager homeManager = TeleportPlugin.getInstance().getHomeManager();
            Set<String> homeNames = homeManager.getHomeNames(playerId);

            // Send header
            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
            context.sendMessage(Message.raw("|           YOUR HOMES               |").color("#ffaa00").bold(true));
            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));

            if (homeNames.isEmpty()) {
                context.sendMessage(Message.raw("You have no homes set!").color("#aaaaaa"));
                context.sendMessage(Message.raw("Use '/home set <name>' to create one.").color("#aaaaaa"));
            } else {
                context.sendMessage(Message.raw("Total homes: ").color("#ffaa00").bold(true)
                        .insert(Message.raw(String.valueOf(homeNames.size())).color("#ffffff")));
                context.sendMessage(Message.raw("").color("#ffffff")); // Empty line

                // List all homes with their locations
                int count = 1;
                for (String homeName : homeNames) {
                    HomeManager.HomeLocation home = homeManager.getHome(playerId, homeName);

                    context.sendMessage(Message.raw(count + ". ").color("#aaaaaa")
                            .insert(Message.raw(homeName).color("#00ff00").bold(true))
                            .insert(Message.raw(" - ").color("#aaaaaa"))
                            .insert(Message.raw(home.toString()).color("#ffffff")));

                    count++;
                }

                context.sendMessage(Message.raw("").color("#ffffff")); // Empty line
                context.sendMessage(Message.raw("Use '/home tp <name>' to teleport!").color("#aaaaaa"));
            }

            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));

            System.out.println("[HomeListCommand] Player " + playerId + " listed homes (" + homeNames.size() + " total)");

        } catch (Exception e) {
            System.err.println("[HomeListCommand] Error: " + e.getMessage());
            e.printStackTrace();
            context.sendMessage(Message.raw("[X] Error listing homes!").color("#ff5555"));
        }
    }
}