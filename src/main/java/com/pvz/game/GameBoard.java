package com.pvz.game;

import com.pvz.entities.Plant;
import com.pvz.entities.Peashooter;
import com.pvz.entities.Sunflower;
import com.pvz.entities.Wallnut;
import com.pvz.entities.SpikeWeed;
import com.pvz.entities.KernelPult;
import com.pvz.entities.BonkChoy;
import com.pvz.entities.Pea;
import com.pvz.entities.Sun;
import com.pvz.entities.Mower;
import com.pvz.entities.zombies.Chicken;
import com.pvz.entities.zombies.*;
import java.util.*;

public class GameBoard {
    private static GameBoard instance;
    private static final int GRID_WIDTH = 9;
    private static final int GRID_HEIGHT = 5;
    private static final int CELL_WIDTH = 95;
    private static final int CELL_HEIGHT = 113;
    private static final int ZOMBIE_SPAWN_OFFSET = 120;

    private List<Plant> plants;
    private List<Zombie> zombies;
    private List<Pea> projectiles;
    private List<Sun> suns;
    private List<Mower> mowers;
    private List<Chicken> chickens;
    private int score;
    private int sun;
    private boolean gameOver;
    private boolean playerWon;
    private int wave;
    private int wavesCleared;
    private boolean waveActive = true;
    private int spawnedThisWave = 0;
    private int targetZombiesPerWave;
    private long lastWaveStartTime;
    private long lastZombieSpawnTime;
    private long lastSunSpawnTime;
    private long sunSpawnInterval = 12000;
    private long nextRandomSpawnTime;
    private boolean randomSpawnActive = true;
    private LevelConfig levelConfig;
    private boolean debugMode = false;
    private String lastNewZombieType = null;
    private Set<String> sessionDiscoveredZombies;
    private boolean flagSpawnedThisWave = false;

    public GameBoard(LevelConfig config) {
        this.levelConfig = config;
        plants = new ArrayList<>();
        zombies = new ArrayList<>();
        projectiles = new ArrayList<>();
        suns = new ArrayList<>();
        mowers = new ArrayList<>();
        chickens = new ArrayList<>();
        score = 0;
        sun = 100;
        gameOver = false;
        playerWon = false;
        sessionDiscoveredZombies = new HashSet<>();
        wave = 0;
        targetZombiesPerWave = config != null ? config.getZombiesPerWaveBase() : 5;
        waveActive = false;
        spawnedThisWave = 0;
        wavesCleared = 0;
        flagSpawnedThisWave = false;
        lastWaveStartTime = System.currentTimeMillis();
        lastZombieSpawnTime = System.currentTimeMillis();
        nextRandomSpawnTime = System.currentTimeMillis() + getRandomSpawnInterval();
        lastSunSpawnTime = System.currentTimeMillis();
        initMowers();

        if (config != null && config.getLevelNumber() == 6) {
            this.debugMode = true;
        }
    }

    public boolean plantAt(Plant plant, int gridX, int gridY) {
        if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
            return false;
        }
        
        if (sun < plant.getCost()) {
            return false;
        }

        for (Plant p : plants) {
            int existingGridX = (int) (p.getX() / CELL_WIDTH);
            int existingGridY = (int) (p.getY() / CELL_HEIGHT);
            if (existingGridX == gridX && existingGridY == gridY) {
                return false;
            }
        }

        double x = gridX * CELL_WIDTH;
        double y = gridY * CELL_HEIGHT;
        if (plant instanceof SpikeWeed) {
            y += CELL_HEIGHT - plant.getHeight();
        }
        plant.setX(x);
        plant.setY(y);
        plants.add(plant);
        sun -= plant.getCost();
        return true;
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void addProjectile(Pea pea) {
        projectiles.add(pea);
    }

    public void addChicken(Chicken chicken) {
        if (chicken != null) {
            chickens.add(chicken);
        }
    }

