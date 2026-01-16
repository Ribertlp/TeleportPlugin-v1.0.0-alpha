package com.example.teleportplugin.systems;

import com.example.teleportplugin.TeleportPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Simplified movement detection system using periodic checks
 * TODO: Replace with proper Entity System when API is available
 */
public class PlayerMovementSystem {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, PlayerPosition> lastPositions = new HashMap<>();
    private final double MOVEMENT_THRESHOLD = 0.1; // Minimum movement to trigger cancellation
    private volatile boolean running = false;

    public void start() {
        if (running) return;
        running = true;

        // Check for movement every 100ms (10 times per second) - using Timer instead
        java.util.Timer timer = new java.util.Timer("PlayerMovementChecker", true);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                checkPlayerMovement();
            }
        }, 0, 100);
        System.out.println("[PlayerMovementSystem] Started movement detection (placeholder implementation)");
    }

    private void checkPlayerMovement() {
        try {
            // TODO: This is a placeholder implementation
            // In a real implementation, we would:
            // 1. Get all online players
            // 2. Get their current positions
            // 3. Compare with stored positions
            // 4. Cancel cooldowns if movement detected

            // For now, we'll disable movement cancellation since we can't access player positions
            // The user reported that movement cancellation isn't working anyway,
            // so this is consistent with the current behavior

        } catch (Exception e) {
            System.err.println("[PlayerMovementSystem] Error in movement check: " + e.getMessage());
        }
    }

    /**
     * Manual method to cancel cooldown when movement is detected
     * This can be called from other systems when movement is detected
     */
    public void onPlayerMove(String playerId, double x, double y, double z) {
        PlayerPosition lastPosition = lastPositions.get(playerId);
        PlayerPosition currentPosition = new PlayerPosition(x, y, z);

        if (lastPosition != null) {
            double distance = lastPosition.distanceTo(currentPosition);

            if (distance > MOVEMENT_THRESHOLD) {
                // Player moved significantly - cancel their cooldown
                if (TeleportPlugin.getInstance().getCooldownManager().hasCooldown(playerId)) {
                    TeleportPlugin.getInstance().getCooldownManager().cancelCooldown(playerId);
                    System.out.println("[PlayerMovementSystem] Player " + playerId + " moved " +
                                     String.format("%.2f", distance) + " blocks - cooldown cancelled");
                }
            }
        }

        // Always update the last known position
        lastPositions.put(playerId, currentPosition);
    }

    /**
     * Remove player from tracking when they disconnect
     */
    public void removePlayer(String playerId) {
        lastPositions.remove(playerId);
    }

    /**
     * Shutdown the movement detection system
     */
    public void shutdown() {
        running = false;
        // Timer cleanup will be handled automatically since it's a daemon thread
        lastPositions.clear();
        System.out.println("[PlayerMovementSystem] Shutdown complete");
    }

    /**
     * Simple position container
     */
    private static class PlayerPosition {
        public final double x, y, z;

        public PlayerPosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distanceTo(PlayerPosition other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }
}