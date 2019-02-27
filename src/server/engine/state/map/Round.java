package server.engine.state.map;

import java.util.TreeSet;

public class Round {
    protected TreeSet<Wave> waves;
    protected boolean isBoss;
    protected long startTime;

    Round(TreeSet<Wave> waves, boolean isBoss) {
        this.waves = waves;
        this.isBoss = isBoss;
        this.startTime = 0;
    }

    public boolean start() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public boolean hasWavesLeft() {
        return !waves.isEmpty();
    }

    public boolean isWaveReady() {
        if (waves.isEmpty()) return false;
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
