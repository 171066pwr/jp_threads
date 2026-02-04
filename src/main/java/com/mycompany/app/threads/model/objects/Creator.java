package com.mycompany.app.threads.model.objects;

import com.mycompany.app.threads.model.concurrency.PausableRunnable;
import com.mycompany.app.threads.model.map.Area;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.ObjectType;
import com.mycompany.app.threads.model.map.Tile;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Log4j2
public class Creator implements PausableRunnable {
    private Area area;
    private int retryLimit = 100;
    private int maxUnits;
    private int maxPerks;
    private long period;
    private Map<ObjectType, Integer> spawnChances;
    private Random rand = new Random();

    public Creator(Area area, CreatorOptions options) {
        this.area = area;
        this.retryLimit = options.retryLimit;
        this.maxUnits = options.maxUnits;
        this.maxPerks = options.maxPerks;
        this.period = options.period;
        this.spawnChances = options.spawnChances;
        rand.setSeed(System.currentTimeMillis());
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            checkPaused();
            try{
                spawn();
                Thread.sleep(period);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public void setPeriod(int period) {
        this.period = period < 0 ? (-period) : period;
    }

    Optional<Tile> getRandomLocation() {
            Point point = new Point(rand.nextInt(area.width), rand.nextInt(area.height));
            return area.probe(point);
    }

    private void spawn() {
        if(area.getUnitCount() < maxUnits) {
            createUnit();
        }
        if(area.getPerkCount() < maxPerks) {
            createPerk();
        }
    }

    private void createPerk() {
        log.info("Creating perk...");
        createObject(ObjectType.COOKIE);
    }

    private void createUnit() {
        log.info("Creating unit...");
        ObjectType type = calculateSpawnWinner();
        createObject(type);
    }

    private void createObject(ObjectType type) {
        int iteration = 0;
        Optional<Tile> location;
        while((location = getRandomLocation()).isPresent() && iteration < retryLimit) {
            Tile tile = location.get();
            synchronized (tile) {
                GameObject object = type.create(area);
                if (tile.add(object)) {
                    object.setCurrentTile(tile);
                    area.notifyListeners(new GameEvent(tile.coordinates, object, GameEvent.EventType.CREATION));
                    new Thread(object).start();
                    logCreationSucceeded(type.name(), tile.coordinates);
                    break;
                } else {
                    logCreationFailed("already occupied", tile.coordinates);
                }
            }
        }
    }

    private ObjectType calculateSpawnWinner() {
        ObjectType winner = ObjectType.SCOUT;
        int spawnChance = 0;

        for(ObjectType type : spawnChances.keySet()) {
            Integer roll = rand.nextInt(1000);
            int rolled = spawnChances.get(type) * roll;
            if(rolled > spawnChance) {
                spawnChance = rolled;
                winner = type;
            }
        }
        return winner;
    }

    static void logCreationFailed(String message, Point point) {
        log.info("{} Creation failed: {}", point, message);
    }

    static void logCreationSucceeded(String message, Point point) {
        log.info("{} Object created: {}", point, message);
    }

    @Builder
    public static class CreatorOptions {
        public static int scoutBias = 4;
        @Builder.Default
        public int retryLimit = 100;
        @Builder.Default
        public int maxUnits = 50;
        @Builder.Default
        public int maxPerks = 50;
        @Builder.Default
        public long period = 1000;
        @Builder.Default
        public Map<ObjectType, Integer> spawnChances = getDefaultSpawnChances();

        public static Map<ObjectType, Integer> getDefaultSpawnChances() {
            return Arrays.stream(ObjectType.values())
                    .filter(ObjectType::isUnit)
                    .collect(Collectors.toMap(e -> e, e -> (e == ObjectType.SCOUT ? scoutBias : 1)));
        }
    }
}
