package com.example.teleportplugin.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HomeManager {
    // Player Name -> Map von Home-Namen -> Home-Positionen (simplified for now)
    private final Map<String, Map<String, HomeLocation>> homes;

    public HomeManager() {
        this.homes = new HashMap<>();
        System.out.println("[HomeManager] Home data system initialized");
    }

    public void setHome(String playerId, String name, double x, double y, double z) {
        homes.computeIfAbsent(playerId, k -> new HashMap<>())
              .put(name, new HomeLocation(x, y, z));
        System.out.println("[HomeManager] Set home '" + name + "' for player " + playerId);
    }

    public HomeLocation getHome(String playerId, String name) {
        Map<String, HomeLocation> playerHomes = homes.get(playerId);
        if (playerHomes == null) {
            return null;
        }
        return playerHomes.get(name);
    }

    public boolean deleteHome(String playerId, String name) {
        Map<String, HomeLocation> playerHomes = homes.get(playerId);
        if (playerHomes == null) {
            return false;
        }
        boolean removed = playerHomes.remove(name) != null;
        if (removed) {
            System.out.println("[HomeManager] Deleted home '" + name + "' for player " + playerId);
        }
        return removed;
    }

    public Set<String> getHomeNames(String playerId) {
        Map<String, HomeLocation> playerHomes = homes.get(playerId);
        if (playerHomes == null) {
            return Set.of();
        }
        return playerHomes.keySet();
    }

    public boolean hasHome(String playerId, String name) {
        Map<String, HomeLocation> playerHomes = homes.get(playerId);
        return playerHomes != null && playerHomes.containsKey(name);
    }

    public static class HomeLocation {
        public final double x, y, z;

        public HomeLocation(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return String.format("(%.1f, %.1f, %.1f)", x, y, z);
        }
    }
}