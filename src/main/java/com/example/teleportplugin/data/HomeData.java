package com.example.teleportplugin.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON-based data persistence for homes
 */
public class HomeData {
    private static final String HOMES_FILE = "homes.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Represents a home location with coordinates and world info
     */
    public static class HomeLocation {
        public final double x, y, z;
        public final String worldId; // For future multi-world support

        public HomeLocation(double x, double y, double z, String worldId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.worldId = worldId;
        }

        public HomeLocation(double x, double y, double z) {
            this(x, y, z, "default");
        }

        @Override
        public String toString() {
            return String.format("(%.1f, %.1f, %.1f) in %s", x, y, z, worldId);
        }
    }

    /**
     * Load homes from JSON file
     */
    public static Map<String, Map<String, HomeLocation>> loadHomes(Path dataDir) {
        Path homesFile = dataDir.resolve(HOMES_FILE);

        try {
            if (Files.exists(homesFile)) {
                String json = Files.readString(homesFile);
                Type type = new TypeToken<Map<String, Map<String, HomeLocation>>>(){}.getType();
                Map<String, Map<String, HomeLocation>> homes = GSON.fromJson(json, type);

                if (homes == null) {
                    homes = new HashMap<>();
                }

                System.out.println("[HomeData] Loaded " + homes.size() + " players' homes from " + homesFile);
                return homes;
            } else {
                System.out.println("[HomeData] No existing homes file found, starting with empty data");
                return new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("[HomeData] Error loading homes: " + e.getMessage());
            System.out.println("[HomeData] Starting with empty homes data");
            return new HashMap<>();
        }
    }

    /**
     * Save homes to JSON file
     */
    public static void saveHomes(Map<String, Map<String, HomeLocation>> homes, Path dataDir) {
        try {
            Files.createDirectories(dataDir);
            Path homesFile = dataDir.resolve(HOMES_FILE);
            String json = GSON.toJson(homes);
            Files.writeString(homesFile, json);

            int totalHomes = homes.values().stream()
                    .mapToInt(playerHomes -> playerHomes.size())
                    .sum();
            System.out.println("[HomeData] Saved " + totalHomes + " homes for " + homes.size() + " players to " + homesFile);
        } catch (IOException e) {
            System.err.println("[HomeData] Error saving homes: " + e.getMessage());
        }
    }
}