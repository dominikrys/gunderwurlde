package data.map;

import data.entity.enemy.Enemy;

public class Wave implements Comparable<Wave> {
    protected long startTime;
    protected int spawnInterval;
    protected Enemy enemyToSpawn;
    
    Wave(long startTime, int spawnInterval, Enemy enemyToSpawn) {
        this.startTime = startTime;
        this.spawnInterval = spawnInterval;
        this.enemyToSpawn = enemyToSpawn;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public Enemy getEnemyToSpawn() {
        return enemyToSpawn;
    }

    @Override
    public int compareTo(Wave w) {
        if (this.startTime < w.startTime) return -1;
        else if (this.startTime == w.startTime) return 0;
        else return 1;
    }

}