    private boolean isPaused = false;

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public boolean collectSunAt(double x, double y) {
        if (isPaused) {
            return false;
        }
        Iterator<Sun> iterator = suns.iterator();
        while (iterator.hasNext()) {
            Sun s = iterator.next();
            if (s.getX() <= x && x <= s.getX() + s.getWidth() &&
                s.getY() <= y && y <= s.getY() + s.getHeight()) {
                sun += s.getValue();
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void update() {
        if (gameOver) return;

        for (Plant plant : new ArrayList<>(plants)) {
            plant.update();
            
            if (plant instanceof Sunflower && plant.canAction()) {
                double sunX = plant.getX() + plant.getWidth() / 2 - 15;
                double sunY = plant.getY() + plant.getHeight() / 2 - 15;
                suns.add(new Sun(sunX, sunY, true));
                plant.action();
            } else if (plant instanceof Peashooter && plant.canAction()) {
                Zombie target = findNearestZombie(plant);
                if (target != null) {
                    plant.action();
                }
            } else if ((plant instanceof KernelPult || plant instanceof BonkChoy) && plant.canAction()) {
                plant.action();
            }
        }

        for (Zombie zombie : new ArrayList<>(zombies)) {
            zombie.update();

            if (!zombie.isAlive() && zombie.isDying()) {
                score += 10;
                sun += zombie.getReward();
                zombie.setDying(false);
                continue;
            }
            
            if (!zombie.isEating() && !zombie.isDying()) {
                for (Plant plant : new ArrayList<>(plants)) {
                    if (plant.isAlive() && zombie.intersects(plant)) {
                        zombie.startEating(plant);
                        break;
                    }
                }
            }
        }

        // Update suns
        for (Sun s : new ArrayList<>(suns)) {
            s.update();
        }

        // Spawn suns periodically
        if (System.currentTimeMillis() - lastSunSpawnTime > sunSpawnInterval) {
            spawnSun();
            lastSunSpawnTime = System.currentTimeMillis();
        }

        // Update projectiles
        for (Pea pea : new ArrayList<>(projectiles)) {
            pea.update();
        }

        // Update mowers
        for (Mower mower : new ArrayList<>(mowers)) {
            mower.update();
        }

        // Fixed wave-based zombie spawning
        long now = System.currentTimeMillis();
        
        if (!debugMode) {
            if (waveActive && spawnedThisWave >= targetZombiesPerWave) {
                waveActive = false;
                if (wave >= (levelConfig != null ? levelConfig.getTotalWaves() : 2)) {
                    randomSpawnActive = false;
                }
            }
            
            long waveDelay = levelConfig != null ? levelConfig.getWaveDelay() : 60000;

            // Start next wave after delay from last wave start
            if (!waveActive && now - lastWaveStartTime > waveDelay) {
                int maxWaves = levelConfig != null ? levelConfig.getTotalWaves() : 2;
                if (wave < maxWaves) {
                    wave++;
                    targetZombiesPerWave = (levelConfig != null ? levelConfig.getZombiesPerWaveBase() : 5) + (wave * 2);
                    spawnedThisWave = 0;
                    flagSpawnedThisWave = false;
                    waveActive = true;
                    lastWaveStartTime = now;
                }
            }
            
            // Spawn within active wave
            if (waveActive && spawnedThisWave < targetZombiesPerWave && 
                now - lastZombieSpawnTime > 2500) {
                spawnZombie();
                spawnedThisWave++;
                lastZombieSpawnTime = now;
            }

            // Random ambient zombie spawn (independent of waves)
            if (randomSpawnActive && now >= nextRandomSpawnTime) {
                spawnRandomZombie();
                nextRandomSpawnTime = now + getRandomSpawnInterval();
            }
        }

        // Check collisions (projectiles hitting zombies)
        checkProjectileCollisions();

        // Check plant-zombie collisions (zombies eating plants)
        checkZombieCollisions();

        // Let left-side mowers handle any incoming zombies
        handleMowerInterception();

        // Handle active mower collisions after activation
        handleMowerCollisions();

        // Remove dead entities
        plants.removeIf(p -> !p.isAlive());
        mowers.removeIf(m -> !m.isAlive());
        zombies.removeIf(z -> !z.isAlive());
        projectiles.removeIf(p -> !p.isAlive());
        suns.removeIf(s -> !s.isAlive());

        // Win condition: all waves cleared + no zombies left
        int maxWaves = levelConfig != null ? levelConfig.getTotalWaves() : 2;
        if (!randomSpawnActive && wave >= maxWaves && !waveActive && zombies.isEmpty()) {
            playerWon = true;
            gameOver = true;
        }

        // Lose condition: Zombie passed x < 5 on a row without an active mower
        if (!debugMode && zombies.stream().anyMatch(z -> z.getX() < 5 && findMowerForRow((int)(z.getY() / CELL_HEIGHT)) == null)) {
            gameOver = true;
        }
    }

    public String getAndClearLastNewZombieType() {
        String type = lastNewZombieType;
        lastNewZombieType = null;
        return type;
    }

    public Zombie spawnZombieByType(String type, double x, double y) {
        GameProgress.getInstance().discoverZombie(type);
        if ("FlagZombie".equals(type)) {
            flagSpawnedThisWave = true;
        }
        if (!sessionDiscoveredZombies.contains(type) && !debugMode) {
            lastNewZombieType = type;
            sessionDiscoveredZombies.add(type);
        }
        
        switch (type) {
            case "BasicZombie": return new BasicZombie(x, y);
            case "PharaohZombie": return new PharaohZombie(x, y);
            case "FlagZombie": return new FlagZombie(x, y);
            case "AllStarZombie": return new AllStarZombie(x, y);
            case "ExcavatorZombie": return new ExcavatorZombie(x, y);
            case "ChickenWranglerZombie": return new ChickenWranglerZombie(x, y);
            default: return new BasicZombie(x, y);
        }
    }

    private void spawnZombie() {
        int lane = (int) (Math.random() * GRID_HEIGHT);
        double x = GRID_WIDTH * CELL_WIDTH + ZOMBIE_SPAWN_OFFSET;
        double y = lane * CELL_HEIGHT + 10;
        
        List<String> allowed = levelConfig != null ? levelConfig.getAvailableZombieTypes() : 
                Arrays.asList("BasicZombie");
        
        String type;
        if (!flagSpawnedThisWave && allowed.contains("FlagZombie") && spawnedThisWave >= targetZombiesPerWave - 1) {
            type = "FlagZombie";
        } else {
            type = chooseWaveZombieType(allowed);
        }
        Zombie z = spawnZombieByType(type, x, y);
        zombies.add(z);
        System.out.println("Wave spawn " + type + " row " + lane);
    }

    private String chooseWaveZombieType(List<String> allowed) {
        if (allowed.size() == 1) {
            return allowed.get(0);
        }

        List<String> choice = new ArrayList<>(allowed);
        if (flagSpawnedThisWave) {
            choice.remove("FlagZombie");
        }
        if (choice.isEmpty()) {
            choice = new ArrayList<>(allowed);
        }

        if (wave <= 1 && choice.contains("BasicZombie")) {
            return "BasicZombie";
        }

        double r = Math.random();
        if (r < 0.30 && choice.contains("BasicZombie")) {
            return "BasicZombie";
        }
        if (r < 0.55 && choice.contains("PharaohZombie")) {
            return "PharaohZombie";
        }
        if (r < 0.70 && choice.contains("FlagZombie")) {
            return "FlagZombie";
        }
        if (r < 0.80 && choice.contains("AllStarZombie")) {
            return "AllStarZombie";
        }
        if (r < 0.90 && choice.contains("ChickenWranglerZombie")) {
            return "ChickenWranglerZombie";
        }
        if (choice.contains("ExcavatorZombie")) {
            return "ExcavatorZombie";
        }
        if (choice.contains("AllStarZombie")) {
            return "AllStarZombie";
        }
        if (choice.contains("PharaohZombie")) {
            return "PharaohZombie";
        }
        return choice.contains("BasicZombie") ? "BasicZombie" : choice.get(0);
    }

    private void spawnRandomZombie() {
        int lane = (int) (Math.random() * GRID_HEIGHT);
        double x = GRID_WIDTH * CELL_WIDTH + ZOMBIE_SPAWN_OFFSET;
        double y = lane * CELL_HEIGHT + 10;
        
        List<String> allowed = levelConfig != null ? levelConfig.getAvailableZombieTypes() : 
                Arrays.asList("BasicZombie");
        List<String> ambientAllowed = new ArrayList<>(allowed);
        ambientAllowed.remove("FlagZombie");
        if (ambientAllowed.isEmpty()) {
            ambientAllowed.addAll(allowed);
        }
        
        String type;
        double r = Math.random();
        if (ambientAllowed.size() == 1 || r < 0.65) {
            type = ambientAllowed.contains("BasicZombie") ? "BasicZombie" : ambientAllowed.get(0);
        } else if (r < 0.80 && ambientAllowed.contains("PharaohZombie")) {
            type = "PharaohZombie";
        } else if (r < 0.90 && ambientAllowed.contains("AllStarZombie")) {
            type = "AllStarZombie";
        } else if (r < 0.96 && ambientAllowed.contains("ChickenWranglerZombie")) {
            type = "ChickenWranglerZombie";
        } else if (ambientAllowed.contains("ExcavatorZombie")) {
            type = "ExcavatorZombie";
        } else {
            type = ambientAllowed.get(0);
        }
        
        Zombie z = spawnZombieByType(type, x, y);
        zombies.add(z);
        System.out.println("Random spawn " + type + " row " + lane);
    }

    private void spawnSun() {
        double x = Math.random() * (GRID_WIDTH * CELL_WIDTH - 20);
        suns.add(new Sun(x, 0));
    }

    private long getRandomSpawnInterval() {
        long min = levelConfig != null ? levelConfig.getRandomSpawnMin() : 5000;
        long max = levelConfig != null ? levelConfig.getRandomSpawnMax() : 10000;
        return (min + (long)(Math.random() * Math.max(1, max - min))) * 3;
    }
    
    public boolean hasZombieAhead(Plant plant) {
        return findNearestZombie(plant) != null;
    }

    public Zombie findNearestZombie(Plant plant) {
        Zombie nearest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() || zombie.isDying()) {
                continue;
            }
            if (Math.abs(zombie.getY() - plant.getY()) < CELL_HEIGHT / 3.0) {
                double dist = zombie.getX() - plant.getX();
                if (dist > 0 && dist < minDist) {
                    minDist = dist;
                    nearest = zombie;
                }
            }
        }
        return nearest;
    }

    private void handleMowerCollisions() {
        for (Mower mower : new ArrayList<>(mowers)) {
            if (!mower.isAlive() || !mower.isActive()) {
                continue;
            }
            for (Zombie zombie : new ArrayList<>(zombies)) {
                if (zombie.isAlive() && !zombie.isDying() && mower.intersects(zombie)) {
                    zombie.setAlive(false);
                    score += 5;
                }
            }
        }
    }

    private void checkProjectileCollisions() {
        for (Pea pea : new ArrayList<>(projectiles)) {
            for (Zombie zombie : new ArrayList<>(zombies)) {
                if (zombie.isDying()) {
                    continue;
                }
                if (pea.intersects(zombie)) {
                    if (pea.isLobbed()) {
                        zombie.takeLobbedDamage(pea.getDamage());
                    } else {
                        // Đạn bay thẳng từ Peashooter mặc định là trúng phía trước
                        zombie.takeFrontalDamage(pea.getDamage());
                    }
                    
                    // Nếu là đạn bơ, làm zombie bất động trong 10 giây
                    if (pea.isButter()) {
                        zombie.stun(10000);
                    }

                    pea.setAlive(false);
                    
                    if (!zombie.isAlive()) {
                        score += 10;
                        sun += zombie.getReward();
                    }
                    break;
                }
            }
        }
    }

    private void checkZombieCollisions() {
        for (Zombie zombie : new ArrayList<>(zombies)) {
            if (!zombie.isAlive() || zombie.isDying()) {
                continue;
            }
            for (Plant plant : new ArrayList<>(plants)) {
                if (zombie.intersects(plant)) {
                    if (zombie.canAttack()) {
                        zombie.attack(plant);
                    }
                }
            }
        }
    }

    private void initMowers() {
        for (int row = 0; row < GRID_HEIGHT; row++) {
            double y = row * CELL_HEIGHT + (CELL_HEIGHT - 30) / 2.0;
            mowers.add(new Mower(-40, y, row));
        }
    }

    private Mower findMowerForRow(int row) {
        for (Mower mower : mowers) {
            if (mower.isAlive() && mower.getRow() == row) {
                return mower;
            }
        }
        return null;
    }

    private void handleMowerInterception() {
        for (Zombie zombie : new ArrayList<>(zombies)) {
            if (!zombie.isAlive() || zombie.isDying()) {
                continue;
            }
            if (zombie.getX() < 20) {
                int row = Math.max(0, Math.min(GRID_HEIGHT - 1, (int) (zombie.getY() / CELL_HEIGHT)));
                Mower mower = findMowerForRow(row);
                if (mower != null && !mower.isActive()) {
                    mower.activate();
                    zombie.setAlive(false);
                    score += 5;
                }
            }
        }
    }

    // Getters
    public List<Plant> getPlants() { return new ArrayList<>(plants); }
    public List<Zombie> getZombies() { return new ArrayList<>(zombies); }
    public List<Mower> getMowers() { return new ArrayList<>(mowers); }
    public List<Chicken> getChickens() { return new ArrayList<>(chickens); }
    public List<Pea> getProjectiles() { return new ArrayList<>(projectiles); }
    public List<Sun> getSuns() { return new ArrayList<>(suns); }
    public int getScore() { return score; }
    public int getSun() { return sun; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPlayerWon() { return playerWon; }
    public int getWave() { return wave; }
    public LevelConfig getLevelConfig() { return levelConfig; }
    public static int getGridWidth() { return GRID_WIDTH; }
    public static int getGridHeight() { return GRID_HEIGHT; }
    public static int getCellWidth() { return CELL_WIDTH; }
    public static int getCellHeight() { return CELL_HEIGHT; }
    
    public List<String> getSpecialZombieInfos() {
        List<String> infos = new ArrayList<>();
        for (Zombie z : zombies) {
            int lane = (int)(z.getY() / GameBoard.getCellHeight());
            if (z instanceof PharaohZombie) {
                PharaohZombie pz = (PharaohZombie) z;
                infos.add("Pharaoh R" + lane + ": " + pz.getArmorHealth() + "/300");
            } else if (z instanceof ChickenWranglerZombie) {
                infos.add("Chicken R" + lane + ": " + z.getHealth() + "/180");
            }
        }
        return infos;
    }
    
    public boolean removePlantAt(double x, double y) {
        int gridX = (int) (x / CELL_WIDTH);
        int gridY = (int) (y / CELL_HEIGHT);

        if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
            return false;
        }

        Iterator<Plant> iterator = plants.iterator();
        while (iterator.hasNext()) {
            Plant plant = iterator.next();
            int plantGridX = (int) (plant.getX() / CELL_WIDTH);
            int plantGridY = (int) (plant.getY() / CELL_HEIGHT);
            if (plantGridX == gridX && plantGridY == gridY) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public void reset(LevelConfig config) {
        this.levelConfig = config;
        plants.clear();
        zombies.clear();
        projectiles.clear();
        suns.clear();
        mowers.clear();
        chickens.clear();
        initMowers();
        score = 0;
        sun = 1000;
        sessionDiscoveredZombies.clear();
        gameOver = false;
        playerWon = false;
        wave = 0;
        wavesCleared = 0;
        waveActive = false;
        spawnedThisWave = 0;
        targetZombiesPerWave = config != null ? config.getZombiesPerWaveBase() : 5;
        lastWaveStartTime = System.currentTimeMillis();
        randomSpawnActive = true;
        lastZombieSpawnTime = System.currentTimeMillis();
        nextRandomSpawnTime = System.currentTimeMillis() + getRandomSpawnInterval();
        lastSunSpawnTime = System.currentTimeMillis();
    }

    public static GameBoard getInstance() {
        return instance;
    }
    
    public static void setInstance(GameBoard board) {
        instance = board;
    }
}
