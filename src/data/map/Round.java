package data.map;

import java.util.TreeSet;

public class Round {
    protected TreeSet<Wave> waves;
    protected boolean isBoss;
    protected final long startTime;

    Round(TreeSet<Wave> waves, boolean isBoss) {
        this.waves = waves;
        this.isBoss = isBoss;
        this.startTime = System.currentTimeMillis();
    }
    
    public boolean hasWavesLeft() {
        return !waves.isEmpty();
    }

    public boolean isWaveReady() {
        if ((System.currentTimeMillis() - startTime) >= waves.first().startTime)
            return true;
        else
            return false;
    }

    public Wave getNextWave() {
        return waves.pollFirst();
    }

    public boolean isBoss() {
        return isBoss;
    }

    public void addWave(Wave wave) {
        this.waves.add(wave);
    }

}
