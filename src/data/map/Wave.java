package data.map;

import data.entity.enemy.Enemy;

public class Wave implements Comparable<Wave> {
    protected long startTime;
    protected int spawnInterval; // interval between spawns won't be able to be quicker than game engine speed
                                 // obvs
    protected int amountPerSpawn;
    protected int amountLeftToSpawn;
    protected Enemy enemyToSpawn;
    protected long lastSpawnTime;

    Wave(long startTime, int spawnInterval, Enemy enemyToSpawn, int amountPerSpawn, int totalToSpawn) {
        this.startTime = startTime;
        this.spawnInterval = spawnInterval;
        this.enemyToSpawn = enemyToSpawn;
        this.amountPerSpawn = amountPerSpawn;
        this.amountLeftToSpawn = totalToSpawn;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }
    
    public boolean isDone() {
        if (amountLeftToSpawn == 0) return true;
        else return false;
    }

    public boolean readyToSpawn() {
        if ((System.currentTimeMillis() - lastSpawnTime) >= spawnInterval) return true;
        else return false;
    }

    public Enemy getEnemyToSpawn() {
        return enemyToSpawn;
    }
    
    public int getSpawn() {
        lastSpawnTime = System.currentTimeMillis();
        if (amountPerSpawn > amountLeftToSpawn) {
            amountLeftToSpawn-=amountPerSpawn;
            return amountPerSpawn;
        } else {
            int leftovers = amountLeftToSpawn;
            amountLeftToSpawn = 0;
            return leftovers;
        }        
    }

    @Override
    public int compareTo(Wave w) {
        if (this.startTime < w.startTime)
            return -1;
        else if (this.startTime == w.startTime)
            return 0;
        else
            return 1;
    }

}
