package com.example.teleportplugin.commands.subcommands;

import com.example.teleportplugin.TeleportPlugin;
import com.example.teleportplugin.data.HomeManager;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class HomeSetCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> homeNameArg = withRequiredArg("name",
        "Name of the home to set", ArgTypes.STRING);

    public HomeSetCommand() {
        super("set", "Set a home at your current location - /home set <name>");
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref,
                          @Nonnull PlayerRef playerRef,
                          @Nonnull World world) {
        try {
            String homeName = homeNameArg.get(context);

            if (homeName.length() > 16) {
                context.sendMessage(Message.raw("Home name too long! Maximum 16 characters.").color("#ff5555"));
                return;
            }

            if (!homeName.matches("[a-zA-Z0-9_-]+")) {
                context.sendMessage(Message.raw("Home name can only contain letters, numbers, _ and -").color("#ff5555"));
                return;
            }

            TransformComponent transformComponent = (TransformComponent) store.ensureAndGetComponent(ref, TransformComponent.getComponentType());
            if (transformComponent == null) {
                context.sendMessage(Message.raw("Unable to get your position!").color("#ff5555"));
                return;
            }

            Vector3d position = transformComponent.getTransform().getPosition();
            double x = position.getX();
            double y = position.getY();
            double z = position.getZ();

            HomeManager homeManager = TeleportPlugin.getInstance().getHomeManager();
            String playerId = context.sender().getDisplayName();
            homeManager.setHome(playerId, homeName, x, y, z);

            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
            context.sendMessage(Message.raw("|         HOME SET SUCCESSFUL        |").color("#00ff00").bold(true));
            context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
            context.sendMessage(Message.raw("Home Name: ").color("#ffaa00").bold(true)
                    .insert(Message.raw(homeName).color("#ffffff")));
            context.sendMessage(Message.raw("Position: ").color("#ffaa00").bold(true)
                    .insert(Message.raw(String.format("%.1f, %.1f, %.1f", x, y, z)).color("#ffffff")));
            context.sendMessage(Message.raw("Use '/home tp " + homeName + "' to teleport!").color("#aaaaaa"));

            System.out.println("[HomeSetCommand] Player " + playerId + " set home '" + homeName + "' at " + x + "," + y + "," + z);

        } catch (Exception e) {
            System.err.println("[HomeSetCommand] Error: " + e.getMessage());
            e.printStackTrace();
            context.sendMessage(Message.raw("[X] Error setting home!").color("#ff5555"));
        }
    }
}