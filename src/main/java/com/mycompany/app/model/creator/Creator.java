package com.mycompany.app.model.creator;

import com.mycompany.app.model.concurrency.PausableRunnable;
import com.mycompany.app.model.map.Area;
import com.mycompany.app.model.map.ObjectType;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Log4j2
public class Creator implements PausableRunnable {
    private Area area;
    private int retryLimit = 100;
    private int maxUnits;
    private long period;
    private Map<ObjectType, Integer> spawnChances;

    public Creator(Area area, CreatorOptions options) {
        this.area = area;
        this.retryLimit = options.retryLimit;
        this.maxUnits = options.maxUnits;
        this.period = options.period;
        this.spawnChances = options.spawnChances;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            checkPaused();
            try{
                spawn();
                Thread.sleep(period * 10);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    Point getRandomLocation() {
        Random rand = new Random();
        int iteration = 0;
        while(iteration < retryLimit) {
            Point point = new Point(rand.nextInt(area.width), rand.nextInt(area.height));
            if(!area.probe(point).isOccupied()){
                return point;
            }
        }
        return null;
    }

    synchronized private void spawn() throws CreationFailedException {
        if(area.getUnitCount() < maxUnits) {
            createUnit();
        }
    }

    private void createUnit() throws CreationFailedException {
        log.info("Creating unit...");
        Point point = getRandomLocation();
        ObjectType type = calculateSpawnWinner();
        if(!area.create(point, type)) {
            throw new CreationFailedException(String.format("Point [%d, %d] already occupied", point.x, point.y));
        }
        log.info("Unit created");
    }

    private ObjectType calculateSpawnWinner() {
        Random rand = new Random();
        ObjectType winner = ObjectType.values()[0];
        int spawnChance = 0;

        Map<ObjectType, Integer> calculated = new HashMap<>();
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

    private void setSpawnChance(ObjectType type, int chance) {
        spawnChances.put(type, chance);
    }

    private int getTotalSpawnChances() {
        return spawnChances.entrySet().stream()
                .map(e -> e.getValue())
                .reduce(0, Integer::sum);
    }

    @Builder
    public static class CreatorOptions {
        @Builder.Default
        public int retryLimit = 100;
        @Builder.Default
        public int maxUnits = 500;
        @Builder.Default
        public long period = 1;
        @Builder.Default
        public Map<ObjectType, Integer> spawnChances = getDefaultSpawnChances();

        public static Map<ObjectType, Integer> getDefaultSpawnChances() {
            return Arrays.stream(ObjectType.values())
                    .collect(Collectors.toMap(e -> e, e -> 1));
        }
    }

    static class CreationFailedException extends Exception {
        String message;
        CreationFailedException(String message) {
            message = "Creation failed: " + message;
        }
    }
}
