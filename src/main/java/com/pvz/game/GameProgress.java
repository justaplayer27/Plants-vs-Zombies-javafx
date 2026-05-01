package com.pvz.game;

import java.io.*;
import java.util.*;

public class GameProgress {
    private static GameProgress instance;
    private int maxUnlockedLevel;
    private Set<String> discoveredZombies = new HashSet<>();
    private Set<String> discoveredPlants = new HashSet<>();
    private static final String SAVE_FILE = "progress.txt";
    private static final boolean DEBUG_UNLOCK_ALL_LEVELS = true;

    private GameProgress() {
        load();
    }

    public static GameProgress getInstance() {
        if (instance == null) {
            instance = new GameProgress();
        }
        return instance;
    }

    public int getMaxUnlockedLevel() {
        return maxUnlockedLevel;
    }

    public boolean isZombieDiscovered(String type) {
        return discoveredZombies.contains(type);
    }

    public Set<String> getDiscoveredZombies() {
        return new HashSet<>(discoveredZombies);
    }

    public boolean isPlantDiscovered(String type) {
        return discoveredPlants.contains(type);
    }

    public Set<String> getDiscoveredPlants() {
        return new HashSet<>(discoveredPlants);
    }

    public void discoverZombie(String type) {
        if (!discoveredZombies.contains(type)) {
            discoveredZombies.add(type);
            save();
        }
    }

    public void discoverPlant(String type) {
        if (!discoveredPlants.contains(type)) {
            discoveredPlants.add(type);
            save();
        }
    }

    public void unlockLevel(int level) {
        if (level > maxUnlockedLevel) {
            maxUnlockedLevel = level;
            save();
        }
    }

    private void load() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    maxUnlockedLevel = Integer.parseInt(line.trim());
                }
                String zombiesLine = reader.readLine();
                if (zombiesLine != null && !zombiesLine.isEmpty()) {
                    discoveredZombies.clear();
                    discoveredZombies.addAll(Arrays.asList(zombiesLine.split(",")));
                }
                String plantsLine = reader.readLine();
                if (plantsLine != null && !plantsLine.isEmpty()) {
                    discoveredPlants.clear();
                    discoveredPlants.addAll(Arrays.asList(plantsLine.split(",")));
                }
            } catch (Exception e) {
                maxUnlockedLevel = 1;
            }
        } else {
            maxUnlockedLevel = 1;
        }

        if (DEBUG_UNLOCK_ALL_LEVELS) {
            maxUnlockedLevel = Math.max(maxUnlockedLevel, 5);
        }
        ensureStartingDiscoveries();
    }

    private void ensureStartingDiscoveries() {
        discoveredPlants.add("Peashooter");
        discoveredPlants.add("Sunflower");
        discoveredZombies.add("BasicZombie");

        if (maxUnlockedLevel >= 2) {
            discoveredPlants.add("Wallnut");
        }
        if (maxUnlockedLevel >= 3) {
            discoveredPlants.add("KernelPult");
        }
        if (maxUnlockedLevel >= 4) {
            discoveredPlants.add("SpikeWeed");
        }
        if (maxUnlockedLevel >= 5) {
            discoveredPlants.add("BonkChoy");
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write(String.valueOf(maxUnlockedLevel));
            writer.newLine();
            writer.write(String.join(",", discoveredZombies));
            writer.newLine();
            writer.write(String.join(",", discoveredPlants));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        maxUnlockedLevel = 1;
        discoveredZombies.clear();
        discoveredPlants.clear();
        ensureStartingDiscoveries();
        save();
    }
}
