package com.example.teleportplugin.cooldown;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents an individual teleport cooldown with countdown
 */
public class TeleportCooldown {
    private static final Timer TIMER = new Timer("TeleportCooldown", true);

    private final String playerId;
    private final String homeName;
    private final int totalSeconds;
    private final Runnable onSuccess;
    private final Runnable onCancel;

    private TimerTask countdownTask;
    private TimerTask teleportTask;
    private volatile int remainingSeconds;
    private volatile boolean cancelled = false;

    public TeleportCooldown(String playerId, String homeName, int cooldownSeconds,
                           Runnable onSuccess, Runnable onCancel) {
        this.playerId = playerId;
        this.homeName = homeName;
        this.totalSeconds = cooldownSeconds;
        this.remainingSeconds = cooldownSeconds;
        this.onSuccess = onSuccess;
        this.onCancel = onCancel;
    }

    /**
     * Start the cooldown countdown
     */
    public void start() {
        if (cancelled) return;

        // Schedule the final teleport using Timer
        teleportTask = new TimerTask() {
            @Override
            public void run() {
                if (!cancelled) {
                    System.out.println("[TeleportCooldown] Cooldown completed for player " + playerId + " - executing teleport to '" + homeName + "'");
                    onSuccess.run();
                }
            }
        };
        TIMER.schedule(teleportTask, totalSeconds * 1000L);

        // Schedule countdown messages (every second)
        if (totalSeconds > 0) {
            countdownTask = new TimerTask() {
                @Override
                public void run() {
                    if (cancelled) {
                        return;
                    }

                    if (remainingSeconds <= 0) {
                        // Countdown finished, stop the countdown task
                        if (countdownTask != null) {
                            countdownTask.cancel();
                        }
                        return;
                    }

                    // Send countdown message at key intervals
                    if (remainingSeconds <= 5 || remainingSeconds % 5 == 0) {
                        System.out.println("[TeleportCooldown] Player " + playerId + ": Teleporting to '" + homeName + "' in " + remainingSeconds + " seconds...");
                        // Note: Actual message sending should be implemented in command classes
                    }

                    remainingSeconds--;
                }
            };
            TIMER.scheduleAtFixedRate(countdownTask, 0, 1000);
        }
    }

    /**
     * Cancel the cooldown
     */
    public void cancel() {
        if (cancelled) return;

        cancelled = true;

        if (countdownTask != null) {
            countdownTask.cancel();
        }

        if (teleportTask != null) {
            teleportTask.cancel();
        }

        if (onCancel != null) {
            onCancel.run();
        }

        System.out.println("[TeleportCooldown] Cooldown cancelled for player " + playerId);
    }

    /**
     * Get remaining seconds
     */
    public int getRemainingSeconds() {
        return Math.max(0, remainingSeconds);
    }

    /**
     * Check if cooldown is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Shutdown the shared timer (call on plugin disable)
     */
    public static void shutdownTimer() {
        TIMER.cancel();
        TIMER.purge();
        System.out.println("[TeleportCooldown] Timer shutdown complete");
    }
}