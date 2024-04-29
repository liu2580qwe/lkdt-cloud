package org.lkdt.modules.radar.supports.radarDataHandle;

public class PrioritizedRunnable implements Runnable, Comparable<PrioritizedRunnable> {
    private long time;

    public PrioritizedRunnable(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int compareTo(PrioritizedRunnable prioritizedRunnable) {
        if(time > prioritizedRunnable.getTime()){
            return 1;
        } else if(time < prioritizedRunnable.getTime()){
            return -1;
        }
        return 0;
    }

    @Override
    public void run() {

    }
}

