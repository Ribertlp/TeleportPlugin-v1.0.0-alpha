package com.example.teleportplugin.data;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HomeManager {
    // Player Name -> Map von Home-Namen -> Home-Positionen
    private final Map<String, Map<String, HomeData.HomeLocation>> homes;
    private final Path dataDirectory;

    public HomeManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.homes = HomeData.loadHomes(dataDirectory);
        System.out.println("[HomeManager] Home data system initialized with JSON persistence");
    }

    public void setHome(String playerId, String name, double x, double y, double z) {
        setHome(playerId, name, x, y, z, "default");
    }

    public void setHome(String playerId, String name, double x, double y, double z, String worldId) {
        homes.computeIfAbsent(playerId, k -> new HashMap<>())
              .put(name, new HomeData.HomeLocation(x, y, z, worldId));
        saveData();
        System.out.println("[HomeManager] Set home '" + name + "' for player " + playerId + " at " + new HomeData.HomeLocation(x, y, z, worldId));
    }

    public HomeData.HomeLocation getHome(String playerId, String name) {
        Map<String, HomeData.HomeLocation> playerHomes = homes.get(playerId);
        if (playerHomes == null) {
            return null;
        }
        return playerHomes.get(name);
    }

    public boolean deleteHome(String playerId, String name) {
        Map<String, HomeData.HomeLocation> playerHomes = homes.get(playerId);
        if (playerHomes == null) {
            return false;
        }
        boolean removed = playerHomes.remove(name) != null;
        if (removed) {
            saveData();
            System.out.println("[HomeManager] Deleted home '" + name + "' for player " + playerId);
        }
        return removed;
    }

    public Set<String> getHomeNames(String playerId) {
        Map<String, HomeData.HomeLocation> playerHomes = homes.get(playerId);
        if (playerHomes == null) {
            return Set.of();
        }
        return playerHomes.keySet();
    }

    public boolean hasHome(String playerId, String name) {
        Map<String, HomeData.HomeLocation> playerHomes = homes.get(playerId);
        return playerHomes != null && playerHomes.containsKey(name);
    }

    /**
     * Get the number of homes a player has
     */
    public int getHomeCount(String playerId) {
        Map<String, HomeData.HomeLocation> playerHomes = homes.get(playerId);
        return playerHomes == null ? 0 : playerHomes.size();
    }

    /**
     * Save data to JSON file
     */
    private void saveData() {
        HomeData.saveHomes(homes, dataDirectory);
    }

    /**
     * Manual save method for plugin shutdown
     */
    public void saveAll() {
        saveData();
        System.out.println("[HomeManager] Manual save completed");
    }
}