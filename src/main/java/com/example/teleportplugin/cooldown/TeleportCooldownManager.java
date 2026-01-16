package com.example.teleportplugin.cooldown;

import com.example.teleportplugin.TeleportPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages teleport cooldowns and movement detection
 */
public class TeleportCooldownManager {
    private final Map<String, TeleportCooldown> activeCooldowns = new ConcurrentHashMap<>();
    private final int cooldownSeconds;

    public TeleportCooldownManager(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
        System.out.println("[TeleportCooldownManager] Initialized with " + cooldownSeconds + " second cooldown");
    }

    /**
     * Start a teleport cooldown for a player
     */
    public void startCooldown(String playerId, String homeName, Runnable onSuccess, Runnable onCancel) {
        // Cancel any existing cooldown
        cancelCooldown(playerId);

        // Wrap onSuccess to remove cooldown from map
        Runnable wrappedOnSuccess = () -> {
            activeCooldowns.remove(playerId);  // Remove from map when complete
            onSuccess.run();
        };

        // Wrap onCancel to remove cooldown from map
        Runnable wrappedOnCancel = () -> {
            activeCooldowns.remove(playerId);  // Remove from map when cancelled
            if (onCancel != null) {
                onCancel.run();
            }
        };

        TeleportCooldown cooldown = new TeleportCooldown(
            playerId,
            homeName,
            cooldownSeconds,
            wrappedOnSuccess,
            wrappedOnCancel
        );

        activeCooldowns.put(playerId, cooldown);
        cooldown.start();

        System.out.println("[TeleportCooldownManager] Started " + cooldownSeconds + "s cooldown for player " + playerId + " to home '" + homeName + "'");
    }

    /**
     * Cancel a player's cooldown (e.g., when they move)
     */
    public void cancelCooldown(String playerId) {
        TeleportCooldown cooldown = activeCooldowns.remove(playerId);
        if (cooldown != null) {
            cooldown.cancel();
            System.out.println("[TeleportCooldownManager] Cancelled cooldown for player " + playerId);
        }
    }

    /**
     * Check if a player has an active cooldown
     */
    public boolean hasCooldown(String playerId) {
        return activeCooldowns.containsKey(playerId);
    }

    /**
     * Get remaining cooldown time for a player
     */
    public int getRemainingTime(String playerId) {
        TeleportCooldown cooldown = activeCooldowns.get(playerId);
        return cooldown != null ? cooldown.getRemainingSeconds() : 0;
    }

    /**
     * Called when a player moves - cancels their cooldown if movement cancellation is enabled
     */
    public void onPlayerMove(String playerId) {
        if (TeleportPlugin.getInstance().getConfig().enableMovementCancellation) {
            if (hasCooldown(playerId)) {
                cancelCooldown(playerId);
                // Note: The actual message sending should be done in the caller
                System.out.println("[TeleportCooldownManager] Player " + playerId + " moved during cooldown - teleport cancelled");
            }
        }
    }

    /**
     * Cleanup method for plugin shutdown
     */
    public void shutdown() {
        activeCooldowns.values().forEach(TeleportCooldown::cancel);
        activeCooldowns.clear();
        System.out.println("[TeleportCooldownManager] Shutdown complete");
    }
}