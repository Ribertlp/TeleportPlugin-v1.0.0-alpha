package com.example.teleportplugin.permissions;

import com.example.teleportplugin.TeleportPlugin;
import com.hypixel.hytale.server.core.entity.entities.Player;

/**
 * Manages permission checks and home limits
 */
public class PermissionManager {
    private final TeleportPlugin plugin;

    public PermissionManager(TeleportPlugin plugin) {
        this.plugin = plugin;
        System.out.println("[PermissionManager] Permission system initialized");
    }

    /**
     * Check if player has permission to set homes
     */
    public boolean canSetHome(Player player) {
        return hasPermission(player, plugin.getConfig().getHomeSetPermission());
    }

    /**
     * Check if player has permission to teleport to homes
     */
    public boolean canTeleportToHome(Player player) {
        return hasPermission(player, plugin.getConfig().getHomeTpPermission());
    }

    /**
     * Check if player has permission to delete homes
     */
    public boolean canDeleteHome(Player player) {
        return hasPermission(player, plugin.getConfig().getHomeDeletePermission());
    }

    /**
     * Check if player has permission to list homes
     */
    public boolean canListHomes(Player player) {
        return hasPermission(player, plugin.getConfig().getHomeListPermission());
    }

    /**
     * Get the maximum number of homes a player can set based on permissions
     * Checks for teleport.home.set.X permissions where X is the limit
     */
    public int getMaxHomes(Player player) {
        // Check for specific limit permissions (teleport.home.set.1, teleport.home.set.5, etc.)
        int maxFound = 0;

        // Check common limits: 1, 3, 5, 10, 15, 20, 25, 50, 100
        int[] commonLimits = {1, 3, 5, 10, 15, 20, 25, 50, 100};

        for (int limit : commonLimits) {
            String permission = plugin.getConfig().getHomeSetLimitPermission(limit);
            if (hasPermission(player, permission)) {
                maxFound = Math.max(maxFound, limit);
            }
        }

        // If no specific limit found, check for unlimited permission
        if (maxFound == 0 && hasPermission(player, plugin.getConfig().getHomeSetLimitPermission(999))) {
            return Integer.MAX_VALUE; // Unlimited
        }

        // If still no limit found, use default
        if (maxFound == 0) {
            maxFound = plugin.getConfig().defaultMaxHomes;
        }

        return maxFound;
    }

    /**
     * Check if player can set another home (doesn't exceed limit)
     */
    public boolean canSetAnotherHome(Player player, String playerId) {
        int maxHomes = getMaxHomes(player);
        int currentHomes = plugin.getHomeManager().getHomeCount(playerId);

        boolean canSet = currentHomes < maxHomes;

        System.out.println("[PermissionManager] Player " + playerId + " has " + currentHomes + "/" + maxHomes + " homes. Can set another: " + canSet);

        return canSet;
    }

    /**
     * Get a formatted string showing current/max homes for a player
     */
    public String getHomesLimitString(Player player, String playerId) {
        int maxHomes = getMaxHomes(player);
        int currentHomes = plugin.getHomeManager().getHomeCount(playerId);

        if (maxHomes == Integer.MAX_VALUE) {
            return currentHomes + "/unlimited homes";
        } else {
            return currentHomes + "/" + maxHomes + " homes";
        }
    }

    /**
     * Check if player has a specific permission
     * This is a wrapper that will use Hytale's permission system once we know the API
     */
    private boolean hasPermission(Player player, String permission) {
        // TODO: Replace with actual Hytale permission check
        // For now, we'll use a placeholder that always returns true
        // This needs to be implemented with the actual Hytale permission API

        // Placeholder implementation - replace with actual permission check:
        // return player.hasPermission(permission);

        // TODO: Use proper player identification when Player API is fully available
        // Note: getUuid() is deprecated, but still used in examples - we'll use toString for now
        System.out.println("[PermissionManager] Checking permission '" + permission + "' for player (placeholder: true)");
        return true; // Placeholder - always true for now
    }
}