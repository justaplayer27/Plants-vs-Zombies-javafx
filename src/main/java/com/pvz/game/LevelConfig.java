package com.pvz.game;

import java.util.*;

public class LevelConfig {
    private int levelNumber;
    private int totalWaves;
    private int zombiesPerWaveBase;
    private long waveDelay;
    private long randomSpawnMin;
    private long randomSpawnMax;
    private List<String> availableZombieTypes;
    private List<String> availablePlantTypes;

    public LevelConfig(int levelNumber, int totalWaves, int zombiesPerWaveBase, long waveDelay,
                       long randomSpawnMin, long randomSpawnMax,
                       List<String> availableZombieTypes, List<String> availablePlantTypes) {
        this.levelNumber = levelNumber;
        this.totalWaves = totalWaves;
        this.zombiesPerWaveBase = zombiesPerWaveBase;
        this.waveDelay = waveDelay;
        this.randomSpawnMin = randomSpawnMin;
        this.randomSpawnMax = randomSpawnMax;
        this.availableZombieTypes = availableZombieTypes;
        this.availablePlantTypes = availablePlantTypes;
    }

    public int getLevelNumber() { return levelNumber; }
    public int getTotalWaves() { return totalWaves; }
    public int getZombiesPerWaveBase() { return zombiesPerWaveBase; }
    public long getWaveDelay() { return waveDelay; }
    public long getRandomSpawnMin() { return randomSpawnMin; }
    public long getRandomSpawnMax() { return randomSpawnMax; }
    public List<String> getAvailableZombieTypes() { return availableZombieTypes; }
    public List<String> getAvailablePlantTypes() { return availablePlantTypes; }

    public static LevelConfig getLevel(int level) {
        switch (level) {
            case 1:
                return new LevelConfig(1, 2, 5, 60000,
                        5000, 10000,
                        Arrays.asList("BasicZombie", "FlagZombie"),
                        Arrays.asList("Peashooter", "Sunflower"));
            case 2:
                return new LevelConfig(2, 2, 6, 60000,
                        5000, 10000,
                        Arrays.asList("BasicZombie", "FlagZombie", "PharaohZombie"),
                        Arrays.asList("Peashooter", "Sunflower", "Wallnut"));
            case 3:
                return new LevelConfig(3, 3, 7, 60000,
                        5000, 10000,
                        Arrays.asList("BasicZombie", "FlagZombie", "PharaohZombie", "ExcavatorZombie"),
                        Arrays.asList("Peashooter", "Sunflower", "Wallnut", "KernelPult"));
            case 4:
                return new LevelConfig(4, 3, 8, 60000,
                        5000, 10000,
                        Arrays.asList("BasicZombie", "FlagZombie", "PharaohZombie", "ExcavatorZombie", "ChickenWranglerZombie"),
                        Arrays.asList("Peashooter", "Sunflower", "Wallnut", "KernelPult", "SpikeWeed"));
            case 5:
                return new LevelConfig(5, 4, 9, 60000,
                        5000, 10000,
                        Arrays.asList("BasicZombie", "FlagZombie", "PharaohZombie", "ExcavatorZombie", "ChickenWranglerZombie", "AllStarZombie"),
                        Arrays.asList("Peashooter", "Sunflower", "Wallnut", "SpikeWeed", "KernelPult", "BonkChoy"));
            case 6: // Màn Custom dành cho Testing
                return new LevelConfig(6, 0, 0, 0,
                        0, 0,
                        Arrays.asList("BasicZombie", "PharaohZombie", "FlagZombie", "AllStarZombie", "ExcavatorZombie", "ChickenWranglerZombie"),
                        Arrays.asList("Peashooter", "Sunflower", "Wallnut", "SpikeWeed", "KernelPult", "BonkChoy"));
            default:
                return getLevel(1);
        }
    }
}
