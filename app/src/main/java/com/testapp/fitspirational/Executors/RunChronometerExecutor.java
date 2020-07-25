package com.testapp.fitspirational.Executors;

import android.os.SystemClock;
import android.widget.Chronometer;

public class RunChronometerExecutor {

    private Chronometer chronometer;
    private long pauseOffSet;

    public RunChronometerExecutor(Chronometer chronometer) {
        this.chronometer = chronometer;
        this.pauseOffSet = 0;
    }

    public void startChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
        chronometer.start();
    }

    public void stopChronometer() {
        pauseOffSet = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
    }

    public int getTimeElapsedSeconds() {
        return (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
    }
}
