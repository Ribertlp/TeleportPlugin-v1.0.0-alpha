package com.example.teleportplugin.commands.subcommands;

import com.example.teleportplugin.TeleportPlugin;
import com.example.teleportplugin.cooldown.TeleportCooldownManager;
import com.example.teleportplugin.data.HomeManager;
import com.example.teleportplugin.data.HomeData.HomeLocation;
import com.example.teleportplugin.permissions.PermissionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class HomeTpCommand extends CommandBase {

    private final RequiredArg<String> homeNameArg = withRequiredArg("name",
        "Name of the home to teleport to", ArgTypes.STRING);

    public HomeTpCommand() {
        super("tp", "Teleport to a home - /home tp <name>");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        Ref<EntityStore> ref = context.senderAsPlayerRef();
        if (ref == null || !ref.isValid()) {
            context.sendMessage(Message.raw("Player not in world!").color("#ff5555"));
            return;
        }

        Store<EntityStore> store = ref.getStore();
        World world = ((EntityStore) store.getExternalData()).getWorld();

        try {
            // TODO: Player API temporarily disabled until we have proper access
            // Get player entity for permission checks
            // Player player = (Player) store.ensureAndGet(ref);
            PermissionManager permissionManager = TeleportPlugin.getInstance().getPermissionManager();
            TeleportCooldownManager cooldownManager = TeleportPlugin.getInstance().getCooldownManager();

            // TODO: Permission checks temporarily disabled
            // Check permission
            // if (!permissionManager.canTeleportToHome(player)) {
            //     context.sendMessage(Message.raw("You don't have permission to teleport to homes!").color("#ff5555"));
            //     return;
            // }

            String homeName = homeNameArg.get(context);
            String playerId = context.sender().getDisplayName();

            // Check if player already has a cooldown
            if (cooldownManager.hasCooldown(playerId)) {
                int remaining = cooldownManager.getRemainingTime(playerId);
                context.sendMessage(Message.raw("You're already teleporting! Wait " + remaining + " more seconds.").color("#ff5555"));
                return;
            }

            HomeManager homeManager = TeleportPlugin.getInstance().getHomeManager();
            HomeLocation home = homeManager.getHome(playerId, homeName);

            if (home == null) {
                context.sendMessage(Message.raw("Home '" + homeName + "' not found!").color("#ff5555"));
                context.sendMessage(Message.raw("Use '/home list' to see your homes.").color("#aaaaaa"));
                return;
            }

            // Start teleport cooldown
            int cooldownSeconds = TeleportPlugin.getInstance().getConfig().teleportCooldownSeconds;

            context.sendMessage(Message.raw("Teleporting to home '" + homeName + "' in " + cooldownSeconds + " seconds...").color("#ffaa00"));
            context.sendMessage(Message.raw("Don't move or the teleport will be cancelled!").color("#aaaaaa"));

            // Define success callback
            Runnable onSuccess = () -> {
                // Execute teleport in the correct world thread
                world.execute(() -> {
                    try {
                        TransformComponent transformComponent = (TransformComponent) store.ensureAndGetComponent(ref, TransformComponent.getComponentType());
                        if (transformComponent == null) {
                            context.sendMessage(Message.raw("Unable to teleport you!").color("#ff5555"));
                            return;
                        }

                        Vector3d homePosition = new Vector3d(home.x, home.y, home.z);
                        Vector3f currentRotation = transformComponent.getRotation().clone();

                        // Try multiple teleportation methods
                        transformComponent.getPosition().assign(homePosition);
                        store.addComponent(ref, Teleport.getComponentType(), new Teleport(homePosition, currentRotation));
                        transformComponent.getTransform().setPosition(homePosition);

                        context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
                        context.sendMessage(Message.raw("|       TELEPORT SUCCESSFUL!        |").color("#00ff00").bold(true));
                        context.sendMessage(Message.raw("+====================================+").color("#55ffff"));
                        context.sendMessage(Message.raw("Home: ").color("#ffaa00").bold(true)
                                .insert(Message.raw(homeName).color("#ffffff")));
                        context.sendMessage(Message.raw("Location: ").color("#ffaa00").bold(true)
                                .insert(Message.raw(String.format("%.1f, %.1f, %.1f", home.x, home.y, home.z)).color("#ffffff")));

                        System.out.println("[HomeTpCommand] Player " + playerId + " teleported to home '" + homeName + "' at " + home.x + "," + home.y + "," + home.z);

                    } catch (Exception e) {
                        System.err.println("[HomeTpCommand] Error in teleport execution: " + e.getMessage());
                        e.printStackTrace();
                        context.sendMessage(Message.raw("[X] Error teleporting to home!").color("#ff5555"));
                    }
                });
            };

            // Define cancel callback
            Runnable onCancel = () -> {
                context.sendMessage(Message.raw("Teleport cancelled!").color("#ff5555"));
            };

            // Start the cooldown
            cooldownManager.startCooldown(playerId, homeName, onSuccess, onCancel);

        } catch (Exception e) {
            System.err.println("[HomeTpCommand] Error: " + e.getMessage());
            e.printStackTrace();
            context.sendMessage(Message.raw("[X] Error teleporting to home!").color("#ff5555"));
        }
    }
}